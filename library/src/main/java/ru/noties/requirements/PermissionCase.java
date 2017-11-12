package ru.noties.requirements;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;

/**
 * A helper case to ease the pressure working with Android permissions. {@link #PermissionCase(String)}
 * takes single permission (which is good in terms of UX instead of String[] like Activity.requestPermissions
 * suggests). The only method that must be overriden is `showPermissionRationale`. This class contains
 * few helper methods:
 * <ul>
 * <li>{@link #requestPermission()}
 * <li>{@link #requestPermission(String, int)}
 * <li>{@link #checkSelfPermission(String)}
 * <li>{@link #navigateToSettingsScreen()}
 * </ul>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@TargetApi(Build.VERSION_CODES.M)
public abstract class PermissionCase extends RequirementCase {

    private final String permission;
    private final int requestCode;

    public PermissionCase(@NonNull String permission) {
        this(permission, RequestCode.createRequestCode(permission));
    }

    public PermissionCase(@NonNull String permission, int requestCode) {
        this.permission = permission;
        this.requestCode = requestCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean meetsRequirement() {
        return checkSelfPermission(permission);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startResolution() {
        if (shouldShowRequestPermissionRationale(permission)) {
            showPermissionRationale();
        } else {
            requestPermission();
        }
    }

    /**
     * Should display rationale. Further actions must be taken in this method: either {@link #deliverResult(Result)}
     * with cancellation event or {@link #requestPermission()}
     */
    protected abstract void showPermissionRationale();

    /**
     * Override this method if you would like to display a confirmation to a user when he/she
     * checks `never` checkbox on the system permission request dialog. Please note that default
     * implementation exits immediately with cancellation event
     */
    protected void showExplanationOnNever() {
        deliverResult(Result.FAILURE);
    }

    /**
     * This method must be called after rationale was shown ({@link #showPermissionRationale()}) and
     * user gave his/her agreement
     */
    protected void requestPermission() {
        activity().requestPermissions(new String[]{permission}, requestCode);
    }

    /**
     * Opens settings screen of the application. This method must be used <strong>only</strong> if
     * you show some confirmation in {@link #showExplanationOnNever()}, which should indicate further
     * user actions
     */
    protected void navigateToSettingsScreen() {
        final Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", appContext().getPackageName(), null)
        );
        startActivityForResult(intent, requestCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.requestCode == requestCode) {
            final Result result = meetsRequirement()
                    ? Result.SUCCESS
                    : Result.FAILURE;
            deliverResult(result);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (this.requestCode == requestCode) {
            if (grantResults.length > 0
                    && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                deliverResult(Result.SUCCESS);
            } else {
                // check if user turned on NEVER
                if (!shouldShowRequestPermissionRationale(permission)) {
                    // never
                    showExplanationOnNever();
                } else {
                    deliverResult(Result.FAILURE);
                }
            }
            return true;
        }
        return false;
    }

    @NonNull
    public String permission() {
        return permission;
    }

    public int requestCode() {
        return requestCode;
    }

    @Override
    public String toString() {
        return "PermissionCase{" +
                "permission='" + permission + '\'' +
                ", requestCode=" + requestCode +
                '}';
    }
}
