package simple.util.til;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class PFileManager {

    private final static int MB = 1024 * 1024;
    private final static int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;//10MB

    private static int READ_BUFFER_SIZE = 4 * 1024;
    public static boolean doEncode = true;
    public static String secretKey = "abcdefghijklmnopqrstuvwxyz123456";

    /**
     * 二进制读取文件
     *
     * @param path
     * @return
     */
    public static ByteArrayOutputStream readByteData(String path) {
        //MyLog.d("path: " + path);
        try {
            if (null == path) {
                return null;
            }
            File jsonFile = new File(path);
            if (null != jsonFile && jsonFile.exists() && jsonFile.isFile()) {

                FileInputStream fileInputStream = new FileInputStream(jsonFile);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[READ_BUFFER_SIZE];
                int len = 0;
                try {
                    while ((len = fileInputStream.read(buffer)) != -1) {
                        baos.write(buffer, 0, len);
                    }
                    fileInputStream.close();
                } catch (Throwable e) {
                    //
                    return null;
                }
                return baos;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 从文件读取数据
     *
     * @param path
     * @return
     */
    public static String readData(String path) {
        if (doEncode) {
            return readData(path, secretKey);
        } else {
            return readData(path, null);
        }
    }

    /**
     * 根据密钥对读取到的数据做解码，获取正确数据
     *
     * @param path
     * @param secret
     * @return
     */
    public static String readData(String path, String secret) {
        String resultData = null;
        ByteArrayOutputStream baos = readByteData(path);
        if (null == baos) {
            return resultData;
        } else {

            try {
                String cacheStr = new String(baos.toByteArray(), "UTF-8");
                if (null != secret) {
                    resultData = AES256Cipher.AES_Decode(cacheStr, secret);
                } else {
                    resultData = cacheStr;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return resultData;
    }

    /**
     * 二进制写入文件
     *
     * @param bytedata
     * @param path
     */
    public static void writeByteData(byte[] bytedata, String path) {
        try {
            if (null == bytedata || null == path) {
                return;
            }
            File cacheFile = new File(path);
            if (!cacheFile.exists()) {
                cacheFile.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);
            fileOutputStream.write(bytedata);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将content写入指定文件
     *
     * @param content
     * @param path
     */
    public static void writeData(String content, String path) {
        if (!isEnoughFreeSpaceOnSD()) {
            return;
        }
        if (doEncode) {
            writeData(content, path, secretKey);
        } else {
            writeData(content, path, null);
        }
    }

    /**
     * 根据密钥对数据做加密，并保存到文件
     *
     * @param content
     * @param path
     * @param secret
     */
    public static void writeData(String content, String path, String secret) {
        String saveData = content;
        if (null != saveData) {
            if (null != secret) {
                saveData = AES256Cipher.AES_Encode(saveData, secret);
            }
            if (null != saveData) {
                writeByteData(saveData.getBytes(), path);
            }
        }
    }

    public static File saveBitmap(String name, Bitmap mBitmap) {
        File f = new File(name);
        try {
            f.createNewFile();
        } catch (IOException e) {
            // DebugMessage.put("在保存图片时出错："+e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    /**
     * 判断SD卡上是否有足够的剩余空间
     *
     * @return
     */
    public static boolean isEnoughFreeSpaceOnSD() {
        if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSD()) {
            return false;
        }
        return true;
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

    /**
     * 获取文件的大小
     *
     * @param fileSize 文件的大小
     * @return
     */
    public static String FormetFileSize(long fileSize) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "K";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 获取文件大小
     *
     * @param path
     * @return
     */
    public static long getFileSize(String path) {

        long filesize = 0;

        File file = new File(path);
        if (null != file && file.exists() && file.isFile()) {
            filesize = file.length();
        }
        return filesize;
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param sPath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public static boolean deleteFolder(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 判断目录或文件是否存在
        if (!file.exists()) {  // 不存在返回 false
            return flag;
        } else {
            // 判断是否为文件
            if (file.isFile()) {  // 为文件时调用删除文件方法
                return deleteFile(sPath);
            } else {  // 为目录时调用删除目录方法
                return deleteDirectory(sPath);
            }
        }
    }

    public static void deleteFile(File file) {
        if(file == null)
            return;

        if (file.isFile()) {
            file.delete();
            return;
        } else if (file.isDirectory()) {
            deleteDirectory(file.getPath());
        }
    }

    /**
     * 根据文件路径删除文件
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (null != file && file.exists() && file.isFile()) {
            return file.delete();
        }
        return true;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 更新文件的修改时间
     *
     * @param dir
     * @param filename
     */
    public static void updateFileModifyTime(String dir, String filename) {
        if (null == dir || null == filename) {
            return;
        }

        File file = new File(dir, filename);
        long now = System.currentTimeMillis();
        file.setLastModified(now);
    }

    /**
     * 如果指定目录dir下，文件个数大于filecount，则删除指定比例percent的文件
     * filecount大于0，则直接删除指定比例文件
     * percent的值为0到1
     *
     * @param dir
     * @param filecount
     * @param percent
     */
    public static ArrayList<String> deleteSomeFiles(String dir, int filecount, float percent, String fileext) {

        ArrayList<String> deleteFileNames = new ArrayList<String>();

        if (null == dir || filecount == 0 || percent == 0) {
            return deleteFileNames;
        }

        File fileDir = new File(dir);
        File[] files = fileDir.listFiles();
        if (files != null && files.length > filecount) {
            int deleteFileCount = (int) (files.length * percent);
            Arrays.sort(files, new FileLastModifySort());

            for (int i = 0; i < deleteFileCount; i++) {
                File pfile = files[i];
                if (pfile.getName().endsWith(fileext)) {
                    pfile.delete();
                    deleteFileNames.add(pfile.getName());
                }
            }
        }

        return deleteFileNames;
    }

    static class FileLastModifySort implements Comparator<File> {

        public int compare(File file1, File file2) {
            if (file1.lastModified() > file2.lastModified()) {
                return 1;
            } else if (file1.lastModified() == file2.lastModified()) {
                return 0;
            } else {
                return -1;
            }
        }

    }

}
