/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package qiushuo.treasurebox.toshl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author shuo.qius
 * @version $Id: Bill.java, v 0.1 Feb 29, 2016 5:34:00 PM qiushuo Exp $
 */
public class Bill {
    public static final String DEFAULT_ACCOUNT = "cash";
    public static final String DEFAULT_TYPE = "default";
    
    private final boolean hey;
    /** format e.g. <code>"20140904AKX9E1WP"</code> */
    private final String billId;
    /** format e.g. <code>"2014-09-04"</code> */
    private final String gmt;
    /** negative number for expense, positive number for income */
    private long amount;
    private String account = DEFAULT_ACCOUNT;
    private String type;
    private Set<String> tags = new TreeSet<>();
    private final List<String> extraTags = new ArrayList<>(0);
    private String desc;

    /**
     * @param gmt "yyyy-MM-dd"
     */
    public Bill(String gmt, boolean hey) {
        this.billId = genBillId(gmt.replace("-", "").trim());
        this.gmt = gmt;
        this.hey = hey;
    }

    private static final Random rnd = new Random();

    /**
     * @return
     */
    public String getOnlyTag() {
        if (this.tags == null || this.tags.isEmpty())
            return null;
        if (this.tags.size() > 1)
            throw new IllegalArgumentException("tags size > 1: " + this);
        return this.tags.iterator().next();
    }

    /**
     * @return
     */
    public String getOnlyExtraTag() {
        if (this.extraTags == null || this.extraTags.isEmpty())
            return null;
        if (this.extraTags.size() > 1)
            throw new IllegalArgumentException("extraTags size > 1: " + this);
        return this.extraTags.iterator().next();
    }

    /**
     * <code>"20140929AKX9E1WP"</code>
     * 
     * @param date e.g. "20140929"
     */
    public static String genBillId(String date) {
        if (date == null || date.length() != 8) {
            throw new IllegalArgumentException("date format must with length of 8: " + date);
        }
        StringBuilder sb = new StringBuilder(16);
        sb.append(date);
        sb.append('_');
        for (int i = 0; i < 7; ++i) {
            int n = rnd.nextInt(36);
            char c = '0';
            if (n < 10) {
                c = (char) ('0' + n);
            } else {
                c = (char) ('A' + n - 10);
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    /**
     * Setter method for property <tt>amount</tt>.
     * 
     * @param amount value to be assigned to property amount
     */
    public Bill setAmount(long amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Setter method for property <tt>account</tt>.
     * 
     * @param account value to be assigned to property account
     */
    public Bill setAccount(String account) {
        this.account = account;
        return this;
    }

    /**
     * Setter method for property <tt>type</tt>.
     * 
     * @param type value to be assigned to property type
     */
    public Bill setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Setter method for property <tt>tags</tt>.
     * 
     * @param tags value to be assigned to property tags
     */
    public Bill setTags(Set<String> tags) {
        this.tags = tags;
        return this;
    }

    /**
     * @param tag
     */
    public Bill addTag(String tag) {
        if (this.tags == null) {
            this.tags = new TreeSet<>();
        }
        this.tags.add(tag);
        return this;
    }

    /**
     * Setter method for property <tt>extraTags</tt>.
     * 
     * @param extraTags value to be assigned to property extraTags
     */
    public Bill setExtraTags(List<String> extraTags) {
        this.extraTags.addAll(extraTags);
        return this;
    }

    /**
     * @param extraTag
     */
    public Bill addExtraTag(String extraTag) {
        if (extraTags == null) {
            return this;
        }
        this.extraTags.add(extraTag);
        return this;
    }

    /**
     * @param extraTags
     */
    public Bill addExtraTags(Collection<String> extraTags) {
        if (extraTags == null || extraTags.isEmpty()) {
            return this;
        }
        this.extraTags.addAll(extraTags);
        return this;
    }

    /**
     * Setter method for property <tt>desc</tt>.
     * 
     * @param desc value to be assigned to property desc
     */
    public Bill setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    /**
     * Getter method for property <tt>billId</tt>.
     * 
     * @return property value of billId
     */
    public String getBillId() {
        return billId;
    }

    /**
     * Getter method for property <tt>gmt</tt>.
     * 
     * @return property value of gmt
     */
    public String getGmt() {
        return gmt;
    }

    /**
     * Getter method for property <tt>amount</tt>.
     * 
     * @return property value of amount
     */
    public long getAmount() {
        return amount;
    }

    /**
     * Getter method for property <tt>account</tt>.
     * 
     * @return property value of account
     */
    public String getAccount() {
        return account;
    }

    /**
     * Getter method for property <tt>type</tt>.
     * 
     * @return property value of type
     */
    public String getType() {
        return type;
    }

    /**
     * Getter method for property <tt>tags</tt>.
     * 
     * @return property value of tags
     */
    public Set<String> getTags() {
        return tags;
    }

    /**
     * Getter method for property <tt>extraTags</tt>.
     * 
     * @return property value of extraTags
     */
    public List<String> getExtraTags() {
        return extraTags;
    }

    /**
     * Getter method for property <tt>desc</tt>.
     * 
     * @return property value of desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Getter method for property <tt>hey</tt>.
     * 
     * @return property value of hey
     */
    public boolean isHey() {
        return hey;
    }

}
