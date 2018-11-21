package ru.noties.requirements;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

/**
 * Represents stateless requirement case.
 * <p>
 * Library provides a subclass for easier interaction with Android Permissions, see: {@link PermissionCase}
 * <p>
 * Changed in 1.1.0: added type parameter to indicate `target` of this requirement case
 * Changed in 2.0.0: removed type parameter
 *
 * @see #meetsRequirement()
 * @see #startResolution()
 * @see PermissionCase
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class RequirementCase {

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


    private EventDispatcher dispatcher;
    private Callback callback;


    public final void attach(@NonNull EventDispatcher dispatcher, @NonNull Callback callback) {
        this.dispatcher = dispatcher;
        this.callback = callback;
    }

    public final void detach() {
        this.dispatcher = null;
        this.callback = null;
    }

    /**
     * @since 2.0.0
     */
    public final boolean isAttached() {
        return dispatcher != null && callback != null;
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
        return activity().getApplicationContext();
    }

    /**
     * Please note that `startActivityForResult` and according permission methods must be
     * started via defined in this class methods: {@link #startActivityForResult(Intent, int)},
     * {@link #requestPermission(String, int)}, {@link #checkSelfPermission(String)},
     * {@link #shouldShowRequestPermissionRationale(String)} as they will properly use
     * {@link EventDispatcher} that is supplied to this class. Usage of these methods directly on
     * Activity instance might break things especially if custom {@link EventSource} is used.
     *
     * @return Activity to which this requirement case currently attached
     */
    @NonNull
    protected Activity activity() {
        return dispatcher().activity();
    }

    @NonNull
    private Callback callback() {
        Preconditions.checkNonNull(callback, "This requirement case is not attached: " + getClass().getSimpleName());
        return callback;
    }

    // @since 1.1.0
    @NonNull
    private EventDispatcher dispatcher() {
        Preconditions.checkNonNull(dispatcher, "This requirement case is not attached: " + getClass().getSimpleName());
        return dispatcher;
    }

    /**
     * Call this method after resolution has finished (in whatever case: success or cancellation)
     *
     * @param result true for success, false for failure
     * @since 1.0.1
     */
    protected void deliverResult(boolean result) {
        callback().onRequirementCaseResult(result, null);
    }

    /**
     * @since 2.0.0
     */
    protected void deliverSuccess() {
        callback().onRequirementCaseResult(true, null);
    }

    /**
     * The same as {@link #deliverFailure(Payload)} with `null` as payload argument
     *
     * @see #deliverFailure(Payload)
     * @since 2.0.0
     */
    protected void deliverFailure() {
        deliverFailure(null);
    }

    /**
     * @param payload {@link Payload} to be delivered as a failure reason
     * @since 2.0.0
     */
    protected void deliverFailure(@Nullable Payload payload) {
        callback().onRequirementCaseResult(false, payload);
    }

    /**
     * Call this method after resolution has finished (in whatever case: success or cancellation)
     *
     * @param result  true for success, false for failure
     * @param payload {@link Payload} to identify _error_ state. Please note that it will be ignored in case of success result
     * @since 1.0.1
     * @deprecated 2.0.0 use {@link #deliverSuccess()}, {@link #deliverFailure()} or {@link #deliverFailure(Payload)}
     */
    @SuppressWarnings({"SameParameterValue", "DeprecatedIsStillUsed"})
    @Deprecated
    protected void deliverResult(boolean result, @Nullable Payload payload) {
        callback().onRequirementCaseResult(result, payload);
    }

    protected void startActivityForResult(@NonNull Intent intent, @IntRange(from = 0, to = RequestCode.MAX) int requestCode) {
        dispatcher().startActivityForResult(intent, requestCode);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    protected void requestPermission(@NonNull String permission, @IntRange(from = 0, to = RequestCode.MAX) int requestCode) {
        dispatcher().requestPermission(permission, requestCode);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    protected boolean checkSelfPermission(@NonNull String permission) {
        return dispatcher().checkSelfPermission(permission);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    protected boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return dispatcher().shouldShowRequestPermissionRationale(permission);
    }


    interface Callback {
        // @since 1.0.1
        void onRequirementCaseResult(boolean result, @Nullable Payload payload);
    }
}
