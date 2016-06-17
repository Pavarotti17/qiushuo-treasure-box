/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package qiushuo.treasurebox.toshl.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author shuo.qius
 * @version $Id: CommonUtil.java, v 0.1 Feb 29, 2016 5:35:17 PM qiushuo Exp $
 */
public class CommonUtil {
    /**
     * @param line
     * @return
     */
    public static TreeSet<String> splitTags(String line) {
        TreeSet<String> set = new TreeSet<>();
        if (line == null || line.isEmpty()) {
            return set;
        }
        set.addAll(Arrays.asList(StringUtils.split(line, ',')).stream().map(s -> s.trim())
            .collect(Collectors.toList()));
        return set;
    }

    /**
     * @param date "Jun 09, 2013"
     * @return "2013-06-09"
     */
    public static String convertDateHumanFormat(String date) {
        date = date.replace(",", "");
        String[] ss = date.split(" ");
        String day = ss[1];
        String mon = ss[0];
        String year = ss[2];
        if ("Jan".equalsIgnoreCase(mon)) {
            mon = "01";
        } else if ("Feb".equalsIgnoreCase(mon)) {
            mon = "02";
        } else if ("Mar".equalsIgnoreCase(mon)) {
            mon = "03";
        } else if ("Apr".equalsIgnoreCase(mon)) {
            mon = "04";
        } else if ("May".equalsIgnoreCase(mon)) {
            mon = "05";
        } else if ("Jun".equalsIgnoreCase(mon)) {
            mon = "06";
        } else if ("Jul".equalsIgnoreCase(mon)) {
            mon = "07";
        } else if ("Aug".equalsIgnoreCase(mon)) {
            mon = "08";
        } else if ("Sep".equalsIgnoreCase(mon)) {
            mon = "09";
        } else if ("Oct".equalsIgnoreCase(mon)) {
            mon = "10";
        } else if ("Nov".equalsIgnoreCase(mon)) {
            mon = "11";
        } else if ("Dec".equalsIgnoreCase(mon)) {
            mon = "12";
        }
        if (day.length() == 1) {
            day = "0" + day;
        }
        return year + "-" + mon + "-" + day;
    }

    /**
     * @param date "1/31/16"
     * @return "2016-01-31"
     */
    public static String convertDate(String date) {
        date = date.trim();
        String[] ss = date.split("/");
        String mon = ss[0];
        String day = ss[1];
        String year = ss[2];

        year = "20" + year;
        if (day.length() == 1) {
            day = "0" + day;
        }
        if (mon.length() == 1) {
            mon = "0" + mon;
        }

        return year + "-" + mon + "-" + day;
    }

    /**
     * @param amt -123,456.00
     * @return 12345600
     */
    public static long convertAmount(String amt) {
        amt = amt.replace(",", "");
        amt = amt.trim();
        boolean neg = amt.startsWith("-");
        if (neg) {
            amt = amt.substring(1);
        }
        if (amt.trim().length() == 0) {
            return 0;
        } else if (amt.indexOf('.') >= 0) {
            int dot = amt.indexOf('.');
            String integer = amt.substring(0, dot);
            String fraction = amt.substring(dot + 1).trim();
            long l1 = Long.parseLong(integer);
            long l2 = 0;
            if (fraction.length() == 1) {
                l2 = Long.parseLong(fraction) * 10;
            } else if (fraction.length() == 2) {
                l2 = Long.parseLong(fraction);
            } else if (fraction.length() > 2) {
                l2 = Long.parseLong(fraction.substring(0, 2));
            }
            long a = l1 * 100 + l2;
            return neg ? -a : a;
        } else {
            long a = Long.parseLong(amt);
            return neg ? -a : a;
        }
    }

    /**
     * extract ${xxx} from string
     * 
     * @param str
     * @return
     */
    public static List<String> extractPlaceHolder(String str) {
        if (str == null) {
            return Collections.emptyList();
        }
        str = str.trim();
        if (str.length() == 0) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>(1);
        for (int offset = 0, idx = 0; (idx = str.indexOf("{", offset)) >= 0;) {
            int last = str.indexOf("}", idx + "{".length());
            String ph = str.substring(idx + "{".length(), last).trim();
            offset = last + 1;
            list.add(ph);
        }
        return list;
    }

    /**
     * @param str
     * @return
     */
    public static String removePlaceHolder(String str) {
        if (str == null) {
            return null;
        }
        str = str.replace("${", "{");

        StringBuilder sb = new StringBuilder();
        int offset = 0;
        for (int idx = 0; (idx = str.indexOf("{", offset)) >= 0;) {
            int last = str.indexOf("}", idx + "{".length());
            sb.append(str.substring(offset, idx)).append(' ');
            offset = last + 1;
        }
        sb.append(str.substring(offset, str.length()));
        return sb.toString();
    }
}
