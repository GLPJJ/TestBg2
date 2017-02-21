package simple.util.crash;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import simple.config.Simple;
import simple.util.til.AppDirUtil;
import simple.util.til.ToolUtil;
import simple.util.tools.DateTools;

public class AppCrashHandler {

    private Context context;

    int versionCode;
    String versionName;
    File m_fileDir;

    private UncaughtExceptionHandler uncaughtExceptionHandler;

    private static AppCrashHandler instance;

    private static final String VERSION_NAME = "versionName";

    private static final String STACK_TRACE = "STACK_TRACE";
    /**
     * 错误报告文件的扩展名
     */
    private static final String CRASH_REPORTER_EXTENSION = ".txt";

    /**
     * 使用Properties来保存设备的信息和错误堆栈信息
     */
    private Properties mDeviceCrashInfo = new Properties();

    private AppCrashHandler(final Context context) {
        this.context = context;

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            versionCode = info.versionCode;
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        m_fileDir = AppDirUtil.getDumpDirFile();
        // get default
        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        // install
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, final Throwable ex) {
                // save log
                //saveException(ex, true);

                // 收集设备信息
                collectCrashDeviceInfo();
                // 保存错误报告文件
                saveCrashInfoToFile(ex);

                // uncaught
                uncaughtExceptionHandler.uncaughtException(thread, ex);
            }
        });
    }

    /**
     * 收集程序设备的信息
     */
    public void collectCrashDeviceInfo() {
        mDeviceCrashInfo.put(VERSION_NAME, versionName);
        // 使用反射来收集设备信息.在Build类中包含各种设备信息,
        // 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mDeviceCrashInfo.put(field.getName(), field.get(null));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    private String saveCrashInfoToFile(Throwable ex) {
        String result = ToolUtil.GetInfoFromThrowable(ex);
        try {
            mDeviceCrashInfo.put(STACK_TRACE, result);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        String fileName = null;
        try {
            String timestamp = DateTools.GetStrTimeFmt(System.currentTimeMillis(), "yyyyMMdd-HHmmss");
            fileName = "chesscircle_crash-" + timestamp + CRASH_REPORTER_EXTENSION;
            File file = new File(m_fileDir, fileName);

            Log.e("AppCrashHandler", "new Exception file occur : " + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream trace = new FileOutputStream(file);

            StringBuilder sb = new StringBuilder();
            sb.append('{');
            Iterator<Map.Entry<Object, Object>> i = mDeviceCrashInfo.entrySet().iterator();
            boolean hasMore = i.hasNext();
            while (hasMore) {
                Map.Entry<Object, Object> entry = i.next();

                Object key = entry.getKey();
                sb.append(key == mDeviceCrashInfo ? "(this Map)" : key.toString());
                sb.append('=');
                Object value = entry.getValue();
                sb.append(value == mDeviceCrashInfo ? "(this Map)" : value.toString());

                if (hasMore = i.hasNext()) {
                    sb.append(",\n");
                }
            }
            sb.append('}');
            String temp = sb.toString();

            trace.write(temp.getBytes());
            trace.close();

            Simple.Config.reportError(temp);

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public static AppCrashHandler getInstance(Context mContext) {
        if (instance == null) {
            instance = new AppCrashHandler(mContext);
        }

        return instance;
    }

    public final void saveException(Throwable ex, boolean uncaught) {
        CrashSaver.save(context, ex, uncaught);
    }

    public void setUncaughtExceptionHandler(UncaughtExceptionHandler handler) {
        if (handler != null) {
            this.uncaughtExceptionHandler = handler;
        }
    }
}
