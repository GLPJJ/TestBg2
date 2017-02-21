package simple.config;

import android.content.Context;

import java.util.Map;

/**
 * Created by glp on 2017/2/14.
 */

public interface SimpleConfig {

    String getHost();

    String getWBSocket();

    String getToken();

    String getCCode();

    String getUser();

    String getAndroidId();

    boolean isTextNeedFilter();

    boolean isLogOpen();

    void goWebview(Context context, String title, String url);

    void reportError(String error);

    Map<String, String> getHttpHeadAuth(Map<String, String> params);
}
