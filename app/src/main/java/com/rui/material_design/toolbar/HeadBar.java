package com.rui.material_design.toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rui.material_design.R;

public class HeadBar extends RelativeLayout {

    private TextView tvLeft;
    private TextView tvCenter;
    private ImageView ivRight;
    private TextView tvRight;

    private String mleft;
    private String mTitle;
    private String mRight;
    private boolean isIvRightVis;
    private boolean isTvRightVis;
    private boolean isTvLeftVis;
    private int ivRightRes;
    private int leftImgRes;
    private int tvRightImgRes;
    private int rightTextSize;
    private int leftTextSize;
    private int rightTextColor;
    private int leftTextColor;

    public HeadBar(Context context) {
        this(context, null);
    }

    public HeadBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeadBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HeadBar);
        mTitle = ta.getString(R.styleable.HeadBar_titleText);
        mleft = ta.getString(R.styleable.HeadBar_leftText);
        mRight = ta.getString(R.styleable.HeadBar_rightText);
        ivRightRes = ta.getResourceId(R.styleable.HeadBar_rightIvImgRes, -1);
        leftImgRes = ta.getResourceId(R.styleable.HeadBar_leftImgRes, -1);
        tvRightImgRes = ta.getResourceId(R.styleable.HeadBar_rightTvImgRes, -1);
        isTvLeftVis = ta.getBoolean(R.styleable.HeadBar_isTvLeftVis, false);
        isIvRightVis = ivRightRes != -1;
        isTvRightVis = !TextUtils.isEmpty(mRight) || tvRightImgRes != -1 || isIvRightVis;

        rightTextSize = ta.getDimensionPixelSize(R.styleable.HeadBar_rightTextSize, sp2px(getContext(), 14));
        leftTextSize = ta.getDimensionPixelSize(R.styleable.HeadBar_leftTextSize, sp2px(getContext(), 14));
        rightTextColor = ta.getColor(R.styleable.HeadBar_rightTextColor, Color.BLACK);
        leftTextColor = ta.getColor(R.styleable.HeadBar_leftTextColor, Color.BLACK);
        ta.recycle();

        tvLeft.setTextColor(leftTextColor);
        tvLeft.setTextSize(px2sp(getContext(), leftTextSize));
        tvRight.setTextColor(rightTextColor);
        tvRight.setTextSize(px2sp(getContext(), rightTextSize));
        if (isTvLeftVis) {
            tvLeft.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        if (!TextUtils.isEmpty(mleft)) {
            tvLeft.setText(mleft);
        }
        if (!TextUtils.isEmpty(mTitle)) {
            tvCenter.setText(mTitle);
        }
        if (!TextUtils.isEmpty(mRight)) {
            tvRight.setText(mRight);
        }
        tvRight.setVisibility(isTvRightVis ? VISIBLE : GONE);
        ivRight.setVisibility(isIvRightVis ? VISIBLE : GONE);
        if (ivRightRes != -1) {
            ivRight.setImageResource(ivRightRes);
        }
        if (leftImgRes != -1) {
            tvLeft.setCompoundDrawablesWithIntrinsicBounds(leftImgRes, 0, 0, 0);
        }
        if (tvRightImgRes != -1) {
            tvRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, tvRightImgRes, 0);
        }

    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.head_bar, this, true);
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvRight = (TextView) findViewById(R.id.tv_right);
        tvCenter = (TextView) findViewById(R.id.tv_center);
        ivRight = (ImageView) findViewById(R.id.iv_right);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    private int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public void SetIvRightOnclickListener(OnClickListener listener) {
        if (ivRight.getVisibility() == VISIBLE)
            ivRight.setOnClickListener(listener);
    }

    public void SetTvRightOnclickListener(OnClickListener listener) {
        if (tvRight.getVisibility() == VISIBLE)
            tvRight.setOnClickListener(listener);
    }

    /**
     * 此方法一般在fragment中使用时调用，同时将所在activity的backclick方法重写屏蔽点击事件；
     * activity如果需要修改该按钮的点击事件，请重写baseactivity的backclick方法
     *
     * @param listener
     */
    public void SetBtnLeftOnClickListener(OnClickListener listener) {
        if (tvLeft.getVisibility() == VISIBLE)
            tvLeft.setOnClickListener(listener);
    }

    public void SetTvCenterOnClickListener(OnClickListener listener) {
        if (tvCenter.getVisibility() == VISIBLE)
            tvCenter.setOnClickListener(listener);
    }

    public void setRightText(CharSequence str) {
        if (!TextUtils.isEmpty(str)) {
            tvRight.setText(str);
        }
    }

    public void setCenterText(CharSequence str) {
        if (!TextUtils.isEmpty(str)) {
            tvCenter.setText(str);
        }
    }

    /**
     * 设置右边按钮能否点击状态
     */
    public void setTvRightEnable(boolean isEnable) {
        tvRight.setEnabled(isEnable);
        tvRight.setTextColor(isEnable ? Color.parseColor("#323232")
                : Color.parseColor("#989898"));
    }

    public void setTvRightVisible(boolean isVisible) {
        tvRight.setVisibility(isVisible ? VISIBLE : GONE);
    }

    public void setTvLeftVisible(boolean isVisible) {
        tvLeft.setVisibility(isVisible ? VISIBLE : GONE);
    }


}
