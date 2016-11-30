/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package qiushuo.treasurebox.toshl.csv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

import qiushuo.treasurebox.toshl.Bill;
import qiushuo.treasurebox.toshl.util.Constant;

/**
 * 
 * @author shuo.qius
 * @version $Id: AbstractCsvMapper.java, v 0.1 Feb 29, 2016 9:31:03 PM qiushuo Exp $
 */
public abstract class AbstractCsvMapper implements Function<List<String>, Bill> {
    protected final boolean hey;

    public AbstractCsvMapper(boolean hey) {
        this.hey = hey;
    }

    @Override
    public Bill apply(List<String> list) {
        try {
            return applyInternal(list);
        } catch (Exception e) {
            throw new IllegalArgumentException("failed for " + list, e);
        }
    }

    /**
     * @param list
     * @return
     */
    protected abstract Bill applyInternal(List<String> list);

    /**
     * @param tags contains type and tag
     * @return
     */
    protected List<String> getTags(Collection<String> tags) {
        if (tags == null || tags.isEmpty())
            return Collections.emptyList();
        List<String> list = new ArrayList<String>(1);
        for (String t : tags) {
            char c = t.charAt(0);
            if (c != '1') {
                continue;
            }
            list.add(t.trim());
        }
        return list;
    }

    protected Collection<String> getSubTypesFromTag(Collection<String> tags) {
        if (tags == null || tags.isEmpty())
            throw new IllegalArgumentException("tags is null: " + tags);
        boolean topped = false;
        for (String t : tags) {
            if (Constant.topType.contains(t)) {
                topped = true;
                break;
            }
        }
        if (!topped) {
            return Collections.emptySet();
        }
        Collection<String> set = new HashSet<String>(1);
        for (String t : tags) {
            char c = t.charAt(0);
            if (c == '1') {
                continue;
            }
            if (Constant.topType.contains(t)) {
                continue;
            }
            set.add(t);
        }
        return set;
    }

    /**
     * @param tags contains type and tag
     * @return {@link Bill#DEFAULT_TYPE} if no availvable type found
     */
    protected String getType(Collection<String> tags) {
        if (tags == null || tags.isEmpty())
            throw new IllegalArgumentException("tags is null: " + tags);
        String type = null;
        for (String t : tags) {
            if (Constant.topType.contains(t)) {
                return t;
            }
        }

        for (String t : tags) {
            char c = t.charAt(0);
            if (c == '1') {
                continue;
            }
            if (type != null) {
                throw new IllegalArgumentException("duplicate tag: " + tags);
            }
            type = t;
        }
        if (type == null)
            return Bill.DEFAULT_TYPE;
        return type;
    }
}
