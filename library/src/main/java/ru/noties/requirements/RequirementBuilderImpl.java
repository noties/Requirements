package ru.noties.requirements;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

// @since 1.1.0
class RequirementBuilderImpl extends RequirementBuilder {

    private EventDispatcher dispatcher;
    private EventSource source;
    private List<RequirementCase> requirementCases;

    private boolean isBuilt;

    RequirementBuilderImpl(@NonNull EventDispatcher dispatcher, @NonNull EventSource source) {
        this.dispatcher = dispatcher;
        this.source = source;
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
    public Requirement build() {

        checkState();

        isBuilt = true;

        try {
            return new RequirementImpl(
                    dispatcher,
                    source,
                    Collections.unmodifiableList(requirementCases)
            );
        } finally {
            dispatcher = null;
            source = null;
            requirementCases = null;
        }
    }

    private void checkState() {
        if (isBuilt) {
            throw new IllegalStateException("This RequirementBuilder instance has already been built.");
        }
    }
}
