package com.rui.material_design.contacts;

/**
 * 绘制悬停view
 */

public interface ISuspensionInterface {
    //是否需要显示悬停
    boolean isShowAlphabet();

    void setShowAlphabet(boolean showAlphabet);

    //悬停的title的文字
    String getSuspensionTitleText();

}
