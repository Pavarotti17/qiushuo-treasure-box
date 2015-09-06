/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2015 All Rights Reserved.
 */
package dbclient.toshl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * 将monny report手工修正后的txt转换成toshl格式
 * @author shuo.qius
 * @version $Id: Convert.java, v 0.1 2015年4月28日 下午4:40:45 qiushuo Exp $
 */
public class Convert {
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable {
        PrintWriter fout = new PrintWriter(
            new File("/Users/qiushuo/Downloads/toshl_export_hey.csv"));
        fout.println("head");
        BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(new File(
            "/Users/qiushuo/Downloads/toshl_hey.txt"))));
        for (String line = null; (line = fin.readLine()) != null;) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            int idx = 0;
            String date = line.substring(idx, idx = line.indexOf(";", idx));
            date = date.replace('/', '-');
            idx++;
            String tag = line.substring(idx, idx = line.indexOf(";", idx));
            idx++;
            String amount = line.substring(idx, idx = line.indexOf(";", idx));
            idx++;
            String desc = line.substring(idx);
            StringBuilder sb = new StringBuilder();
            sb.append("\"").append(date).append("\"").append(", ");
            sb.append("\"").append(tag).append("\"").append(", ");
            String exp = amount.startsWith("-") ? amount.substring(1) : "";
            String inc = amount.startsWith("-") ? "" : amount;
            sb.append("\"").append(exp).append("\"").append(", ");
            sb.append("\"").append(inc).append("\"").append(", ");
            sb.append("\"").append("CNY").append("\"").append(", ");
            amount = amount.startsWith("-") ? amount.substring(1) : amount;
            sb.append("\"").append(amount).append("\"").append(", ");
            sb.append("\"").append("CNY").append("\"").append(", ");
            sb.append("\"").append(desc).append("\"");

            fout.println(sb);

        }
        fin.close();
        fout.flush();
        fout.close();
    }

}
