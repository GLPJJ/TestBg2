package simple.util.net.observable;

import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.internal.util.InternalObservableUtils;

/**
 * Created by glp on 2016/10/9.
 */

public class HttpSubscriber<T> extends Subscriber<T> {

    final Action1<? super T> onNext;
    final Action1<Throwable> onError;
    final Action0 onCompleted;

    final Observer<? super T> observer;

    public HttpSubscriber(final Action1<? super T> onNext) {
        if (onNext == null) {
            throw new IllegalArgumentException("onNext can not be null");
        }

        Action1<Throwable> onError = InternalObservableUtils.ERROR_NOT_IMPLEMENTED;
        Action0 onCompleted = Actions.empty();

        this.onNext = onNext;
        this.onError = onError;
        this.onCompleted = onCompleted;
        this.observer = null;
    }

    public HttpSubscriber(final Action1<? super T> onNext, final Action1<Throwable> onError) {
        if (onNext == null) {
            throw new IllegalArgumentException("onNext can not be null");
        }
        if (onError == null) {
            throw new IllegalArgumentException("onError can not be null");
        }

        Action0 onCompleted = Actions.empty();

        this.onNext = onNext;
        this.onError = onError;
        this.onCompleted = onCompleted;
        this.observer = null;
    }

    public HttpSubscriber(final Action1<? super T> onNext, final Action1<Throwable> onError, final Action0 onCompleted) {
        if (onNext == null) {
            throw new IllegalArgumentException("onNext can not be null");
        }
        if (onError == null) {
            throw new IllegalArgumentException("onError can not be null");
        }
        if (onCompleted == null) {
            throw new IllegalArgumentException("onComplete can not be null");
        }

        this.onNext = onNext;
        this.onError = onError;
        this.onCompleted = onCompleted;
        this.observer = null;
    }

    public HttpSubscriber(Observer<? super T> observer) {
        if (observer == null) {
            throw new IllegalArgumentException("onComplete can not be null");
        }

        Action1<Throwable> onError = InternalObservableUtils.ERROR_NOT_IMPLEMENTED;
        Action0 onCompleted = Actions.empty();

        this.onNext = null;
        this.onError = onError;
        this.onCompleted = onCompleted;
        this.observer = observer;
    }

    public HttpSubscriber<T> addSubscription(HttpApi.Builder builder) {
        return addSubscription(builder.toString());
    }

    public HttpSubscriber<T> addSubscription(String url) {
        add(new HttpSubscription(url));
        return this;
    }

    @Override
    public void onCompleted() {
        if (observer == null)
            onCompleted.call();
        else
            observer.onCompleted();
    }

    @Override
    public void onError(Throwable e) {
        if (observer == null)
            onError.call(e);
        else
            observer.onError(e);
    }

    @Override
    public void onNext(T o) {
        if (observer == null)
            onNext.call(o);
        else
            observer.onNext(o);
    }
}
