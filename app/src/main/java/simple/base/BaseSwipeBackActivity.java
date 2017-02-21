package simple.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/**
 * Created by glp on 2017/2/17.
 */

public abstract class BaseSwipeBackActivity extends BaseAppCompatActivity implements SwipeBackActivityBase {
    private SwipeBackActivityHelper mHelper;

    protected boolean mIsSwipeBack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHelper = null;
    }

    @Override
    public Resources getResources() {
        //设置资源不跟随系统的大小变化
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();

        try {//这里会崩溃，就是设置失败
            res.updateConfiguration(config, res.getDisplayMetrics());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return res;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        return super.findViewById(id);
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        mIsSwipeBack = enable;
        getSwipeBackLayout().setEnableGesture(enable);
    }

    public void setAutoSwipeBackEnable(boolean isFilter) {
        if (mIsSwipeBack) {
            getSwipeBackLayout().setEnableGesture(!isFilter);
        }
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }
}
