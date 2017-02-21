package simple.view.window;

import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * Created by glp on 2016/11/26.
 * <p>
 * 本Popupwindow 只支持传具体参数，不接受 match_parent,wrap_content
 * 不然popwindow显示会有问题！！！
 */

public class CustomPopupWindow extends PopupWindow {

    public static CustomPopupWindow CreatePopup(@NonNull PopupInflater inflater) {
        CustomPopupWindow pop = new CustomPopupWindow(inflater.onInflater());
        pop.initPop(inflater.isTouchOutside(), inflater.getLayoutParams());
        return pop;
    }

    private CustomPopupWindow(View contentView) {
        super(contentView);
    }

    public void initPop(boolean outside, ViewGroup.LayoutParams layoutParams) {
        //获取popwindow焦点
        //setFocusable(true);
        //设置popwindow如果点击外面区域，便关闭。
        setOutsideTouchable(outside);
        setBackgroundDrawable(new ColorDrawable(0));
//        setWidth(BaseTools.dip2px(getContentView().getContext(), 160));
//        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(layoutParams.width);
        setHeight(layoutParams.height);
    }

    public void showDownUtil(View anchor, int xoff, int yoff) {
//        int[] location = new int[2];
//        anchor.getLocationOnScreen(location);
//        LogUtil.e("CustomPopupWindow", "x:" + location[0] + ",y:" + location[1]
//                + ",width:" + getWidth() + ",height:" + getHeight()
//                + ",content.w:" + getContentView().getWidth()
//                + ",content.h:" + getContentView().getHeight());

        showAsDropDown(anchor, xoff, yoff);
    }

    public interface PopupInflater {
        @NonNull
        View onInflater();

        boolean isTouchOutside();

        /**
         * 不要传 match_parent wrap_content 不便于计算popwindow显示的位置
         *
         * @return
         */
        @NonNull
        ViewGroup.LayoutParams getLayoutParams();
    }
}
