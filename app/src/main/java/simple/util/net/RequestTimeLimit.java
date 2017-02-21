package simple.util.net;

/**
 * 请求限制
 */
public class RequestTimeLimit {
    public static int GET_AMONT_TIME_LIMIT = 30;
    public static int GET_STATISTICAL_TIME_LIMIT = 60 * 5;
    public static int GET_GAME_PLAYING_TIME_LIMIT = 30;
    public static int GET_GAME_RECENT_TIME_LIMIT = 60;
    private static long lastGetAmontTime = 0;//最后次获取金币时间
    private static long lastGetStatisticalTime = 0;//最后次获取掌机时间
    private static long lastGetGamePlayingTime = 0;//最后次请求列表
    private static long lastGetRecentGameTime = 0;//最后次请求最近游戏列表

    /**
     * 重置获取上次时间限制
     */
    public static void resetGetTime() {
        lastGetAmontTime = 0;
        lastGetStatisticalTime = 0;
        lastGetGamePlayingTime = 0;
        lastGetRecentGameTime = 0;
    }

    public static void setLastGetAmontTime(long time) {
        lastGetAmontTime = time;
    }

    public static long getLastGetAmontTime() {
        return lastGetAmontTime;
    }

    public static void setLastGetStatisticalTime(long time) {
        lastGetStatisticalTime = time;
    }

    public static long getLastGetStatisticalTime() {
        return lastGetStatisticalTime;
    }

    public static void setLastGetGetGamePlayingTime(long time) {
        lastGetGamePlayingTime = time;
    }

    public static long getLastGetGamePlayingTime() {
        return lastGetGamePlayingTime;
    }

    public static void setLastRecentGameTime(long time) {
        lastGetRecentGameTime = time;
    }

    public static long getLastRecentGameTime() {
        return lastGetRecentGameTime;
    }
}
