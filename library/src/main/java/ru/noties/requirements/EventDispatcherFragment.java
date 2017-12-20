package ru.noties.requirements;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

@SuppressWarnings("WeakerAccess")
public class EventDispatcherFragment<F extends Fragment> extends EventDispatcher<F> {

    private final F fragment;

    public EventDispatcherFragment(@NonNull F fragment) {
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public Activity activity() {
        return fragment.getActivity();
    }

    @NonNull
    @Override
    public F target() {
        return fragment;
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
        return fragment.getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.M)
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return fragment.shouldShowRequestPermissionRationale(permission);
    }
}
