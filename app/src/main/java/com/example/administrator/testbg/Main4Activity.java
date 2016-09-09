package com.example.administrator.testbg;

import android.os.Bundle;

import com.netease.nim.uikit.base.BaseAppCompatActivity;

//分页2
public class Main4Activity extends BaseMyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        //setSwipeBackEnable(false);
    }

    @Override
    protected BaseAppCompatActivity.TransitionMode getOverridePendingTransitionMode() {
        return BaseAppCompatActivity.TransitionMode.BOTTOM;
    }
}
