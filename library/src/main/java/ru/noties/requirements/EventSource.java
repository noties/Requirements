package ru.noties.requirements;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;

/**
 * Helper class to receive `onActivityResult` and `onRequestPermissionsResult` events from activity or
 * fragment. Once instance is obtained via {@link #create()} method, actual events must be redirected
 * to this instance by calling {@link #onActivityResult(int, int, Intent)}
 * and {@link #onRequestPermissionsResult(int, String[], int[])}. Both of them return a boolean
 * indicating if event was consumed.
 *
 * @see #create()
 */
public abstract class EventSource {

    /**
     * Factory method to obtain default instance of {@link EventSource}
     *
     * @return new instance of {@link EventSource}
     */
    @NonNull
    public static EventSource create() {
        return new EventSourceImpl();
    }

    /**
     * Instance of this class will be returned from {@link #subscribe(Listener)} method. In contains
     * only one method {@link #unsubscribe()}
     */
    public interface Subscription {

        /**
         * Call this method after you no longer interested in receiving events
         */
        void unsubscribe();
    }

    /**
     * Listener to be supplied to {@link #subscribe(Listener)} method.
     */
    public interface Listener {

        /**
         * @return a boolean indicating if event was consumed
         */
        boolean onActivityResult(int requestCode, int resultCode, Intent data);

        /**
         * @return a boolean indicating if event was consumed
         */
        @TargetApi(Build.VERSION_CODES.M)
        boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    }

    /**
     * Holder of this instance (Activity or Fragment) must call this method when `onActivityResult` event is received
     *
     * @return a boolean indicating if event was consumed
     */
    public abstract boolean onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * Holder of this instance (Activity or Fragment) must call this method when `onRequestPermissionsResult` event is received
     *
     * @return a boolean indicating if event was consumed
     */
    @TargetApi(Build.VERSION_CODES.M)
    public abstract boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    /**
     * Initiates a subscription for both events
     *
     * @param listener {@link Listener} to receive events
     * @return {@link Subscription}
     */
    @NonNull
    public abstract Subscription subscribe(@NonNull Listener listener);
}
