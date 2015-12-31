import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2015 All Rights Reserved.
 */

/**
 * 
 * @author shuo.qius
 * @version $Id: Temp.java, v 0.1 2015年12月29日 下午4:50:06 qiushuo Exp $
 */
public class Temp {
    private static String[] temps = new String[] { "20151115000730015J0100000ROO",
            "20151115000730015J01000015NV", "20151115000730015J0100000VPL",
            "20151115000730015J0100000D0M", "20151115000730015J0100000MFW",
            "20151116000730016D1100002LFL", "20151116000730016D11000020TV",
            "20151116000730016D1100002LFK", "20151116000730016D1100002J7N",
            "20151116000730016D1100002LFJ", "20151115000730015J0100000MFX",
            "20151115000730015J0100000MFY", "20151115000730015J0100000TPT",
            "20151115000730015J0100000ROP", "20151115000730015J0100000D0N",
            "20151116000730016D1100002O7H", "20151116000730016D1100002GM7",
            "20151116000730016D1100002DSP", "20151116000730016D11000023R7",
            "20151116000730016D1100002QMH" };

    public static void main(String[] args) {
        Set<String> set = new HashSet<String>();
        for (String t : temps) {
            set.add(t);
        }
        for (int i = 0; i < temps.length; ++i) {
            for (int j = 0; j < temps.length; ++j) {
                if (i == j) {
                    continue;
                }
                String t = temps[i] + "|" + temps[j];
                set.add(t);
            }
        }
        List<String> list = new ArrayList<String>();
        Random rnd = new Random();
        for (String t : set) {
            list.add(t);
            if (rnd.nextInt(3) == 0) {
                list.add("|");
            }
        }
        
        for(String t:list){
            System.out.println(t);
        }

    }

    /**
     * @param args
     */
    public static void main0(String[] args) throws Throwable {
        File src = new File("/Users/qiushuo/temp/faultinjectuserIdvoucher/rz04.txt");
        BufferedReader fin = new BufferedReader(new FileReader(src));
        File tar1 = new File(src.getParentFile(), src.getName() + "_publish");
        File tar2 = new File(src.getParentFile(), src.getName() + "_pay");
        File tar3 = new File(src.getParentFile(), src.getName() + "_consult");
        PrintWriter out1 = new PrintWriter(tar1);
        PrintWriter out2 = new PrintWriter(tar2);
        PrintWriter out3 = new PrintWriter(tar3);

        int i = 0;
        for (String line = null; (line = fin.readLine()) != null;) {
            line = line.trim();
            if (line.length() != 16) {
                System.err.println(line);
                return;
            }
            int sw = i++ % 3;
            if (sw == 0) {
                out1.println(line);
            } else if (sw == 1) {
                out2.println(line);
            } else if (sw == 2) {
                out3.println(line);
            }

        }
        out1.flush();
        out1.close();
        out2.flush();
        out2.close();
        out3.flush();
        out3.close();

    }
    //    /**
    //     * @param args
    //     */
    //    public static void main(String[] args) throws Throwable {
    //        BufferedReader fin = new BufferedReader(new FileReader(new File(
    //            "/Users/qiushuo/all_zone_tid_result_shadowUID6-10w_trim.csv")));
    //        Set<String> rz04 = new HashSet<String>();
    //        Set<String> rz05 = new HashSet<String>();
    //        Set<String> rz11 = new HashSet<String>();
    //        Set<String> rz12 = new HashSet<String>();
    //        Set<String> rz13 = new HashSet<String>();
    //        Set<String> rz15 = new HashSet<String>();
    //        for (String line = null; (line = fin.readLine()) != null;) {
    //            line = line.trim();
    //            if (line.length() != 16) {
    //                System.err.println(line);
    //                return;
    //            }
    //            String uid = line.substring(13, 15);
    //            uid = uid.toUpperCase();
    //            char uid1 = uid.charAt(0);
    //            char uid2 = uid.charAt(1);
    //            int u = uid1 - '0';
    //            u *= 10;
    //            u += uid2 - 'A';
    //            if (u < 20) {
    //                rz05.add(line);
    //                //        System.out.println("rz05: "+uid+", "+u+", "+line);
    //            } else if (u >= 20 && u <= 39) {
    //                rz11.add(line);
    //                //          System.out.println("rz11: "+uid+", "+u+", "+line);
    //            } else if (u >= 40 && u <= 49) {
    //                rz12.add(line);
    //                //            System.out.println("rz12: "+uid+", "+u+", "+line);
    //            } else if (u >= 60 && u <= 79) {
    //                rz13.add(line);
    //                //              System.out.println("rz13: "+uid+", "+u+", "+line);
    //            } else if (u >= 50 && u <= 59) {
    //                rz15.add(line);
    //                //                System.out.println("rz15: "+uid+", "+u+", "+line);
    //            } else if (u >= 80 && u <= 99) {
    //                rz04.add(line);
    //                //System.out.println("rz04: "+uid+", "+u+", "+line);
    //            }
    //        }
    //        print(rz15);
    //
    //    }
    //
    //    private static void print(Set<String> set) {
    //        for (String s : set) {
    //            System.out.println(s);
    //        }
    //    }

}
