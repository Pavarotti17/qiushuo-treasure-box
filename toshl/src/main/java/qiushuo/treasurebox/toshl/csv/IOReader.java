/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package qiushuo.treasurebox.toshl.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * 
 * @author shuo.qius
 * @version $Id: IOReader.java, v 0.1 Feb 29, 2016 4:59:15 PM qiushuo Exp $
 */
public class IOReader {
    /**
     * @param path
     * @param classpath
     * @param skipLine
     * @return
     * @throws IOException
     */
    public static List<List<String>> readCsvFile(String path, boolean classpath, int skipLine)
                                                                                              throws IOException {
        InputStream in = null;
        try {
            in = classpath ? IOReader.class.getResourceAsStream(path) : new FileInputStream(
                new File(path));
            return readCsvFile(in, skipLine);
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * @param in
     * @return
     * @throws IOException
     */
    public static List<List<String>> readCsvFile(InputStream in, int skipLine) throws IOException {
        @SuppressWarnings("resource")
        List<CSVRecord> list = new CSVParser(new InputStreamReader(in, "utf8"), CSVFormat.RFC4180)
            .getRecords();
        for (int i = 0; i < skipLine; ++i) {
            list.remove(0);
        }
        List<List<String>> rst = new ArrayList<>(list.size());
        for (CSVRecord r : list) {
            List<String> line = new ArrayList<String>();
            for (Iterator<String> iter = r.iterator(); iter.hasNext();) {
                String col = iter.next();
                col = escapeCsvCol(col);
                line.add(col);
            }
            if (line.isEmpty())
                continue;
            if (line.size() == 1 && line.iterator().next().trim().isEmpty())
                continue;
            rst.add(line);
        }
        return rst;
    }

    /**
     * @param col
     * @return
     */
    public static String escapeCsvCol(String col) {
        if (col == null)
            return col;
        col = col.trim();
        if (col.length() > 0 && col.charAt(0) == '"') {
            if (col.length() < 2 || col.charAt(col.length() - 1) != '"') {
                throw new IllegalArgumentException(
                    "csv column start with \" should also end with \": " + col);
            }
            boolean inEscape = false;
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < col.length() - 1; ++i) {
                char c = col.charAt(i);
                if (inEscape) {
                    if (c != '"') {
                        throw new IllegalArgumentException(
                            "csv column with \" escape must have \"\" format: " + col);
                    }
                    sb.append('"');
                    inEscape = false;
                } else {
                    if (c == '"') {
                        inEscape = true;
                    } else {
                        sb.append(c);
                    }
                }
            }
            if (inEscape) {
                throw new IllegalArgumentException(
                    "csv column with \" escape must have \"\" format: " + col);
            }
            return sb.toString();
        } else {
            return col;
        }
    }

}
