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
 * @author <a href="mailto:shuo.qius@gmail.com">QIU Shuo</a>
 */
public class IndexFileUtil {
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
     * @param rpath trimmed
     */
    public static File fromRelavant2File(File root, String rpath) {
        File file = root;
        for (;;) {
            int idx = rpath.indexOf(Config.INDEX_FILE_PATH_SEPERATOR);
            if (idx < 0) {
                file = new File(file, rpath);
                break;
            }
            file = new File(file, rpath.substring(0, idx));
            rpath = rpath.substring(1 + idx);
        }
        return file;
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

}
