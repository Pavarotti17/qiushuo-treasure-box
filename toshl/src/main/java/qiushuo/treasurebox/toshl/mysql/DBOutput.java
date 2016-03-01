/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package qiushuo.treasurebox.toshl.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

    //    +----------+--------------+------+-----+---------+-------+
    //    | Field    | Type         | Null | Key | Default | Extra |
    //    +----------+--------------+------+-----+---------+-------+
    //    | ID       | varchar(16)  | NO   | PRI | NULL    |       |
    //    | SRC      | varchar(128) | YES  |     | NULL    |       |
    //    | GMT      | varchar(11)  | NO   |     | NULL    |       |
    //    | AMOUNT   | bigint(18)   | NO   |     | NULL    |       |
    //    | TYPE     | varchar(128) | NO   |     | NULL    |       |
    //    | EVENT    | varchar(128) | YES  |     | NULL    |       |
    //    | SUB_TYPE | varchar(128) | YES  |     | NULL    |       |
    //    | INFO     | varchar(256) | YES  |     | NULL    |       |
    //    | EXT_INFO | varchar(128) | YES  |     | NULL    |       |
    //    +----------+--------------+------+-----+---------+-------+

    /**
     * @param list
     * @param hey
     * @throws Throwable
     */
    public void output(Collection<Bill> list, boolean hey) throws Throwable {
        cleanData(hey);
        insertData(list, hey);
    }

    private void insertData(Collection<Bill> list, boolean hey) throws Throwable {
        logger.info("output data, hey=" + hey + ", size=" + list.size());
        Connection conn = createConn(DB_URL, DB_USER, DB_PASSWORD);
        try {
            int row = 0;
            for (Bill b : list) {
                row += sqlWrite(
                    conn,
                    "INSERT INTO bill (ID,SRC,GMT,AMOUNT,AMOUNT_DESP,`TYPE`,`EVENT`,SUB_TYPE,INFO,EXT_INFO) VALUES (?,?,?,?,?,?,?,?,?,?)",
                    Arrays.asList(b.getBillId(), b.isHey() ? "hey" : "we", b.getGmt(),
                        b.getAmount(), String.valueOf(b.getAmount() * 0.01d), b.getType(),
                        b.getOnlyTag(), b.getOnlyExtraTag(), b.getDesc(), null));
            }
            if (row != list.size()) {
                logger.error("inserted_rows(" + row + ") != list_size(" + list.size()
                             + "), import inconsistently");
            } else {
                logger.info("import successful");
            }
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
            }
        }
    }

    private void cleanData(boolean hey) throws Throwable {
        logger.info("clean data, hey=" + hey);
        Connection conn = createConn(DB_URL, DB_USER, DB_PASSWORD);
        if (hey) {
            conn.createStatement().executeUpdate("delete from bill where src='hey'");
        } else {
            conn.createStatement().executeUpdate("delete from bill where src='we'");
        }
        conn.close();
    }

    private static int sqlWrite(Connection conn, String sql, List<Object> args) throws Throwable {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            if (args != null && args.size() > 0) {
                for (int i = 0; i < args.size(); ++i) {
                    ps.setObject(i + 1, args.get(i));
                }
            }
            return ps.executeUpdate();
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
            }
        }
    }

    private static Connection createConn(String url, String user, String password)
                                                                                  throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
