package com.example.administrator.testbg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Main2Activity extends BaseMyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void onGo(View v) {
        startActivity(new Intent(this, Main3Activity.class));
    }
}
