package simple.util.net.observable;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by glp on 2016/10/9.
 */

public class OkHttpCallMgr {

    public static OkHttpCallMgr GetInstance() {
        if (ins == null)
            ins = new OkHttpCallMgr();
        return ins;
    }

    public synchronized boolean isInCallList(String url) {
        if (mMap.containsKey(url))
            return true;
        return false;
    }

    public synchronized boolean addCall(String url, okhttp3.Call call) {
        if (mMap.containsKey(url) || call == null)
            return false;

        mMap.put(url, call);
        return true;
    }

    public synchronized void removeCall(String url) {
        if (mMap.containsKey(url)) {
            okhttp3.Call call = mMap.get(url);
            if (!call.isExecuted()) {
                call.cancel();
            }
            mMap.remove(url);
        }
    }


    static OkHttpCallMgr ins = null;
    private Map<String, okhttp3.Call> mMap = new HashMap<>();

}
