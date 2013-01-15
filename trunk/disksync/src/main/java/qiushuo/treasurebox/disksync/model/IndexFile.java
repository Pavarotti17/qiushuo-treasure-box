package qiushuo.treasurebox.disksync.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import qiushuo.treasurebox.disksync.common.Config;
import qiushuo.treasurebox.disksync.common.StringUtils;

/**
 * (created at 2013-1-10)
 * 
 * @author <a href="mailto:QiuShuo1985@gmail.com">QIU Shuo</a>
 */
public class IndexFile {
    private static final byte[] READ_BUF = new byte[Config.READ_FILE_BUFFER_LEN];

    /**
     * format: <code>md5:size:timestamp,path</code><br/>
     * example:
     * <code>1234567890ABCDEF1234567890ABCDEF:1234567:1357872482845,China/G 5/张艺谋/活着/huozhe.avi</code>
     */
    public static String getIndexString4File(File root, File file) throws IOException, NoSuchAlgorithmException {
        String size = String.valueOf(file.length());
        String timestamp = String.valueOf(file.lastModified());
        String path = StringUtils.getRelevantPath(root, file);

        byte[] md5bytes;
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            for (int len = 0; (len = in.read(READ_BUF)) >= 0;) {
                md5.update(READ_BUF, 0, len);
            }
            md5bytes = md5.digest();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
        StringBuilder sb =
                new StringBuilder(md5bytes.length * 2 + size.length() + timestamp.length() + path.length() + 3);
        sb.append(StringUtils.bytes2String(md5bytes))
          .append(':')
          .append(size)
          .append(':')
          .append(timestamp)
          .append(',')
          .append(path);
        return sb.toString();
    }

    /**
     * format: <code>[],path/</code><br/>
     * example: <code>[],China/G 5/张艺谋/</code>
     */
    public static String getIndexString4EmptyDir(File root, File dir) throws IOException {
        String path = StringUtils.getRelevantPath(root, dir);
        return new StringBuilder(4 + path).append("[],").append(path).append('/').toString();
    }

    //    /**
    //     * format: <code>md5:size:,path</code>
    //     */
    //    private void readOldIndexFile(File indexFile) throws Exception {
    //        fileContentIndex = new HashMap<String, FileContent>();
    //        fileIndex = new HashMap<FileIndexKey, String>();
    //        InputStream fin = null;
    //        try {
    //            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(indexFile), "utf-8"));
    //            for (String line = null; (line = in.readLine()) != null;) {
    //                int comma = line.indexOf(',');
    //                if (comma < 0) continue;
    //                String indexString = line.substring(0, comma).trim();
    //                String path = line.substring(1 + comma).trim();
    //                //QS_TODO
    //            }
    //        } finally {
    //            try {
    //                fin.close();
    //            } catch (Exception e) {
    //            }
    //        }
    //    }
    //
    //    private Map<FileIndexKey, String> fileIndex;
    //    private Map<String, FileContent> fileContentIndex;
    //
    //    private static class FileContent {
    //        private final FileIndexKey key;
    //        private final long gmt;
    //
    //        /**
    //         * @param dirPath e.g. "China/G6/"
    //         * @param gmt last modified time stamp
    //         */
    //        public FileContent(byte[] md5, int size, long gmt) {
    //            this.key = new FileIndexKey(md5, size);
    //            this.gmt = gmt;
    //        }
    //
    //        @Override
    //        public int hashCode() {
    //            return (int) (key.hashCode() + gmt);
    //        }
    //
    //        @Override
    //        public boolean equals(Object obj) {
    //            if (obj == this) return true;
    //            if (!(obj instanceof FileContent)) return false;
    //            FileContent that = (FileContent) obj;
    //            return that.gmt == this.gmt && (that.key == null ? this.key == null : that.key.equals(this.key));
    //        }
    //    }
}
