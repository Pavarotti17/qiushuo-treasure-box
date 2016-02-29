/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package qiushuo.treasurebox.toshl.csv;

import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;

import qiushuo.treasurebox.toshl.model.Bill;
import qiushuo.treasurebox.toshl.util.CommonUtil;

/**
 * 
 * @author shuo.qius
 * @version $Id: V1CsvMapper.java, v 0.1 Feb 29, 2016 5:38:05 PM qiushuo Exp $
 */
public class V1CsvMapper extends AbstractCsvMapper implements Function<List<String>, Bill> {
    /**
     * date,         tags,              expAmt,  incAmt, curr,  currancyAmt, mainCurrency,desc<br/>
     * "2015-04-28", "traffic-commute", "25.00", "",     "CNY", "25.00",     "CNY",       ""
     * 
     * @see java.util.function.Function#apply(java.lang.Object)
     */
    @SuppressWarnings("unused")
    @Override
    public Bill apply(List<String> list) {
        if (list == null || list.size() != 8) {
            throw new IllegalArgumentException("list must have 8 columns: " + list);
        }
        int i = 0;
        String date = list.get(i++);
        String tags = list.get(i++);
        String expAmt = list.get(i++);
        String incAmt = list.get(i++);
        String curr = list.get(i++);
        String currAmt = list.get(i++);
        String mainCurr = list.get(i++);
        String desc = list.get(i++);
        if (!"CNY".equals(mainCurr)) {
            throw new IllegalArgumentException("currancyAmt must be CNY: " + list);
        }

        Bill b = new Bill(date);
        boolean exp = incAmt == null || incAmt.trim().isEmpty();
        long amount = CommonUtil.convertAmount(currAmt);
        amount = exp ? -amount : amount;
        b.setAmount(amount);
        b.setDesc(desc);
        b.setExtraTags(CommonUtil.extractPlaceHolder(desc));

        TreeSet<String> tagset = CommonUtil.splitTags(tags);

        b.setType(getType(tagset));
        List<String> events = getTags(tagset);
        if (events.size() > 1)
            throw new IllegalArgumentException("event number bigger than 1: " + tagset);
        if (!events.isEmpty())
            b.addTag(events.get(0));

        return b;
    }

}
