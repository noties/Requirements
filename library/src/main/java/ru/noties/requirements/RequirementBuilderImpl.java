package ru.noties.requirements;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class RequirementBuilderImpl extends RequirementBuilder {

    private List<RequirementCase> requirementCases;

    private boolean isBuilt;

    RequirementBuilderImpl() {
        this.requirementCases = new ArrayList<>(3);
    }

    @NonNull
    @Override
    public RequirementBuilder add(@NonNull RequirementCase requirementCase) {

        checkState();

        requirementCases.add(requirementCase);

        return this;
    }

    @NonNull
    @Override
    public RequirementBuilder addIf(boolean result, @NonNull RequirementCase requirementCase) {

        checkState();

        if (result) {
            add(requirementCase);
        }

        return this;
    }

    @NonNull
    @Override
    public RequirementBuilder addAll(@NonNull Collection<? extends RequirementCase> requirementCases) {

        checkState();

        for (RequirementCase requirementCase : requirementCases) {
            Preconditions.checkNonNull(requirementCase, "Cannot add null RequirementCase");
            add(requirementCase);
        }

        return this;
    }

    @NonNull
    @Override
    public RequirementBuilder addAllIf(boolean result, @NonNull Collection<? extends RequirementCase> requirementCases) {

        checkState();

        if (result) {
            addAll(requirementCases);
        }

        return this;
    }

    @NonNull
    @Override
    public RequirementBuilder fork() {

        checkState();

        final RequirementBuilderImpl builder = new RequirementBuilderImpl();
        builder.requirementCases.addAll(requirementCases);

        return builder;
    }

    @NonNull
    @Override
    public Requirement build(@NonNull Activity activity, @NonNull EventSource eventSource) {

        checkState();

        isBuilt = true;

        try {
            return new RequirementImpl(
                    activity,
                    eventSource,
                    Collections.unmodifiableList(requirementCases)
            );
        } finally {
            requirementCases = null;
        }
    }

    private void checkState() {
        if (isBuilt) {
            throw new IllegalStateException("This RequirementBuilder instance was already built. If you " +
                    "need to create multiple Requirements sharing some cases, use `fork()` method of this builder " +
                    "before it was built.");
        }
    }
}
