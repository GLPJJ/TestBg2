package simple.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import simple.util.til.ToolUtil;

/**
 * Created by glp on 2016/11/27.
 * <p>
 * EventLinearLayout 强制捕获点击事件
 * <p>
 * EventLinearLayout 包含两个子控件，EditText和ImageView
 */

public class EventLinearLayout extends LinearLayout {

    EditText mEditText;
    ImageView mImageView;

    public EventLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EventLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

        initFindChild(child);
    }

    protected void initFindChild(View v) {
        if (v == null)
            return;

        if (v instanceof EditText) {
            mEditText = (EditText) v;
        } else if (v instanceof ImageView) {
            mImageView = (ImageView) v;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mEditText != null && mImageView != null) {
            //如果输入框可用，或者点击了图片
            if (mEditText.isEnabled() || ToolUtil.IsMotionEventInView(ev, mImageView))
                return false;
            return true;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
