package com.example.administrator.testbg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

//主页
public class Main2Activity extends BaseMyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setSwipeBackEnable(false);
    }

    public void onGo(View v) {
        startActivity(new Intent(this, Main3Activity.class));
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return true;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.NONE;
    }
}
