package ru.noties.requirements;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.Collection;

/**
 * Builder class to create a {@link Requirement}.
 * <p>
 * Please note that one instance of this builder can be built exactly once. If you need to create
 * multiple {@link Requirement}s that share some information between each other (some common
 * {@link RequirementCase}s please use {@link #fork()} method which will copy current information into a new instance.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public abstract class RequirementBuilder {

    /**
     * Factory method to obtain an instance of {@link RequirementBuilder}
     *
     * @return new instance of {@link RequirementBuilder}
     */
    @NonNull
    public static RequirementBuilder create() {
        return new RequirementBuilderImpl();
    }

    /**
     * Adds a {@link RequirementCase} to this builder
     *
     * @param requirementCase {@link RequirementCase} to add
     * @return this instance for chaining
     */
    @NonNull
    public abstract RequirementBuilder add(@NonNull RequirementCase requirementCase);

    /**
     * Adds a {@link RequirementCase} to this builder only if `result` is true
     *
     * @param result          boolean to check if {@link RequirementCase} should be added
     * @param requirementCase {@link RequirementCase} to add if `result` is true
     * @return this instance for chaining
     */
    @NonNull
    public abstract RequirementBuilder addIf(boolean result, @NonNull RequirementCase requirementCase);

    /**
     * Adds a collection of {@link RequirementCase} to this builder. Please note that collection must
     * not contain `null` as elements
     *
     * @param requirementCases collection of {@link RequirementCase} to add
     * @return this instance for chaining
     */
    @NonNull
    public abstract RequirementBuilder addAll(
            @NonNull Collection<? extends RequirementCase> requirementCases
    );

    /**
     * Adds a collection of {@link RequirementCase} to this builder. Please note that collection must
     * not contain `null` as elements only if `result` is true
     *
     * @param result           boolean to check if collection of {@link RequirementCase} should be added
     * @param requirementCases collection of {@link RequirementCase} to add if `result` is true
     * @return this instance for chaining
     */
    @NonNull
    public abstract RequirementBuilder addAllIf(
            boolean result,
            @NonNull Collection<? extends RequirementCase> requirementCases
    );

    /**
     * @return new instance of this builder with all contents copied.
     */
    @NonNull
    public abstract RequirementBuilder fork();

    /**
     * Please note that if no {@link RequirementCase} were added, then build {@link Requirement}
     * will always be in `success` state
     *
     * @return {@link Requirement}
     * @see EventSource
     */
    @NonNull
    public abstract Requirement build(@NonNull Activity activity, @NonNull EventSource eventSource);
}
