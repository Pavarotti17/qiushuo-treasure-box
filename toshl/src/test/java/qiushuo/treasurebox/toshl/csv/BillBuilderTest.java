/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package qiushuo.treasurebox.toshl.csv;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import junit.framework.Assert;

import org.junit.Test;

import qiushuo.treasurebox.toshl.AbstractToshlTest;
import qiushuo.treasurebox.toshl.Bill;

/**
 * 
 * @author shuo.qius
 * @version $Id: BillBuilderTest.java, v 0.1 Mar 1, 2016 10:24:23 AM qiushuo Exp $
 */
public class BillBuilderTest extends AbstractToshlTest {
    @Test
    public void readV2() throws IOException {
        List<Bill> list = IOReader.readCsvFile("/toshl_export.csv", true, 1).stream()
            .map(new V2CsvMapper(false)).collect(Collectors.toList()).stream()
            .collect(Collectors.toList());
        System.out.println(list.size());
        for (int i = 0; i < list.size(); ++i) {
            System.out.println(i + ": " + list.get(i));
        }
        Assert.assertEquals(17, list.size());
        //        10: Bill[hey=false,billId=20140506_0A1YXHH,gmt=2014-05-06,amount=-27600,account=现金,type=traffic-flight,tags=[141001_MA],extraTags=[],desc=桂鹏 机票 垫支 已在20140917退桂鹏 等待航空公司退款 
        //                马航已全额退
        //                已清]
        Assert.assertEquals("141001_MA", list.get(10).getTags().iterator().next());
        Assert.assertEquals("traffic-flight", list.get(10).getType());
        Assert.assertEquals(-27600, list.get(10).getAmount());
        Assert.assertEquals(false, list.get(10).isHey());
        Assert.assertEquals(false, list.get(10).getDesc().isEmpty());

        //13: Bill[hey=false,billId=20150722_TYKPFD4,gmt=2015-07-22,amount=-947867,account=现金,type=watch,tags=[],extraTags=[],desc=]
        Assert.assertEquals(-947867, list.get(13).getAmount());
        Assert.assertEquals("2015-07-22", list.get(13).getGmt());

        //4: Bill[hey=false,billId=20130830_I2N4WQN,gmt=2013-08-30,amount=1401402,account=现金,type=salary,tags=[],extraTags=[],desc=]
        Assert.assertEquals(1401402, list.get(4).getAmount());
    }

    @Test
    public void readV1() throws IOException {
        List<Bill> list = IOReader.readCsvFile("/toshl_export_hey_init.csv", true, 1).stream()
            .map(new V1CsvMapper(true)).collect(Collectors.toList()).stream()
            .collect(Collectors.toList());
        //        System.out.println(list.size());
        //        for (int i = 0; i < list.size(); ++i) {
        //            System.out.println(i + ": " + list.get(i));
        //        }
        Assert.assertEquals(591, list.size());
        //46: Bill[billId=20150415_HG51RUC,gmt=2015-04-15,amount=-1614000,account=cash,type=traffic-flight,tags=[150721_ER],extraTags=[],desc=20150721_成都-巴黎 往返机票]
        Assert.assertEquals("traffic-flight", list.get(46).getType());
        Assert.assertTrue(list.get(46).getExtraTags().isEmpty());
        Assert.assertEquals("150721_ER", list.get(46).getTags().iterator().next());
        Assert.assertEquals(1, list.get(46).getTags().size());
        //8: Bill[billId=20150425_17WZHVO,gmt=2015-04-25,amount=-100000,account=cash,type=house,tags=[],extraTags=[窗帘],desc=窗帘订金 尾款还有2980 ]
        Assert.assertEquals("house", list.get(8).getType());
        Assert.assertTrue(list.get(8).getTags().isEmpty());
        Assert.assertEquals("窗帘", list.get(8).getExtraTags().iterator().next());
        Assert.assertEquals(1, list.get(8).getExtraTags().size());
        Assert.assertTrue(list.get(8).getAmount() < 0);
        //569: Bill[billId=20140811_FJOMUZ7,gmt=2014-08-11,amount=160000,account=cash,type=salary,tags=[],extraTags=[],desc=]
        Assert.assertEquals("salary", list.get(569).getType());
        Assert.assertTrue(list.get(569).getAmount() > 0);
    }

}
