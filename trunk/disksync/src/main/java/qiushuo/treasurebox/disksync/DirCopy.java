package qiushuo.treasurebox.disksync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * (created at 2010-11-4)
 * 
 * @author <a href="mailto:QiuShuo1985@gmail.com">QIU Shuo</a>
 */
public class DirCopy {
    private static SimpleDateFormat df = new SimpleDateFormat("[yyyy-MM-dd,HH:mm:ss] ");

    private static void log(String msg) {
        System.out.println(new StringBuilder().append(df.format(new Date())).append(msg));
    }

    private static void err(String msg) {
        System.err.println(new StringBuilder().append(df.format(new Date())).append(msg));
    }

    private static boolean exist(File fromFile, File toFile) {
        if (toFile.exists() && fromFile.isFile() && toFile.isFile()) {
            long len1 = fromFile.length();
            long len2 = toFile.length();
            return len1 == len2;
        } else {
            return false;
        }
    }

    public static void deleteDir(File dir) {
        if (dir == null) return;
        if (dir.isFile()) dir.delete();
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

    private byte[] buffer;

    public DirCopy(int bufferSize) {
        super();
        buffer = new byte[bufferSize];
    }

    /**
     * @param toDir toDirPath/fromDirName
     */
    public void copyDirFile(File fromFile, File toRoot) {
        if (fromFile.isFile()) {
            //if (false) {
            File toFile = copyFile(fromFile, toRoot);
            if (toFile != null) log(toFile.getAbsolutePath());
            //}
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
        } else if (exist(fromFile, toFile)) {
            return toFile;
        }
        FileInputStream fin = null;
        FileOutputStream fout = null;
        try {
            fin = new FileInputStream(fromFile);
            fout = new FileOutputStream(toFile);
            for (int b = 0; (b = fin.read(buffer)) >= 0;) {
                if (b > 0) {
                    //System.out.println(b);
                    fout.write(buffer, 0, b);
                }
            }
        } catch (Exception e) {
            err("fromFile:" + fromFile.getAbsolutePath() + "; toFile:" + toFile.getAbsolutePath() + ". exception: " + e);
            return null;
        } finally {
            try {
                fin.close();
            } catch (Exception e1) {
            }
            try {
                fout.flush();
            } catch (Exception e2) {
            }
            try {
                fout.close();
            } catch (Exception e2) {
            }
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

    public static void main(String[] args) throws Exception {
        int size = 128;
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
}
