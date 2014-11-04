/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package dbclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * 
 * @author shuo.qius
 * @version $Id: Parser.java, v 0.1 2014年9月26日 下午4:00:58 qiushuo Exp $
 */
public class Parser {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) throws Throwable {
        BufferedReader fin = null;
        try {
            fin = new BufferedReader(new FileReader(new File("/Users/qiushuo/temp/pointcore.txt")));

            int i = 1;
            for (String line = null; (line = fin.readLine()) != null;) {
                if (line.trim().length() <= 0) {
                    continue;
                }
                int idx = line.indexOf(',');
                String type = line.substring(0, idx).trim();
                String level = line.substring(idx + 1).trim();
                String code = "AE0";
                if ("WARN".equals(level)) {
                    code += "3";
                } else {
                    code += "5";
                }
                if ("系统错误".equals(type)) {
                    code += "0";
                } else {
                    code += "1";
                }
                code += "0908";
                String c3 = String.format("%03d", i++);
                code += c3;
                System.out.println(code);
            }

        } finally {
            try {
                fin.close();
            } catch (Exception e) {
            }
        }
    }

}
