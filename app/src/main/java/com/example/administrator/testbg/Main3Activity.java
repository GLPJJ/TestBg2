package com.example.administrator.testbg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

//分页1
public class Main3Activity extends BaseMyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
    }

    public void onGo(View v) {
        startActivity(new Intent(this, Main4Activity.class));
    }
}
