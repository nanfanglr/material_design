package com.rui.material_design.contacts;

/**
 * 转化拼音
 */

public interface IPinyinComparatorInterface {
    /**
     * 获取拼音首字母
     *
     * @return
     */
    String getPinyinFirstChar();

    void setPinyinFristChar();

    /**
     * 获取拼音
     *
     * @return
     */
    String getPinyin();

    /**
     * 将名称(无论汉字或者其他)转化为纯大写拼音
     *
     */
    void setPinyin();
}
