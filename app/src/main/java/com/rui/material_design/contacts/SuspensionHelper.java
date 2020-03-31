package com.rui.material_design.contacts;

import android.text.TextUtils;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.Collections;
import java.util.List;

/**
 * 与拼音相关帮助类
 */

public class SuspensionHelper {

    /**
     * 将参数中的字符串转化为纯大写拼音字符串
     *
     * @param string
     * @return
     */
    public static String stringToPinyin(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        if (!TextUtils.isEmpty(string)) {
            int length = string.length();
            for (int i = 0; i < length; i++) {
                char c = string.charAt(i);
                String s = Pinyin.toPinyin(c);
                stringBuffer.append(s);
            }
        }
        return stringBuffer.toString();
    }

    /**
     * 设置悬停等相关信息
     *
     * @param list
     */
    public static  <T extends AbstractSuspension> List<T> setSuspensionInfo(List<T> list) {
        if (list != null) {
            for (AbstractSuspension abstractSuspension : list) {
                abstractSuspension.setPinyin();//设置拼音
                abstractSuspension.setPinyinFristChar();//设置拼音首字母
            }

            Collections.sort(list, new PinyinComparator<T>());//按字母比较器排序

            //设置哪些model所在item需要绘制分割线
            String index = "-1";
            for (AbstractSuspension abstractSuspension : list) {
                if (TextUtils.equals(index, abstractSuspension.getPinyinFirstChar())) {
                    abstractSuspension.setDrawDivider(true);
                    continue;
                }
                abstractSuspension.setShowAlphabet(true);
                index = abstractSuspension.getPinyinFirstChar();
            }
        }

        return list;

    }

}
