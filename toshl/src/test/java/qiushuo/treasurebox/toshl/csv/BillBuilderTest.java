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
            .map(new V2CsvMapper()).collect(Collectors.toList()).stream()
            .collect(Collectors.toList());
//        System.out.println(list.size());
//        for (int i = 0; i < list.size(); ++i) {
//            System.out.println(i + ": " + list.get(i));
//        }
        Assert.assertEquals(3881, list.size());
        //34: Bill[billId=20130930_VJDN4T4,gmt=2013-09-30,amount=-7897,account=现金,type=traffic-commute,tags=[1310_Philippines],extraTags=[],desc=香港机场地铁]
        Assert.assertEquals("traffic-commute", list.get(34).getType());
        Assert.assertTrue(list.get(34).getExtraTags().isEmpty());
        Assert.assertEquals("1310_Philippines", list.get(34).getTags().iterator().next());
        Assert.assertEquals(1, list.get(34).getTags().size());
        Assert.assertEquals(-7897L, list.get(34).getAmount());
        //2602: Bill[billId=20150607_R7KCK8Y,gmt=2015-06-07,amount=-20700,account=现金,type=house,tags=[],extraTags=[音响],desc=音视频转换器 ]
        Assert.assertEquals("house", list.get(2602).getType());
        Assert.assertEquals("2015-06-07", list.get(2602).getGmt());
        Assert.assertTrue(list.get(2602).getTags().isEmpty());
        Assert.assertEquals("音响", list.get(2602).getExtraTags().iterator().next());
        Assert.assertEquals(1, list.get(2602).getExtraTags().size());
        Assert.assertTrue(list.get(2602).getAmount() < 0);
        //3878: Bill[billId=20160229_G95UQC3,gmt=2016-02-29,amount=1821000,account=现金,type=salary,tags=[],extraTags=[],desc=]
        Assert.assertEquals("salary", list.get(3878).getType());
        Assert.assertTrue(list.get(3878).getAmount() > 0);
        //798: Bill[billId=20140506_U4WQYIX,gmt=2014-05-06,amount=-27600,account=现金,type=traffic-flight,tags=[141001_MA],extraTags=[],desc=桂鹏 机票 垫支 已在20140917退桂鹏 等待航空公司退款 
        //马航已全额退
        // 已清]
        Assert.assertEquals("traffic-flight", list.get(798).getType());
        Assert.assertTrue(list.get(798).getExtraTags().isEmpty());
        Assert.assertEquals("141001_MA", list.get(798).getTags().iterator().next());
        Assert.assertEquals(1, list.get(798).getTags().size());
        Assert.assertEquals(-27600L, list.get(798).getAmount());
        Assert.assertNotNull(list.get(798).getDesc());
        //2889: Bill[billId=20150801_API22LA,gmt=2015-08-01,amount=-7786,account=现金,type=traffic-commute,tags=[150721_ER],extraTags=[],desc=]
        Assert.assertEquals(-7786L, list.get(2889).getAmount());
        Assert.assertEquals("traffic-commute", list.get(2889).getType());
        Assert.assertEquals(1, list.get(2889).getTags().size());
        Assert.assertEquals("150721_ER", list.get(2889).getTags().iterator().next());
    }

    @Test
    public void readV1() throws IOException {
        List<Bill> list = IOReader.readCsvFile("/toshl_export_hey_init.csv", true, 1).stream()
            .map(new V1CsvMapper()).collect(Collectors.toList()).stream()
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
