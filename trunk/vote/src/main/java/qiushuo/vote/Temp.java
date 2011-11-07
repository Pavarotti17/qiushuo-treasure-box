package qiushuo.vote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * (created at 2011-4-25)
 * 
 * @author <a href="mailto:shuo.qius@alibaba-inc.com">QIU Shuo</a>
 */
public class Temp {

    public static void main(String[] args) {
        BufferedReader fin = null;
        try {
            fin =
                    new BufferedReader(new FileReader(new File(
                            "/home/qiushuo/work/work/qiushuo/qiushuo-treasure-box/vote/src/main/java/availablep.txt")));
            for (String line = null; (line = fin.readLine()) != null;) {
                line = line.trim();
                line = line.substring(0,line.indexOf("->"));
                line = line.trim();
                if (line.contains(":")) {
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                fin.close();
            } catch (Exception e2) {
            }
        }
    }

}
