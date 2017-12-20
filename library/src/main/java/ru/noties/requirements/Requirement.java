package ru.noties.requirements;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @see RequirementBuilder
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class Requirement {

    // todo: update javadoc

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
}
