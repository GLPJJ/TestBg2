package com.example.glp.testbg2;

import android.os.Bundle;

import com.netease.nim.uikit.base.BaseSwipeBackActivity;

public class BaseActivity extends BaseSwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return true;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }

}
