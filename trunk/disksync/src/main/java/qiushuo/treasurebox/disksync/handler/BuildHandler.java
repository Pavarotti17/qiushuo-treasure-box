package qiushuo.treasurebox.disksync.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import qiushuo.treasurebox.disksync.Shell;
import qiushuo.treasurebox.disksync.common.Confirm;
import qiushuo.treasurebox.disksync.model.FileIndexKey;

/**
 * (created at 2011-11-7)
 * 
 * @author <a href="mailto:QiuShuo1985@gmail.com">QIU Shuo</a>
 */
public class BuildHandler extends PathArgumentsHandler {
    private static final String INDEX_FILE_NAME = ".fileContentIndex.qs";

    @Override
    public synchronized void handle(Shell shell, String cmdArg, BufferedReader sin) throws Exception {
        arguments = cmdArg;
        String rootPath = parserPath();
        rootPath = rootPath.trim();
        if (rootPath.length() == 0) {
            rootPath = shell.getWorkRoot().trim();
        }
        File root = new File(rootPath);
        rootPath = root.getCanonicalPath();
        root = new File(rootPath);
        System.out.println("build index for " + rootPath);
        if (!root.isDirectory()) {
            throw new IllegalArgumentException("build root must be dir");
        }
        if (!Confirm.confirm(sin)) return;

        //QS_TODO
    }

    /**
     * format: <code>md5:size,path</code>
     */
    private void readOldIndexFile(File indexFile) throws Exception {
        fileContentIndex = new HashMap<String, FileContent>();
        fileIndex = new HashMap<FileIndexKey, String>();
        InputStream fin = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(indexFile), "utf-8"));
            for (String line = null; (line = in.readLine()) != null;) {
                int comma = line.indexOf(',');
                if (comma < 0) continue;
                String indexString = line.substring(0, comma).trim();
                String path = line.substring(1 + comma).trim();
                //QS_TODO
            }
        } finally {
            try {
                fin.close();
            } catch (Exception e) {
            }
        }
    }

    private Map<FileIndexKey, String> fileIndex;
    private Map<String, FileContent> fileContentIndex;

    private static class FileContent {
        private final FileIndexKey key;
        private final long gmt;

        /**
         * @param dirPath e.g. "China/G6/"
         * @param gmt last modified time stamp
         */
        public FileContent(byte[] md5, int size, long gmt) {
            this.key = new FileIndexKey(md5, size);
            this.gmt = gmt;
        }

        @Override
        public int hashCode() {
            return (int) (key.hashCode() + gmt);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof FileContent)) return false;
            FileContent that = (FileContent) obj;
            return that.gmt == this.gmt && (that.key == null ? this.key == null : that.key.equals(this.key));
        }
    }

}
