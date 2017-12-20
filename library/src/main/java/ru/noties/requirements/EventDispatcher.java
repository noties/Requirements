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
 * Provides 2 factory methods to work with Activity and Fragment.
 *
 * @see #create(Activity)
 * @see #create(Fragment)
 * @since 1.1.0
 */
public abstract class EventDispatcher<T> {

    @NonNull
    public static <A extends Activity> EventDispatcher<A> create(@NonNull A activity) {
        return new EventDispatcherActivity<>(activity);
    }

    @SuppressWarnings("unused")
    @NonNull
    public static <F extends android.app.Fragment> EventDispatcher<F> create(@NonNull F fragment) {
        return new EventDispatcherFragment<>(fragment);
    }

    @NonNull
    public abstract Activity activity();

    @NonNull
    public abstract T target();


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
