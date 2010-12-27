package qiushuo.treasurebox.disksync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

    public static void main(String[] args) throws Exception {
        int size = 32;
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

    public DirCopy(int bufferSize) {
        super();
        buffer = new byte[bufferSize];
    }

    private byte[] buffer;

    /**
     * @param toDir toDirPath/fromDirName
     */
    private void copyDirFile(File fromFile, File toRoot) {
        if (fromFile.isFile()) {
            File toFile = copyFile(fromFile, toRoot);
            if (toFile != null) log(toFile.getAbsolutePath());
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
        InputStream fin = null;
        OutputStream fout = null;
        try {
            fin = new FileInputStream(fromFile);
            fout = new FileOutputStream(toFile);
            for (int b = 0; (b = fin.read(buffer)) >= 0;) {
                if (b > 0) fout.write(buffer, 0, b);
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
}
