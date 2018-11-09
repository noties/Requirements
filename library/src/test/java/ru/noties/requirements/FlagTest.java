package ru.noties.requirements;

import android.support.annotation.NonNull;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FlagTest {

    @Test
    public void test() {
        assertFlag(Flag.create());
    }

    private void assertFlag(@NonNull Flag flag) {

        assertFalse(flag.isSet());

        flag.mark();

        assertTrue(flag.isSet());
    }

}