package com.example.administrator.testbg.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.administrator.testbg.R;

/**
 * Created by Administrator on 2017/2/20.
 */

public class RangeSeekbar extends View {
	Drawable mDrawableThumb;
	Drawable mDrawableProgress;
	int mRadius = 20;
	int mHeight = 100;

	int mProgress = 0;
	int mMax = 100;
	int mThumbX = 0;


	boolean mDragging = false;

	public RangeSeekbar(Context context) {
		this(context, null);
	}

	public RangeSeekbar(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RangeSeekbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAttr(context, attrs, defStyleAttr);
	}

	void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs
					, R.styleable.RangeSeekbar, defStyleAttr, 0);
			try {
				mDrawableThumb = a.getDrawable(R.styleable.RangeSeekbar_thumb);
				mDrawableProgress = a.getDrawable(R.styleable.RangeSeekbar_rangeDrawable);
				mMax = a.getInt(R.styleable.RangeSeekbar_max, 100);
				mProgress = a.getInt(R.styleable.RangeSeekbar_progress, 0);
			} finally {
				a.recycle();
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		setMeasuredDimension(getMeasuredWidth(), resolveSizeAndState(mHeight, heightMeasureSpec, 0));
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		boolean ret = super.onTouchEvent(event);

		if (isEnabled()) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_UP: {
					Log.i("RangeSeekbar", String.format("ACTION_UP x:%f,y:%f", event.getX(), event.getY()));
					mDragging = false;
					break;
				}
				case MotionEvent.ACTION_MOVE: {
					Log.i("RangeSeekbar", String.format("ACTION_MOVE x:%f,y:%f", event.getX(), event.getY()));

					if (mDragging) {
						float x = Math.max(0, event.getX());
						x = Math.min(x, getWidth());
						mProgress = (int) ((x) / (getWidth() / mMax));
						mProgress = Math.min(mProgress, mMax);

						Log.i("RangeSeekbar", String.format("ACTION_MOVE mProgress:%d", mProgress));

						invalidate();
					}
					break;
				}
				case MotionEvent.ACTION_DOWN: {
					Log.i("RangeSeekbar", String.format("ACTION_DOWN x:%f,y:%f", event.getX(), event.getY()));

					if (event.getX() <= mThumbX + mDrawableThumb.getIntrinsicWidth()
							&& event.getX() >= mThumbX) {
						mDragging = true;
						return true;
					}
					break;
				}
			}
		}

		Log.i("RangeSeekbar", String.valueOf(ret));
		return true;//如果ACTION_DOWN return false，则会导致后面的Event不会传递过来
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int thumbW = mDrawableThumb == null ? 0 : mDrawableThumb.getIntrinsicWidth();

		if (mDrawableProgress != null) {
			canvas.save();
			mDrawableProgress.setBounds(0, 0, getWidth() - thumbW, mRadius);
			canvas.translate(thumbW / 2, (getHeight() - mRadius) / 2);
			mDrawableProgress.draw(canvas);
			canvas.restore();
		}

		if (mDrawableThumb != null) {
			canvas.save();
			mDrawableThumb.setBounds(0, 0, thumbW, mDrawableThumb.getIntrinsicHeight());
			mThumbX = (int) ((getWidth() - mDrawableThumb.getIntrinsicWidth()) * 1.0f / mMax * mProgress);
			Log.i("RangeSeekbar onDraw", String.format("getWidth()=%d,%d", getWidth(), mThumbX));
			canvas.translate(mThumbX, (getHeight() - mDrawableThumb.getIntrinsicHeight()) / 2);
			mDrawableThumb.draw(canvas);
			canvas.restore();
		}
	}
}
