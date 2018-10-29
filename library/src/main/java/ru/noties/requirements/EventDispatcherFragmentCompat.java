package ru.noties.requirements;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;

/**
 * @since 2.0.0
 */
public class EventDispatcherFragmentCompat extends EventDispatcher {

    private final Fragment fragment;

    public EventDispatcherFragmentCompat(@NonNull Fragment fragment) {
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public Activity activity() {
        return fragment.requireActivity();
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, int requestCode) {
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.M)
    public void requestPermission(@NonNull String permission, int requestCode) {
        fragment.requestPermissions(new String[]{permission}, requestCode);
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.M)
    public boolean checkSelfPermission(@NonNull String permission) {
        return fragment.requireActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.M)
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return fragment.shouldShowRequestPermissionRationale(permission);
    }
}
