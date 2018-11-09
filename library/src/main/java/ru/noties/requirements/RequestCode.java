package ru.noties.requirements;

import android.support.annotation.NonNull;

/**
 * Utility class to generate request codes, that can be used in `startActivityForResult` and
 * `requestPermissions`
 *
 * @see #createRequestCode(String)
 * @see #createRequestCode(Class)
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class RequestCode {

    // this is Android restriction on requestCodes, one cannot use a value higher than that.
    // previously Fragments were using even lower value (0xff), but now it seems to be changed
    // to use the same 0xFFFF value
    public static final int MAX = 0xffff;

    // this method will also make resulting value unsigned (negative request codes are also
    // handled not so good by Android)
    public static int createRequestCode(@NonNull String tag) {
        return Math.abs(tag.hashCode() % MAX);
    }

    public static int createRequestCode(@NonNull Class<?> type) {
        return createRequestCode(type.getName());
    }

    private RequestCode() {
    }
}
