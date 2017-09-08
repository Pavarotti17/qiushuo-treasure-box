/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package qiushuo.treasurebox.toshl.transfer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import qiushuo.treasurebox.toshl.csv.IOReader;
import qiushuo.treasurebox.toshl.mysql.DBOutput;

/**
 * @author shuo.qius
 * @version $Id: ToshlTransfer.java, v 0.1 Jun 3, 2016 6:00:17 PM qiushuo Exp $
 */
public class ToshlTransfer {
    private static final Logger logger = Logger.getLogger(ToshlTransfer.class);
    private static final SimpleDateFormat mysql = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat csv = new SimpleDateFormat("M/d/YY");

    public static void main(String[] args) throws Throwable {
        final String path = "/Users/shuo.qius/Downloads/toshl/toshl_export_hey.csv";
        System.out.println(IOReader.readCsvFile(path, false, 1).size());
        IOReader.readCsvFile(path, false, 1).stream().map(line -> {
            List<String> line2 = new ArrayList<>(line.size());
            line2.add(line.get(0));
            line2.add("baby");
            line2.add(extractCat(line.get(3)));
            line2.add(extractTag(line.get(3)));
            line2.add(line.get(4));
            line2.add(line.get(5));
            line2.add(line.get(6));
            line2.add(line.get(7));
            line2.add(line.get(8));
            line2.add(line.get(9));
            return line2;
        }).filter(line -> line.get(2).contains("baby") || line.get(3).contains("baby")).map(getEscaper()).forEach(
                linePrinter());
    }

    public static void main1(String[] args) throws Throwable {
        List<List<String>> legacy = getLegacy();
        legacy.stream().map(getEscaper()).forEach(linePrinter());
    }

    public static void main0(String[] args) throws Throwable {
        System.out.println(IOReader.readCsvFile("/Users/shuo.qius/Downloads/toshl_export.csv", false, 1).size());
        IOReader.readCsvFile("/Users/shuo.qius/Downloads/toshl_export.csv", false, 1).stream().map(line -> {
            List<String> line2 = new ArrayList<>(line.size());
            line2.add(line.get(0));
            line2.add("we");
            line2.add(extractCat(line.get(3)));
            line2.add(extractTag(line.get(3)));
            line2.add(line.get(4));
            line2.add(line.get(5));
            line2.add(line.get(6));
            line2.add(line.get(7));
            line2.add(line.get(8));
            line2.add(line.get(9));
            return line2;
        }).map(getEscaper()).forEach(linePrinter());
    }

    private static Consumer<List<String>> linePrinter() {
        return line -> {
            StringBuilder sb = new StringBuilder();
            boolean f = true;
            for (String col : line) {
                if (f) {
                    f = false;
                } else {
                    sb.append(',');
                }
                sb.append(col);
            }
            System.out.println(sb);
        };
    }

    private static Function<List<String>, List<String>> getEscaper() {
        return line -> line.stream().map(s -> {
            StringBuilder sb = new StringBuilder();
            boolean escaped = false;
            for (int i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);
                switch (c) {
                case '"':
                    sb.append('"');
                    escaped = true;
                    sb.append(c);
                    break;
                case '\t':
                case '\n':
                case '\r':
                    c = ' ';
                case ',':
                    escaped = true;
                default:
                    sb.append(c);
                }
            }
            if (escaped) {
                return "\"" + sb + "\"";
            }
            return sb.toString();
        }).collect(Collectors.toList());
    }

    private static String extractTag(String line) {
        line = line.replace('，', ',');
        String[] ss = StringUtils.split(line, ',');
        if (ss == null || ss.length < 1) {
            throw new IllegalArgumentException(line);
        }
        Set<String> tags = new HashSet<>();
        for (String s : ss) {
            s = s.trim();
            if (s.charAt(0) <= '9' && s.charAt(0) >= '0' || "baby".equalsIgnoreCase(s)) {
                tags.add(s);
            }
        }
        return String.join(", ", tags);
    }

    private static String extractCat(String line) {
        line = line.replace('，', ',');
        String[] ss = StringUtils.split(line, ',');
        if (ss == null || ss.length < 1) {
            throw new IllegalArgumentException(line);
        }
        String cat = null;
        for (String s : ss) {
            s = s.trim();
            if (s.charAt(0) <= '9' && s.charAt(0) >= '0' || "baby".equalsIgnoreCase(s)) {
                continue;
            }
            if (cat == null) {
                cat = s;
            } else {
                throw new IllegalArgumentException(line);
            }
        }
        if (cat == null) {
            cat = "default";
        }
        return cat.trim();
    }

    private static List<List<String>> getLegacy() throws Throwable {
        List<List<String>> list = new ArrayList<>();
        {
            Connection conn = DBOutput.createConn();
            ResultSet rs = conn.prepareStatement(
                    " select gmt,amount,type,event,sub_type,info from bill where src='hey' and gmt < '2015-04-28' order by gmt ")
                               .executeQuery();
            for (; rs.next();) {
                String gmt = csv.format(mysql.parse(rs.getString(1)));
                String account = "hey";
                String cat = rs.getString(3);
                String tags = rs.getString(4);
                tags = tags == null ? "" : tags;
                String expAmt = "0";
                String inAmt = "0";
                String amtStr = "0";
                String cur = "CNY";
                {
                    boolean exp = true;
                    long amt = rs.getLong(2);
                    if (amt < 0) {
                        amt *= -1;
                    } else {
                        exp = false;
                    }
                    final String old = String.valueOf(amt);
                    String a = old;
                    a = a.substring(0, a.length() - 2);
                    List<Character> temp = new ArrayList<>();
                    for (int i = 1; i <= a.length(); ++i) {
                        temp.add(a.charAt(a.length() - i));
                        if (i % 3 == 0) {
                            temp.add(',');
                        }
                    }
                    a = null;
                    String a2 = "";
                    for (int i = temp.size() - 1; i >= 0; --i) {
                        a2 += temp.get(i);
                    }
                    a2 = a2 + ".00";
                    if (a2.charAt(0) == ',') {
                        a2 = a2.substring(1);
                    }
                    if (exp) {
                        expAmt = a2;
                    } else {
                        inAmt = a2;
                    }
                    amtStr = a2;
                }
                String desc = rs.getString(6);
                if (rs.getString(5) != null) {
                    desc += " {" + rs.getString(5) + "}";
                }

                List<String> row = new ArrayList<>(10);
                row.add(gmt);
                row.add(account);
                row.add(cat);
                row.add(tags);
                row.add(expAmt);
                row.add(inAmt);
                row.add(cur);
                row.add(amtStr);
                row.add(cur);
                row.add(desc);
                list.add(row);
            }
            conn.close();
        }

        return list;
    }

}
