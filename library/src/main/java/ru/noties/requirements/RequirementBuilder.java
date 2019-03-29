package ru.noties.requirements;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Builder class to create a {@link Requirement}.
 * <p>
 * Please note that one instance of this builder can be built exactly once.
 * <p>
 * Changed in 1.1.0: added type argument
 * Changed in 2.0.0: removed type argument
 */
public class RequirementBuilder {

    private EventDispatcher dispatcher;
    private EventSource source;
    private List<RequirementCase> requirementCases;

    private boolean isBuilt;

    public RequirementBuilder(
            @NonNull EventDispatcher dispatcher,
            @NonNull EventSource source) {
        this.dispatcher = dispatcher;
        this.source = source;
        this.requirementCases = new ArrayList<>(3);
    }

    /**
     * Adds a {@link RequirementCase} to this builder
     *
     * @param requirementCase {@link RequirementCase} to add
     * @return this instance for chaining
     * @throws IllegalStateException if this builder instance had been built already
     */
    @NonNull
    public RequirementBuilder add(@NonNull RequirementCase requirementCase) {

        checkState();

        requirementCases.add(requirementCase);

        return this;
    }

    /**
     * Adds a {@link RequirementCase} to this builder only if `result` is true
     *
     * @param result          boolean to check if {@link RequirementCase} should be added
     * @param requirementCase {@link RequirementCase} to add if `result` is true
     * @return this instance for chaining
     * @throws IllegalStateException if this builder instance had been built already
     */
    @NonNull
    public RequirementBuilder addIf(
            boolean result,
            @NonNull RequirementCase requirementCase) {

        checkState();

        if (result) {
            add(requirementCase);
        }

        return this;
    }

    /**
     * Adds a collection of {@link RequirementCase} to this builder. Please note that collection must
     * not contain `null` as elements
     *
     * @param requirementCases collection of {@link RequirementCase} to add
     * @return this instance for chaining
     * @throws IllegalStateException if this builder instance had been built already
     */
    @NonNull
    public RequirementBuilder addAll(@NonNull Collection<? extends RequirementCase> requirementCases) {

        checkState();

        for (RequirementCase requirementCase : requirementCases) {
            Preconditions.checkNonNull(requirementCase, "Cannot add null RequirementCase");
            add(requirementCase);
        }

        return this;
    }

    /**
     * Adds a collection of {@link RequirementCase} to this builder. Please note that collection must
     * not contain `null` as elements only if `result` is true
     *
     * @param result           boolean to check if collection of {@link RequirementCase} should be added
     * @param requirementCases collection of {@link RequirementCase} to add if `result` is true
     * @return this instance for chaining
     * @throws IllegalStateException if this builder instance had been built already
     */
    @NonNull
    public RequirementBuilder addAllIf(
            boolean result,
            @NonNull Collection<? extends RequirementCase> requirementCases) {

        checkState();

        if (result) {
            addAll(requirementCases);
        }

        return this;
    }

    /**
     * Please note that if no {@link RequirementCase} were added, then build {@link Requirement}
     * will always be in `success` state
     *
     * @return {@link Requirement}
     * @throws IllegalStateException if this builder instance had been built already
     * @see EventSource
     */
    @NonNull
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
