package com.example.administrator.testbg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.example.administrator.testbg.R;

//启动页
public class MainActivity extends BaseMyActivity {

    static Handler sHandler = new Handler();
    boolean mActivie = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSwipeBackEnable(false);
    }

    @Override
    public void finish() {
        mActivie = false;
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
                finish();
            }
        }, 1000);
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return false;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.FADE;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            return true;

        return super.onKeyDown(keyCode, event);
    }
}
