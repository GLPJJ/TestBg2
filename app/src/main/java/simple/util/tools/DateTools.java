package simple.util.tools;

import android.text.TextUtils;

import com.example.administrator.testbg.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import simple.util.til.ToolUtil;

public class DateTools {
    private final static String TAG = "DateTools";

    /**
     * 格式化时间（输出类似于 刚刚, 4分钟前, 一小时前, 昨天这样的时间）
     *
     * @param sjc_time 需要格式化的时间 如"2014-07-14 19:01:45"
     * @param pattern  输入参数time的时间格式 如:"yyyy-MM-dd HH:mm:ss"
     *                 <p/>如果为空则默认使用"yyyy-MM-dd HH:mm:ss"格式
     * @return time为null，或者时间格式不匹配，输出空字符""
     */
    public static String formatDisplayTime(String sjc_time, String pattern) {
        String display = "";
        int tMin = 60 * 1000;
        int tHour = 60 * tMin;
        int tDay = 24 * tHour;
        String time = getStrTime_ymd_hms(Long.valueOf(sjc_time));
        pattern = "yyyy-MM-dd HH:mm:ss";
        if (time != null) {
            try {
                Date tDate = new SimpleDateFormat(pattern).parse(time);
                Date today = new Date();
                SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
                SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
                Date thisYear = new Date(thisYearDf.parse(
                        thisYearDf.format(today)).getTime());
                Date yesterday = new Date(todayDf.parse(todayDf.format(today))
                        .getTime());
                Date beforeYes = new Date(yesterday.getTime() - tDay);
                if (tDate != null) {
                    SimpleDateFormat halfDf = new SimpleDateFormat("MM-dd HH:mm");
                    long dTime = today.getTime() - tDate.getTime();
                    if (tDate.before(thisYear)) {
                        display = new SimpleDateFormat("yyyy-MM-dd").format(tDate);
                    } else {

                        if (dTime < tMin) {
                            display = ToolUtil.GetString(R.string.time_just);
                        } else if (dTime < tHour) {
                            display = ToolUtil.GetString(R.string.time_minutes_ago, (int) Math.ceil(dTime / tMin));
                        } else if (dTime < tDay && tDate.after(yesterday)) {
                            display = ToolUtil.GetString(R.string.time_hour_ago, (int) Math.ceil(dTime / tHour));
                        } else if (tDate.after(beforeYes) && tDate.before(yesterday)) {
                            display = halfDf.format(tDate);
                        } else {
                            display = halfDf.format(tDate);
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return display;
    }

    /*
     * 将时间戳转为字符串 ，格式：yyyy.MM.dd  星期几
     */
    public static String getRecordTimeNodeMonth(long cc_time) {
        StringBuffer re_StrTime = new StringBuffer();
        re_StrTime.append(GetStrSecondFmt(cc_time, "MM")).append(ToolUtil.GetString(R.string.month));
        return re_StrTime.toString();
    }

    /**
     * 判断2天是否是同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isTheSameDay(long date1, long date2) {
        Date d1 = new Date(date1 * 1000L);
        Date d2 = new Date(date2 * 1000L);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
                && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
                && (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH));
    }

    public static String getRecordTimeNodeDayDisplay(String sjc_time) {
        String display = "";
        int tMin = 60 * 1000;
        int tHour = 60 * tMin;
        int tDay = 24 * tHour;
        String time = getStrTime_ymd_hms(Long.valueOf(sjc_time));
        String pattern = "yyyy-MM-dd HH:mm:ss";
        if (time != null) {
            try {
                Date tDate = new SimpleDateFormat(pattern).parse(time);
                Date today = new Date();
                SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
                SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
                Date thisYear = new Date(thisYearDf.parse(
                        thisYearDf.format(today)).getTime());
                Date yesterday = new Date(todayDf.parse(todayDf.format(today))
                        .getTime());
                Date beforeYes = new Date(yesterday.getTime() - tDay);
                if (tDate != null) {
                    long dTime = today.getTime() - tDate.getTime();
                    if (dTime < tDay && tDate.after(yesterday)) {
                        display = ToolUtil.GetString(R.string.today);
                    } else if (tDate.after(beforeYes) && tDate.before(yesterday)) {
                        display = ToolUtil.GetString(R.string.yesterday);
                    } else {
                        SimpleDateFormat halfDf = new SimpleDateFormat("dd" + ToolUtil.GetString(R.string.day));
                        display = halfDf.format(tDate);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return display;
    }

    public static String getDailyTime_ymd(long cc_time) {
        return GetStrSecondFmt(cc_time, "yyyy/MM/dd");
    }

    public static String getDailyTime_md(long cc_time) {
        return GetStrSecondFmt(cc_time, "MM/dd");
    }

    public static String getDailyTime_d(long cc_time) {
        return GetStrSecondFmt(cc_time, "d");
    }

    public static String getMatchBeginDate(long cc_time) {
        return GetStrSecondFmt(cc_time, "MM/dd\nHH:mm");
    }

    public static String getHMofTime(long time) {
        long h = time / 3600;
        long m = (time % 3600) / 60;
        return String.format("%d:%d", h, m);
    }

    /**
     * @return 返回当前时间戳 （单位秒）
     */
    public static long getCurrentUnixTime() {
        return getCurrentTime() / 1000L;
    }

    /**
     * @return 返回当前微秒
     */
    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static String getStrTime_ymd_hms(long cc_time) {
        return GetStrSecondFmt(cc_time, "yyyy-MM-dd HH:mm:ss");
    }

    // 将时间戳转为字符串
    public static String getStrTime_ymd_hm(long cc_time) {
        return GetStrSecondFmt(cc_time, "yyyy-MM-dd HH:mm");
    }

    public static String getStrTime_ymd(long cc_time) {
        return GetStrSecondFmt(cc_time, "yyyy-MM-dd");
    }

    public static String getStrTime_y(long cc_time) {
        return GetStrSecondFmt(cc_time, "yyyy");
    }

    public static String getStrTime_md(long cc_time) {
        return GetStrSecondFmt(cc_time, "MM-dd");
    }

    public static String getStrTime_hm(long cc_time) {
        return GetStrSecondFmt(cc_time, "HH:mm");
    }

    public static String getStrTime_hms(long cc_time) {
        return GetStrSecondFmt(cc_time, "HH:mm:ss");
    }

    public static String getOtherStrTime_ymd_hm(long cc_time) {
        return GetStrSecondFmt(cc_time, "yyyy/MM/dd  HH:mm");
    }

    public static String getOtherStrTime_md_hm(long cc_time) {
        return GetStrSecondFmt(cc_time, "MM/dd  HH:mm");
    }

    public static String GetStrSecondFmt(long seconds, String sdf) {
        if (TextUtils.isEmpty(sdf))
            return "";
        return GetStrSecondFmt(seconds, new SimpleDateFormat(sdf));
    }

    public static String GetStrSecondFmt(long seconds, SimpleDateFormat sdf) {
        return GetStrTimeFmt(seconds * 1000L, sdf);
    }

    public static String GetStrTimeFmt(long micro_seconds, String sdf) {
        if (TextUtils.isEmpty(sdf))
            return "";
        return GetStrTimeFmt(micro_seconds, new SimpleDateFormat(sdf));
    }

    public static String GetStrTimeFmt(long micro_seconds, SimpleDateFormat sdf) {
        if (sdf == null)
            return "";
        return sdf.format(new Date(micro_seconds));
    }
}

