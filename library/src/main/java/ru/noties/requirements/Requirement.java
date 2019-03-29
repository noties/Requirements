package ru.noties.requirements;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import ru.noties.requirements.fragment.FragmentEventController;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class Requirement {

    /**
     * @since 2.0.0
     */
    @NonNull
    public static RequirementBuilder builder(
            @NonNull EventDispatcher eventDispatcher,
            @NonNull EventSource eventSource) {
        return new RequirementBuilder(eventDispatcher, eventSource);
    }

    /**
     * @since 2.0.0
     */
    @NonNull
    public static RequirementBuilder builder(
            @NonNull Activity activity,
            @NonNull EventSource eventSource) {
        return new RequirementBuilder(EventDispatcher.activity(activity), eventSource);
    }

    /**
     * @since 2.0.0
     */
    @NonNull
    public static RequirementBuilder builder(@NonNull EventController eventController) {
        return new RequirementBuilder(eventController.eventDispatcher(), eventController.eventSource());
    }

    /**
     * @see FragmentEventController
     * @see #builder(EventController)
     * @since 2.0.0
     */
    @NonNull
    public static RequirementBuilder builder(@NonNull FragmentActivity fragmentActivity) {
        return builder(FragmentEventController.create(fragmentActivity));
    }

    /**
     * @since 2.0.0
     */
    @NonNull
    public static RequirementBuilder builder(@NonNull Fragment fragment) {
        return builder(FragmentEventController.create(fragment));
    }

    /**
     * @since 2.0.0
     */
    @NonNull
    public static RequirementBuilder builder(@NonNull FragmentManager manager) {
        return builder(FragmentEventController.create(manager));
    }

    /**
     * Listener to be notified about requirement resolution process
     *
     * @see #validate(Listener)
     */
    public interface Listener {

        /**
         * Indicates that requirement is satisfied
         */
        void onRequirementSuccess();

        /**
         * Indicates that requirement resolution was cancelled
         *
         * @param payload {@link Payload} to identify this cancellation event
         */
        void onRequirementFailure(@Nullable Payload payload);

        /**
         * Will be called after requirement resolution has finished
         *
         * @since 2.0.0
         */
        void onComplete();
    }

    /**
     * @since 2.0.0
     */
    public static abstract class ListenerAdapter implements Listener {

        @Override
        public void onRequirementSuccess() {

        }

        @Override
        public void onRequirementFailure(@Nullable Payload payload) {

        }

        @Override
        public void onComplete() {

        }
    }

    /**
     * Please note that this method allows multiple listeners. If resolution process is started and
     * validate was called again, then listener won\'t trigger full validation process, but instead
     * subscribe for the result (when it will be obtained)
     *
     * @param listener {@link Listener} to be notified about resolution progress
     */
    public abstract void validate(@NonNull Listener listener);

    /**
     * Synchronous method to check if the whole requirement cases chain meets requirement. Simply
     * returns true or false without triggering requirement resolution
     *
     * @return a boolean indicating if this requirement is valid (meets requirement)
     * @since 1.1.0
     */
    public abstract boolean isValid();

    /**
     * A method to cancel requirement resolution process. The same as calling {@link #cancel(Payload)}
     * with null payload
     *
     * @see #cancel(Payload)
     * @see #isInProgress()
     * @since 1.0.1
     */
    public abstract void cancel();

    /**
     * A method to cancel requirement resolution process. If there are active listeners, they will
     * receive supplied payload in {@link Listener#onRequirementFailure(Payload)}
     *
     * @param payload {@link Payload} to pass to active listeners of resolution process
     * @since 1.0.1
     */
    public abstract void cancel(@Nullable Payload payload);

    /**
     * A method to check if requirement resolution is currently in progress
     *
     * @return a boolean indicating if requirement resolution is in progress
     * @since 1.0.1
     */
    public abstract boolean isInProgress();

    /**
     * Destroys this {@link Requirement}. Please note this is done automatically
     * when Activity goes through `#onDestroy` automatically
     *
     * @see #isDestroyed()
     * @since 2.0.0
     */
    public abstract void destroy();

    /**
     * @return flag indicating if this {@link Requirement} is destroyed (after Activity natural
     * `#onDetsroy` lifecycle event or via manual call to {@link #destroy()}
     * @see #destroy()
     * @since 2.0.0
     */
    public abstract boolean isDestroyed();
}
