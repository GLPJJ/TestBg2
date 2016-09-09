package com.example.glp.testbg2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Main2Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void onGo(View v) {
        startActivity(new Intent(this, Main3Activity.class));
    }
}
