/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package dbclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author qiushuo
 * @version $Id: Solo2.java, v 0.1 2014年9月2日 下午8:10:40 qiushuo Exp $
 */
public class Solo2 {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) throws Throwable {
        BufferedReader fin = new BufferedReader(new FileReader(new File(
            "/Users/qiushuo/work/alipay/budgetcore_ldc2/dalgen/mysql-bc/sql/ddl.sql")));
        try {
            for (String line = null; (line = fin.readLine()) != null;) {
                int idx = line.indexOf("COMMENT");
                if (idx < 0) {
                    System.out.println(line);
                    continue;
                }
                int c1 = line.lastIndexOf(',');
                int c2 = line.lastIndexOf(';');
                int c = Math.max(c1, c2);
                String part1 = line.substring(0, idx);
                String part2 = "";
                if (c >= idx) {
                    part2 = line.substring(c);
                }
                System.out.println(part1 + part2);
            }
        } finally {
            try {
                fin.close();
            } catch (Exception e) {
            }
        }
    }

}
