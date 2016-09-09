package com.example.administrator.testbg;

import com.netease.nim.uikit.base.BaseSwipeBackActivity;

/**
 * Created by Administrator on 2016/9/9.
 */
public class BaseMyActivity extends BaseSwipeBackActivity {
    @Override
    protected boolean toggleOverridePendingTransition() {
        return true;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }
}
