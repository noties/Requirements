package ru.noties.requirements;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

// @since 1.1.0
class RequirementBuilderImpl<T> extends RequirementBuilder<T> {

    private EventDispatcher<T> dispatcher;
    private EventSource source;
    private List<RequirementCase<? super T>> requirementCases;

    private boolean isBuilt;

    RequirementBuilderImpl(@NonNull EventDispatcher<T> dispatcher, @NonNull EventSource source) {
        this.dispatcher = dispatcher;
        this.source = source;
        this.requirementCases = new ArrayList<>(3);
    }

    @NonNull
    @Override
    public RequirementBuilder<T> add(@NonNull RequirementCase<? super T> requirementCase) {

        checkState();

        requirementCases.add(requirementCase);

        return this;
    }

    @NonNull
    @Override
    public RequirementBuilder<T> addIf(boolean result, @NonNull RequirementCase<? super T> requirementCase) {

        checkState();

        if (result) {
            add(requirementCase);
        }

        return this;
    }

    @NonNull
    @Override
    public RequirementBuilder<T> addAll(@NonNull Collection<? extends RequirementCase<? super T>> requirementCases) {

        checkState();

        for (RequirementCase<? super T> requirementCase : requirementCases) {
            Preconditions.checkNonNull(requirementCase, "Cannot add null RequirementCase");
            add(requirementCase);
        }

        return this;
    }

    @NonNull
    @Override
    public RequirementBuilder<T> addAllIf(boolean result, @NonNull Collection<? extends RequirementCase<? super T>> requirementCases) {

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
                    Collections.unmodifiableList((List<? extends RequirementCase>) requirementCases)
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
