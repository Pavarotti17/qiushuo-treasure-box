package qiushuo.treasurebox.disksync;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * (created at 2010-11-4)
 * 
 * @author <a href="mailto:QiuShuo1985@gmail.com">QIU Shuo</a>
 */
public class DirCopy {
    public static void main(String[] args) throws Exception {
        DirCopy copier = new DirCopy();
        BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("source dir or file path:");
        String fromFilePath = sin.readLine().trim();
        System.out.print("\r\nto this dir:");
        String toRootPath = sin.readLine().trim();
        copier.copyDirFile(new File(fromFilePath), new File(toRootPath));
    }

    /**
     * @param toDir toDirPath/fromDirName
     */
    private void copyDirFile(File fromFile, File toRoot) {
        if (fromFile.isFile()) {
            File toFile = copyFile(fromFile, toRoot);
            System.out.println(toFile.getAbsolutePath());
        } else {
            File toDir = new File(toRoot, fromFile.getName());
            if (!toDir.exists()) {
                toDir.mkdirs();
            }
            System.out.println(toDir.getAbsolutePath() + File.separator);
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
        BufferedInputStream fin = null;
        BufferedOutputStream fout = null;
        try {
            fin = new BufferedInputStream(new FileInputStream(fromFile), 1024 * 1024 * 16);
            fout = new BufferedOutputStream(new FileOutputStream(toFile), 1024 * 1024 * 16);
            while (true) {
                int b = fin.read();
                if (b == -1) break;
                fout.write(b);
            }
            return toFile;
        } catch (Exception e) {
            throw new RuntimeException("fromFile:"
                                       + fromFile.getAbsolutePath()
                                       + "; toFile:"
                                       + toFile.getAbsolutePath(), e);
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
    }

}
