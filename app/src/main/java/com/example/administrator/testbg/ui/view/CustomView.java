package com.example.administrator.testbg.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import simple.util.tools.LogUtil;

/**
 * Created by glp on 2017/3/10.
 */

public class CustomView extends View {
    static final String TAG = "CustomView";

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        LogUtil.i(TAG, "onMeasure" + "," + getContentDescription());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        LogUtil.i(TAG, "onLayout" + "," + getContentDescription());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        LogUtil.i(TAG, "onDraw" + "," + getContentDescription());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtil.i(TAG, "dispatchTouchEvent" + "," + getContentDescription());

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.i(TAG, "onTouchEvent" + "," + getContentDescription());

        return super.onTouchEvent(event);
    }
}
