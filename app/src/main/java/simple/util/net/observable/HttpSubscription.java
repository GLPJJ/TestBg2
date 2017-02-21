package simple.util.net.observable;

import rx.Subscription;

/**
 * Created by glp on 2016/10/9.
 */

public class HttpSubscription implements Subscription {

    String urlId;
    boolean isUnsubscribed = false;

    public HttpSubscription(String urlId) {
        this.urlId = urlId;
    }

    @Override
    public void unsubscribe() {
        isUnsubscribed = true;
        OkHttpCallMgr.GetInstance().removeCall(urlId);
    }

    @Override
    public boolean isUnsubscribed() {
        return isUnsubscribed;
    }
}
