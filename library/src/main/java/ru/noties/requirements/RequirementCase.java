package ru.noties.requirements;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Represents stateless requirement case.
 * <p>
 * Library provides a subclass for easier interaction with Android Permissions, see: {@link PermissionCase}
 *
 * @see #meetsRequirement()
 * @see #startResolution()
 * @see PermissionCase
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class RequirementCase {

    public enum Result {

        /**
         * Indicates that requirement successfully resolved
         */
        SUCCESS,

        /**
         * Indicates that requirement resolution process was cancelled, was not resolved
         * or cannot be resolved. One can provide {@link Payload} to identify exact case
         */
        FAILURE
    }

    /**
     * Synchronous method to check if requirement is satisfied
     *
     * @return a boolean indicating if this requirement is satisfied
     */
    public abstract boolean meetsRequirement();

    /**
     * Starts requirement resolution process. This method will be called only if
     * {@link #meetsRequirement()} returns <i>false</i>
     */
    public abstract void startResolution();


    private Activity activity;
    private Callback callback;


    public final void attach(@NonNull Activity activity, @NonNull Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public final void detach() {
        this.activity = null;
        this.callback = null;
    }

    /**
     * Please override this method if your {@link RequirementCase} need to receive `onActivityResult` event
     *
     * @return boolean indicating if this event was consumed
     */
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }

    /**
     * Please override this method if your {@link RequirementCase} need to receive `onRequestPermissionsResult` event
     *
     * @return boolean indicating if this event was consumed
     */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        return false;
    }

    /**
     * @return application Context (only if this case is attached)
     */
    @NonNull
    protected Context appContext() {
        Preconditions.checkNonNull(activity, "This requirement is not attached to activity: " + getClass().getSimpleName());
        return activity.getApplicationContext();
    }

    /**
     * @return Activity to which this requirement case currently attached
     */
    @NonNull
    protected Activity activity() {
        Preconditions.checkNonNull(activity, "This requirement is not attached to activity: " + getClass().getSimpleName());
        return activity;
    }

    @NonNull
    private Callback callback() {
        Preconditions.checkNonNull(callback, "This requirement is not attached to activity: " + getClass().getSimpleName());
        return callback;
    }

    /**
     * Call this method after resolution has finished (in whatever case: success or cancellation)
     *
     * @param result {@link Result} to indicate result of requirement resolution
     * @see #deliverResult(Result, Payload)
     */
    protected void deliverResult(@NonNull Result result) {
        deliverResult(result, null);
    }

    /**
     * Call this method after resolution has finished (in whatever case: success or cancellation)
     *
     * @param result  {@link Result} to indicate result of requirement resolution
     * @param payload {@link Payload} to identify _error_ state. Please note that it will be ignored in case of {@link Result#SUCCESS}
     */
    @SuppressWarnings("SameParameterValue")
    protected void deliverResult(@NonNull Result result, @Nullable Payload payload) {
        callback().onRequirementCaseResult(result, payload);
    }

    /**
     * Helper method to `activity().startActivityForResult(Intent,int)`
     */
    protected void startActivityForResult(@NonNull Intent intent, @IntRange(from = 0, to = RequestCode.MAX) int requestCode) {
        activity().startActivityForResult(intent, requestCode);
    }

    /**
     * Helper method to `activity().requestPermissions(String[], int)`
     */
    @TargetApi(Build.VERSION_CODES.M)
    protected void requestPermission(@NonNull String permission, @IntRange(from = 0, to = RequestCode.MAX) int requestCode) {
        activity().requestPermissions(new String[]{permission}, requestCode);
    }

    /**
     * Helper method to `activity().checkSelfPermission(String) == PackageManager.PERMISSION_GRANTED`
     */
    @TargetApi(Build.VERSION_CODES.M)
    protected boolean checkSelfPermission(@NonNull String permission) {
        return activity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Helper method to `activity().shouldShowRequestPermissionRationale(String)`
     */
    @TargetApi(Build.VERSION_CODES.M)
    protected boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return activity().shouldShowRequestPermissionRationale(permission);
    }


    interface Callback {
        void onRequirementCaseResult(@NonNull Result result, @Nullable Payload payload);
    }
}
