package com.example.administrator.testbg;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;


/**
 * Created by glp on 2016/9/10.
 */

public abstract class BaseMy2Activity extends AppCompatActivity implements SwipeBackActivityBase {

    private SwipeBackActivityHelper mHelper;

    /**
     * overridePendingTransition mode
     */
    public enum TransitionMode {
        LEFT, RIGHT, TOP, BOTTOM, SCALE, FADE, NONE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (toggleOverridePendingTransition()) {
            switch (getOverridePendingTransitionMode()) {
                case LEFT:
                    overridePendingTransition(com.netease.nim.uikit.R.anim.left_in, com.netease.nim.uikit.R.anim.left_out);
                    break;
                case RIGHT:
                    overridePendingTransition(com.netease.nim.uikit.R.anim.right_in, com.netease.nim.uikit.R.anim.right_out);
                    break;
                case TOP:
                    overridePendingTransition(com.netease.nim.uikit.R.anim.top_in, com.netease.nim.uikit.R.anim.top_out);
                    break;
                case BOTTOM:
                    overridePendingTransition(com.netease.nim.uikit.R.anim.bottom_in, com.netease.nim.uikit.R.anim.bottom_out);
                    break;
                case SCALE:
                    overridePendingTransition(com.netease.nim.uikit.R.anim.scale_in, com.netease.nim.uikit.R.anim.scale_out);
                    break;
                case FADE:
                    overridePendingTransition(com.netease.nim.uikit.R.anim.fade_in, com.netease.nim.uikit.R.anim.fade_out);
                    break;
            }
        }
        super.onCreate(savedInstanceState);

        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHelper = null;
    }

    @Override
    public Resources getResources() {
        //设置资源不跟随系统的大小变化
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();

        try {//这里会崩溃，就是设置失败
            res.updateConfiguration(config, res.getDisplayMetrics());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    @Override
    public void finish() {
        super.finish();
        if (toggleOverridePendingTransition()) {
            switch (getOverridePendingTransitionMode()) {
                case LEFT:
                    overridePendingTransition(com.netease.nim.uikit.R.anim.left_in, com.netease.nim.uikit.R.anim.left_out);
                    break;
                case RIGHT:
                    overridePendingTransition(com.netease.nim.uikit.R.anim.right_in, com.netease.nim.uikit.R.anim.right_out);
                    break;
                case TOP:
                    overridePendingTransition(com.netease.nim.uikit.R.anim.top_in, com.netease.nim.uikit.R.anim.top_out);
                    break;
                case BOTTOM:
                    overridePendingTransition(com.netease.nim.uikit.R.anim.bottom_in, com.netease.nim.uikit.R.anim.bottom_out);
                    break;
                case SCALE:
                    overridePendingTransition(com.netease.nim.uikit.R.anim.scale_in, com.netease.nim.uikit.R.anim.scale_out);
                    break;
                case FADE:
                    overridePendingTransition(com.netease.nim.uikit.R.anim.fade_in, com.netease.nim.uikit.R.anim.fade_out);
                    break;
                case NONE:
                    overridePendingTransition(0, 0);
                    break;
            }
        }
    }

    /**
     * toggle overridePendingTransition
     *
     * @return
     */
    protected abstract boolean toggleOverridePendingTransition();

    /**
     * get the overridePendingTransition mode
     */
    protected abstract TransitionMode getOverridePendingTransitionMode();

}
