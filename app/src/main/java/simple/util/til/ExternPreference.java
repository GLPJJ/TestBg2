package simple.util.til;

import android.content.Context;
import android.content.SharedPreferences;

import simple.config.Simple;

/**
 * Created by glp on 2017/1/17.
 */

public class ExternPreference {

    static final String Preference = "ExternPreference";
    static final String URL = "url";
    static final String WS = "ws";

    SharedPreferences mSharedPreferences;
    private static ExternPreference sIns;

    private ExternPreference() {
        mSharedPreferences = Simple.Context.getSharedPreferences(Preference, Context.MODE_PRIVATE);
    }

    public static ExternPreference GetIns() {
        if (sIns == null) {
            sIns = new ExternPreference();
        }
        return sIns;
    }

    public void setUrl(String url) {
        mSharedPreferences.edit().putString(URL, url).apply();
    }

    public String getUrl() {
        return mSharedPreferences.getString(URL, "");
    }

    public void setWS(String ws) {
        mSharedPreferences.edit().putString(WS, ws).apply();
    }

    public String getWS() {
        return mSharedPreferences.getString(WS, "");
    }

    public void setUrlAndWS(String url, String ws) {
        setUrl(url);
        setWS(ws);
    }
}
