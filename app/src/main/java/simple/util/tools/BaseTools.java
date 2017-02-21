package simple.util.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

import java.util.Locale;

import simple.config.Simple;

/**
 * 基础工具类
 */
public class BaseTools {

    /**
     * 获取当前版本
     */
    public static String getAppVersionName(Context context) {
        String verionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            verionName = pi.versionName;
            if (verionName == null || verionName.length() <= 0) {
                return "";
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        LogUtil.d("version", "current version:" + verionName);
        return verionName;
    }

    /**
     * 判断用户名是否符合规则
     *
     * @param username
     * @return
     */
    public static boolean checkUserName(String username) {
        return username.matches("[0-9A-Za-z_]*");
    }

    /**
     * 验证手机格式,根据区号
     */
    public static boolean isMobileNO(String countryCode, String mobiles) {
        if ("86".equals(countryCode) && !isMobileNO(mobiles)) {
            return false;
        }
        return true;
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
        return true;//这里还是先去掉这个手机号判断吧
        /*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
        联通：130、131、132、145、152、155、156、175、176、185、186
        电信：133、149、153、173、177、180、181、189、（1349卫通）
        虚拟网：17
        总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
        */
//        String telRegex = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[34578]"代表第二位可以为3、4、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
//        if (TextUtils.isEmpty(mobiles))
//            return false;
//        else
//            return mobiles.matches(telRegex);
    }

    public static boolean isNO(String mobiles) {
        String telRegex = "^[0-9]*$";//
        if (TextUtils.isEmpty(mobiles))
            return false;
        else
            return mobiles.matches(telRegex);
    }

    public static String getLanguage() {
        Locale locale = Simple.Context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String languageCountry = language + "-" + country;
        LogUtil.d("getLanguage", languageCountry);
        return languageCountry;
    }
}
