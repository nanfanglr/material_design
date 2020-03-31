package com.rui.material_design.contacts;

/**
 * Created by zhangyuncai on 2017/9/24.
 * 绘制分割线
 * 是否需要绘制分割线
 */

public interface IDrawDividerInterface {
    /**
     * 设置是否需要绘制分割线
     *
     * @param isDrawDivider true:需要绘制分割线
     */
    void setDrawDivider(boolean isDrawDivider);

    /**
     * 是否需要绘制分割线
     *
     * @return
     */
    boolean isDrawDivider();
}
