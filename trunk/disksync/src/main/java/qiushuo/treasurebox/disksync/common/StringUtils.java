package qiushuo.treasurebox.disksync.common;

/**
 * (created at 2011-11-7)
 * 
 * @author <a href="mailto:QiuShuo1985@gmail.com">QIU Shuo</a>
 */
public class StringUtils {
    
    public static byte fromString2Byte(char c1, char c2) {
        byte b = 0;
        if (c2 >= '0' && c2 <= '9') {
            b += (c2 - '0');
        } else if (c2 >= 'A' && c2 <= 'F') {
            b += (c2 - 'A' + 10);
        }
        if (c1 >= '0' && c1 <= '9') {
            b += 0xf0 & ((c1 - '0') << 4);
        } else if (c1 >= 'A' && c1 <= 'F') {
            b += 0xf0 & ((c1 - 'A' + 10) << 4);
        }
        return b;
    }

    public static String byte2String(byte b) {
        int up = (b >> 4) & 0x0f;
        int low = b & 0x0f;
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
