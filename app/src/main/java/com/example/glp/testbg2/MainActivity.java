package com.example.glp.testbg2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.netease.nim.uikit.base.BaseAppCompatActivity;
import com.netease.nim.uikit.base.BaseSwipeBackActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSwipeBackEnable(false);
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return false;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.NONE;
    }

    public void onGo(View v){
        startActivity(new Intent(this,Main2Activity.class));
    }
}
