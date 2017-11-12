package ru.noties.requirements;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @see RequirementBuilder
 * @see RequirementBuilder#create()
 */
public abstract class Requirement {

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
}
