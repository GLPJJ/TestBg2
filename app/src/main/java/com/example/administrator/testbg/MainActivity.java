package com.example.administrator.testbg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends BaseMyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSwipeBackEnable(false);
    }

    public void onGo(View v) {
        startActivity(new Intent(this, Main2Activity.class));
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return false;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.BOTTOM;
    }
}
