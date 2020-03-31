package com.rui.material_design.contacts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 *
 */

public class FriendDividerDecoration extends RecyclerView.ItemDecoration {
    private int dividerHeight;
    private Context mContext;
    private Paint dividerPaint;

    public FriendDividerDecoration(Context context) {
        mContext = context;
        dividerHeight = dip2px(context, 1f);
        dividerPaint = new Paint();
        dividerPaint.setColor(Color.parseColor("#f2f2f2"));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        Object tag = view.getTag();
        if (tag != null && tag instanceof IDrawDividerInterface) {
            IDrawDividerInterface model = (IDrawDividerInterface) tag;
            if (model != null && model.isDrawDivider()) {
                outRect.top = dividerHeight;
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            Object tag = childView.getTag();
            if (tag != null && tag instanceof IDrawDividerInterface) {
                IDrawDividerInterface model = (IDrawDividerInterface) tag;
                if (model != null && model.isDrawDivider()) {

                    Rect rect = new Rect();
                    rect.left = parent.getPaddingLeft() + dip2px(mContext, 16);
                    rect.right = parent.getMeasuredWidth() - parent.getPaddingRight();
                    rect.top = childView.getTop() - dividerHeight;
                    rect.bottom = childView.getTop();
                    c.drawRect(rect, dividerPaint);

                }
            }

        }
    }

    public int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }
}
