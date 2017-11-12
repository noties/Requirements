package ru.noties.requirements;

/**
 * Helper class to store a boolean flag. Can be useful when displaying a Dialog, which evaluates
 * certain logic when dialog is dismissed (when positive button is clicked this instance is updated
 * with `true` value).
 */
public class MutableBool {

    private boolean value;

    public MutableBool() {
        this(false);
    }

    @SuppressWarnings("WeakerAccess")
    public MutableBool(boolean value) {
        this.value = value;
    }

    public boolean value() {
        return value;
    }

    @SuppressWarnings("SameParameterValue")
    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MutableBool{" +
                "value=" + value +
                '}';
    }
}
