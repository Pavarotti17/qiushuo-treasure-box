package qiushuo.treasurebox.disksync.common;

import java.io.File;

/**
 * (created at 2011-11-7)
 * 
 * @author <a href="mailto:QiuShuo1985@gmail.com">QIU Shuo</a>
 */
public class StringUtils {
    private static final byte[] EMPTY_BYTES = new byte[0];
    public static final String NEW_LINE = nl();

    private static String nl() {
        String nl;
        try {
            nl = System.getProperty("line.separator");
        } catch (Exception e) {
            nl = "\r\n";
        }
        if (nl == null || nl.length() <= 0) {
            nl = "\r\n";
        }
        return nl;
    }

    public static String getRelevantPath(File root, File file) {
        String rst = file.getName();
        for (; !file.getParentFile().getAbsolutePath().equals(root.getAbsolutePath());) {
            file = file.getParentFile();
            rst = file.getName() + Config.INDEX_FILE_PATH_SEPERATOR + rst;
        }
        return rst;
    }

    public static String indent(int indent) {
        final int indentSize = 4;
        StringBuilder sb = new StringBuilder(indent * indentSize);
        for (int i = 0; i < indent * indentSize; ++i) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public static byte[] fromString2Byte(String str) {
        if (str == null) return null;
        if (str.length() == 0) return EMPTY_BYTES;
        if (str.length() % 2 != 0) throw new IllegalArgumentException("string must contains even number of chars");
        try {
            byte[] rst = new byte[str.length() / 2];
            for (int i = 0; i < rst.length; ++i) {
                rst[i] = fromString2Byte(str.charAt(i * 2), str.charAt(i * 2 + 1));
            }
            return rst;
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid string: " + str, e);
        }
    }

    public static byte fromString2Byte(char c1, char c2) {
        byte b = 0;
        if (c2 >= '0' && c2 <= '9') {
            b += (c2 - '0');
        } else if (c2 >= 'A' && c2 <= 'F') {
            b += (c2 - 'A' + 10);
        } else if (c2 >= 'a' && c2 <= 'f') {
            b += (c2 - 'a' + 10);
        } else {
            throw new IllegalArgumentException("char invalid: " + c2);
        }
        if (c1 >= '0' && c1 <= '9') {
            b += 0xf0 & ((c1 - '0') << 4);
        } else if (c1 >= 'A' && c1 <= 'F') {
            b += 0xf0 & ((c1 - 'A' + 10) << 4);
        } else if (c1 >= 'a' && c1 <= 'f') {
            b += 0xf0 & ((c1 - 'a' + 10) << 4);
        } else {
            throw new IllegalArgumentException("char invalid: " + c1);
        }
        return b;
    }

    public static String bytes2String(byte[] bs) {
        if (bs == null) return null;
        if (bs.length <= 0) return "";
        char[] chars = new char[2 * bs.length];
        for (int i = 0, len = bs.length; i < len; ++i) {
            byte b = bs[i];
            final int up = (b >> 4) & 0x0f;
            final int low = b & 0x0f;
            switch (up) {
                case 0:
                    chars[2 * i] = '0';
                    break;
                case 1:
                    chars[2 * i] = '1';
                    break;
                case 2:
                    chars[2 * i] = '2';
                    break;
                case 3:
                    chars[2 * i] = '3';
                    break;
                case 4:
                    chars[2 * i] = '4';
                    break;
                case 5:
                    chars[2 * i] = '5';
                    break;
                case 6:
                    chars[2 * i] = '6';
                    break;
                case 7:
                    chars[2 * i] = '7';
                    break;
                case 8:
                    chars[2 * i] = '8';
                    break;
                case 9:
                    chars[2 * i] = '9';
                    break;
                case 10:
                    chars[2 * i] = 'A';
                    break;
                case 11:
                    chars[2 * i] = 'B';
                    break;
                case 12:
                    chars[2 * i] = 'C';
                    break;
                case 13:
                    chars[2 * i] = 'D';
                    break;
                case 14:
                    chars[2 * i] = 'E';
                    break;
                case 15:
                    chars[2 * i] = 'F';
                    break;
            }
            switch (low) {
                case 0:
                    chars[2 * i + 1] = '0';
                    break;
                case 1:
                    chars[2 * i + 1] = '1';
                    break;
                case 2:
                    chars[2 * i + 1] = '2';
                    break;
                case 3:
                    chars[2 * i + 1] = '3';
                    break;
                case 4:
                    chars[2 * i + 1] = '4';
                    break;
                case 5:
                    chars[2 * i + 1] = '5';
                    break;
                case 6:
                    chars[2 * i + 1] = '6';
                    break;
                case 7:
                    chars[2 * i + 1] = '7';
                    break;
                case 8:
                    chars[2 * i + 1] = '8';
                    break;
                case 9:
                    chars[2 * i + 1] = '9';
                    break;
                case 10:
                    chars[2 * i + 1] = 'A';
                    break;
                case 11:
                    chars[2 * i + 1] = 'B';
                    break;
                case 12:
                    chars[2 * i + 1] = 'C';
                    break;
                case 13:
                    chars[2 * i + 1] = 'D';
                    break;
                case 14:
                    chars[2 * i + 1] = 'E';
                    break;
                case 15:
                    chars[2 * i + 1] = 'F';
                    break;
            }
        }
        return new String(chars);
    }

    public static String byte2String(byte b) {
        final int up = (b >> 4) & 0x0f;
        final int low = b & 0x0f;
        char[] chars = new char[2];
        switch (up) {
            case 0:
                chars[0] = '0';
                break;
            case 1:
                chars[0] = '1';
                break;
            case 2:
                chars[0] = '2';
                break;
            case 3:
                chars[0] = '3';
                break;
            case 4:
                chars[0] = '4';
                break;
            case 5:
                chars[0] = '5';
                break;
            case 6:
                chars[0] = '6';
                break;
            case 7:
                chars[0] = '7';
                break;
            case 8:
                chars[0] = '8';
                break;
            case 9:
                chars[0] = '9';
                break;
            case 10:
                chars[0] = 'A';
                break;
            case 11:
                chars[0] = 'B';
                break;
            case 12:
                chars[0] = 'C';
                break;
            case 13:
                chars[0] = 'D';
                break;
            case 14:
                chars[0] = 'E';
                break;
            case 15:
                chars[0] = 'F';
                break;
        }
        switch (low) {
            case 0:
                chars[1] = '0';
                break;
            case 1:
                chars[1] = '1';
                break;
            case 2:
                chars[1] = '2';
                break;
            case 3:
                chars[1] = '3';
                break;
            case 4:
                chars[1] = '4';
                break;
            case 5:
                chars[1] = '5';
                break;
            case 6:
                chars[1] = '6';
                break;
            case 7:
                chars[1] = '7';
                break;
            case 8:
                chars[1] = '8';
                break;
            case 9:
                chars[1] = '9';
                break;
            case 10:
                chars[1] = 'A';
                break;
            case 11:
                chars[1] = 'B';
                break;
            case 12:
                chars[1] = 'C';
                break;
            case 13:
                chars[1] = 'D';
                break;
            case 14:
                chars[1] = 'E';
                break;
            case 15:
                chars[1] = 'F';
                break;
        }
        return new String(chars);
    }
}
