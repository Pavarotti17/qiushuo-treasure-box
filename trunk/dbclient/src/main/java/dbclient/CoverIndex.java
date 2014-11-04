/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package dbclient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author shuo.qius
 * @version $Id: CoverIndex.java, v 0.1 Jun 5, 2014 7:13:18 PM shuo.qius Exp $
 */
public class CoverIndex {
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static final String  URL                   = "jdbc:mysql://mysqldb1.lab1.alipay.net:3306/bdcore_19?characterEncoding=gbk";
    private static final String  BID                   = "20140606000461000LSPPX0MU1H1DLP9WEB3WPELPWWMW3B8XX1797XX10000000";
    private static final boolean NEW_UUID              = true;
    private static final long    STATISTIC_INTERVAL    = 2000;
    private static final long    SAME_CONCURRENCY_TIME = 5;
    private static final int     MAX_CONCURRENCY       = 200;
    private ExecutorService      threadpool            = Executors.newCachedThreadPool();
    private static AtomicInteger concurrency           = new AtomicInteger(7);
    private final String         url;
    private final String         user;
    private final String         password;

    /**
     * @param url
     * @param user
     * @param password
     */
    public CoverIndex(String url, String user, String password) {
        super();
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private List<String> sbids = new ArrayList<String>(100);

    public void run() throws Exception {
        Connection conn = createConn(url, user, password);
        sbids = executeQuery(String.class, conn, "select SBID from  bc_sub_budget_97 where bid=?",
            new Object[] { BID }, "SBID");
        conn.close();
        for (int i = 0; i < concurrency.get(); ++i) {
            threadpool.execute(new Runner());
        }
        for (; concurrency.get() < MAX_CONCURRENCY; concurrency.incrementAndGet()) {
            Thread.sleep(SAME_CONCURRENCY_TIME * STATISTIC_INTERVAL);
            threadpool.execute(new Runner());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    long tps = 0;
                    try {
                        Thread.sleep(STATISTIC_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    long time = System.currentTimeMillis();
                    synchronized (lock) {
                        tps = (long) (cnt * 1000d / (time - lastTime));
                    }
                    lastTime = time;
                    cnt = 0;
                    System.out.println("cur: " + concurrency.get() + "\ttps: " + tps);
                }
            }
        }).start();
    }

    private Object lock     = new Object();
    private long   lastTime = System.currentTimeMillis();
    private long   cnt      = 0;

    private void tps() {
        synchronized (lock) {
            ++cnt;
        }
    }

    @SuppressWarnings("unused")
    private String executeQueryForString(Connection conn, String sql, Object[] args, String col)
                                                                                                throws Exception {
        List<Object> list = executeQuery(Object.class, conn, sql, args, col);
        return (String) list.get(0);
    }

    private <T> List<T> executeQuery(Class<T> clz, Connection conn, String sql, Object[] args,
                                     String col) throws Exception {
        List<T> list = new ArrayList<T>();
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            setParams(ps, args);
            ResultSet rs = ps.executeQuery();
            for (; rs.next();) {
                Object strVal = rs.getObject(col);
                list.add((T) strVal);
            }
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
            }
        }
        return list;
    }

    private int executeWrite(Connection conn, String sql, Object[] args) throws Exception {
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
        if (args == null || args.length == 0)
            return;
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

    private class Runner implements Runnable {
        //private final String   SQL   = "select CURRENT_AMOUNT from bc_budget_route_99 where sbid like ?";
        // private final Object[] PARAM = new Object[] { "20140606000461000LZ8MR3Q2C1JM3SS62037GJTQND9MYGW%" };
        private Connection conn;
        private Random     rnd = new Random();

        public Runner() throws SQLException {
            conn = createConn(url, user, password);
        }

        private void listRoute() throws Exception {
            executeQuery(Long.class, conn,
                "select CURRENT_AMOUNT from bc_sub_budget_97 where bid = ? order by sbid",
                new Object[] { BID }, "CURRENT_AMOUNT");
        }

        private void insertControlOrder(){
            String sql="";
        }
        
//        private void updateRoute() throws Exception {
//            int idx = rnd.nextInt(sbids.size());
//            String sbid = sbids.get(idx);
//            int row = executeWrite(
//                conn,
//                "update bc_budget_route_99 set CURRENT_AMOUNT=CURRENT_AMOUNT+1, GMT_MODIFY=now(), version=version+1 where sbid=?",
//                new Object[] { sbid });
//            if (row != 1) {
//                throw new IllegalArgumentException("update affected row is " + row);
//            }
//        }

        /** 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            System.out.println("thread added");
            try {
                for (int i = 0;; ++i) {
                    this.listRoute();
                    this.updateRoute();
                    tps();
                    //                    updateMax(insert, lockCO, update, commitTime);
                    //                    printTime(insert, lockCO, update, commitTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String genID() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("2014mmddxxxxxxxx");
        if (NEW_UUID) {
            sb.append(UUIDCompressor.genIncrementUUID());
        } else {
            sb.append(UUID.randomUUID().toString().replaceAll("-", ""));
        }
        sb.append("xxxxxxxx00000000");
        return sb.toString();
    }

    // -hmysqldb1.lab1.alipay.net -ubdcore -pali88 -Dbdcore_00
    public static void main(String[] args) throws Exception {
        String url = URL;
        String user = "bdcore";
        String password = "ali88";
        new CoverIndex(url, user, password).run();
    }

    //this.executeWrite("delete from bc_control_order_92", new Object[] {});
    private static Connection createConn(String url, String user, String password)
                                                                                  throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static class UUIDCompressor {
        private static final int    POW_LEN       = 12;
        private static final long[] POW_36_12     = new long[POW_LEN];
        static {
            int i = -1;
            POW_36_12[++i] = 36L;
            POW_36_12[++i] = 1296L;
            POW_36_12[++i] = 46656L;
            POW_36_12[++i] = 1679616L;
            POW_36_12[++i] = 60466176L;
            POW_36_12[++i] = 2176782336L;
            POW_36_12[++i] = 78364164096L;
            POW_36_12[++i] = 2821109907456L;
            POW_36_12[++i] = 101559956668416L;
            POW_36_12[++i] = 3656158440062976L;
            POW_36_12[++i] = 131621703842267136L;
            POW_36_12[++i] = 4738381338321616896L;

            for (i = 1; i < POW_36_12.length; ++i) {
                assert POW_36_12[i] == 36 * POW_36_12[i - 1];
            }
        }

        public static final long    MAX_POW_36_12 = POW_36_12[POW_36_12.length - 1];
        public static final int     UUID_LEN      = 32;

        public static String genIncrementUUID() {
            return compressUUID(System.currentTimeMillis(), UUID.randomUUID());
        }

        public static String compressShortLong(long val, int strSize) {
            if (strSize > POW_36_12.length) {
                throw new IllegalArgumentException("strSize " + strSize + " bigger than max size: "
                                                   + POW_36_12.length);
            }
            if (val < 0 || val >= MAX_POW_36_12) {
                throw new IllegalArgumentException(
                    "value "
                            + val
                            + " is negative or too big to be contained in 36-base string with size of "
                            + strSize);
            }
            char[] rst = new char[POW_36_12.length];
            int idx = 0;
            for (int i = 0; val > 0 && i < POW_36_12.length; ++i) {
                int mod = (int) (val % 36);
                rst[idx++] = convert36(mod);
                long div = (long) (val / 36);
                val = div;
            }
            if (idx > strSize) {
                throw new IllegalArgumentException("value too big, string overflowed");
            }
            StringBuilder sb = new StringBuilder(strSize);
            for (int i = 0; i < strSize - idx; ++i) {
                sb.append('0');
            }
            for (int i = idx; i > 0; --i) {
                sb.append(rst[i - 1]);
            }
            return sb.toString();
        }

        /**
         * @return 13 char
         * */
        static String compressLong(long val) {
            boolean negative = false;
            StringBuilder sb = new StringBuilder(13);
            if (val < 0) {
                val = -1L * (val + 1);
                negative = true;
            }
            assert val >= 0;
            if (val < MAX_POW_36_12) {
                if (negative) {
                    sb.append('2');
                } else {
                    sb.append('0');
                }
            } else {
                if (negative) {
                    sb.append('3');
                } else {
                    sb.append('1');
                }
                val -= MAX_POW_36_12;
            }
            sb.append(compressShortLong(val, 12));
            return sb.toString();
        }

        static String compressUUID(long timestamp, UUID uuid) {
            long time = getLocalTime(timestamp);
            StringBuilder sb = new StringBuilder(UUID_LEN);
            sb.append(compressShortLong(time, 6));
            long high = uuid.getMostSignificantBits();
            long low = uuid.getLeastSignificantBits();
            sb.append(compressLong(high));
            sb.append(compressLong(low));
            return sb.toString();
        }

        private static char convert36(int val) {
            if (val >= 0 && val < 10) {
                return (char) ('0' + val);
            }
            return (char) ('A' + val - 10);
        }

        /**
         * deal with Greenwich time-diff
         * 
         * @param time
         * @return
         */
        private static long getLocalTime(long time) {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTimeInMillis(time);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            int mm = calendar.get(Calendar.MILLISECOND);
            long t = mm + 1000 * second + 1000 * 60 * minute + 1000 * 3600 * hour;
            return t;
        }

    }

}
