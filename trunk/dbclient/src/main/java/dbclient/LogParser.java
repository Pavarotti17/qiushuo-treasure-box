/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package dbclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * @author qiushuo
 * @version $Id: LogParser.java, v 0.1 2014年9月4日 下午9:02:21 qiushuo Exp $
 */
public class LogParser {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) throws Throwable {
        File file = new File("/Users/qiushuo/work/runtime/20140903_pointcore/budgetcore_error.log");
        new LogParser().parse(file);
    }

    private Map<String,Long> map=new TreeMap<String, Long>();
    
    private void parse(File f) throws Throwable {
        BufferedReader fin = null;
        try {
            fin = new BufferedReader(new FileReader(f));
            for(String line=null;(line=fin.readLine())!=null;){
                if (!line.contains("for key 'BC_MANAGE_ORDER_BS_U', BudgetIncreaseReq=2014090300046100")) {
                    continue;
                }
                int idx=line.indexOf("BudgetIncreaseReq=");
                String bid = line.substring(idx + "BudgetIncreaseReq=".length(), idx + "BudgetIncreaseReq=".length()+64);
                Long l=map.get(bid);
                if(l==null){
                    l=0L;
                }
                l+=1;
                map.put(bid, l);
            }
        } finally {
            try {
                fin.close();
            } catch (Exception e) {
            }
        }
        for(String bid:map.keySet()){
            System.out.println(bid+", "+map.get(bid));
        }
    }

}
