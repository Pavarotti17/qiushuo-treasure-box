package qiushuo.treasurebox.disksync.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * clean windows' invalid file or dir<br/>
 * (created at 2010-8-23)
 * 
 * @author <a href="mailto:shuo.qius@gmail.com">QIU Shuo</a>
 */
public class WindowsTrashFileCleaner {
    private final boolean delete;

    /**
     * @param delete true: delete file whose file name is invalid; false: not
     *            delete those files, just print it
     */
    public WindowsTrashFileCleaner(boolean delete) {
        this.delete = delete;
    }

    public boolean cleanInsideDir(File dir) {
        File[] fs = dir.listFiles();
        if (fs == null || fs.length <= 0)
            return false;
        boolean cleaned = false;
        for (File f : fs) {
            if (!validName(f)) {
                dealDirFile(f);
                cleaned = true;
            } else if (f.isDirectory()) {
                cleaned = cleanInsideDir(f) || cleaned;
            }
        }
        return cleaned;
    }

    private void dealDirFile(File f) {
        if (!delete) {
            System.out.println(f.getAbsolutePath());
        } else {
            dealQuietly(f);
        }
    }

    public static void main(String[] args) throws IOException {
        printHelp();
        BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("\r\n>>");
            String line = sin.readLine();
            if (line == null || line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
                break;
            } else if (line.length() == 0) {
                printHelp();
                continue;
            }
            line = line.trim();
            int ind = line.indexOf(' ');
            if (ind < 0) {
                printHelp();
                continue;
            }
            String cmd = line.substring(0, ind).trim();
            String arg = line.substring(ind + 1);
            if ("print".equalsIgnoreCase(cmd)) {
                new WindowsTrashFileCleaner(false).cleanInsideDir(new File(arg));
            } else if ("delete".equalsIgnoreCase(cmd)) {
                new WindowsTrashFileCleaner(true).cleanInsideDir(new File(arg));
            } else {
                printHelp();
                continue;
            }
        }
    }

    private static boolean validName(File f) {
        String name = f.getName();
        if (name == null || name.length() <= 0 || name.contains("/") || name.contains("\\") || name.contains(":")
                || name.contains("*") || name.contains("?") || name.contains("\"") || name.contains("<")
                || name.contains(">") || name.contains("|")) {
            return false;
        }
        return true;
    }

    private static boolean dealQuietly(File file) {
        if (file == null) {
            return false;
        }
        try {
            if (file.isDirectory()) {
                cleanDirectory(file);
            }
        } catch (Exception e) {
        }

        try {
            return file.delete();
        } catch (Exception e) {
            return false;
        }
    }

    private static void cleanDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        File[] files = directory.listFiles();
        if (files == null) { // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }

        IOException exception = null;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            try {
                forceDelete(file);
            } catch (IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    private static void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        cleanDirectory(directory);
        if (!directory.delete()) {
            String message = "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }

    private static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    private static void printHelp() {
        System.out.println("\t'print\t${path}' for print invalid files under ${path}");
        System.out.println("\t'delete\t${path}' for delete invalid files under ${path}");
        System.out.println("\t'exit' for quit");
    }
}
