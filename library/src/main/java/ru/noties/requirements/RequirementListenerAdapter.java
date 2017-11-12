package ru.noties.requirements;

import android.support.annotation.Nullable;

/**
 * Simple abstract class that implements all methods from {@link ru.noties.requirements.Requirement.Listener}
 */
@SuppressWarnings("unused")
public abstract class RequirementListenerAdapter implements Requirement.Listener {
    @Override
    public void onRequirementSuccess() {

    }

    @Override
    public void onRequirementFailure(@Nullable Payload payload) {

    }
}
