package simple.intefaces;

import android.os.Handler;

import com.bumptech.glide.manager.LifecycleListener;

/**
 * Created by glp on 2017/1/9.
 * <p>
 * <p>
 * An interface for listening to Activity/Fragment lifecycle events.
 */

public interface ILifecycle {

    /**
     * Adds the given listener to the set of listeners managed by this Lifecycle implementation.
     */
    void addListener(LifecycleListener listener);

    /**
     * Remove the given listener
     *
     * @param listener
     */
    void removeListener(LifecycleListener listener);

    Handler getMainHandler();
}
