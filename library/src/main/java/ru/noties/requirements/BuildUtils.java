package ru.noties.requirements;

import android.os.Build;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.os.Build.VERSION_CODES.KITKAT_WATCH;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;
import static android.os.Build.VERSION_CODES.M;
import static android.os.Build.VERSION_CODES.N;
import static android.os.Build.VERSION_CODES.N_MR1;
import static android.os.Build.VERSION_CODES.O;

/**
 * Utility class to check if certain features of Android SDK can be used
 *
 * @see #isAtLeast(int)
 */
public abstract class BuildUtils {

    @IntDef({
            JELLY_BEAN, // 4.1
            JELLY_BEAN_MR1, // 4.2
            JELLY_BEAN_MR2, // 4.3
            KITKAT, // 4.4
            KITKAT_WATCH, // 4.4W
            LOLLIPOP, // 5.0
            LOLLIPOP_MR1, // 5.1
            M, // 6.0
            N, // 7.0
            N_MR1, // 7.1
            O // 8.0
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface SdkVersion {
    }

    public static boolean isAtLeast(@SdkVersion int sdkVersion) {
        return VERSION >= sdkVersion;
    }

    private static final int VERSION = Build.VERSION.SDK_INT;

    private BuildUtils() {
    }
}
