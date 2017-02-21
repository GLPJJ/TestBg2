package simple.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.example.administrator.testbg.R;

/**
 * Created by Administrator on 2017/2/20.
 */

public class RangeSeekbar extends View {

    RangeSeekbarListener mRangeSeekbarListener;

    Drawable mDrawableThumb;
    Drawable mDrawableProgress;
    int mRadius = 20;
    int mHeight = 100;
    int mSampleWidth = 0;

    int mProgress = 0;
    int mMax = 100;
    float mThumbX = 0;
    int mProgressHeight = 0;

    boolean mClickLeft = false;
    boolean mDragging = false;
    float mDownX = 0;

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

    public RangeSeekbarListener getRangeSeekbarListener() {
        return mRangeSeekbarListener;
    }

    public void setRangeSeekbarListener(RangeSeekbarListener rangeSeekbarListener) {
        mRangeSeekbarListener = rangeSeekbarListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(getMeasuredWidth(), resolveSizeAndState(mHeight, heightMeasureSpec, 0));
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

        boolean ret = super.onTouchEvent(event);

        if (isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP: {
//                    Log.i("RangeSeekbar", String.format("ACTION_UP x:%f,y:%f", event.getX(), event.getY()));
                    if (mClickLeft) {
                        mClickLeft = false;
                    }
                    mDragging = false;
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
//                    Log.i("RangeSeekbar", String.format("ACTION_MOVE x:%f,y:%f", event.getX(), event.getY()));
                    if (mDragging) {
                        float diff = event.getX() - mDownX;
                        mThumbX = Math.min(Math.max(0, mDownX + diff), getWidth() - mDrawableThumb.getIntrinsicWidth());
                        onThumbPosChange(mThumbX);
                        invalidate();
                    }
                    break;
                }
                case MotionEvent.ACTION_DOWN: {
//                    Log.i("RangeSeekbar", String.format("ACTION_DOWN x:%f,y:%f", event.getX(), event.getY()));


                    if (event.getX() <= mThumbX + mDrawableThumb.getIntrinsicWidth()
                            && event.getX() >= mThumbX) {
                        mDragging = true;
                        mDownX = event.getX();
                        return true;
                    } else if (event.getX() < getWidth() / 2) {
                        mClickLeft = true;
                        mThumbX = event.getX();
                        onThumbPosChange(mThumbX);
                        invalidate();
                        return false;
                    }
                    break;
                }
            }
        }

//        Log.i("RangeSeekbar", String.valueOf(ret));
        return false;//如果ACTION_DOWN return false，则会导致后面的Event不会传递过来
    }

    /**
     * Converts a drawable to a tiled version of itself. It will recursively
     * traverse layer and state list drawables.
     */
    private Drawable tileify(Drawable drawable, boolean clip) {
        // TODO: This is a terrible idea that potentially destroys any drawable
        // that extends any of these classes. We *really* need to remove this.

        if (drawable instanceof BitmapDrawable) {
            final Drawable.ConstantState cs = drawable.getConstantState();
            final BitmapDrawable clone = (BitmapDrawable) cs.newDrawable(getResources());
            clone.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);

            if (mSampleWidth <= 0) {
                mSampleWidth = clone.getIntrinsicWidth();
            }

            if (clip) {
                return new ClipDrawable(clone, Gravity.LEFT, ClipDrawable.HORIZONTAL);
            } else {
                return clone;
            }
        }

        return drawable;
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
//            Log.i("RangeSeekbar onDraw", String.format("getWidth()=%d,%f", getWidth(), mThumbX));
            canvas.translate(mProgress * (getWidth() - mDrawableThumb.getIntrinsicWidth()) / mMax
                    , (getHeight() - mDrawableThumb.getIntrinsicHeight()) / 2);
            mDrawableThumb.draw(canvas);
            canvas.restore();
        }
    }

    void onThumbPosChange(float pos) {
        int progress = (int) ((pos) * mMax / (getWidth() - mDrawableThumb.getIntrinsicWidth()));
        Log.i("RangeSeekbar onChange", String.format("progress=%d,%d", progress, mProgress));

        if (mProgress != progress) {
            if (mRangeSeekbarListener != null) {
                if (mRangeSeekbarListener.onProgressChange(mProgress, progress))
                    mProgress = progress;
            } else//默认更新拇指值
                mProgress = progress;
        }
    }

    public interface RangeSeekbarListener {
        /**
         * @param oldProgress
         * @param newProgress
         * @return 是否允许更新拇指
         */
        boolean onProgressChange(int oldProgress, int newProgress);
    }
}
