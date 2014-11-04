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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author shuo.qius
 * @version $Id: Main.java, v 0.1 Jun 3, 2014 3:19:50 PM shuo.qius Exp $
 */
public class Main {
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static final boolean NEW_UUID    = true;
    private static final int     CONCURRENCY = 10;
    private ExecutorService      threadpool  = Executors.newCachedThreadPool();
    private final String         url;
    private final String         user;
    private final String         password;

    /**
     * @param url
     * @param user
     * @param password
     */
    public Main(String url, String user, String password) {
        super();
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void run() throws Exception {
        String[] bids = new String[] {
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RFXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RGXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RHXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RIXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RJXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RKXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RLXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RMXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RNXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15ROXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RPXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RQXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RRXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RSXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RTXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RUXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RVXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RWXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RXXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RYXX1764XX00000000",
                "20140922000461000VT3G6067TFWTXI9UYJ3MLPJPYDI15RZXX1764XX00000000" };
        for (int i = 0; i < CONCURRENCY; ++i) {
            threadpool.execute(new Runner(bids[i % bids.length]));
        }

        long lastT = this.time.get();
        long lastC = this.cnt.get();
        for (long time = System.currentTimeMillis();; time = System.currentTimeMillis()) {
            Thread.sleep(5000);
            long c = this.cnt.get();
            long t = this.time.get();
            long interval = System.currentTimeMillis() - time;
            long deltaC = c - lastC;
            long deltaT = t - lastT;
            lastT = t;
            lastC = c;
            double tps = deltaC * 1000d / interval;
            double resp = deltaC <= 0 ? 0d : deltaT * 1d / deltaC;
            System.out.println("tps: " + tps + ", resp=" + resp);
        }

    }

    private AtomicLong time = new AtomicLong(0);
    private AtomicLong cnt  = new AtomicLong(0);

    private void printTime(long elapse) {
        long count = cnt.incrementAndGet();
        long ti = time.addAndGet(elapse);
    }

    private class Runner implements Runnable {
        private Connection   conn;
        private final String BID;

        public Runner(String bid) throws SQLException {
            this.BID = bid;
            conn = createConn(url, user, password);
        }

        private void insertInvoke() throws Exception {
            String sql = "insert into bc_invoke_64 (invoke_id,user_id,gmt_create) values (?,?,now())";
            Object[] args = new Object[] { genID() + genID(), "2088000000000640" };
            if (1 != executeWrite(sql, args)) {
                throw new IllegalArgumentException();
            }
        }

        private String insertControlOrder(String bizUniqueId) throws Exception {
            String coId = genID();
            String sql = "insert into bc_control_order_64 "
                         + "(co_id, tx_id, action_id, out_biz_no, bid, user_id,"
                         + "amount,actual_amount,opt_type,TX_TYPE,status,biz_code,sharding_scope,gmt_trans_dt,gmt_create,gmt_modify) values"
                         + " (?,  ?,  ?,   ?,  ?,  ?,"
                         + "    1,     1,            'S',   'NT',  'P',   '099_qsPT',    '01',now(),now(),now())";
            Object[] args = new Object[] { coId, genID(), "1", bizUniqueId, BID, "2088000000000640" };
            if (1 != executeWrite(sql, args)) {
                throw new IllegalArgumentException();
            }
            return coId;
        }

        private String lockCO(String bizUniqueId) throws Exception {
            String sql = "select co_id, tx_id, action_id, out_biz_no, bid, user_id,"
                         + "amount,actual_amount,opt_type,status,biz_code,sharding_scope,gmt_trans_dt,gmt_create,gmt_modify "
                         + " from bc_control_order_64 where out_biz_no=? and bid=? for update";
            Object[] args = new Object[] { bizUniqueId, BID };
            return executeQuery(sql, args, "co_id");
        }

        private void updateCO(String coId) throws Exception {
            String sql = "update bc_control_order_64 set status='N' where co_id=? and status='P'";
            Object[] args = new Object[] { coId };
            if (1 != executeWrite(sql, args)) {
                throw new IllegalArgumentException();
            }
        }

        private void updateBudget(String bid) throws Exception {
            String sql = "update bc_budget_64 set current_amount=current_amount-1 where bid=? and current_amount>=1";
            Object[] args = new Object[] { bid };
            if (1 != executeWrite(sql, args)) {
                throw new IllegalArgumentException();
            }
        }

        private void commitCO(String bizUniqueId) throws Exception {
            String sql = "update bc_control_order_64 set status='C' where out_biz_no=? and bid=? and (status='N' or status='P')";
            Object[] args = new Object[] { bizUniqueId, BID };
            if (1 != executeWrite(sql, args)) {
                throw new IllegalArgumentException();
            }
        }

        private void startTrans() throws Exception {
            conn.setAutoCommit(false);
        }

        private void commit() throws Exception {
            conn.commit();
            conn.setAutoCommit(true);
        }

        private String executeQuery(String sql, Object[] args, String col) throws Exception {
            String strVal = null;
            PreparedStatement ps = conn.prepareStatement(sql);
            try {
                setParams(ps, args);
                ResultSet rs = ps.executeQuery();
                for (; rs.next();) {
                    strVal = rs.getString(col);
                }
            } finally {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
            return strVal;
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
                for (int i = 0;; ++i) {
                    String bizUniqueId = UUIDCompressor.genIncrementUUID();
                    long time = System.currentTimeMillis();

                    insertInvoke();

                    this.startTrans();
                    String coId = this.insertControlOrder(bizUniqueId);
                    this.updateBudget(BID);
                    this.updateCO(coId);
                    this.commit();

                    this.commitCO(bizUniqueId);

                    long elapse = System.currentTimeMillis() - time;

                    printTime(elapse);
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
        String url = "jdbc:mysql://mypay1.lab5.alipay.net:3306/bdcore_12?characterEncoding=gbk";
        String user = "bdcore";
        String password = "ali88";
        System.out.println("clean data: delete from bc_control_order_64 ");
        Connection conn = createConn(url, user, password);
        conn.createStatement().executeUpdate("delete from bc_control_order_64");
        conn.close();
        System.out.println("started");
        new Main(url, user, password).run();
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
