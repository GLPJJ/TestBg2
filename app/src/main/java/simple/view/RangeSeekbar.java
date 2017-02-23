package simple.view;

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

    static final String TAG = "RangeSeekbar";

    RangeSeekbarListener mRangeSeekbarListener;

    Drawable mDrawableThumb;
    Drawable mDrawableThumbSecond;
    Drawable mDrawableProgress;
    Drawable mDrawableRange;
    int mProgress = 0;
    int mProgressSecond = 0;
    int mMax = 100;
    int mProgressHeight = 20;
    int mHeight = 100;
    int mDotRadius;
    boolean mShowDot = true;

    int mSampleWidth = 0;
    float mThumbXLeft = 0;
    float mThumbXRight = 0;
    int mDragging = 0;//0 没按住拇指，1按住左边拇指，2按住右边拇指
    float mDownX = 0;
    int mThumbW;//拇指宽度，默认读取第一个拇指的宽度
    int mDotUnitW;//结点之间的距离
    int mThumbFloatW;//拇指移动范围长度

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
                mDrawableThumbSecond = a.getDrawable(R.styleable.RangeSeekbar_thumb_second);
                mDrawableProgress = a.getDrawable(R.styleable.RangeSeekbar_drawable_progress);
                mDrawableRange = a.getDrawable(R.styleable.RangeSeekbar_drawable_range);
                mMax = Math.max(1, a.getInt(R.styleable.RangeSeekbar_max, 100));
                mProgress = Math.min(mMax, Math.max(0, a.getInt(R.styleable.RangeSeekbar_progress, 0)));
                mProgressSecond = Math.max(mProgress, Math.min(mMax, Math.max(0
                        , a.getInt(R.styleable.RangeSeekbar_progress_second, mMax))));
//                int defPH = ToolUtil.Dp2Px(10);
                mProgressHeight = Math.max(10, a.getDimensionPixelSize(R.styleable.RangeSeekbar_progress_height
                        , 10));

                mThumbW = mDrawableThumb == null ? 0 : mDrawableThumb.getIntrinsicWidth();
                mHeight = Math.max(mProgressHeight * 6, mThumbW);

//                int defDD = ToolUtil.Dp2Px(15);
                mDotRadius = Math.max(12, a.getDimensionPixelSize(R.styleable.RangeSeekbar_dot_radius, 12));
                mShowDot = a.getBoolean(R.styleable.RangeSeekbar_dot_visible, true);


            } finally {
                a.recycle();
            }
        }
    }

    public RangeSeekbarListener getRangeSeekbarListener() {
        return mRangeSeekbarListener;
    }

    public void setRangeSeekbarListener(RangeSeekbarListener rangeSeekbarListener) {
        mRangeSeekbarListener = rangeSeekbarListener;
    }

    public void setProgressRange(int progress1, int progress2) {
        setProgress(progress1);
        setProgressSecond(progress2);
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        boolean dirty = mProgress != progress;
        mProgress = progress;
        onReCalculation(dirty);
    }

    public int getProgressSecond() {
        return mProgressSecond;
    }

    public void setProgressSecond(int progressSecond) {
        boolean dirty = mProgressSecond != progressSecond;
        mProgressSecond = progressSecond;
        onReCalculation(dirty);
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        boolean dirty = mMax != max;
        mMax = max;
        onReCalculation(dirty);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(getMeasuredWidth(), resolveSizeAndState(mHeight, heightMeasureSpec, 0));

        onReCalculation();
    }

    void onReCalculation() {
        onReCalculation(false);
    }

    void onReCalculation(boolean dirty) {
        mThumbFloatW = getWidth() - mThumbW;
        mDotUnitW = mThumbFloatW / mMax;
        mThumbXLeft = getXFromProgress(mProgress);
        mThumbXRight = getXFromProgress(mProgressSecond);

        Log.d(TAG, String.format("onMeasure:left:%f,right:%f,floatw:%d,unit:%d", mThumbXLeft, mThumbXRight, mThumbFloatW, mDotUnitW));

        if (dirty)
            invalidate();
    }

    /**
     * ViewGroup 包含Touch事件
     * onInterceptTouchEvent
     *
     *
     * 用户点击 - > 点击到某个Activity的ViewGroup
     * -> 调用视图的根 ViewGroup 的dispatchTouchEvent      -----------------dispatchTouchEvent方法只负责事件的分发，当他返回true时，顺序下发就终止了
     * -> 继续调用
     *      第一种情况 还是 ViewGroup -> dispatchTouchEvent(ViewGroup) 调用子View 的 dispatchTouchEvent
     *      第二种情况 View-> dispatchTouchEvent（View） 调用 onTouchEvent
     *
     *
     * onInterceptTouchEvent 1.拦截Down事件的分发。2.中止Up和Move事件向目标View传递，使得目标View所在的ViewGroup捕获Up和Move事件。
     */

    /**
     * 一般情况下，我们不该在普通View内重写dispatchTouchEvent方法，因为它并不执行分发逻辑。
     * 当Touch事件到达View时，我们该做的就是是否在onTouchEvent事件中处理它
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //判断是否点击在thumb或者进度条上
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

//        LogUtil.i(TAG, String.format("onTouchEvent action:%d,x:%f,y:%f", event.getAction(), event.getX(), event.getY()));
        boolean ret = super.onTouchEvent(event);

        if (isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP: {
//                    LogUtil.i(TAG, String.format("ACTION_UP x:%f,y:%f", event.getX(), event.getY()));
                    mDragging = 0;
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
//                    LogUtil.i(TAG, String.format("ACTION_MOVE x:%f,y:%f", event.getX(), event.getY()));
                    float diff = event.getX() - mDownX;
                    if (mDragging == 1) {
                        float temp1 = Math.max(0, mDownX + diff);
                        float temp2 = Math.min(temp1, mThumbXRight);
                        float temp3 = Math.min(temp2, mThumbFloatW);
//                        LogUtil.i(TAG, String.format("temp %f,%f,%f", temp1, temp2, temp3));
                        onThumbPosChange(temp3);
                        invalidate();
                    } else if (mDragging == 2) {
                        float temp1 = Math.max(0, mDownX + diff);
                        float temp2 = Math.max(temp1, mThumbXLeft);
                        float temp3 = Math.min(temp2, mThumbFloatW);
//                        LogUtil.i(TAG, String.format("temp %f,%f,%f", temp1, temp2, temp3));
                        onThumbPosChange(temp3);
                        invalidate();
                    }
                    break;
                }
                case MotionEvent.ACTION_DOWN: {
//                    LogUtil.i(TAG, String.format("ACTION_DOWN x:%f,y:%f,left:%f,right:%f"
//                            , event.getX(), event.getY(), mThumbXLeft, mThumbXRight));
                    if (event.getX() <= mThumbXRight + mDrawableThumbSecond.getIntrinsicWidth()
                            && event.getX() >= mThumbXRight
                            && ((mProgress == mProgressSecond && mProgressSecond < mMax)
                            || mProgress != mProgressSecond)) {
                        mDragging = 2;
                        mDownX = event.getX();
                        disallowParentTouch();
                        return true;
                    } else if (event.getX() <= mThumbXLeft + mDrawableThumb.getIntrinsicWidth()
                            && event.getX() >= mThumbXLeft) {
                        mDragging = 1;
                        mDownX = event.getX();
                        disallowParentTouch();
                        return true;
                    } else {
                        int tempProgress = getProgressFromX(event.getX());
                        if (tempProgress < mProgress) {
                            mDragging = 1;
                            onThumbProgressChange(tempProgress);
                            invalidate();
                        } else if (tempProgress > mProgressSecond) {
                            mDragging = 2;
                            onThumbProgressChange(tempProgress);
                            invalidate();
                        }

                        Log.i(TAG, String.valueOf(false));
                        return false;
                    }
                }
            }
        }

        return true;//如果ACTION_DOWN return false，则会导致后面的Event不会传递过来
    }

    void disallowParentTouch() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    void onThumbPosChange(float pos) {
        int progress = getProgressFromX(pos);
//        LogUtil.i(TAG, String.format("onThumbPosChange Dragging = %d,progress=%d,first=%d,second=%d"
//                , mDragging, progress, mProgress, mProgressSecond));

        onThumbProgressChange(progress);
    }

    void onThumbProgressChange(int progress) {
        if (mDragging == 1) {
            if (mProgress != progress) {
                if (mRangeSeekbarListener != null) {
                    if (mRangeSeekbarListener.onProgressChange(mDragging, mProgress, progress)) {
                        mProgress = progress;
                        mThumbXLeft = getXFromProgress(mProgress);
                    }
                } else {//默认更新拇指值
                    mProgress = progress;
                    mThumbXLeft = getXFromProgress(mProgress);
                }
            }
        } else if (mDragging == 2) {
            if (mProgressSecond != progress) {
                if (mRangeSeekbarListener != null) {
                    if (mRangeSeekbarListener.onProgressChange(mDragging, mProgressSecond, progress)) {
                        mProgressSecond = progress;
                        mThumbXRight = getXFromProgress(mProgressSecond);
                    }
                } else {//默认更新拇指值
                    mProgressSecond = progress;
                    mThumbXRight = getXFromProgress(mProgressSecond);
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDrawableProgress != null) {
            canvas.save();
            mDrawableProgress.setBounds(0, 0, mThumbFloatW, mProgressHeight);
            canvas.translate(mThumbW / 2.0f, (getHeight() - mProgressHeight) / 2.0f);
            mDrawableProgress.draw(canvas);
            canvas.restore();
        }

        if (mDrawableRange != null && mProgressSecond != mProgress) {
            canvas.save();
            mDrawableRange.setBounds(0, 0, (int) getXFromProgress(mProgressSecond - mProgress), mProgressHeight);
            canvas.translate(mThumbW / 2.0f + getXFromProgress(mProgress), (getHeight() - mProgressHeight) / 2.0f);
            mDrawableRange.draw(canvas);
            canvas.restore();
        }

        if (mShowDot) {
            canvas.save();
            mDrawableProgress.setBounds(0, 0, mDotRadius, mDotRadius); // diameter
            mDrawableRange.setBounds(0, 0, mDotRadius, mDotRadius);
            canvas.translate(mThumbW / 2.0f, (getHeight() - mDotRadius) / 2.0f);
            for (int i = 0; i <= mMax; i++) {
                if (i >= mProgress && i <= mProgressSecond) {
                    mDrawableRange.draw(canvas);
                } else {
                    mDrawableProgress.draw(canvas);
                }
                canvas.translate(mDotUnitW, 0);
            }
            canvas.restore();
        }

        if (mDrawableThumb != null) {
            canvas.save();
            mDrawableThumb.setBounds(0, 0, mThumbW, mDrawableThumb.getIntrinsicHeight());
            canvas.translate(getXFromProgress(mProgress), (getHeight() - mDrawableThumb.getIntrinsicHeight()) / 2.0f);
            mDrawableThumb.draw(canvas);
            canvas.restore();
        }

        if (mDrawableThumbSecond != null) {
            canvas.save();
            canvas.translate(getXFromProgress(mProgressSecond), (getHeight() - mDrawableThumbSecond.getIntrinsicHeight()) / 2.0f);
            canvas.scale(mThumbW * 1.0f / mDrawableThumbSecond.getIntrinsicWidth()
                    , mDrawableThumb.getIntrinsicHeight() * 1.0f / mDrawableThumbSecond.getIntrinsicHeight());
            mDrawableThumbSecond.setBounds(0, 0, mDrawableThumbSecond.getIntrinsicWidth()
                    , mDrawableThumbSecond.getIntrinsicHeight());
            mDrawableThumbSecond.draw(canvas);
            canvas.restore();
        }
    }

    int getProgressFromX(float x) {
        return (int) ((x) * mMax / mThumbFloatW);
    }

    float getXFromProgress(int progress) {
        return progress * 1.0f * mThumbFloatW / mMax;
    }

    public interface RangeSeekbarListener {
        /**
         * @param type        1 表示拖动左拇指，2 表示拖动右拇指
         * @param oldProgress 旧值
         * @param newProgress 新值
         * @return 是否允许更新拇指
         */
        boolean onProgressChange(int type, int oldProgress, int newProgress);
    }
}
