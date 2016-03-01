/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package qiushuo.treasurebox.toshl.util;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import qiushuo.treasurebox.toshl.AbstractToshlTest;

/**
 * 
 * @author shuo.qius
 * @version $Id: CommonUtilTest.java, v 0.1 Feb 29, 2016 7:30:09 PM qiushuo Exp $
 */
public class CommonUtilTest extends AbstractToshlTest {

    @Test
    public void escapeAmount() {
        Assert.assertEquals(1234500L, CommonUtil.convertAmount("12,345.00"));
        Assert.assertEquals(1234500L, CommonUtil.convertAmount("12345.00"));
        Assert.assertEquals(1234500L, CommonUtil.convertAmount("12345.0"));
        Assert.assertEquals(1234510L, CommonUtil.convertAmount("12345.101"));
        Assert.assertEquals(1234510L, CommonUtil.convertAmount("12345.10"));
        Assert.assertEquals(1234510L, CommonUtil.convertAmount("12,345.1"));
        Assert.assertEquals(1234500L, CommonUtil.convertAmount("12,345."));
        Assert.assertEquals(-1234500L, CommonUtil.convertAmount("-12,345."));
    }

    @Test
    public void placeHolder() {
        assertPlainList(Arrays.asList("abc", "def"),
            CommonUtil.extractPlaceHolder("ddd${abc}{def}"));
        assertPlainList(Arrays.asList("中文", "def"), CommonUtil.extractPlaceHolder("ddd{中文}${def}"));

        Assert.assertEquals("ddd  e", CommonUtil.removePlaceHolder("ddd{中文}${def}e"));
        Assert.assertEquals("ddd $e f", CommonUtil.removePlaceHolder("ddd{中文}$e f"));
    }
}
