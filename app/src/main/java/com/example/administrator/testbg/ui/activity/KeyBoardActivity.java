package com.example.administrator.testbg.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.testbg.R;

import simple.util.til.ToolUtil;
import simple.view.KeyBackObservableEditText;


public class KeyBoardActivity extends BaseMyActivity {

    ListView list;
    KeyBackObservableEditText edit;

    boolean needChange = true;
    Button btn_face;
    Button btn_more;
    View fl;
    View face;
    View more;
    View control;

    int mOrignalBottom = 0;
    int mLastBottom = 0;
    int mCurHeight = 0;
    int mKeyBoardHeight = 0;
    boolean isBottonShow = false;
    boolean mPendingShowPlaceHolder = false;

    public static void Start(Activity activity) {
        Start(activity, -1);
    }

    public static void Start(Activity activity, int req) {
        if (activity == null)
            return;

        Intent intent = new Intent(activity, KeyBoardActivity.class);
        activity.startActivityForResult(intent, req);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_board);

        list = (ListView) findViewById(R.id.list);
        edit = (KeyBackObservableEditText) findViewById(R.id.edit);
        btn_face = (Button) findViewById(R.id.btn_face);
        btn_more = (Button) findViewById(R.id.btn_more);
        fl = findViewById(R.id.fl);
        face = findViewById(R.id.fl_face);
        more = findViewById(R.id.fl_more);
        control = findViewById(R.id.ll_control);

        list.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 40;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView txt = new TextView(KeyBoardActivity.this);
                txt.setPadding(32, 32, 32, 32);
                txt.setText("Text " + position);
                return txt;
            }
        });

        list.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom
                    , int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                Log.e("MessageFragment", "left=" + left
//                        + ",top=" + top
//                        + ",right=" + right
//                        + ",bottom=" + bottom
//                        + ",oldLeft=" + oldLeft
//                        + ",oldTop=" + oldTop
//                        + ",oldRight=" + oldRight
//                        + ",oldBottom=" + oldBottom);
//
//                Log.e("MessageFragment", "OrignalBottom=" + mOrignalBottom + ",mLastBottom=" + mLastBottom);
                if (mOrignalBottom != 0) {
                    mCurHeight = mOrignalBottom - bottom;//记录当前高度

                    if (bottom > mLastBottom && bottom >= mOrignalBottom) {//底边工具收起
                        isBottonShow = false;
                    } else if (bottom < mLastBottom && mLastBottom >= mOrignalBottom) {//底边工具展开

                        if (mKeyBoardHeight == 0) {
                            mKeyBoardHeight = mCurHeight;
                        }

                        isBottonShow = true;
                    }
                }

                //第一次初始化Bottom，这里针对聊天界面做的优化，如果有其他界面，不合适
                if (oldBottom == 0 && mOrignalBottom == 0) {
                    mOrignalBottom = bottom;
                }

                mLastBottom = bottom;
            }
        });

        //这里监听了光标的描绘
        edit.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // Keyboard -> PlaceHolder
                boolean ret;
                if (mPendingShowPlaceHolder) {
                    // 在设置mPendingShowPlaceHolder时已经调用了隐藏Keyboard的方法，直到Keyboard隐藏前都取消重绘
                    if (isKeyboardVisible()) {
                        ret = false;
                    } else { // 键盘已隐藏，显示PlaceHolder
                        showPlaceHolder(true);
                        mPendingShowPlaceHolder = false;
                        ret = true;
                    }
                } else {
                    boolean isHolderVisible = isPlaceHolderVisible();
                    boolean isKeyVisible = isKeyboardVisible();

                    if (isHolderVisible && isKeyVisible) {
                        showPlaceHolder(false);
                        ret = false;
                    } else if (isKeyVisible) {

                        if (needChange) {
                            changeFaceState(false);
                            changeMoreState(false);
                            needChange = false;
                        }

                        ret = true;
                    } else {
                        ret = true;
                    }
                }

                return ret;
            }
        });

        btn_face.setOnClickListener(clickListener);
        btn_more.setOnClickListener(clickListener);
    }

    void changeFaceState(boolean isKey) {
        if (isKey) {
            btn_face.setText("键盘");
        } else {
            btn_face.setText("蓝色");
        }
    }

    void changeMoreState(boolean isKey) {
        if (isKey) {
            btn_more.setText("键盘");
        } else {
            btn_more.setText("绿色");
        }
    }

    void showFaceKey() {
        face.setVisibility(View.VISIBLE);
        more.setVisibility(View.GONE);

        changeFaceState(true);
        changeMoreState(false);
        needChange = true;
    }

    void showMoreKey() {
        face.setVisibility(View.GONE);
        more.setVisibility(View.VISIBLE);

        changeFaceState(false);
        changeMoreState(true);
        needChange = true;
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isPlaceHolderVisible()) {
                if (face.getVisibility() == View.VISIBLE && view.getId() == R.id.btn_more) {
                    showMoreKey();
                    return;
                } else if (more.getVisibility() == View.VISIBLE && view.getId() == R.id.btn_face) {
                    showFaceKey();
                    return;
                }

                changeFaceState(false);
                changeMoreState(false);
                needChange = false;

                showSoftInput(edit);
            } else {
                if (view.getId() == R.id.btn_more) {
                    showMoreKey();
                } else if (view.getId() == R.id.btn_face) {
                    showFaceKey();
                }

                if (isKeyboardVisible()) {
                    mPendingShowPlaceHolder = true;
                    hideSoftInput(edit);
                } else {
                    showPlaceHolder(true);
                }
            }
        }
    };

    boolean isPlaceHolderVisible() {
        return fl.getVisibility() == View.VISIBLE;
    }

    int mWindowY = 0;
    boolean isFirst = true;

    boolean isKeyboardVisible() {
        if (isFirst || mWindowY == 0) {//计算默认高度位置
            int[] pos = new int[2];
            control.getLocationOnScreen(pos);

            isFirst = false;
            mWindowY = pos[1] + control.getHeight();//计算该view的底部高度
        }

        int[] pos = new int[2];
        control.getLocationOnScreen(pos);

        int curY = pos[1] + control.getHeight();//当前view的底部高度

        int curYAdd = curY + (int)(ToolUtil.Dp2Px(240));
        Log.i("MainActivity", "mWindowY=" + mWindowY + ",curY=" + curY + ",curYAdd=" + curYAdd
                + (isPlaceHolderVisible() ? ",placeholder visible" : ",placeholder invisible"));
        if ((curY < mWindowY && !isPlaceHolderVisible())
                || ((curYAdd < mWindowY) && isPlaceHolderVisible())) {
            return true;
        }
        return false;

        //return (!isPlaceHolderVisible() && isBottonShow) || (isPlaceHolderVisible() && isBottonShow && mCurHeight>mKeyBoardHeight);
    }

    void showPlaceHolder(boolean show) {
        fl.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    void showSoftInput(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
