package ru.noties.requirements.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;

import ru.noties.requirements.EventDispatcher;

class FragmentDispatcher extends EventDispatcher {

    private final Fragment fragment;

    FragmentDispatcher(@NonNull Fragment fragment) {
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
    public void requestPermission(@NonNull String permission, int requestCode) {
        fragment.requestPermissions(new String[]{permission}, requestCode);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Override
    public boolean checkSelfPermission(@NonNull String permission) {
        // this method is not in Fragment, but here it's safe to re-route it to activity
        return activity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return fragment.shouldShowRequestPermissionRationale(permission);
    }
}
