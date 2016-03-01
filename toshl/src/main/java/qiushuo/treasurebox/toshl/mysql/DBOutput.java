/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package qiushuo.treasurebox.toshl.mysql;

import java.util.Collection;

import org.apache.log4j.Logger;

import qiushuo.treasurebox.toshl.Bill;

/**
 * 
 * @author shuo.qius
 * @version $Id: DBOutput.java, v 0.1 Feb 29, 2016 9:47:50 PM qiushuo Exp $
 */
public class DBOutput {
    private static final Logger logger      = Logger.getLogger(DBOutput.class);
    /** every bill can has maximunly {@value #MAX_TAG} tags */
    public static final String  DB_URL      = "jdbc:mysql://127.0.0.1:3306/toshl?characterEncoding=utf8";
    public static final String  DB_USER     = "root";
    public static final String  DB_PASSWORD = "";
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @param list
     * @param hey
     * @throws Throwable
     */
    public void output(Collection<Bill> list, boolean hey) throws Throwable{
        //QS_TODO
    }

}
