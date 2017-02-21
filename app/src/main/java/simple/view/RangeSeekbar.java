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

import simple.util.til.ToolUtil;

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
	boolean mClickLeft = false;
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
				int defPH = ToolUtil.Dp2Px(10);
				mProgressHeight = Math.max(defPH, a.getDimensionPixelSize(R.styleable.RangeSeekbar_progress_height
						, defPH));
				mHeight = mProgressHeight * 5;

				int defDD = ToolUtil.Dp2Px(15);
				mDotRadius = Math.max(defPH, a.getDimensionPixelSize(R.styleable.RangeSeekbar_dot_radius, defDD));
				mShowDot = a.getBoolean(R.styleable.RangeSeekbar_dot_visible, true);

				mThumbW = mDrawableThumb == null ? 0 : mDrawableThumb.getIntrinsicWidth();
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

		mThumbXLeft = getXFromProgress(mProgress);
		mThumbXRight = getXFromProgress(mProgressSecond);
		mThumbFloatW = getWidth() - mThumbW;
		mDotUnitW = mThumbFloatW / mMax;

		Log.d(TAG, String.format("onMeasure:%f,%f", mThumbXLeft, mThumbXRight));
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
//                    Log.i(TAG, String.format("ACTION_UP x:%f,y:%f", event.getX(), event.getY()));
					if (mClickLeft) {
						mClickLeft = false;
					}
					mDragging = 0;
					break;
				}
				case MotionEvent.ACTION_MOVE: {
//					Log.i(TAG, String.format("ACTION_MOVE x:%f,y:%f", event.getX(), event.getY()));
					float diff = event.getX() - mDownX;
					if (mDragging == 1) {
						float temp1 = Math.max(0, mDownX + diff);
						float temp2 = Math.min(temp1, mThumbXRight);
						float temp3 = Math.min(temp2, mThumbFloatW);
//						Log.i(TAG, String.format("temp %f,%f,%f", temp1, temp2, temp3));
						onThumbPosChange(temp3);
						invalidate();
					} else if (mDragging == 2) {
						float temp1 = Math.max(0, mDownX + diff);
						float temp2 = Math.max(temp1, mThumbXLeft);
						float temp3 = Math.min(temp2, mThumbFloatW);
//						Log.i(TAG, String.format("temp %f,%f,%f", temp1, temp2, temp3));
						onThumbPosChange(temp3);
						invalidate();
					}
					break;
				}
				case MotionEvent.ACTION_DOWN: {
//					Log.i(TAG, String.format("ACTION_DOWN x:%f,y:%f,left:%f,right:%f"
//							, event.getX(), event.getY(), mThumbXLeft, mThumbXRight));
					if (event.getX() <= mThumbXRight + mDrawableThumbSecond.getIntrinsicWidth()
							&& event.getX() >= mThumbXRight
							&& ((mProgress == mProgressSecond && mProgressSecond < mMax)
							|| mProgress != mProgressSecond)) {
						mDragging = 2;
						mDownX = event.getX();
						return true;
					} else if (event.getX() <= mThumbXLeft + mDrawableThumb.getIntrinsicWidth()
							&& event.getX() >= mThumbXLeft) {
						mDragging = 1;
						mDownX = event.getX();
						return true;
					} /*else if (event.getX() < getWidth() / 2) {
						mClickLeft = true;
						mThumbXLeft = event.getX();
						onThumbPosChange(mThumbXLeft);
						invalidate();
						return false;
					}*/
					break;
				}
			}
		}

//        Log.i(TAG, String.valueOf(ret));
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

	void onThumbPosChange(float pos) {
		int progress = getProgressFromX(pos);
		Log.i(TAG, String.format("onThumbPosChange Dragging = %d,progress=%d,first=%d,second=%d"
				, mDragging, progress, mProgress, mProgressSecond));

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
