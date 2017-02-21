package simple.util.til;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Comparator;

import simple.config.Simple;

public class AppDirUtil {

    //头像缓存
    public final static String HEAD_PATH = "head";
    //歌手图片缓存
    public final static String SINGER_PATH = "singer";
    //Banner图片缓存
    public final static String BANNER_PATH = "banner";
    //礼物图片缓存
    public final static String GIFT_PATH = "gift";
    //用户小头像
    public final static String SmallHEAD_PATH = "smallhead";
    //更新路径
    public final static String APPUPGRADE_PATH = "apkdownload";
    //下载路径
    public final static String DOWN_PATH = "down";
    //http cache dir
    public final static String CACHE_HTTP_PATH = "cache_http";
    //
    public final static String PATH_SCREEN_DIR = "screensave";
    //dump路径
    public static final String DUMP_PATH = "dump";
    //other 路径
    public static final String OTHER_PATH = "other";

    private final static int MB = 1024 * 1024;
    private final static int CACHE_SIZE = 20 * MB;
    public final static int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;
    private final static long OVER_TIME = 36000;

    /**
     * 获取目录名称
     *
     * @param url
     * @return FileName
     */
    public static String getFileName(String url) {
        int lastIndexStart = url.lastIndexOf("/");
        if (lastIndexStart != -1) {
            return url.substring(lastIndexStart + 1, url.length());
        } else {
            return new Timestamp(System.currentTimeMillis()).toString();
        }
    }

    /**
     * 获取路径
     *
     * @return
     * @throws IOException
     */
    public static String getUrlPath(String url) {
        String path = getDownDir() + url.substring(url.lastIndexOf("/") + 1);
        return path;
    }

    /**
     * 指定App路径
     *
     * @return
     */
    public static File getTempCacheDirFile() {
        return makeDir(getCacheDirFile(), AppDirUtil.CACHE_HTTP_PATH);
    }

    public static String getCacheDir() {
        String dir = makeDir(getCacheDirFile(), AppDirUtil.CACHE_HTTP_PATH).getAbsolutePath() + "/";
        dealSomeWork(dir);
        return dir;
    }

    public static File getDumpDirFile() {
        return makeDir(getAppDirFile(), DUMP_PATH);
    }

    public static String getUpgradeDir() {
        String dir = makeDir(getSuitableDirFile(true), APPUPGRADE_PATH).getAbsolutePath() + "/";
        return dir;
    }

    public static String getDownDir() {
        String dir = makeDir(getAppDirFile(), DOWN_PATH).getAbsolutePath() + "/";
        return dir;
    }

    public static String getOtherPath() {
        String dir = makeDir(getSuitableDirFile(true), OTHER_PATH).getAbsolutePath() + "/";
        return dir;
    }

    /**
     * 获取截图目录
     *
     * @return
     */
    public static String getScreenDir() {
        return makeDir(getAppDirFile(), PATH_SCREEN_DIR).getAbsolutePath() + "/";
    }

    public static String getGiftDir() {
        String dir = makeDir(getAppDirFile(), AppDirUtil.GIFT_PATH).getAbsolutePath() + "/";
        dealSomeWork(dir);
        return dir;
    }

    public static String getHeadDir() {
        String dir = makeDir(getAppDirFile(), AppDirUtil.HEAD_PATH).getAbsolutePath() + "/";
        dealSomeWork(dir);
        return dir;
    }

    public static String getSingerDir() {
        String dir = makeDir(getAppDirFile(), AppDirUtil.SINGER_PATH).getAbsolutePath() + "/";
        dealSomeWork(dir);
        return dir;
    }

    public static String getBannerDir() {
        String dir = makeDir(getAppDirFile(), AppDirUtil.BANNER_PATH).getAbsolutePath() + "/";
        dealSomeWork(dir);
        return dir;
    }

    private static void dealSomeWork(final String dir) {
        ThreadUtil.Execute(new Runnable() {
            @Override
            public void run() {
                removeCache(dir);
            }
        });
    }

    public static String getAppDir() {
        return getAppDirFile().getAbsolutePath();
    }

    public static File getAppDirFile() {
        return getAppDirFile(true);
    }

    public static File getAppDirFile(boolean needSd) {
        return makeDir(getSuitableDirFile(needSd), "TexasCircle");
    }

    public static File getCacheDirFile() {
        return Simple.Context.getCacheDir();
    }

    public static boolean isSDAviableRead() {
        String state = Environment.getExternalStorageState();
        boolean mExternalStorageAvailable;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
        } else {
            mExternalStorageAvailable = false;
        }
        return mExternalStorageAvailable;
    }

    public static boolean isSDAviableWrite() {
        String state = Environment.getExternalStorageState();
        boolean mExternalStorageWriteable;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageWriteable = false;
        }
        return mExternalStorageWriteable;
    }

    public static File getSuitableDirFile(boolean needSd) {
        File suitableDir;
        if (needSd) {
            //如果SD卡可用
            if (isSDAviableWrite())
                suitableDir = Environment.getExternalStorageDirectory();
            else//不可用
                suitableDir = Simple.Context.getFilesDir();
        } else { // 不存SD卡
            suitableDir = Simple.Context.getFilesDir();
        }
        return suitableDir;
    }

    private static File makeDir(File file, String name) {
        File dataDir = new File(file, name);
        if (!dataDir.isDirectory())
            dataDir.mkdirs();
        return dataDir;
    }

    /**
     * 计算存储目录下的文件大小，
     * 当文件总大小大于规定的cache_size或者sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE的规定
     * 那么删除40%最近没有被使用的文件
     *
     * @param dirPath
     */
    public static void removeCache(String dirPath) {
        // System.out.println("removeCache");

        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (null == files || files.length == 0) {
            return;
        }
        int dirSize = 0;
        for (int i = 0; i < files.length; i++) {// 未判断多级目录缓存文件
            dirSize += files[i].length();
        }
        if (dirSize > CACHE_SIZE || FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSD()) {
            int removeFactor = (int) ((0.4 * files.length) + 1);
            Arrays.sort(files, new FileLastModifySort());

            // clear some file
            if (removeFactor <= files.length) {
                for (int i = 0; i < removeFactor; i++) {
                    files[i].delete();
                }
            }

        }
    }

    /**
     * 删除过期文件
     *
     * @param dirPath
     * @param filename
     */
    public static void removeExpiredCache(String dirPath, String filename) {
        if (null == dirPath || null == filename) {
            return;
        }
        File file = new File(dirPath, filename);
        if (System.currentTimeMillis() - file.lastModified() > OVER_TIME) {
            file.delete();
        }
    }

    /**
     * 计算sdcard上的剩余空间
     *
     * @return
     */
    public static int freeSpaceOnSD() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat.getBlockSize()) / MB;
        return (int) sdFreeMB;
    }

    static class FileLastModifySort implements Comparator<File> {

        @Override
        public int compare(File arg0, File arg1) {
            if (arg0.lastModified() > arg1.lastModified()) {
                return 1;
            } else if (arg0.lastModified() == arg1.lastModified()) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
