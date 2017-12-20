package ru.noties.requirements;

import android.support.annotation.NonNull;

/**
 * Helper class to store a boolean flag. Can be useful when displaying a Dialog, which evaluates
 * certain logic when dialog is dismissed (when positive button is clicked this instance is updated
 * with `true` value).
 *
 * @see #create()
 * @see #synchronizedFlag()
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Flag {

    @NonNull
    public static Flag create() {
        return new Flag();
    }

    @NonNull
    public static Flag synchronizedFlag() {
        return new SyncFlag();
    }

    private boolean value;

    public Flag() {
    }

    public void mark() {
        this.value = true;
    }

    public boolean isSet() {
        return value;
    }

    @Override
    public String toString() {
        return "Flag{" +
                "value=" + value +
                '}';
    }

    private static class SyncFlag extends Flag {

        @Override
        public synchronized void mark() {
            super.mark();
        }

        @Override
        public synchronized boolean isSet() {
            return super.isSet();
        }

        @Override
        public synchronized String toString() {
            return super.toString();
        }
    }
}
