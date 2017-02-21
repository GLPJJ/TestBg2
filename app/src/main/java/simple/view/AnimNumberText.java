package simple.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by glp on 2016/9/10.
 * <p>
 * 设置整数的TextView
 */

public class AnimNumberText extends AppCompatTextView {

    public AnimNumberText(Context context) {
        super(context);
    }

    public AnimNumberText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimNumberText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAnimText(long val) {
        setAnimText(val, false);
    }

    public synchronized void setAnimText(float val, boolean isFloat) {

        float f = 0.0f;
        if (mObjectAnimator != null && mObjectAnimator.isRunning()) {
            mObjectAnimator.end();
            f = Float.parseFloat(getText().toString());
        } else if (!TextUtils.isEmpty(getText())) {
            f = Float.parseFloat(getText().toString());
            if (f == val)
                return;
        }
        mIsFloat = isFloat;

        //修改number属性，会调用setNumber方法
        mObjectAnimator = ObjectAnimator.ofFloat(this, "number", f, val);
        mObjectAnimator.setDuration(mDuration);
        //加速器，从慢到快到再到慢
        mObjectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mObjectAnimator.start();
    }

    /**
     * 获取当前数字
     *
     * @return
     */
    public float getNumber() {
        return mNumber;
    }

    /**
     * 根据正则表达式，显示对应数字样式
     *
     * @param number
     */
    public void setNumber(float number) {
        this.mNumber = number;
        if (mIsFloat) {
            setText(String.format("%.2f", number));
        } else {
            setText(String.format("%.0f", number));
        }
    }

    ObjectAnimator mObjectAnimator;

    boolean mIsFloat = false;
    //显示数字
    float mNumber;
    //动画时长
    int mDuration = 1500;//毫秒
}
