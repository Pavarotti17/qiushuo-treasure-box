/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package qiushuo.treasurebox.toshl;

import junit.framework.Assert;

import org.junit.Test;

import qiushuo.treasurebox.toshl.Bill;

/**
 * 
 * @author shuo.qius
 * @version $Id: BillTest.java, v 0.1 Feb 29, 2016 7:25:18 PM qiushuo Exp $
 */
public class BillTest extends AbstractToshlTest {

    @Test
    public void genBillId() {
        Bill b = new Bill("2016-01-01");
        Assert.assertEquals("2016-01-01", b.getGmt());
        Assert.assertEquals("20160101", b.getBillId().substring(0, 8));
        Assert
            .assertNotSame(new Bill("2016-01-01").getBillId(), new Bill("2016-01-01").getBillId());
    }
}
