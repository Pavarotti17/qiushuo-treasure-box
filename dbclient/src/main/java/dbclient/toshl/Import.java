/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package dbclient.toshl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 
 * @author shuo.qius
 * @version $Id: Import.java, v 0.1 2014年11月4日 上午11:01:18 qiushuo Exp $
 */
public class Import {
    /*
     *
      ---- total:
      select * from (
        select tag,sum(amt) sum from (
          select b.bill_id bill_id , t.tag tag , b.gmt_create dt,-0.01*b.amount amt  
          from toshl_bill b join toshl_tag t on b.bill_id=t.bill_id where t.tag not like '1%' and b.amount<0 and 
          b.gmt_create>'2013-11-04' 
          group by b.bill_id
        ) ttt group by tag  with rollup 
      ) ttttt order by sum desc;
      
      ---- for each tag:
      select b.bill_id bill_id , t.tag tag , b.gmt_create dt,-0.01*b.amount amt , b.`desc` `desc`
      from toshl_bill b join toshl_tag t on b.bill_id=t.bill_id where t.tag not like '1%' and b.amount<0 
      and b.gmt_create>'2013-11-04' and t.tag='people'
      order by b.gmt_create desc
     * */

    /** every bill can has maximunly {@value #MAX_TAG} tags */
    private static final int     MAX_TAG        = 2;
    private static final String  DB_URL         = "jdbc:mysql://127.0.0.1:3306/toshl?characterEncoding=utf8";
    private static final String  DB_USER        = "root";
    private static final String  DB_PASSWORD    = "";
    private static final boolean BILL_TAG_TABLE = false;

    static class Bill {
        private static final Random rnd       = new Random();
        /** format e.g. <code>"20140904AKX9E1WP"</code> */
        private String              billId;
        /** negative number for expense, positive number for income */
        private long                amount;
        /** format e.g. <code>"2014-09-04"</code> */
        private String              gmtCreate;
        private String              desc;
        private Set<String>         tags      = new HashSet<String>(1, 1);
        private List<String>        extraTags = new ArrayList<String>(1);

        public Bill autoSet(String date, String tags, String exp, String inc, String currency,
                            String amt, String mainCurrency, String desc) {
            this.genBillId(date.replace("-", ""));
            amt = amt.replace(",", "");
            this.gmtCreate = date;
            this.setTags(tags);
            parseExtraTags(desc);
            boolean income = inc == null || inc.trim().length() == 0;
            if (amt == null || amt.trim().length() == 0) {
                this.amount = 0;
            } else if (amt.indexOf('.') >= 0) {
                int dot = amt.indexOf('.');
                String amtStr = amt.substring(0, dot) + amt.substring(dot + 1);
                this.amount = Long.parseLong(amtStr);
            } else {
                this.amount = 0;
            }
            if (income) {
                this.amount = this.amount * -1;
            }
            return this;
        }

        /**
         * Getter method for property <tt>tags</tt>.
         * 
         * @return property value of tags
         */
        public Set<String> getTags() {
            return tags;
        }

        public Bill setTags(String tags) {
            if (tags == null || tags.trim().length() == 0) {
                return this;
            }
            if (this.tags == null) {
                this.tags = new HashSet<String>(1, 1);
            }
            String[] tagList = tags.split(",");
            for (String tag : tagList) {
                this.tags.add(tag.trim());
            }
            checkTagsNumber();
            return this;
        }

        /**
         * Getter method for property <tt>billId</tt>.
         * 
         * @return property value of billId
         */
        public String getBillId() {
            return billId;
        }

        /**
         * <code>"20140929AKX9E1WP"</code>
         * 
         * @param date e.g. "20140929"
         */
        public Bill genBillId(String date) {
            if (date == null || date.length() != 8) {
                throw new IllegalArgumentException("date format must with length of 8: " + date);
            }
            StringBuilder sb = new StringBuilder(16);
            sb.append(date);
            sb.append('_');
            for (int i = 0; i < 7; ++i) {
                int n = rnd.nextInt(36);
                char c = '0';
                if (n < 10) {
                    c = (char) ('0' + n);
                } else {
                    c = (char) ('A' + n - 10);
                }
                sb.append(c);
            }
            this.billId = sb.toString();
            return this;
        }

        private void checkTagsNumber() {
            if (tags != null) {
                if (tags.size() > MAX_TAG) {
                    throw new IllegalArgumentException("too much tags for " + this);
                }
            }
        }

        private void parseExtraTags(String str) {
            this.extraTags = extractPlaceHolder(str);
            this.desc = removePlaceHolder(str);
        }

        public String getExtraTags0() {
            if (extraTags == null || extraTags.isEmpty()) {
                return null;
            }
            return extraTags.get(0);
        }

        public String getExtraTags1() {
            if (extraTags == null || extraTags.size() < 2) {
                return null;
            }
            return extraTags.get(1);
        }

        public String getExtraTags2() {
            if (extraTags == null || extraTags.size() < 3) {
                return null;
            }
            return extraTags.get(2);
        }

        /**
         * Getter method for property <tt>amount</tt>.
         * 
         * @return property value of amount
         */
        public long getAmount() {
            return amount;
        }

        /**
         * Setter method for property <tt>amount</tt>.
         * 
         * @param amount value to be assigned to property amount
         */
        public Bill setAmount(long amount) {
            this.amount = amount;
            return this;
        }

        /**
         * Getter method for property <tt>gmtCreate</tt>.
         * 
         * @return property value of gmtCreate
         */
        public String getGmtCreate() {
            return gmtCreate;
        }

        /**
         * Setter method for property <tt>gmtCreate</tt>.
         * 
         * @param gmtCreate value to be assigned to property gmtCreate
         */
        public Bill setGmtCreate(String gmtCreate) {
            this.gmtCreate = gmtCreate;
            return this;
        }

        /**
         * Getter method for property <tt>desc</tt>.
         * 
         * @return property value of desc
         */
        public String getDesc() {
            return desc;
        }

        /**
         * Setter method for property <tt>desc</tt>.
         * 
         * @param desc value to be assigned to property desc
         */
        public Bill setDesc(String desc) {
            parseExtraTags(desc);
            return this;
        }

        /** 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "Bill [billId=" + billId + ", amount=" + amount + ", gmtCreate=" + gmtCreate
                   + ", desc=" + desc + ", tags=" + tags + "]";
        }

    }

    private final String url;
    private final String user;
    private final String password;

    public Import(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void run(List<Bill> bills) throws Throwable {
        cleanData();

        System.out.println("start imported");
        Connection conn = null;
        try {
            conn = createConn(url, user, password);

            for (Bill bill : bills) {
                System.out.println(bill);
                sqlWrite(
                    conn,
                    "insert into bill (bill_id,amount,`desc`,gmt_create,`type`,`event`,`tag3`,`tag4`,`tag5`) values(?,?,?,?,?,?,?,?,?)",
                    new Object[] { bill.getBillId(), bill.getAmount(), bill.getDesc(),
                            bill.getGmtCreate(), getType(bill), getEvent(bill),
                            bill.getExtraTags0(), bill.getExtraTags1(), bill.getExtraTags2() });
                if (BILL_TAG_TABLE) {
                    sqlWrite(
                        conn,
                        "insert into toshl_bill (bill_id,amount,`desc`,gmt_create) values(?,?,?,?)",
                        new Object[] { bill.getBillId(), bill.getAmount(), bill.getDesc(),
                                bill.getGmtCreate() });
                    for (String tag : bill.getTags()) {
                        sqlWrite(conn, "insert into toshl_tag (bill_id,tag) values(?,?)",
                            new Object[] { bill.getBillId(), tag });
                    }
                }
            }
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
            }
        }
    }

    private String getType(Bill bill) {
        Set<String> tags = bill.getTags();
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        for (String t : tags) {
            if (!"1".equals(t.substring(0, 1))) {
                return t;
            }
        }
        return null;
    }

    private String getEvent(Bill bill) {
        Set<String> tags = bill.getTags();
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        for (String t : tags) {
            if ("1".equals(t.substring(0, 1))) {
                return t;
            }
        }
        return null;
    }

    private void cleanData() throws Throwable {
        System.out.println("clean data ");
        Connection conn = createConn(url, user, password);
        if (BILL_TAG_TABLE) {
            conn.createStatement().executeUpdate("truncate table toshl_bill");
            conn.createStatement().executeUpdate("truncate table toshl_tag");
        }
        conn.createStatement().executeUpdate("truncate table bill");
        conn.close();
    }

    private int sqlWrite(Connection conn, String sql, Object[] args) throws Throwable {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; ++i) {
                    ps.setObject(i + 1, args[i]);
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

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static List<Bill> parseBills(String text) {
        int i = 0;
        for (; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (c == NL) {
                break;
            }
        }
        ++i;
        List<Bill> list = new ArrayList<Bill>();
        for (; i < text.length();) {
            if (!hasNext(text, i)) {
                break;
            }
            StringBuilder sb = new StringBuilder();
            i = readQuote(text, i, sb);
            String date = sb.toString();

            sb = new StringBuilder();
            i = readQuote(text, i, sb);
            String tags = sb.toString();

            sb = new StringBuilder();
            i = readQuote(text, i, sb);
            String expAmt = sb.toString();

            sb = new StringBuilder();
            i = readQuote(text, i, sb);
            String incAmt = sb.toString();

            sb = new StringBuilder();
            i = readQuote(text, i, sb);
            String currency = sb.toString();

            sb = new StringBuilder();
            i = readQuote(text, i, sb);
            String currencyAmt = sb.toString();

            sb = new StringBuilder();
            i = readQuote(text, i, sb);
            String mainCurrency = sb.toString();

            sb = new StringBuilder();
            i = readQuote(text, i, sb);
            String desc = sb.toString();
            desc = escapeNL(desc);

            Bill bill = new Bill().autoSet(date, tags, expAmt, incAmt, currency, currencyAmt,
                mainCurrency, desc);
            System.out.println(bill);

            list.add(bill);
        }

        return list;
    }

    private static String removePlaceHolder(String str) {
        if (str == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        int offset = 0;
        for (int idx = 0; (idx = str.indexOf("${", offset)) >= 0;) {
            int last = str.indexOf("}", idx + "${".length());
            sb.append(str.substring(offset, idx)).append(' ');
            offset = last + 1;
        }
        sb.append(str.substring(offset, str.length()));
        return sb.toString();
    }

    /**
     * extract ${xxx} from string
     * 
     * @param str
     * @return
     */
    private static List<String> extractPlaceHolder(String str) {
        if (str == null) {
            return Collections.emptyList();
        }
        str = str.trim();
        if (str.length() == 0) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<String>(1);
        for (int offset = 0, idx = 0; (idx = str.indexOf("${", offset)) >= 0;) {
            int last = str.indexOf("}", idx + "${".length());
            String ph = str.substring(idx + "${".length(), last).trim();
            offset = last + 1;
            list.add(ph);
        }
        return list;
    }

    private static String escapeNL(String str) {
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            switch (c) {
                case '\r':
                    sb.append(' ');
                    break;
                case '\n':
                    sb.append(';');
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    private static final char NL = '\n';

    private static boolean hasNext(String text, int i) {
        for (; i < text.length(); ++i) {
            if ('"' == text.charAt(i)) {
                return true;
            }
        }
        return false;
    }

    private static int readQuote(String text, int i, StringBuilder sb) {
        for (; i < text.length(); ++i) {
            char c = text.charAt(i);
            if ('"' == c) {
                ++i;
                break;
            }
        }

        for (; i < text.length(); ++i) {
            char c = text.charAt(i);
            if ('"' == c) {
                break;
            }
            sb.append(c);
        }

        return ++i;
    }

    private static Connection createConn(String url, String user, String password)
                                                                                  throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) throws Throwable {
        System.out
            .println("enter toshl report cvs path:( /Users/qiushuo/Downloads/toshl_export.csv ) ");
        StringBuilder text = new StringBuilder();
        FileInputStream fin = null;
        try {
            BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
            String path = sin.readLine().trim();
            fin = new FileInputStream(new File(path));
            BufferedReader f = new BufferedReader(new InputStreamReader(fin));
            for (String line = null; (line = f.readLine()) != null;) {
                text.append(line);
                text.append(NL);
            }
            f.close();
        } finally {
            try {
                fin.close();
            } catch (Exception e) {
            }
        }
        final List<Bill> bills = parseBills(text.toString());
        String url = DB_URL;
        String user = DB_USER;
        String password = DB_PASSWORD;

        System.out.println("started");
        new Import(url, user, password).run(bills);
        System.out.println("finished");
    }
}
