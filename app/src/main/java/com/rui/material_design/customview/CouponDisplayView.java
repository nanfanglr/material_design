package com.rui.material_design.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CouponDisplayView extends View {

    private Paint mPaint;

    public CouponDisplayView(Context context) {
        super(context);
        init();
    }

    public CouponDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CouponDisplayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("width=",""+getWidth());
        Log.d("width=",""+getHeight());
        int saveCnt = canvas.saveLayer(0, 0, getWidth(), getHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(createLeftCircle(getWidth(), getHeight()), 0, 0, mPaint);
        canvas.drawBitmap(createRightCircle(getWidth(), getHeight()), getWidth() - getHeight() / 2, 0, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        canvas.drawBitmap(createBGRect(getWidth(), getHeight()), 0, 0, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(saveCnt);

    }

    private Bitmap createBGRect(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, width, height, paint);
        return bitmap;
    }

    private Bitmap createLeftCircle(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(height / 2, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        canvas.drawCircle(0, height / 2, height / 2, paint);
        return bitmap;
    }

    private Bitmap createRightCircle(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(height / 2, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        canvas.drawCircle(height / 2, height / 2, height / 2, paint);
        return bitmap;
    }
}
