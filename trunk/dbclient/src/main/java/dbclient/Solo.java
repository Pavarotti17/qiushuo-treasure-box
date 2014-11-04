/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package dbclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * 
 * @author shuo.qius
 * @version $Id: Solo.java, v 0.1 Jun 4, 2014 5:35:25 PM shuo.qius Exp $
 */
public class Solo {

    public static void main(String[] args) throws Exception {
        StringBuilder t = new StringBuilder();
        BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(
            "/Users/qiushuo/Desktop/ddlchange.sql")));
        for (String line = null; (line = fin.readLine()) != null;) {
            t.append(line);
            t.append("\r\n");
        }
        fin.close();
        String or = t.toString();
        PrintWriter fout = new PrintWriter(new File("/Users/qiushuo/Desktop/ddlchange2.sql"));
        for (int i = 0; i < 100; ++i) {
            String str = or.replaceAll("xxxx", String.format("%02d", i));
            str = str.replaceAll("xxyxx", String.format("%02d", i / 5));
            fout.print(str);
            fout.print("\r\n\r\n");
        }
        fout.close();

    }
}
