package ru.noties.requirements;

import android.support.annotation.NonNull;

/**
 * Simple interface that provides both {@link EventDispatcher} and {@link EventSource}
 *
 * @since 2.0.0
 */
public interface EventController {

    @NonNull
    EventDispatcher eventDispatcher();

    @NonNull
    EventSource eventSource();
}
