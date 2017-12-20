package ru.noties.requirements;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

@SuppressWarnings("WeakerAccess")
public class EventDispatcherActivity<A extends Activity> extends EventDispatcher<A> {

    private final A activity;

    public EventDispatcherActivity(@NonNull A activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public Activity activity() {
        return activity;
    }

    @NonNull
    @Override
    public A target() {
        return activity;
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.M)
    public void requestPermission(@NonNull String permission, int requestCode) {
        activity.requestPermissions(new String[]{permission}, requestCode);
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.M)
    public boolean checkSelfPermission(@NonNull String permission) {
        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.M)
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return activity.shouldShowRequestPermissionRationale(permission);
    }
}
