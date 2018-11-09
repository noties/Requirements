package ru.noties.requirements;

import android.support.annotation.NonNull;

import java.util.Collection;

/**
 * Builder class to create a {@link Requirement}.
 * <p>
 * Please note that one instance of this builder can be built exactly once.
 * <p>
 * Changed in 1.1.0: added type argument
 * Changed in 2.0.0: removed type argument
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public abstract class RequirementBuilder {

    /**
     * Factory method to obtain an instance of {@link RequirementBuilder}
     *
     * @return new instance of {@link RequirementBuilder}
     * @see EventDispatcher
     * @see EventSource
     */
    @NonNull
    public static RequirementBuilder create(@NonNull EventDispatcher eventDispatcher, @NonNull EventSource eventSource) {
        return new RequirementBuilderImpl(eventDispatcher, eventSource);
    }

    // maybe add: #create(Activity, EventSource) <- implicitly create ActivityEventDispatcher
    // and


    /**
     * Adds a {@link RequirementCase} to this builder
     *
     * @param requirementCase {@link RequirementCase} to add
     * @return this instance for chaining
     * @throws IllegalStateException if this builder instance had been built already
     */
    @NonNull
    public abstract RequirementBuilder add(@NonNull RequirementCase requirementCase)
            throws IllegalStateException;

    /**
     * Adds a {@link RequirementCase} to this builder only if `result` is true
     *
     * @param result          boolean to check if {@link RequirementCase} should be added
     * @param requirementCase {@link RequirementCase} to add if `result` is true
     * @return this instance for chaining
     * @throws IllegalStateException if this builder instance had been built already
     */
    @NonNull
    public abstract RequirementBuilder addIf(boolean result, @NonNull RequirementCase requirementCase)
            throws IllegalStateException;

    /**
     * Adds a collection of {@link RequirementCase} to this builder. Please note that collection must
     * not contain `null` as elements
     *
     * @param requirementCases collection of {@link RequirementCase} to add
     * @return this instance for chaining
     * @throws IllegalStateException if this builder instance had been built already
     */
    @NonNull
    public abstract RequirementBuilder addAll(
            @NonNull Collection<? extends RequirementCase> requirementCases
    ) throws IllegalStateException;

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
    public abstract RequirementBuilder addAllIf(
            boolean result,
            @NonNull Collection<? extends RequirementCase> requirementCases
    ) throws IllegalStateException;

    /**
     * Please note that if no {@link RequirementCase} were added, then build {@link Requirement}
     * will always be in `success` state
     *
     * @return {@link Requirement}
     * @throws IllegalStateException if this builder instance had been built already
     * @see EventSource
     */
    @NonNull
    public abstract Requirement build() throws IllegalStateException;
}
