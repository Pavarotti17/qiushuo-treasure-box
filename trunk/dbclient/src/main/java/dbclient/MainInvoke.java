/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package dbclient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author shuo.qius
 * @version $Id: MainInvoke.java, v 0.1 Jun 3, 2014 6:01:47 PM shuo.qius Exp $
 */
public class MainInvoke {
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static final String  BID         = "2014mmddxxxxxxxx0YEZZP3QCSCL2EXC4N233SCCS3OHB7QKxxxxxxxx00000000";
    private static final boolean NEW_UUID    = true;
    private static final int     CONCURRENCY = 10;
    private static final int     LOOP        = 50;
    private ExecutorService      threadpool  = Executors.newCachedThreadPool();
    private final String         url;
    private final String         user;
    private final String         password;

    /**
     * @param url
     * @param user
     * @param password
     */
    public MainInvoke(String url, String user, String password) {
        super();
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void run() throws Exception {
        for (int i = 0; i < CONCURRENCY; ++i) {
            threadpool.execute(new Runner());
        }
    }

    private static AtomicInteger seq   = new AtomicInteger(0);
    private AtomicLong           timeI = new AtomicLong(0);
    private AtomicLong           cnt   = new AtomicLong(0);
    private Object               lock  = new Object();
    private long                 maxI  = 0;

    private void updateMax(long i) {
        synchronized (lock) {
            if (maxI < i) {
                maxI = i;
            }
        }
    }

    private void printTime(long i) {
        long c = cnt.incrementAndGet();
        long ti = timeI.addAndGet(i);
        if (c % LOOP == 0) {
            String msg = String.valueOf(new Date());
            msg += "   |  " + String.format("%s", (ti * 1d / c));
            msg += "  | ";
            msg += String.format("   max: %s", maxI);
            System.out.println(msg);
            synchronized (lock) {
                maxI = 0;
                timeI.set(0);
                cnt.set(0);
            }
        }
    }

    private class Runner implements Runnable {
        private final String name = "t-" + seq.getAndIncrement();
        private Connection   conn;

        public Runner() throws SQLException {
            conn = createConn(url, user, password);
        }

        private void executeWrite(String sql, Object[] args) throws Exception {
            PreparedStatement ps = conn.prepareStatement(sql);
            try {
                setParams(ps, args);
                ps.executeUpdate();
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

        private void insertInvoke() throws Exception {
            String sql = "insert into bc_invoke_93 (user_id,invoke_id,gmt_create) values ('2088000000000930',?,now())";
            Object[] args = new Object[] { genID() + "0YEZZP3QCSCL2EXC4N233SCCS3OHB7QK1" };
            executeWrite(sql, args);
        }

        /** 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            try {
                for (int i = 0;; ++i) {
                    long time = System.currentTimeMillis();
                    this.insertInvoke();
                    long time2 = System.currentTimeMillis();
                    long insert = time2 - time;
                    updateMax(insert);
                    printTime(insert);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //this.executeWrite("delete from bc_control_order_92", new Object[] {});
    private static Connection createConn(String url, String user, String password)
                                                                                  throws SQLException {
        return DriverManager.getConnection(url, user, password);
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
        String url = "jdbc:mysql://mysqldb1.lab1.alipay.net:3306/bdcore_18?characterEncoding=gbk";
        String user = "bdcore";
        String password = "ali88";
        System.out.println("clean data: delete from bc_invoke_93 ");
        Connection conn = createConn(url, user, password);
        conn.createStatement().executeUpdate("delete from bc_invoke_93");
        conn.close();
        System.out.println("started");
        new MainInvoke(url, user, password).run();
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
