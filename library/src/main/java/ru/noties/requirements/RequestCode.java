package ru.noties.requirements;

import android.support.annotation.NonNull;

/**
 * Utility class to generate request codes, that can be used in `startActivityForResult` and
 * `requestPermissions`
 *
 * @see #createRequestCode(String)
 */
@SuppressWarnings("WeakerAccess")
public abstract class RequestCode {

    public static final int MAX = 0xffff;

    public static int createRequestCode(@NonNull String tag) {
        return Math.abs(tag.hashCode() % MAX);
    }

    private RequestCode() {
    }
}
