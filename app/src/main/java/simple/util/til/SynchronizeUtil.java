package simple.util.til;

import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.functions.Action0;
import simple.bean.SynchronizeBean;
import simple.util.tools.DateTools;

/**
 * Created by glp on 2017/1/10.
 * <p>
 * 化异步为同步
 */

public class SynchronizeUtil {

    //线程安全
    static Map<Long, SynchronizeBean> sMap = new ConcurrentHashMap<>();

    /**
     * @param handler
     * @param doAction 需要执行的异步方法
     * @param data     指定返回的数据结构
     * @return
     */
    public static SynchronizeBean SynchronizeDeal(Handler handler, final Action0 doAction, @NonNull SynchronizeBean data) {
        if (handler == null || doAction == null) {
            return data;
        }

        final long timeId = DateTools.getCurrentTime();
        sMap.put(timeId, data);
        handler.post(new Runnable() {
            @Override
            public void run() {
                doAction.call();
            }
        });

        while (!sMap.get(timeId).isFinish())
            ThreadUtil.Wait();

        sMap.remove(timeId);
        return data;
    }

}
