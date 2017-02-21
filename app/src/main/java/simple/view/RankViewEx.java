package simple.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.example.administrator.testbg.R;

/**
 * Created by glp on 2016/10/27.
 */

public class RankViewEx extends AppCompatTextView {

    //前三名的draw ID
    Drawable mFirstImg;
    Drawable mSecondImg;
    Drawable mThirdImg;
    //普通名次的背景图片
    Drawable mComImg;
    int mRank;//名次

    public RankViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RankViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    void init(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs
                , R.styleable.RankViewEx, 0, 0);
        try {
            mFirstImg = a.getDrawable(R.styleable.RankViewEx_firstImg);
            mSecondImg = a.getDrawable(R.styleable.RankViewEx_secondImg);
            mThirdImg = a.getDrawable(R.styleable.RankViewEx_thirdImg);
            mComImg = a.getDrawable(R.styleable.RankViewEx_comImg);
            mRank = a.getInt(R.styleable.RankViewEx_rank, 0);
        } finally {
            a.recycle();
        }

        if (mFirstImg == null)
            mFirstImg = getResources().getDrawable(R.mipmap.record_detail_sng_rank_1);
        if (mSecondImg == null)
            mSecondImg = getResources().getDrawable(R.mipmap.record_detail_sng_rank_2);
        if (mThirdImg == null)
            mThirdImg = getResources().getDrawable(R.mipmap.record_detail_sng_rank_3);
        if (mComImg == null)
            mComImg = getResources().getDrawable(R.drawable.match_rank_common_bg);

        setRank(mRank);
    }

    public void setRankTagView(int rank) {
        setRank(rank);
    }

    public void setRank(int rank) {
        if (rank <= 0)
            return;
        else if (rank == 1) {
            setRankDrawable(mFirstImg);
        } else if (rank == 2) {
            setRankDrawable(mSecondImg);
        } else if (rank == 3) {
            setRankDrawable(mThirdImg);
        } else {
            setScaleX(1.0f);
            setScaleY(1.0f);
            setText(String.valueOf(rank));
            setBackgroundDrawable(mComImg);
        }
    }

    private void setRankDrawable(Drawable drawable) {
        if (drawable == null)
            return;

        setText("");
        setBackgroundDrawable(drawable);
        setScaleX(0.8f);
        setScaleY(0.8f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
}
