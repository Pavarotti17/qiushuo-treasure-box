/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package qiushuo.treasurebox.toshl.csv;

import java.util.Collection;
import java.util.List;

import qiushuo.treasurebox.toshl.Bill;
import qiushuo.treasurebox.toshl.util.CommonUtil;

/**
 * 
 * @author shuo.qius
 * @version $Id: V2CsvMapper.java, v 0.1 Feb 29, 2016 9:12:42 PM qiushuo Exp $
 */
public class V2CsvMapper extends AbstractCsvMapper {

    /**
     * @param hey
     */
    public V2CsvMapper(boolean hey) {
        super(hey);
    }

    /** 
     * "Date",    "Account", "Category", "Tags",                   "Expense amount","Income amount","Currency","In main currency","Main currency","Description" <br/>
     * "1/31/85", 现金,       Unsorted,   CE-Mac,                    588.00,          0,              CNY,       588.00,            CNY,            电源线
     * 8/11/15,   现金,       unsorted,   "traffic-train, 150721_ER",296.00,          0,              CHF,      "1,897.44",         CNY,            回巴黎
     * 
     * @see qiushuo.treasurebox.toshl.csv.AbstractCsvMapper#applyInternal(java.util.List)
     */
    @SuppressWarnings("unused")
    @Override
    protected Bill applyInternal(List<String> list) {
        if (list == null || list.size() != 10) {
            throw new IllegalArgumentException("list must have 8 columns: " + list);
        }
        int i = 0;
        String dateStr = list.get(i++);
        String accountStr = list.get(i++);
        String categoryStr = list.get(i++);
        String tagsStr = list.get(i++);
        String expAmtStr = list.get(i++);
        String incAmtStr = list.get(i++);
        String currStr = list.get(i++);
        String currAmtStr = list.get(i++);
        String mainCurrStr = list.get(i++);
        String descStr = list.get(i++);
        if (!"CNY".equals(mainCurrStr)) {
            throw new IllegalArgumentException("currancyAmt must be CNY: " + list);
        }

        String date = CommonUtil.convertDate(dateStr);
        Bill b = new Bill(date, hey);
        b.setAccount(accountStr);
        b.setDesc(CommonUtil.removePlaceHolder(descStr));
        b.setExtraTags(CommonUtil.extractPlaceHolder(descStr));

        boolean exp = incAmtStr == null || incAmtStr.isEmpty()
                      || CommonUtil.convertAmount(incAmtStr) == 0;
        long amt = CommonUtil.convertAmount(currAmtStr);
        amt = exp ? -amt : amt;
        b.setAmount(amt);

        setTagType(b, CommonUtil.splitTags(tagsStr), categoryStr);
        return b;
    }

    /**
     * @param b
     * @param tagsStr
     * @param typeStr
     */
    public void setTagType(Bill b, Collection<String> tagsStr, String typeStr) {
        b.setType(getType(tagsStr));
        b.addExtraTags(getSubTypesFromTag(tagsStr));
        List<String> events = getTags(tagsStr);
        if (events.size() > 1)
            throw new IllegalArgumentException("event number bigger than 1: " + tagsStr);
        if (!events.isEmpty())
            b.addTag(events.get(0));
    }

}
