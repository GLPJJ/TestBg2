package simple.base;

import android.os.Bundle;

import com.example.administrator.testbg.R;

public class TestActivity extends BaseSimpleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSwipeBackEnable(false);
    }
}
