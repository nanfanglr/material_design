package com.rui.material_design.contacts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

/**
 * 具备悬停view的装饰类
 */

public class SuspensionDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    private int dividerHeight = 2;//分割线高度
    private Paint dividerPaint;//分割线画笔

    private int alphabetViewHeight = 0;//字母view高度
    private Paint alphabetViewBgPaint;//字母view背景
    private Paint alphabetTextPaint;//字母文字画笔
    private float alphabetTextSize;//字母文字大小
    private float alphabetTextPaddingLeft;//字母文字paddingleft距离

    private Rect mTextBounds;//用于存放测量文字Rect

    private boolean isCoordinatorLayout = false;//RecyclerView的父控件是否是CoordinatorLayout

    private int textColor = Color.parseColor("#323232");


    public SuspensionDecoration(Context context) {
        mContext = context;

        dividerPaint = new Paint();
        dividerPaint.setColor(Color.parseColor("#ff0000"));

        alphabetViewBgPaint = new Paint();
        alphabetViewBgPaint.setColor(Color.parseColor("#f2f2f2"));

        alphabetTextPaint = new Paint();
        alphabetTextPaint.setColor(textColor);
        mTextBounds = new Rect();


        alphabetViewHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());
        alphabetTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 36, context.getResources().getDisplayMetrics());
        alphabetTextPaddingLeft = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());

        alphabetTextPaint.setTextSize(alphabetTextSize);
    }

    /**
     * 设置字体
     *
     * @param sp
     */
    public void setAlphabetTextSize(int sp) {
        alphabetTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, mContext.getResources().getDisplayMetrics());
        alphabetTextPaint.setTextSize(alphabetTextSize);
    }

    /**
     * 设置高度,单位dp
     *
     * @param dp
     */
    public void setAlphabetViewHeight(int dp) {
        alphabetViewHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
    }

    public void setTextColor(@ColorInt int textColor) {
        this.textColor = textColor;
        alphabetTextPaint.setColor(textColor);
    }

    public void setCoordinatorLayout(boolean coordinatorLayout) {
        isCoordinatorLayout = coordinatorLayout;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (view.getTag() instanceof ISuspensionInterface) {
            ISuspensionInterface model = (ISuspensionInterface) view.getTag();
            if (model != null && model.isShowAlphabet()) {
                outRect.top = alphabetViewHeight;
            }
        }

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            if (childView.getTag() instanceof ISuspensionInterface) {
                ISuspensionInterface model = (ISuspensionInterface) childView.getTag();
                if (model != null && model.isShowAlphabet()) {
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();

                    Rect rect = new Rect();
                    rect.left = parent.getPaddingLeft();
                    rect.right = parent.getMeasuredWidth() - parent.getPaddingRight();
                    rect.top = childView.getTop() - alphabetViewHeight;
                    rect.bottom = childView.getTop();
                    c.drawRect(rect, alphabetViewBgPaint);

                    String titleText = model.getSuspensionTitleText();
                    alphabetTextPaint.getTextBounds(titleText, 0, titleText.length(), mTextBounds);
                    c.drawText(titleText, alphabetTextPaddingLeft, childView.getTop() - params.topMargin - (alphabetViewHeight / 2 - mTextBounds.height() / 2), alphabetTextPaint);

                }
            }

        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (false) {
            return;
        }

        if (!(parent.getLayoutManager() instanceof LinearLayoutManager)) {
            return;
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        if (firstVisibleItemPosition < 0) {
            return;
        }
        View firstVisibleChildView = parent.findViewHolderForLayoutPosition(firstVisibleItemPosition).itemView;//出现一个奇怪的bug，有时候child为空，所以将 child = parent.getChildAt(i)。-》 parent.findViewHolderForLayoutPosition(pos).itemView
//        View firstVisibleChildView = parent.getChildAt(firstVisibleItemPosition);//出现一个奇怪的bug，有时候child为空，所以将 child = parent.getChildAt(i)。-》 parent.findViewHolderForLayoutPosition(pos).itemView
        if (firstVisibleChildView == null) {
            return;
        }

        if (!(firstVisibleChildView.getTag() instanceof ISuspensionInterface)) {
            return;
        }

        ISuspensionInterface model = (ISuspensionInterface) firstVisibleChildView.getTag();
        if (model != null && !TextUtils.isEmpty(model.getSuspensionTitleText())) {
            boolean flag = false;//定义一个flag，Canvas是否位移过的标志
            //当第一个可见的item在屏幕中还剩的高度小于title区域的高度时，我们也该开始做悬浮Title的“交换动画”
            RecyclerView.ViewHolder viewHolderForLayoutPosition = parent.findViewHolderForLayoutPosition(firstVisibleItemPosition + 1);
            if (viewHolderForLayoutPosition == null) {
                return;
            }
            View secondVisibleChildView = viewHolderForLayoutPosition.itemView;
            if (secondVisibleChildView != null && secondVisibleChildView.getTag() instanceof ISuspensionInterface) {
                ISuspensionInterface secondVisibleModel = (ISuspensionInterface) secondVisibleChildView.getTag();
                if (secondVisibleModel != null && !TextUtils.equals(model.getSuspensionTitleText(), secondVisibleModel.getSuspensionTitleText())) {
                    if ((firstVisibleChildView.getTop() + firstVisibleChildView.getHeight()) < alphabetViewHeight) {
                        c.save();//每次绘制前 保存当前Canvas状态，
                        flag = true;
                        c.translate(0, firstVisibleChildView.getHeight() + firstVisibleChildView.getTop() - alphabetViewHeight);
                    }
                }
            }


            String titleText = model.getSuspensionTitleText();

            Rect suspensionRect = new Rect();//悬停view背景
            suspensionRect.left = parent.getPaddingLeft();
            suspensionRect.right = parent.getRight() - parent.getPaddingRight();
//            suspensionRect.top = parent.getTop();
//            suspensionRect.top = 0;//正常应该用parent.getTop(),但是这里用0可以顺便照顾下使用CoordinatorLayout
            suspensionRect.top = isCoordinatorLayout ? 0 : parent.getTop();
//            suspensionRect.bottom = parent.getTop() + alphabetViewHeight;
//            suspensionRect.bottom = alphabetViewHeight;//正常应该用parent.getTop(),但是这里用0可以顺便照顾下使用CoordinatorLayout
            suspensionRect.bottom = isCoordinatorLayout ? alphabetViewHeight : (parent.getTop() + alphabetViewHeight);
            c.drawRect(suspensionRect, alphabetViewBgPaint);

            alphabetTextPaint.getTextBounds(titleText, 0, titleText.length(), mTextBounds);
            c.drawText(titleText, suspensionRect.left + alphabetTextPaddingLeft,
                    suspensionRect.top + alphabetViewHeight / 2 + mTextBounds.height() / 2,
                    alphabetTextPaint);

            if (flag) {
                c.restore();//恢复画布到之前保存的状态
            }

        }

    }
}
