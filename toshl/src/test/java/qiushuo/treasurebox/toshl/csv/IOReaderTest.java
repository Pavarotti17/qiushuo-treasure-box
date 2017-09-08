/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package qiushuo.treasurebox.toshl.csv;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import qiushuo.treasurebox.toshl.AbstractToshlTest;

/**
 * 
 * @author shuo.qius
 * @version $Id: IOReaderTest.java, v 0.1 Feb 29, 2016 6:13:45 PM qiushuo Exp $
 */
public class IOReaderTest extends AbstractToshlTest {

    @Test
    public void escapeCsvCol() {
        Assert.assertNull(IOReader.escapeCsvCol(null));
        Assert.assertEquals("", IOReader.escapeCsvCol(""));
        Assert.assertEquals("a", IOReader.escapeCsvCol("a"));
        Assert.assertEquals("ab", IOReader.escapeCsvCol("ab "));
        Assert.assertEquals("", IOReader.escapeCsvCol("\"\""));
        Assert.assertEquals("abc", IOReader.escapeCsvCol("\"abc\""));
        Assert.assertEquals("ab\"c", IOReader.escapeCsvCol("\"ab\"\"c\""));
        Assert.assertEquals(" ab\"c", IOReader.escapeCsvCol("  \" ab\"\"c\"  "));
        Assert.assertEquals(" ab\"\"c", IOReader.escapeCsvCol("  \" ab\"\"\"\"c\"  "));
        Assert.assertEquals("ab\r\nc", IOReader.escapeCsvCol("\"ab\r\nc\""));
        try {
            IOReader.escapeCsvCol("  \" ");
            Assert.assertTrue("must throw", false);
        } catch (IllegalArgumentException e) {
        }
        try {
            IOReader.escapeCsvCol("  \"\"\" ");
            Assert.assertTrue("must throw", false);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void readCsv() throws Exception {
        List<List<String>> list = IOReader.readCsvFile("/testcsv.csv", true, 1);
        Assert.assertEquals(4, list.size());
        list.stream().forEach(l -> System.out.println(l));
        Assert.assertEquals("窗帘订金 尾款还有2980${窗帘}", list.get(1).get(list.get(1).size() - 1));
        List<String> row3=list.get(2);
        System.out.println(row3);
        
    }
}
