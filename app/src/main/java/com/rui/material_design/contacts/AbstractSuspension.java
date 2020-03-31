package com.rui.material_design.contacts;

import android.text.TextUtils;

import com.soyute.comservice.helper.pinyin.IPinyinComparatorInterface;
import com.soyute.mvp.base.model.BaseModel;


/**
 * 对悬停view,字母表,分割线的抽象类整合
 */

public abstract class AbstractSuspension extends BaseModel implements ISuspensionInterface, IPinyinComparatorInterface, IDrawDividerInterface {

    protected String nameToPinyin;//名称转化成纯大写拼音,悬停view,字母表,分割线都以这个为基准作为判断
    protected String nameFirstChar;//名称转化成纯大写拼音的首字母

    protected boolean isShowAlphabet;//是否需要显示字母ITEM
    protected boolean isDrawDivider;//是否需要绘制分割线

    //是否需要显示字母ITEM
    @Override
    public boolean isShowAlphabet() {
        return isShowAlphabet;
    }

    @Override
    public void setShowAlphabet(boolean showAlphabet) {
        this.isShowAlphabet = showAlphabet;
    }

    //悬停的title的文字
    @Override
    public String getSuspensionTitleText() {
        if (TextUtils.isEmpty(nameFirstChar)) {
            return "";
        }
        return nameFirstChar;
    }

    /**
     * 获取拼音首字母
     *
     * @return
     */
    @Override
    public String getPinyinFirstChar() {
        if (TextUtils.isEmpty(nameFirstChar)) {
            return "";
        }
        return nameFirstChar;
    }

    public void setNameFirstChar(String nameFirstChar) {
        this.nameFirstChar = nameFirstChar;
    }

    @Override
    public void setPinyinFristChar() {
        if (!TextUtils.isEmpty(nameToPinyin)) {
            String stringFirstChar = String.valueOf(nameToPinyin.charAt(0));
            boolean matches = stringFirstChar.matches("[A-Z]");
            if (matches) {
                nameFirstChar = stringFirstChar;
            } else {
                nameFirstChar = "#";
            }

        } else {
            nameFirstChar = "#";
        }
    }

    /**
     * 获取拼音
     *
     * @return
     */
    @Override
    public String getPinyin() {
        if (TextUtils.isEmpty(nameToPinyin)) {
            return "";
        }
        return nameToPinyin;
    }

    /**
     * 将名称(无论汉字或者其他)转化为纯大写拼音
     */
    @Override
    public void setPinyin() {

    }

    /**
     * 设置是否需要绘制分割线
     *
     * @param isDrawDivider true:需要绘制分割线
     */
    @Override
    public void setDrawDivider(boolean isDrawDivider) {
        this.isDrawDivider = isDrawDivider;
    }

    /**
     * 是否需要绘制分割线
     *
     * @return
     */
    @Override
    public boolean isDrawDivider() {
        return isDrawDivider;
    }

}
