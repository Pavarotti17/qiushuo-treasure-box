package qiushuo.treasurebox.disksync.model;

import qiushuo.treasurebox.disksync.common.StringUtils;

/**
 * (created at 2011-11-7)
 * 
 * @author <a href="mailto:QiuShuo1985@gmail.com">QIU Shuo</a>
 */
public class IndexKey {
    private final byte[] md5;
    private final int fileSize;

    public IndexKey(byte[] md5, int fileSize) {
        if (md5.length < 4) throw new IllegalArgumentException("md5 argument");
        if (md5.length != 16) throw new IllegalArgumentException("md5 byte[] length must be 16");
        this.md5 = md5;
        this.fileSize = fileSize;
    }

    public IndexKey(String indexString) {
        indexString = indexString.trim();
        if (indexString.charAt(32) != ':') throw new IllegalArgumentException("indexString format err: " + indexString);
        this.md5 = new byte[16];
        for (int i = 0, j = 0; i < 32; ++j) {
            char c1 = indexString.charAt(i++);
            char c2 = indexString.charAt(i++);
            this.md5[j] = StringUtils.fromString2Byte(c1, c2);
        }
        this.fileSize = Integer.parseInt(indexString.substring(33).trim());
    }

    @Override
    public int hashCode() {
        return (md5[md5.length - 1] << 24)
               + (md5[md5.length - 2] << 16)
               + (md5[md5.length - 3] << 8)
               + (md5[md5.length - 4]);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof IndexKey)) return false;
        IndexKey that = (IndexKey) obj;
        if (that.fileSize != this.fileSize) return false;
        if (that.md5.length != this.md5.length) return false;
        for (int i = 0; i < this.md5.length; ++i) {
            if (that.md5[i] != this.md5[i]) return false;
        }
        return true;
    }

    private String toString;
    @Override
    public String toString() {
        if (toString != null) return toString;
        String size = String.valueOf(fileSize);
        StringBuilder sb = new StringBuilder(33 + size.length());
        for (int i = 0; i < md5.length; ++i) {
            sb.append(StringUtils.byte2String(md5[i]));
        }
        sb.append(':').append(size);
        return toString = sb.toString();
    }

}
