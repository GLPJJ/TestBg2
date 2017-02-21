package simple.util.tools;

import android.text.TextUtils;

import simple.config.Simple;

/**
 */
public class LogUtil {
    public static final boolean LOG = true;

    public static int d(String tag, String msg) {
        if (Simple.Config.isLogOpen() && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(msg)) {
            return android.util.Log.d(tag, msg);
        } else {
            return -1;
        }
    }

    public static int i(String tag, String msg) {
        if (Simple.Config.isLogOpen() && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(msg)) {
            return android.util.Log.i(tag, msg);
        } else {
            return -1;
        }
    }

    public static int w(String tag, String msg) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(msg))
            return android.util.Log.w(tag, msg);
        return -1;
    }

    public static int e(String tag, String msg) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(msg))
            return android.util.Log.e(tag, msg);
        return -1;
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(msg) && tr != null)
            return android.util.Log.e(tag, msg, tr);
        return -1;
    }
}
