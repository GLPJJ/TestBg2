package simple.util.til;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import simple.config.Simple;

/**
 * Created by glp on 2016/8/29.
 */

public class BitmapUtil {

    LruCache<String, Bitmap> mMemoryCache;

    public BitmapUtil() {
        // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
        // LruCache通过构造函数传入缓存值，以KB为单位。
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 使用最大可用内存值的1/8作为缓存的大小。
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void AddBitmapToMemoryCache(int resId, int w, int h, Bitmap bitmap) {
        String key = String.format("%d_%d_%d", resId, w, h);
        AddBitmapToMemoryCache(key, bitmap);
    }

    public synchronized void AddBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (bitmap == null)
            return;

        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public synchronized Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 替换 ImageView的 setImageBitmap，setImageResource，BitmapFactory的decodeResource
     * ReadBitmapAutoSize 创建出一个bitmap，再将其设为ImageView的source，加载显示
     *
     * @param filePath
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap ReadBitmapAutoSize(String filePath, int reqWidth, int reqHeight) {
        //outWidth和outHeight是目标图片的最大宽度和高度，用作限制
        FileInputStream fs = null;
        BufferedInputStream bs = null;
        try {
            fs = new FileInputStream(filePath);
            bs = new BufferedInputStream(fs);
            BitmapFactory.Options options = SetBitmapOption(filePath, reqWidth, reqHeight);
            return BitmapFactory.decodeStream(bs, null, options);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                bs.close();
                fs.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void RecycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            System.gc();
            System.runFinalization();
        }
    }

    private static BitmapFactory.Options SetBitmapOption(String file, int reqWidth, int reqHeight) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        //设置只是解码图片的边距，此操作目的是度量图片的实际宽度和高度
        BitmapFactory.decodeFile(file, opt);

        opt.inDither = false;
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        //设置加载图片的颜色数为16bit，默认是RGB_8888，表示24bit颜色和透明通道，但一般用不上
        opt.inSampleSize = 1;
        //设置缩放比,1表示原比例，2表示原来的四分之一....
        //计算缩放比
        opt.inSampleSize = CalculateInSampleSize(opt, reqWidth, reqHeight);

        opt.inJustDecodeBounds = false;//最后把标志复原

        //设置图片的DPI为当前手机的屏幕dpi
        opt.inTargetDensity = Simple.Context.getResources().getDisplayMetrics().densityDpi;
        opt.inScaled = true;

        return opt;
    }

    private static int CalculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            int heightRatio = Math.round((float) height / (float) reqHeight);
            int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
