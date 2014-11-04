/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package dbclient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author shuo.qius
 * @version $Id: InitBudgets.java, v 0.1 2014年9月25日 上午11:15:46 qiushuo Exp $
 */
public class InitBudgets {
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public InitBudgets(String url, String user, String password) {
        super();
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private static final String     BID_PREFIX = "2014092500046100xxxxxxxxxxxx";
    private static final String     BID_SUFFIX = "XX1764XX00000000";
    private static final AtomicLong ser        = new AtomicLong(0);

    private static String genBid() {
        // 2014092500046100xxxxxxxxxxxx00000000000000000000XX1764XX00000000
        // 2014092500046100xxxxxxxxxxxx00000000000000000001XX1764XX00000000
        // 2014092500046100xxxxxxxxxxxx00000000000000000002XX1764XX00000000
        // ....
        StringBuilder sb = new StringBuilder(64);
        sb.append(BID_PREFIX).append(String.format("%020d", ser.getAndIncrement()))
            .append(BID_SUFFIX);
        return sb.toString();
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://mypay1.lab5.alipay.net:3306/bdcore_12?characterEncoding=gbk";
        String user = "bdcore";
        String password = "ali88";
        System.out.println("clean data: delete from bc_control_order_64 ");
        Connection conn = createConn(url, user, password);
        conn.createStatement().executeUpdate("delete from bc_control_order_64");
        conn.close();
        System.out.println("started");
        new InitBudgets(url, user, password).run();
    }

    private static final int CONCURRENCY = 1;
    private ExecutorService  threadpool  = Executors.newCachedThreadPool();
    private final String     url;
    private final String     user;
    private final String     password;

    public void run() throws Exception {
        for (int i = 0; i < CONCURRENCY; ++i) {
            threadpool.execute(new Runner());
        }

    }

    private class Runner implements Runnable {
        private Connection conn;

        public Runner() throws SQLException {
            conn = createConn(url, user, password);
        }

        private void insertBudget() throws Exception {
            String sql = "insert into bc_budget_64 "
                         + "(bid,owner_id,current_amount,total_amount,budget_type,`status`,biz_code,sub_budget_size,MERGE_THRESHOLD,MERGE_INDEX,GMT_CREATE,GMT_MODIFY) "
                         + "values (?,'2088000000000640',31415,31415, 'SR',       'ON',    '046_perft',0,           0,              0,          now(),now())";
            Object[] args = new Object[] { genBid() };
            if (1 != executeWrite(sql, args)) {
                throw new IllegalArgumentException();
            }
        }

        private int executeWrite(String sql, Object[] args) throws Exception {
            PreparedStatement ps = conn.prepareStatement(sql);
            try {
                setParams(ps, args);
                return ps.executeUpdate();
            } finally {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }

        private void setParams(PreparedStatement ps, Object[] args) throws SQLException {
            for (int i = 0; i < args.length; ++i) {
                if (args[i] instanceof String) {
                    ps.setString(i + 1, (String) args[i]);
                } else if (args[i] instanceof Number) {
                    ps.setLong(i + 1, ((Number) args[i]).longValue());
                } else {
                    ps.setObject(i + 1, args[i]);
                }
            }
        }

        /** 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            try {
                for (int i = 0; i < Integer.MAX_VALUE; ++i) {
                    insertBudget();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //this.executeWrite("delete from bc_control_order_64", new Object[] {});
    private static Connection createConn(String url, String user, String password)
                                                                                  throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

}
