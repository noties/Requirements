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
 * Provides 1 factory method to work with Activity.
 * <p>
 * Please note that Fragments are intentionally not included as they require <em>special care</em>.
 * When using them one <strong>must be absolutely sure to use both dispatcher and source</strong>
 * that is initialized for a fragment. Otherwise, due to some special requirements that fragments have,
 * mixing different dispatcher and source will result in ignored events (fragments do modify request code).
 *
 * @see #activity()
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
    public static EventDispatcherActivity activity(@NonNull Activity activity) {
        return new EventDispatcherActivity(activity);
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
     * @deprecated 2.0.0 (removed support for fragments)
     */
    @SuppressWarnings({"unused", "DeprecatedIsStillUsed"})
    @NonNull
    @Deprecated
    public static EventDispatcher create(@NonNull Fragment fragment) {
        throw new RuntimeException("Starting with 2.0.0 there is no fragment support. Please roll " +
                "your own EventDispatcher and EventSource if you need this functionality in a Fragment " +
                "(or anything else)");
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
