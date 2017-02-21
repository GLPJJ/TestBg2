package simple.util.net;

import android.content.Context;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import simple.config.Simple;
import simple.util.net.comparator.MapKeyComparator;
import simple.util.til.ToolUtil;

/**
 * Created by zjy on 2015/4/27.
 */
public class NetWork {

    public static HashMap<String, String> getRequestCommonParams() {
        return getRequestCommonParams(true);
    }

    public static HashMap<String, String> getRequestCommonParams(boolean hasUid) {
        return getRequestCommonParams(Simple.Context, hasUid);
    }

    /**
     * 获取公共参数
     *
     * @return
     */
    public static HashMap<String, String> getRequestCommonParams(Context mContext) {
        return getRequestCommonParams(mContext, true);
    }

    public static HashMap<String, String> getRequestCommonParams(Context mContext, boolean hasUid) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("os", Simple.Config.getAndroidId());//1代表android
        if (hasUid) {
            paramsMap.put("uid", Simple.Config.getUser());
        }

        paramsMap.put("ccode", Simple.Config.getCCode());
        return paramsMap;
    }

    /**
     * 传输数据的接口拼接的数据字段
     */
    public static String getRequestParams(Map<String, String> dataMap) {
        if (dataMap == null) {
            dataMap = new HashMap<>();
        }
        int paramsSize = dataMap.size();
        String[] keysArray = new String[paramsSize];
        dataMap.keySet().toArray(keysArray);
        Arrays.sort(keysArray);
        StringBuffer paramsBuf = new StringBuffer();
        StringBuffer valuesBuf = new StringBuffer();
        String paramsKey;
        String paramsValue;
        paramsBuf.append("?");
        for (int i = 0; i < paramsSize; i++) {
            paramsKey = keysArray[i];
            paramsValue = dataMap.get(paramsKey);
            if (paramsValue == null) {
                paramsValue = "";
            }
            if (i == paramsSize - 1) {
                paramsBuf.append(paramsKey).append("=").append(URLEncoder.encode(paramsValue));
            } else {
                paramsBuf.append(paramsKey).append("=").append(URLEncoder.encode(paramsValue)).append("&");
            }
            valuesBuf.append(paramsValue);
        }
        String params = paramsBuf.toString();
        return params;
    }

    /**
     * 获取加密字段
     *
     * @param rand 随机数
     * @param time 时间戳
     * @return
     */
    public static String getSign(String rand, String time) {
        String[] arrays = new String[]{rand, time, Simple.Config.getToken()};
        Arrays.sort(arrays);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < arrays.length; i++) {
            buffer.append(arrays[i]);
        }
        String sign = ToolUtil.GetSha1(buffer.toString());
        //Log.d("sha1" , buffer.toString());
        //Log.d("sha1", "rand :" + rand + ";time :" + time + ";sign :" + sign);
        return sign;
    }

    /**
     * 获取排序Params MD5加密，用于请求头加密
     *
     * @param map
     * @return
     */
    public static String getParamsMD5(Map<String, String> map) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Simple.Config.getToken());
        if (map != null) {
            map = sortMapByKey(map);
            Iterator iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                String value = map.get(key);
                stringBuffer.append(key).append(value);
                //Log.d("getParamsMD5", "key :" + key + "; value:" + map.get(key));
            }
        }
        String paramsStr = stringBuffer.toString();
        //LogUtil.d("getParamsMD5", paramsStr);
        return ToolUtil.GetMd5(paramsStr);
    }

    /**
     * 获取随机数
     *
     * @return
     */
    public static String getRandom() {
        int max = 9999;
        int min = 1000;
        Random random = new Random();
        int randomNum = random.nextInt(max - min) + min;
        return String.valueOf(randomNum);
    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, String> sortMap = new TreeMap<String, String>(new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }
}
