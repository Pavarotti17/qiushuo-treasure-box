/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2015 All Rights Reserved.
 */
package dbclient.toshl;

import java.util.ArrayList;

/**
 * 
 * @author shuo.qius
 * @version $Id: ImportTest.java, v 0.1 2015年12月31日 下午6:24:28 qiushuo Exp $
 */
public class ImportTest {
public static void main(String[] args) {
    Import.readRow("30 Aug 2013,Cash,unsorted,salary,0,\"14,014.02\",CNY,\"14,014.02\",CNY,", 0, new ArrayList<String>());
}
}
