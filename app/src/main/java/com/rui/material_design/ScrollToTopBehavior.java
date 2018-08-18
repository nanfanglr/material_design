package com.rui.material_design;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by rui on 2018/8/18
 * 注意被依赖的View只有实现了NestedScrollingChild接口的才可以将事件传递给CoordinatorLayout。
 * 但注意这个滑动事件是对于CoordinatorLayout的。所以只要CoordinatorLayout有NestedScrollingChild就会滑动
 * ，他滑动就会触发这几个回调。无论你是否依赖了那个View。
 */
public class ScrollToTopBehavior extends CoordinatorLayout.Behavior<View> {
    /**
     * 记录child的当前top值的全局变量
     */
    int offsetTotal = 0;
    boolean scrolling = false;

    public ScrollToTopBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 这里返回true，才会接受到后续滑动事件。
     */
    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child
            , @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return true;
    }

    /**
     * 进行滑动事件处理
     */
    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target
            , int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        offset(child, dyConsumed);
    }

    /**
     * @param child
     * @param dy，在竖直方向上移动的距离，向上为正（原坐标-移动后的坐标），向下为负
     */
    public void offset(View child, int dy) {
        Log.i("TAG", "----------->dy=" + dy);
        //记录了child旧的top值，滑动了就将之前记录给old
        int old = offsetTotal;
        // 滑动后，重新计算child的top值，
        int top = offsetTotal - dy;
        //限制top取值范围在（0，-child.getHeight()）
        // 当向上的时候，限制top值其不能超出-child.getHeight()范围
        top = Math.max(top, -child.getHeight());
        // top取值范围在（0，-child.getHeight()）
        // 当向下的时候。top不断增大，top大于大于零之后，必须限制top零值，保持child在top为0的位置
        top = Math.min(top, 0);
        //将处理后的top值，赋值给记录当前top的变量
        offsetTotal = top;
        if (old == offsetTotal) {
            // 当到到达上边界(top=-child.getHeight())或者下边界时(top=0)，禁止滑动
            scrolling = false;
            return;
        }
        // 计算child的偏移量
        int delta = offsetTotal - old;
        // 设置child的偏移量
        child.offsetTopAndBottom(delta);
        scrolling = true;
    }

    /**
     * 当进行快速滑动
     */
    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child
            , @NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }
}
