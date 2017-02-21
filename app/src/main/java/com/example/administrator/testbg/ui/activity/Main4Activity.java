package com.example.administrator.testbg.ui.activity;

import android.os.Bundle;

import com.example.administrator.testbg.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

//分页2
public class Main4Activity extends BaseMyActivity {

    @OnClick(R.id.btn)
    void onClick() {
        KeyBoardActivity.Start(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        ButterKnife.bind(this);

        setSwipeBackEnable(false);
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.BOTTOM;
    }
}
