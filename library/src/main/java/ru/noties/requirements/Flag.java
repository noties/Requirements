package ru.noties.requirements;

import android.support.annotation.NonNull;

/**
 * Helper class to store a boolean flag. Can be useful when displaying a Dialog, which evaluates
 * certain logic when dialog is dismissed (when positive button is clicked this instance is updated
 * with `true` value).
 * <p>
 * Changed in 1.1.0 (previously `MutableBool`)
 *
 * @see #create()
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Flag {

    /**
     * Factory method to create {@link Flag}
     *
     * @return an instance of Flag
     * @since 1.1.0
     */
    @NonNull
    public static Flag create() {
        return new Flag();
    }

    private boolean value;

    public Flag() {
    }

    /**
     * Marks current instance.
     *
     * @see #isSet()
     * @since 1.1.0
     */
    public void mark() {
        this.value = true;
    }

    /**
     * @return boolean indicating if {@link #mark()} was called
     * @since 1.1.0
     */
    public boolean isSet() {
        return value;
    }

    @Override
    public String toString() {
        return "Flag{" +
                "value=" + value +
                '}';
    }
}
