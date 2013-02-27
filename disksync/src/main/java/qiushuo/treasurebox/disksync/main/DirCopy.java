package qiushuo.treasurebox.disksync.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * (created at 2010-11-4)
 * 
 * @author <a href="mailto:shuo.qius@gmail.com">QIU Shuo</a>
 */
public class DirCopy {
    private static final boolean COPY_DIR_ONLY = false;
    private static SimpleDateFormat df = new SimpleDateFormat("[yyyy-MM-dd,HH:mm:ss] ");

    private static void log(String msg) {
        System.out.println(new StringBuilder().append(df.format(new Date())).append(msg));
    }

    private static void err(String msg) {
        System.err.println(new StringBuilder().append(df.format(new Date())).append(msg));
    }

    /**
     * check: lastModified with precision of second; fileLength
     */
    private static boolean isFileAlreadyExist(File fromFile, File toFile) {
        if (toFile.exists() && fromFile.isFile() && toFile.isFile()) {
            long len1 = fromFile.length();
            long len2 = toFile.length();
            long t1 = fromFile.lastModified() / 1000;
            long t2 = toFile.lastModified() / 1000;
            return len1 == len2 && t1 == t2;
        }
        return false;
    }

    public static void deleteDir(File dir) {
        if (dir == null)
            return;
        if (dir.isFile())
            dir.delete();
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null && children.length > 0) {
                for (File child : children) {
                    deleteDir(child);
                }
            }
        }
        dir.delete();
    }

    private final FileCopier fileCopier;

    public DirCopy(int bufferSize) {
        this.fileCopier = new FileCopier(bufferSize);
    }

    /**
     * @param toDir toDirPath/fromDirName
     */
    public void copyDirFile(File fromFile, File toRoot) {
        if (fromFile.isFile()) {
            if (!COPY_DIR_ONLY) {
                File toFile = copyFile(fromFile, toRoot);
                if (toFile != null) {
                    log(toFile.getAbsolutePath());
                }
            }
        } else {
            File toDir = new File(toRoot, fromFile.getName());
            if (!toDir.exists()) {
                toDir.mkdirs();
            }
            log(toDir.getAbsolutePath() + File.separator);
            File[] underFrom = fromFile.listFiles();
            if (underFrom != null) {
                for (File eachUnderFrom : underFrom) {
                    copyDirFile(eachUnderFrom, toDir);
                }
            }
        }
    }

    private File copyFile(File fromFile, File toDir) {
        File toFile = new File(toDir, fromFile.getName());
        if (toFile.isDirectory()) {
            deleteDir(toFile);
        } else if (isFileAlreadyExist(fromFile, toFile)) {
            return toFile;
        }
        try {
            fileCopier.doFileCopy(fromFile, toFile);
            toFile.setLastModified(fromFile.lastModified());
        } catch (Exception e) {
            return null;
        }

        checkSameContent(fromFile, toFile);
        return toFile;
    }

    private void checkSameContent(File fromFile, File toFile) {
        long sizeFrom = fromFile.length();
        long sizeTo = toFile.length();
        if (sizeFrom != sizeTo) {
            err("size not equal: from=" + fromFile.getAbsolutePath() + ", to=" + toFile.getAbsolutePath());
        }
    }

    /**
     * -Xms240m -Xmx240m -Xmn24m -Xss512k
     */
    public static void main(String[] args) throws Exception {
        int size = 96;
        try {
            size = Integer.parseInt(args[0].trim());
        } catch (Exception e) {
            System.out.println("args[0] is buffer size in MByte, default " + size);
        }
        System.out.println("bufferSize " + size + " MByte");
        DirCopy copier = new DirCopy(1024 * 1024 * size);
        BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("source dir or file path:");
        String fromFilePath = sin.readLine().trim();
        System.out.print("\r\nto this dir:");
        String toRootPath = sin.readLine().trim();
        copier.copyDirFile(new File(fromFilePath), new File(toRootPath));
    }

    /**
     * not thread safe; can be reuse
     */
    private static class FileCopier {
        private static final int BUFFER_POOL_SIZE = 2;
        private final long bufferSize;
        private final BufferPool bufferPool;
        private final BlockingQueue<Buffer> dataQueue;

        public FileCopier(int bufferSize) {
            this.bufferSize = bufferSize;
            this.bufferPool = new BufferPool(BUFFER_POOL_SIZE, bufferSize);
            this.dataQueue = new ArrayBlockingQueue<Buffer>(BUFFER_POOL_SIZE - 1);
        }

        /**
         * MUST NOT invoked concurrently
         */
        public void doFileCopy(File fromFile, File toFile) throws Exception {
            if (fromFile.length() <= bufferSize) {
                copyFileInSingalThread(fromFile, toFile);
                return;
            }
            WriteThread writer = new WriteThread(toFile);
            writer.start();
            FileInputStream fin = null;
            try {
                fin = new FileInputStream(fromFile);
                for (;;) {
                    Buffer buf = bufferPool.getBuffer();
                    try {
                        buf.size = fin.read(buf.data);
                        if (buf.size < 0) {
                            bufferPool.releaseBuffer(buf);
                            break;
                        } else if (buf.size == 0) {
                            bufferPool.releaseBuffer(buf);
                        } else {
                            dataQueue.put(buf);
                        }
                    } catch (Exception e) {
                        bufferPool.releaseBuffer(buf);
                        throw e;
                    }
                }
            } catch (Exception e) {
                err("read fromFile: " + fromFile.getAbsolutePath() + ". exception: " + e);
                throw e;
            } finally {
                try {
                    fin.close();
                } catch (Exception e1) {
                }
                writer.readFinish();
                writer.join();
                try {
                    for (Buffer buf = null; (buf = dataQueue.poll()) != null; bufferPool.releaseBuffer(buf));
                } catch (Exception e2) {
                }
            }
        }

        private void copyFileInSingalThread(File fromFile, File toFile) throws Exception {
            FileInputStream fin = null;
            FileOutputStream fout = null;
            Buffer buf = null;
            try {
                fin = new FileInputStream(fromFile);
                fout = new FileOutputStream(toFile);
                for (buf = bufferPool.getBuffer(); (buf.size = fin.read(buf.data)) >= 0;) {
                    if (buf.size > 0) {
                        fout.write(buf.data, 0, buf.size);
                    }
                }
            } catch (Exception e) {
                err("fromFile: " + fromFile.getAbsolutePath() + ", toFile: " + toFile.getAbsolutePath()
                        + ", exception: " + e);
                throw e;
            } finally {
                try {
                    fin.close();
                } catch (Exception e2) {
                }
                try {
                    fout.flush();
                } catch (Exception e2) {
                }
                try {
                    fout.close();
                } catch (Exception e2) {
                }
                if (buf != null) {
                    bufferPool.releaseBuffer(buf);
                }
            }
        }

        private class WriteThread extends Thread {
            private final File toFile;

            public WriteThread(File toFile) {
                this.toFile = toFile;
                this.readFinished = false;
            }

            private volatile boolean readFinished;

            public void readFinish() {
                readFinished = true;
            }

            private void write(FileOutputStream fout) throws Exception {
                for (Buffer buf = null; (buf = dataQueue.poll(1000, TimeUnit.MILLISECONDS)) != null;) {
                    try {
                        fout.write(buf.data, 0, buf.size);
                    } finally {
                        bufferPool.releaseBuffer(buf);
                    }
                }
            }

            @Override
            public void run() {
                FileOutputStream fout = null;
                try {
                    fout = new FileOutputStream(toFile);
                    for (; !readFinished;) {
                        write(fout);
                    }
                    write(fout);
                } catch (Exception e) {
                    err("write toFile: " + toFile.getAbsolutePath() + ". exception: " + e);
                } finally {
                    try {
                        fout.flush();
                    } catch (Exception e2) {
                    }
                    try {
                        fout.close();
                    } catch (Exception e2) {
                    }
                }
            }
        }

        private static class Buffer {
            public final byte[] data;
            public int size;

            public Buffer(int capacity) {
                this.data = new byte[capacity];
                this.size = 0;
            }
        }

        private static class BufferPool {
            private final Buffer[] pool;
            private final Lock lock;
            private final Condition notEmpty;

            public BufferPool(int capacity, int bufferSize) {
                if (capacity <= 0)
                    throw new IllegalArgumentException("buffer pool at least have one buffer");
                this.lock = new ReentrantLock();
                this.notEmpty = this.lock.newCondition();
                this.pool = new Buffer[capacity];
                for (int i = 0; i < this.pool.length; ++i) {
                    this.pool[i] = new Buffer(bufferSize);
                }
            }

            public Buffer getBuffer() throws InterruptedException {
                lock.lock();
                try {
                    for (;;) {
                        for (int i = 0; i < pool.length; ++i) {
                            if (pool[i] != null) {
                                Buffer buf = pool[i];
                                pool[i] = null;
                                return buf;
                            }
                        }
                        notEmpty.await();
                    }
                } finally {
                    lock.unlock();
                }
            }

            public void releaseBuffer(Buffer buf) {
                lock.lock();
                try {
                    for (int i = 0; i < pool.length; ++i) {
                        if (pool[i] == null) {
                            pool[i] = buf;
                            break;
                        }
                    }
                    notEmpty.signalAll();
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}
