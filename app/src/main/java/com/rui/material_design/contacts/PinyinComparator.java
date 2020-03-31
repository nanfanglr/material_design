package com.rui.material_design.contacts;

import android.text.TextUtils;

import java.util.Comparator;

/**
 * 拼音比较器
 */

public class PinyinComparator<T extends IPinyinComparatorInterface> implements Comparator<T> {
    @Override
    public int compare(IPinyinComparatorInterface pinyin1, IPinyinComparatorInterface pinyin2) {//由 A~Z~#依次排序
        if (pinyin1 == null || TextUtils.isEmpty(pinyin1.getPinyin())) {
            return -1;
        }
        if (pinyin2 == null || TextUtils.isEmpty(pinyin2.getPinyin())) {
            return 1;
        }

        if ("#".equals(pinyin1.getPinyinFirstChar())) {
            return 1;
        }

        if ("#".equals(pinyin2.getPinyinFirstChar())) {
            return -1;
        }

        return pinyin1.getPinyin().compareTo(pinyin2.getPinyin());
    }
}
