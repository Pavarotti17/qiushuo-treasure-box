package qiushuo.treasurebox.disksync.common;

import java.io.BufferedReader;

/**
 * (created at 2011-11-7)
 * 
 * @author <a href="mailto:QiuShuo1985@gmail.com">QIU Shuo</a>
 */
public class Confirm {
    public static boolean confirm(BufferedReader sin) throws Exception {
        System.out.print("\r\nY/N? ");
        for (String line = null; (line = sin.readLine()) != null;) {
            line = line.trim().toLowerCase();
            if ("y".equals(line)) return true;
            if ("n".equals(line)) return false;
        }
        return false;
    }
}
