package simple.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * 始终获取焦点的TextView，用于跑马灯
 */
public class FocusTextVuew extends AppCompatTextView {
    public FocusTextVuew(Context context) {
        super(context);
    }

    public FocusTextVuew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusTextVuew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
//        return super.isFocused();
        return true;
    }
}


