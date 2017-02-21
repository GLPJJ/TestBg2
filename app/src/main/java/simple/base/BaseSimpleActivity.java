package simple.base;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.bumptech.glide.manager.LifecycleListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Unbinder;
import simple.intefaces.ILifecycle;
import simple.util.til.ToolUtil;
import simple.util.tools.LogUtil;

/**
 * Created by glp on 2017/1/9.
 * <p>
 * 通用功能，高移植性
 * <p>
 * <p>
 * onCreate -> onStart -> onResume ---------> onPause --------> onStop -> onDestroy
 * ************ ↑                                                 ↓
 * ************* -----------------------onReStart-------------------
 */

public class BaseSimpleActivity extends BaseSwipeBackActivity implements ILifecycle {

    public static final String Tag = "BaseSimpleActivity";
    //监听Activity生命周期
    List<LifecycleListener> mLifecycleListeners = null;

    private boolean destroyed = false;
    protected Unbinder mUnbinder = null;

    //需要忽略的View
    List<View> mViewsIgnore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.i(Tag, "Activity Life onCreate : " + this.getClass().getSimpleName());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        LogUtil.i(Tag, "Activity Life onDestroy : " + this.getClass().getSimpleName());
        super.onDestroy();

        if (mLifecycleListeners != null) {
            for (LifecycleListener listener : mLifecycleListeners)
                listener.onDestroy();
            mLifecycleListeners.clear();
        }
        if (mViewsIgnore != null) {
            mViewsIgnore.clear();
        }
        //其实没必要解绑
//        if (mUnbinder != null)
//            mUnbinder.unbind();
//        mUnbinder = null;
        destroyed = true;
    }

    @Override
    protected void onStart() {
        LogUtil.i(Tag, "Activity Life onStart : " + this.getClass().getSimpleName());
        super.onStart();

        if (mLifecycleListeners != null) {
            for (LifecycleListener listener : mLifecycleListeners)
                listener.onStart();
        }
    }

    @Override
    protected void onStop() {
        LogUtil.i(Tag, "Activity Life onStop : " + this.getClass().getSimpleName());
        super.onStop();

        if (mLifecycleListeners != null) {
            for (LifecycleListener listener : mLifecycleListeners)
                listener.onStop();
        }
    }

    @Override
    protected void onResume() {
        LogUtil.i(Tag, "Activity Life onResume : " + this.getClass().getSimpleName());
        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtil.i(Tag, "Activity Life onPause : " + this.getClass().getSimpleName());
        super.onPause();

        ToolUtil.closeKeyBoard(this);
    }

    public <T extends View> T findViewByIdT(int id) {
        return (T) super.findViewById(id);
    }

    /**
     * 新增触摸到指定view不要滑动整个activity的view
     *
     * @param view
     */
    public void addIgnoreView(View view) {
        if ((mViewsIgnore != null && mViewsIgnore.contains(view)) || !mIsSwipeBack) {
            return;
        }

        if (mViewsIgnore == null) {
            mViewsIgnore = new ArrayList<>();
        }

        mViewsIgnore.add(view);
    }

    /**
     * 移除忽略
     *
     * @param view
     */
    public void removeIgnoreView(View view) {
        if (view == null)
            return;

        if (mViewsIgnore != null && mViewsIgnore.contains(view)) {
            mViewsIgnore.remove(view);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //获取当前获得当前焦点所在View
            View view = getCurrentFocus();
            if (isClickEt(view, event)) {
                //如果是edittext，则隐藏键盘
                ToolUtil.closeKeyBoard(this, view);
            }

            if (mViewsIgnore != null) {
                for (View v : mViewsIgnore) {
                    if (ToolUtil.IsMotionEventInView(event, v)) {
                        setAutoSwipeBackEnable(true);
                        break;
                    }
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            setAutoSwipeBackEnable(false);
        }

        return super.dispatchTouchEvent(event);
    }

    /**
     * 获取当前点击位置是否为et
     *
     * @param view  焦点所在View
     * @param event 触摸事件
     * @return
     */
    public boolean isClickEt(View view, MotionEvent event) {
        if (view != null && (view instanceof EditText)) {
            return ToolUtil.IsMotionEventInView(event, view);
        }
        return false;
    }

    public void showKeyboard(boolean isShow) {
        if (isShow)
            ToolUtil.showKeyBoard(this);
        else
            ToolUtil.closeKeyBoard(this);
    }

    protected final Handler getHandler() {
        return sHandler;
    }

    public boolean isDestroyedCompatible() {
        if (Build.VERSION.SDK_INT >= 17) {
            return isDestroyedCompatible17();
        } else {
            return destroyed || super.isFinishing();
        }
    }

    @TargetApi(17)
    private boolean isDestroyedCompatible17() {
        return super.isDestroyed();
    }

    public void setHeadBackgroundColor(View view, int argb) {
        view.getBackground().mutate().setAlpha(argb);
        view.invalidate();
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return true;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }

    @Override
    public void addListener(LifecycleListener listener) {
        if (mLifecycleListeners == null) {
            mLifecycleListeners = new ArrayList<>();
        }
        mLifecycleListeners.add(listener);
    }

    @Override
    public void removeListener(LifecycleListener listener) {
        if (mLifecycleListeners != null)
            mLifecycleListeners.remove(listener);
    }

    @Override
    public Handler getMainHandler() {
        return sHandler;
    }

    public <T extends Fragment> T findMemFragment(String tag) {
        if (getSupportFragmentManager().findFragmentByTag(tag) != null)
            return (T) getSupportFragmentManager().findFragmentByTag(tag);
        return null;
    }

    public static String MakeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    public void logI(String msg) {
        LogUtil.i(getClass().getSimpleName(), msg);
    }

    public void logD(String msg) {
        LogUtil.d(getClass().getSimpleName(), msg);
    }

    public void logW(String msg) {
        LogUtil.w(getClass().getSimpleName(), msg);
    }

    public void logE(String msg) {
        LogUtil.e(getClass().getSimpleName(), msg);
    }
}
