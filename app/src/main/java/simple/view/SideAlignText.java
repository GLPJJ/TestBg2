package simple.view;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;

/**
 * Created by glp on 2016/11/23.
 */

public class SideAlignText extends android.support.v7.widget.AppCompatTextView {
    boolean isNeedSide = true;

    public SideAlignText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideAlignText(Context context) {
        super(context);
    }

    public SideAlignText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            //计算是否需要两端对齐
            if (getWidth() <= getPaint().measureText(getText().toString()))
                isNeedSide = false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //拓展测试
        //TextPaint paint = getPaint();
        //paint.setColor(getCurrentTextColor());
        //paint.drawableState = getDrawableState();
        //canvas.drawText("111g", 0, 0, paint);
        //canvas.drawText("555g", 0, getPaddingTop()+getTextSize(), paint); //*****
        //canvas.drawText("666g", 0, getHeight(), paint);
        //canvas.drawText("777g", 0, getHeight() - getTextSize(), paint);
        //canvas.drawText("888g", 0, firstHeight, paint);

        String line = getText().toString();
        if (isNeedSide && line.length() > 1) {

            TextPaint paint = getPaint();
            paint.setColor(getCurrentTextColor());
            paint.drawableState = getDrawableState();

            int count = line.length() - 1;
            int separate = getMeasuredWidth() / count;


            for (int j = 0; j < line.length(); j++) {
                CharSequence subStr = line.subSequence(j, j + 1);
                float drawX = separate * j;
                if (j == 0) {//不处理
                } else if (j < line.length() - 1)//
                    drawX = drawX - paint.measureText(subStr.toString()) / 2;
                else
                    drawX = drawX - paint.measureText(subStr.toString());

                canvas.drawText(subStr.toString(), drawX, getPaddingTop() + getTextSize(), paint);
            }
        } else {
            super.onDraw(canvas);
        }

    }
}
