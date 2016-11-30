/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package qiushuo.treasurebox.toshl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import qiushuo.treasurebox.toshl.csv.IOReader;
import qiushuo.treasurebox.toshl.csv.V1CsvMapper;
import qiushuo.treasurebox.toshl.csv.V2CsvMapper;
import qiushuo.treasurebox.toshl.mysql.DBOutput;

/**
 * 
 * @author shuo.qius
 * @version $Id: Toshl.java, v 0.1 Feb 29, 2016 4:27:39 PM qiushuo Exp $
 */
public class Toshl {
    private static final Logger logger = Logger.getLogger(Toshl.class);

    //    private static final String TOKEN  = getToken();
    //
    //    private static final String getToken() {
    //        try {
    //            return Files
    //                .lines(Paths.get(URI.create("file:///Users/shuo.qius/work/data/qiushuo/toshl_token")))
    //                .collect(Collectors.toList()).get(0);
    //        } catch (Exception e) {
    //            logger.error(e);
    //            return null;
    //        }
    //    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable {
        System.out
            .println("enter toshl report cvs path: /Users/shuo.qius/Downloads/toshl_export.csv ");
        System.out
            .println("enter toshl report cvs path: /Users/shuo.qius/Downloads/toshl_export_hey.csv ");
        BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
        String path = sin.readLine().trim();

        List<Bill> list = new ArrayList<>();
        final boolean hey = path.contains("hey");
        if (hey) {
            list.addAll(IOReader.readCsvFile("/toshl_export_hey_init.csv", true, 1).stream()
                .map(new V1CsvMapper(hey)).collect(Collectors.toList()));
        }
        list.addAll(IOReader.readCsvFile(path, false, 1).stream().map(new V2CsvMapper(hey))
            .collect(Collectors.toList()));

        list.stream().forEach(b -> logger.info(b));

        new DBOutput().output(list, hey);
    }

}
