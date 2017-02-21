package simple.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.administrator.testbg.R;

import java.util.List;

import simple.bean.BannerEy;
import simple.config.Simple;
import simple.util.til.ToolUtil;
import simple.util.tools.LogUtil;
import simple.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * Created by zjy on 2015/8/13.
 */
public class BannerView extends RelativeLayout {
    final String TAG = "BannerView";
    View view;
    Activity activity;
    AutoScrollViewPager vp_banner;
    List<BannerEy> bannerList;
    DotView mDotView;

    BannerPagerAdapter mBannerPagerAdapter = null;
    /**
     * Banner轮播时间间隔
     */
    private static final int BANNER_INTERVAL = 5000;

    public BannerView(Context context) {
        super(context);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.view_banner, null);
        vp_banner = (AutoScrollViewPager) view.findViewById(R.id.vp_banner);
        mDotView = (DotView) view.findViewById(R.id.dotview);
        addView(view);
    }

    public void setBannerInfo(Activity activity, List<BannerEy> bannerList) {
        this.activity = activity;
        this.bannerList = bannerList;
        initAutoScrollViewPager();
    }

    public void startAutoScroll() {
        vp_banner.startAutoScroll(BANNER_INTERVAL);
    }

    private void initAutoScrollViewPager() {
        if (mBannerPagerAdapter == null) {
            mBannerPagerAdapter = new BannerPagerAdapter();
        }
        vp_banner.setAdapter(mBannerPagerAdapter);
        if (bannerList != null && bannerList.size() > 1) {
            vp_banner.setScrollFactgor(bannerList.size() + 1);
            vp_banner.stopAutoScroll();
        }
        vp_banner.setOffscreenPageLimit(1);
        vp_banner.setOnPageClickListener(new AutoScrollViewPager.OnPageClickListener() {
            @Override
            public void onPageClick(AutoScrollViewPager autoScrollPager, int position) {
                BannerEy banner = bannerList.get(position);
                //前往页面
                Simple.Config.goWebview(getContext(), banner.title, banner.url);
            }
        });
        initPageIndicator();
    }

    /**
     * Banner对应的PagerAdapter
     */
    public class BannerPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return getBannerSize();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BannerEy bannerEntity = bannerList.get(position);
            View view_item = LayoutInflater.from(getContext()).inflate(R.layout.list_banner_item, container, false);
            ImageView image = (ImageView) view_item.findViewById(R.id.img_banner);

            if (bannerEntity.local) {
                image.setImageResource(bannerEntity.resId);
            } else {
                //网络异步加载图片。
                Glide.with(activity)//保持跟Activity生命周期一致
                        .load(bannerEntity.image)
                        .placeholder(R.mipmap.banner_loading)//默认图片
                        .crossFade()
                        .into(image);
            }
            container.addView(view_item);//必须这样。
            return view_item;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private void initPageIndicator() {
        int size = getBannerSize();
        if (size <= 1)
            mDotView.setVisibility(View.GONE);
        else {
            mDotView.setDotCount(size);
            mDotView.setSelection(0);

            vp_banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    mDotView.setSelection(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            vp_banner.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            if (mOnBannerTouchListener != null) {
                                mOnBannerTouchListener.onTouch(true);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            if (mOnBannerTouchListener != null) {
                                mOnBannerTouchListener.onTouch(false);
                            }
                            break;
                    }
                    return false;
                }
            });
        }
    }

    /**
     * 获取Banner的数量
     *
     * @return
     */
    public int getBannerSize() {
        return bannerList != null ? bannerList.size() : 0;
    }

    /**
     * 设置Banner的LayoutParams
     *
     * @param context
     * @param view
     */
    public static void setBannerLayoutParams(View view) {
        int width = ToolUtil.GetScreenWidth();
        //5:3
        //设置IMA的初始化大小
        int img_width = width;
        int img_height = (int) (Float.valueOf(img_width) / 360 * 203);
        ViewGroup.LayoutParams img_layout = view.getLayoutParams();
        img_layout.height = img_height;
        img_layout.width = img_width;
        LogUtil.d("banner", "img_height :" + img_height + ";img_width :" + img_width);
        view.setLayoutParams(img_layout);
    }

    public void onResume() {
        if (vp_banner != null && bannerList != null && getBannerSize() > 1) {
            vp_banner.startAutoScroll(BANNER_INTERVAL);
        }
    }

    public void onPause() {
        if (vp_banner != null && bannerList != null && getBannerSize() > 1) {
            vp_banner.stopAutoScroll();
        }
    }

    /**
     * Banner的ViewPager点击监听，避免和SwipeRefreshLayout发生冲突
     */
    public interface OnBannerTouchListener {
        public void onTouch(boolean isTouch);
    }

    OnBannerTouchListener mOnBannerTouchListener;

    public void setOnBannerTouchListener(OnBannerTouchListener listener) {
        this.mOnBannerTouchListener = listener;

    }
}
