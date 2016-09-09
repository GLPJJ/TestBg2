package com.example.glp.testbg2;

import android.os.Bundle;

public class Main3Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        setSwipeBackEnable(false);
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.BOTTOM;
    }
}
