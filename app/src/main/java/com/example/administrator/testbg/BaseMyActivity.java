package com.example.administrator.testbg;

/**
 * Created by Administrator on 2016/9/9.
 */
public class BaseMyActivity extends BaseSwipeActivity {
    @Override
    protected boolean toggleOverridePendingTransition() {
        return true;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }
}
