/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package qiushuo.treasurebox.toshl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

/**
 * 
 * @author shuo.qius
 * @version $Id: ToshlTest.java, v 0.1 Feb 29, 2016 6:39:43 PM qiushuo Exp $
 */
public abstract class ToshlTest {
    /**
     * list of collection is not supported
     * 
     * @param expect
     * @param actual
     */
    public static <T> void assertPlainList(List<T> expect, Collection<T> actual) {
        if (expect == null || actual == null) {
            Assert.assertEquals(expect == null, actual == null);
            return;
        }
        Assert.assertEquals(expect.size(), actual.size());
        for (Iterator<T> iter1 = expect.iterator(), iter2 = actual.iterator(); iter1.hasNext();) {
            T e = iter1.next();
            T a = iter2.next();
            Assert.assertEquals(e, a);
        }
    }
}
