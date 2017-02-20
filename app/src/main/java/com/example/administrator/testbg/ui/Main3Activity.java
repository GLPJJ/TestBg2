package com.example.administrator.testbg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.administrator.testbg.R;

//分页1
public class Main3Activity extends BaseMyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        setSwipeBackEnable(false);
    }

    public void onGo(View v) {
        startActivity(new Intent(this, Main4Activity.class));
    }
}
