package ru.noties.requirements;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 * Abstraction to allow resolving requirements from any context (not from android.content.Context).
 * Provides 3 factory methods to work with Activity and Fragment.
 *
 * @see #create(Activity)
 * @see #create(Fragment)
 * @since 1.1.0
 */
public abstract class EventDispatcher {

    /**
     * Factory method to obtain an instance of EventDispatcher that dispatches events through android.app.Activity
     *
     * @param activity from which to dispatch events
     * @return an instance of {@link EventDispatcher}
     * @see EventDispatcherActivity
     * @since 2.0.0
     */
    @NonNull
    public static EventDispatcher activity(@NonNull Activity activity) {
        return new EventDispatcherActivity(activity);
    }

    /**
     * Factory method to obtain an instance of EventDispatcher that dispatches events through android.app.Fragment.
     * <p>
     * Please note that if you use this dispatcher or create your own, {@link EventSource} should
     * consume events from a Fragment (and not from Activity).
     * <p>
     * Please note that it is advised to use Activity based dispatcher (in association with proper
     * {@link EventSource}
     *
     * @param fragment from which to dispatch events
     * @return an instance of {@link EventDispatcher}
     * @see EventDispatcherFragment
     * @since 2.0.0
     */
    @SuppressWarnings("unused")
    @NonNull
    public static EventDispatcher fragment(@NonNull Fragment fragment) {
        return new EventDispatcherFragment(fragment);
    }

    /**
     * Factory method to obtain an instance of EventDispatcher that dispatches events through android.support.v4.app.Fragment
     * <p>
     * Please note that if you use this dispatcher or create your own, {@link EventSource} should
     * consume events from a Fragment (and not from Activity).
     * <p>
     * Please note that it is advised to use Activity based dispatcher (in association with proper
     * {@link EventSource}
     *
     * @param fragment from which to dispatch events
     * @return an instance of {@link EventDispatcher}
     * @see EventDispatcherFragmentCompat
     * @since 2.0.0
     */
    @NonNull
    public static EventDispatcher fragment(@NonNull android.support.v4.app.Fragment fragment) {
        return new EventDispatcherFragmentCompat(fragment);
    }

    /**
     * Factory method to obtain an instance of EventDispatcher that dispatches events through android.app.Activity
     *
     * @param activity from which to dispatch events
     * @return an instance of {@link EventDispatcher}
     * @see EventDispatcherActivity
     * @deprecated 2.0.0 in favor of {@link #activity()} in order to make this more explicit
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @NonNull
    @Deprecated
    public static EventDispatcher create(@NonNull Activity activity) {
        return new EventDispatcherActivity(activity);
    }

    /**
     * Factory method to obtain an instance of EventDispatcher that dispatches events through android.app.Fragment.
     * <p>
     * Please note that if you use this dispatcher or create your own, {@link EventSource} should
     * consume events from a Fragment (and not from Activity)
     *
     * @param fragment from which to dispatch events
     * @return an instance of {@link EventDispatcher}
     * @see EventDispatcherFragment
     * @deprecated 2.0.0 in favor of {@link #fragment(Fragment)} in order to make this more explicit
     */
    @SuppressWarnings({"unused", "DeprecatedIsStillUsed"})
    @NonNull
    @Deprecated
    public static EventDispatcher create(@NonNull Fragment fragment) {
        return new EventDispatcherFragment(fragment);
    }

    /**
     * @return associated Activity
     */
    @NonNull
    public abstract Activity activity();


    public abstract void startActivityForResult(
            @NonNull Intent intent,
            @IntRange(from = 0, to = RequestCode.MAX) int requestCode
    );

    @RequiresApi(Build.VERSION_CODES.M)
    public abstract void requestPermission(
            @NonNull String permission,
            @IntRange(from = 0, to = RequestCode.MAX) int requestCode
    );

    @RequiresApi(Build.VERSION_CODES.M)
    public abstract boolean checkSelfPermission(@NonNull String permission);

    @RequiresApi(Build.VERSION_CODES.M)
    public abstract boolean shouldShowRequestPermissionRationale(@NonNull String permission);
}
