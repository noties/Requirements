package ru.noties.requirements;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

abstract class Preconditions {

    static void checkNonNull(@Nullable Object who, @NonNull String message) {
        if (who == null) {
            throw new IllegalStateException(message);
        }
    }

    private Preconditions() {
    }
}
