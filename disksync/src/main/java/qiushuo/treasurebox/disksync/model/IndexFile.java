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
     * @throws IllegalArgumentException if line is not right format for file
     *             content
     */
    public static FileContent decodeAsFileContent(String line) throws IllegalArgumentException {
        try {
            line = line.trim();
            int in = line.indexOf(':');
            final String md5Str = line.substring(0, in);
            line = line.substring(in + 1);
            in = line.indexOf(':');
            final long size = Long.parseLong(line.substring(0, in));
            line = line.substring(in + 1);
            in = line.indexOf(',');
            final long time = Long.parseLong(line.substring(0, in));
            String path = line.substring(in + 1).trim();
            return new FileContent(path, md5Str, size, time);
        } catch (Exception e) {
            throw new IllegalArgumentException("format error: " + line, e);
        }
    }

    public static FileContent buildIndexFileContent(File root, File file) throws IOException, NoSuchAlgorithmException {
        byte[] md5bytes;
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            for (int len = 0; (len = in.read(READ_BUF)) >= 0;) {
                if (len > 0) {
                    md5.update(READ_BUF, 0, len);
                }
            }
            md5bytes = md5.digest();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
        return new FileContent(StringUtils.getRelevantPath(root, file), md5bytes, file.length(), file.lastModified());
    }

    /**
     * format: <code>[],path/</code><br/>
     * example: <code>[],China/G 5/张艺谋/</code>
     */
    public static String getIndexString4EmptyDir(File root, File dir) throws IOException {
        String path = StringUtils.getRelevantPath(root, dir);
        return new StringBuilder(4 + path.length()).append("[],")
                                                   .append(path)
                                                   .append(Config.INDEX_FILE_PATH_SEPERATOR)
                                                   .toString();
    }

    /**
     * format: <code>md5:size:timestamp,path</code><br/>
     * example:
     * <code>1234567890ABCDEF1234567890ABCDEF:1234567:1357872482845,China/G 5/张艺谋/活着/huozhe.avi</code>
     */
    public static class FileContent {
        private final String path;
        private final String md5String;
        private final byte[] md5;
        private final long size;
        private final long timestamp;

        public FileContent(String path, String md5String, long size, long timestamp) throws NoSuchAlgorithmException {
            this.path = path;
            this.md5String = md5String.toUpperCase();
            this.md5 = StringUtils.fromString2Byte(md5String);
            if (this.md5.length != MessageDigest.getInstance("MD5").getDigestLength()) {
                throw new IllegalArgumentException("wrong md5 string: " + md5String);
            }
            this.size = size;
            this.timestamp = timestamp;
        }

        public FileContent(String path, byte[] md5, long size, long timestamp) throws NoSuchAlgorithmException {
            this.path = path;
            this.md5String = StringUtils.bytes2String(md5).toUpperCase();
            this.md5 = md5;
            if (this.md5.length != MessageDigest.getInstance("MD5").getDigestLength()) {
                throw new IllegalArgumentException("wrong md5 string: " + md5String);
            }
            this.size = size;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(md5String.length() + 20 + path.length() + 3);
            sb.append(md5String).append(':').append(size).append(':').append(timestamp).append(',').append(path);
            return sb.toString();
        }

        public String getPath() {
            return path;
        }

        public String getMd5String() {
            return md5String;
        }

        /**
         * @return do not modify it!
         */
        public byte[] getMd5() {
            return md5;
        }

        public long getSize() {
            return size;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
