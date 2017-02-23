package simple.util.til;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import simple.config.Simple;
import simple.util.tools.DateTools;
import simple.util.tools.LogUtil;


/**
 * @version 创建时间：2013-10-21 下午4:12:39
 *          类说明
 */
public class ToolUtil {

    public static final String TAG = "ToolUtil";

    public static Locale s_timeStyle = Locale.CHINA;

    ///////////////////////////////////////字符处理/////////////////////////////////////////////////////////////

    /**
     * 没有表情的过滤
     */
    public class NoEmojiInputFilter implements InputFilter {

        private final String reg = "[^a-zA-Z0-9\u4E00-\u9FA5_]";

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern pattern = Pattern.compile(reg);
            if (source.equals(" ")) {
                return source;
            }
            //正则匹配是否是表情符号
            Matcher matcher = pattern.matcher(source.toString());
            if (!matcher.matches()) {
                //不包含表情
                return source;
            }
            return "";
        }
    }

    /**
     * 名称规则
     * 支持1.中英文 2.特殊字符 3.空格 4 ' 点号
     */
    public static class NameRuleFilter implements InputFilter {
        private final static String TAG = "NameRuleFilter";
        private final String reg = "^['A-Za-z0-9\\u4e00-\\u9fa5\\x20\\x5f\\x09]*$";

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            //越南版本不做过滤判断
            if (Simple.Config.isTextNeedFilter()) {
                Pattern pattern = Pattern.compile(reg);
                Matcher matcher = pattern.matcher(source.toString());
                if (matcher.matches()) {
                    return source;
                }
            } else {
                return source;
            }
            return "";
        }
    }

    /**
     * 输入过滤器，没有空格和回车
     */
    public static class NoSpaceAndEnterInputFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (/*source.equals(" ") || */source.toString().indexOf("\n") != -1) { // for backspace
                return "";
            }
            return source;
        }
    }

    //限制字符数量输入，汉字算2个字符，英文算1个
    public static class ChineseLengthFilter implements InputFilter {
        private final int mMax;

        public ChineseLengthFilter(int max) {
            mMax = max;
        }

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                   int dstart, int dend) {
            int addCharLen = countWords(source.toString());
            int keep = mMax - (countWords(dest.toString()) - (dend - dstart));
            if (keep <= 0) {
                return "";
            } else if (keep >= addCharLen) {
                return null; // keep original
            } else if (source.length() == addCharLen) {//说明全是英文
                keep += start;
                return source.subSequence(start, keep);
            } else if (source.length() * 2 == addCharLen) {//说明全是中文
                keep = start + keep / 2;
                return source.subSequence(start, keep);
            } else {//既有中文，又有英文,一个一个判断
                StringBuffer sb = new StringBuffer();

                for (int i = 0; i < source.length(); i++) {
                    char c = source.charAt(i);
                    int len = isChinese(c) ? 2 : 1;
                    keep -= len;
                    if (keep < 0)
                        break;
                    sb.append(c);
                }
                return sb.toString();
            }
        }

        /**
         * @return the maximum length enforced by this input filter
         */
        public int getMax() {
            return mMax;
        }

    }

    /**
     * 编辑框计算字符数量
     *
     * @param str
     * @return
     */
    private static int countWords(String str) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str.trim())) {
            return 0;
        }
        int len = 0;
        char c;
        for (int i = str.length() - 1; i >= 0; i--) {
            c = str.charAt(i);
            if (isChinese(c)) {
                len += 2;
            } else {
                len++;
            }
        }
        return len;
    }

    /**
     * 判断是否是中文字符
     * 根据Unicode编码完美的判断中文汉字和符号
     *
     * @param c
     * @return
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS //【4E00-9FFF】 CJK Unified Ideographs 中日韩统一表意文字
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS //【F900-FAFF】 CJK Compatibility Ideographs 中日韩兼容表意文字
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A  //【3400-4DBF】 CJK Unified Ideographs Extension A 中日韩统一表意文字扩充A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B //【3400-4DBF】 CJK Unified Ideographs Extension B 中日韩统一表意文字扩充B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION //【3000-303F】 CJK Symbols and Punctuation 中日韩符号和标点
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS //【FF00-FFEF】 Halfwidth and Fullwidth Forms 半角及全角字符
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {//【2000-206F】 General Punctuation 一般标点符号
            return true;
        }
        return false;
    }


    ////////////////////////////////////////算法加密////////////////////////////////////////////////////////////

    /**
     * md5加密
     *
     * @param plainText
     * @return
     */
    public static String GetMd5(String plainText) {
        // 返回字符串
        String md5Str = "";

        if (plainText == null || TextUtils.isEmpty(plainText))
            return md5Str;

        try {
            // 操作字符串
            StringBuffer buf = new StringBuffer();
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 添加要进行计算摘要的信息,使用 plainText 的 byte 数组更新摘要。
            md.update(plainText.getBytes());

            // 计算出摘要,完成哈希计算。
            byte b[] = md.digest();
            int i;

            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");

                // 将整型 十进制 i 转换为16位，用十六进制参数表示的无符号整数值的字符串表示形式。
                buf.append(Integer.toHexString(i));
            }

            // 32位的加密
            md5Str = buf.toString();

            // 16位的加密
            // md5Str = buf.toString().md5Strstring(8,24);

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return md5Str;
    }

    //SHA1 加密实例
    public static String GetSha1(String info) {
        byte[] digesta = null;
        try {
            // 得到一个SHA-1的消息摘要(创建具有指定算法名称的信息摘要)
            MessageDigest alga = MessageDigest.getInstance("SHA-1");
            // 添加要进行计算摘要的信息
            alga.update(info.getBytes());
            // 得到该摘要
            digesta = alga.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // 将摘要转为字符串
        String rs = byte2hex(digesta);
        return rs;
    }

    private static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }

    /**
     * 获得Hash码
     *
     * @param fileName
     * @param hashType
     * @return
     * @throws Exception
     */
    public static String GetHash(String fileName, String hashType)
            throws Exception {
        InputStream fis;
        fis = new FileInputStream(fileName);
        byte[] buffer = new byte[1024];
        MessageDigest md5 = MessageDigest.getInstance(hashType);
        int numRead = 0;
        while ((numRead = fis.read(buffer)) > 0) {
            md5.update(buffer, 0, numRead);
        }
        fis.close();
        return toHexString(md5.digest());
    }

    private static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    private static char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    ////////////////////////////////////////设备相关////////////////////////////////////////////////////////////

    /**
     * 获取IMEI
     *
     * @return
     */
    public static String GetIMEI() {
        String res = ((TelephonyManager) Simple.Context
                .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (TextUtils.isEmpty(res)) {
            return "";
        }
        return res;
    }

    /**
     * 获取MacAddress,当取不到时取IMEI码
     *
     * @return
     */
    public static String GetMacAddress() {
        try {
            //谷歌服务不可用等等因素
            return AdvertisingIdClient.getAdvertisingIdInfo(Simple.Context).getId();
        } catch (Throwable e) {
            e.printStackTrace();

            String macAddress = ((WifiManager) Simple.Context.getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                    .getConnectionInfo().getMacAddress();
            if (TextUtils.isEmpty(macAddress))
                return GetIMEI();
            else
                return macAddress.replaceAll(":", "").trim();
        }
    }

    public static String GetDeviceId() {
        String id = "";
        try {
            File file = new File(AppDirUtil.getOtherPath() + "/" + "other");
            if (!file.exists() || file.isDirectory()) {

                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(GetMd5(GetMacAddress() + String.valueOf(DateTools.getCurrentTime())).getBytes());
                fos.flush();
                fos.close();

            }

            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            id = br.readLine();
            br.close();
            fis.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return id;
    }

    ////////////////////////////////////////键盘相关////////////////////////////////////////////////////////////

    public static boolean isKeyBoardOpen(Activity con, View view) {
        InputMethodManager imm = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive(view);
    }

    public static boolean isKeyBoardOpen(Activity con) {
        InputMethodManager imm = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    /**
     * 关闭键盘
     *
     * @param activity
     */
    public static void closeKeyBoard(Activity activity) {
        closeKeyBoard(activity, null);
    }

    public static void closeKeyBoard(Activity activity, View view) {
        if (activity == null)
            return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view == null) {
            view = activity.getCurrentFocus();
            if (view == null)
                return;
        }
        IBinder ibinder = view.getWindowToken();
        if (ibinder != null)
            imm.hideSoftInputFromWindow(ibinder, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * 显示软键盘
     *
     * @param activity
     */
    public static void showKeyBoard(Activity activity) {
        showKeyBoard(activity, null, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void showKeyBoard(Activity activity, View view) {
        showKeyBoard(activity, view, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void showKeyBoard(Activity activity, View view, int flag) {
        if (activity == null)
            return;

        if (view == null) {
            view = activity.getCurrentFocus();
            if (view == null)
                return;
        }

        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, flag);//flag = 0
    }

    ////////////////////////////////////////界面UI相关////////////////////////////////////////////////////////////
    public static String GetString(@StringRes int resId) {
        return Simple.Context.getString(resId);
    }

    public static String GetString(@StringRes int resId, Object... formatArgs) {
        return Simple.Context.getString(resId, formatArgs);
    }

    public static int GetColor(@ColorRes int resId) {
        return ContextCompat.getColor(Simple.Context, resId);
    }

    public static int GetDimen(@DimenRes int resId) {
        return Simple.Context.getResources().getDimensionPixelSize(resId);
    }

    public static int GetStatusHeight() {
        /**
         * 获取状态栏高度
         * */
        int statusBarHeight1 = 0;
        //获取status_bar_height资源的ID
        int resourceId = Simple.Context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = Simple.Context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }

    public static int GetScreenWidth() {
        DisplayMetrics displayMetrics = GetDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int GetScreenHeight() {
        DisplayMetrics displayMetrics = GetDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static DisplayMetrics GetDisplayMetrics() {
        WindowManager wm = (WindowManager) (Simple.Context.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dMetrics = new DisplayMetrics();
        if (wm != null && wm.getDefaultDisplay() != null) {
            wm.getDefaultDisplay().getMetrics(dMetrics);
        }
        return dMetrics;
    }

    public static int Dp2Px(float dipValue) {
        if (Simple.Context != null) {
            final float scale = Simple.Context.getResources().getDisplayMetrics().density;
            return (int) (dipValue * scale + 0.5f);
        } else {
            return (int) (dipValue * 2 + 0.5f);
        }
    }

    public static int Px2Dp(float pxValue) {
        final float scale = Simple.Context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 似乎 setTextSize，自带转换文字大小
     *
     * @param pxValue
     * @return
     */
    @Deprecated
    public static int Px2Sp(float pxValue) {
        final float fontScale = Simple.Context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 似乎 setTextSize，自带转换文字大小
     *
     * @param sp
     * @return
     */
    @Deprecated
    public static int Sp2Px(float sp) {
        final float fontScale = Simple.Context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }

    public static boolean IsMotionEventInView(MotionEvent ev, View view) {
        return IsPointInView(ev.getRawX(), ev.getRawY(), view);
    }

    public static boolean IsPointInView(float x, float y, View view) {
        if (view == null)
            return false;


        int[] pos = new int[2];
        view.getLocationOnScreen(pos);
        int viewX = pos[0];
        int viewY = pos[1];

        //Log.i("xxx", "Event x=" + x + ",y=" + y);
        //Log.i("xxx", "View x=" + viewX + ",y=" + viewY + ",w=" + view.getWidth() + ",h=" + view.getHeight());
        if (x < viewX || x > (viewX + view.getWidth()) || y < viewY || y > (viewY + view.getHeight())) {
            return false;
        }
        return true;
    }

    ////////////////////////////////////////其他////////////////////////////////////////////////////////////

    /**
     * 显示内存
     *
     * @param context
     */
    public static void displayMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        LogUtil.i("memory", "系统剩余内存:" + (info.availMem >> 10) + "k");
        LogUtil.i("memory", "系统是否处于低内存运行：" + info.lowMemory);
        LogUtil.i("memory", "当系统剩余内存低于" + info.threshold + "时就看成低内存运行");
    }

    public static void clearCookie(Context context) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    /**
     * 取消通知栏的通知
     *
     * @param context
     * @param notifyId
     */
    public static void cancelNotification(Context context, int notifyId) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notifyId);
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public static boolean isAppOnForeground(Activity activity) {
        ActivityManager activityManager = (ActivityManager) activity.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = activity.getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 复制文件，检查SD卡存在与否在外层做
     */
    @SuppressWarnings("resource")
    public static boolean copyFile(String oldPath, String newPath) {
        File oldFile = new File(oldPath);
        if (!oldFile.exists()) {
            return false;
        }

        File newFile = new File(newPath);
        if (newFile.exists()) {
            newFile.delete();
        }

        //开始复制文件
        FileInputStream in = null;
        FileOutputStream out = null;
        FileChannel inC = null;
        FileChannel outC = null;
        try {
            in = new FileInputStream(oldFile);
            out = new FileOutputStream(newFile);
            inC = in.getChannel();
            outC = out.getChannel();
            int length = 1024;
            while (true) {
                if (inC.position() >= inC.size()) {
                    inC.close();
                    outC.close();
                    in.close();
                    out.close();
                    return true;
                }

                if ((inC.size() - inC.position()) < 1024)
                    length = (int) (inC.size() - inC.position());
                else
                    length = 1024;

                inC.transferTo(inC.position(), length, outC);
                inC.position(inC.position() + length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            inC.close();
            outC.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static float StrVerToFloat(String strVer) {
        float ver = 0.0f;
        if (strVer != null) {
            String str = "";
            char Char;
            boolean isPoint = false;
            for (int i = 0; i < strVer.length(); ++i) {
                Char = strVer.charAt(i);
                if ((Char >= '0' && Char <= '9'))
                    str += Char;
                else if (!isPoint && Char == '.') {
                    isPoint = true;
                    str += Char;
                }
            }
            try {
                return Float.parseFloat(str);
            } catch (Throwable e) {
            }
        }
        return ver;
    }

    /**
     * 匹配登陆口令是否合规
     *
     * @param key
     * @return true 正确， false 反之
     */
    public static boolean matcherLoginKey(String key) {
        try {
            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(key.trim())) {
                return false;
            }
            //匹配由数字和26个英文字母组成的字符串
            Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
            Matcher matcher = pattern.matcher(key);
            if (matcher.find() && key.length() == 6) {
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * @param con
     * @param fileName
     * @param paramName
     * @return
     */
    public static String[] getSharedPreferencesParam(Context con, String fileName, String[] paramName) {
        int size = paramName.length;
        String param[] = new String[size];
        SharedPreferences sp = con.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        for (int i = 0; i < size; i++) {
            param[i] = sp.getString(paramName[i], null);
        }
        return param;
    }

    /**
     * paramName的数组长度和 data的数组长度保持一致，并且两者数据要一一对应
     *
     * @param con
     * @param fileName
     * @param paramName
     * @param data
     */
    public static void saveSharedPreferencesParam(Context con, String fileName, String[] paramName, String[] data) {
        int size = paramName.length;
        SharedPreferences sp = con.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        for (int i = 0; i < size; i++) {
            sp.edit().putString(paramName[i], data[i]).commit();
        }
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String GetMetaValue(String name) {
        String value = null;
        try {
            ApplicationInfo ai = Simple.Context.getPackageManager().getApplicationInfo(Simple.Context.getPackageName()
                    , PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            value = bundle.getString(name);//获取友盟的配置渠道
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String GetNetworkInfo(Context context) {
        String info = "";
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetInfo = connectivity.getActiveNetworkInfo();
            if (activeNetInfo != null) {
                if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    info = activeNetInfo.getTypeName();
                } else {
                    StringBuilder sb = new StringBuilder();
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    sb.append(activeNetInfo.getTypeName());
                    sb.append(" [");
                    if (tm != null) {
                        // Result may be unreliable on CDMA networks
                        sb.append(tm.getNetworkOperatorName());
                        sb.append("#");
                    }
                    sb.append(activeNetInfo.getSubtypeName());
                    sb.append("]");
                    info = sb.toString();
                }
            }
        }
        return info;
    }

    public static Bitmap ConvertViewToBitmap(View contentView) {
        return ConvertViewToBitmap(contentView, false);
    }

    public static Bitmap ConvertViewToBitmap(View contentView, boolean isAlpha) {
        try {
            Bitmap bitmap;
            if (isAlpha) {
                bitmap = Bitmap.createBitmap(contentView.getWidth(), contentView.getHeight(), Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(contentView.getWidth(), contentView.getHeight(), Bitmap.Config.RGB_565);
            }
            //利用bitmap生成画布
            Canvas canvas = new Canvas(bitmap);
            //把view中的内容绘制在画布上
            contentView.draw(canvas);

            return bitmap;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断当前设备是否是模拟器。如果返回TRUE，则当前是模拟器，不是返回FALSE
     *
     * @return
     */
    public static boolean IsEmulator() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            String name = bluetoothAdapter.getName();
            boolean isEmpty = TextUtils.isEmpty(name);
            LogUtil.i(TAG, "Bluetooth:" + (isEmpty ? "null" : name) + ";" + bluetoothAdapter.getAddress());

            //这里逍遥模拟器的蓝牙名字是空的，一般真机都是有名字的，但不排除个别没有名字
            return isEmpty;
            //尝试开启蓝牙设备
//            boolean ret = false;
//            if (bluetoothAdapter.isEnabled())
//                return false;
//            else {
//                ret = bluetoothAdapter.enable();
//                if (ret && bluetoothAdapter.isEnabled()) {
//                    bluetoothAdapter.disable();
//                } else {
//                    ret = false;
//                }
//            }
//            //SystemProperties.get("ro.kernel.qemu").equals("1");
//            String imei = GetIMEI();
//            if (!TextUtils.isEmpty(imei) && imei.equals("000000000000000")) {
//                return true;
//            }
//            return isEmulatorByBuild();
        } catch (Throwable e) {

        }
        return false;
    }

    public static boolean isEmulatorByBuild() {
        LogUtil.i(TAG, "Build.FINGERPRINT: " + Build.FINGERPRINT
                + ", Build.MODEL: " + Build.MODEL
                + ", Build.MANUFACTURER: " + Build.MANUFACTURER
                + ", Build.BRAND: " + Build.BRAND
                + ", Build.DEVICE: " + Build.DEVICE
                + ", Build.PRODUCT: " + Build.PRODUCT);
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
//                || Build.FINGERPRINT.startsWith("unknown") // 魅族MX4: unknown
                || Build.MODEL.equals("sdk")
                || Build.MODEL.equals("google_sdk")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    public static String GetInfoFromThrowable(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        String result = info.toString();
        printWriter.close();

        return result;
    }

    /**
     * 用来判断服务是否运行.
     *
     * @param context
     * @param className 判断的服务名字：包名+类名
     * @return true 在运行, false 不在运行
     */

    public static boolean IsServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
