package ru.noties.requirements;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PreconditionsTest {

    @Test
    public void check_not_null() {

        try {
            Preconditions.checkNonNull(new Object(), "message");
            assertTrue(true);
        } catch (Throwable t) {
            fail();
        }

        try {
            Preconditions.checkNonNull(null, "it is null 654");
            fail();
        } catch (Throwable t) {
            assertEquals(t.getMessage(), "it is null 654", t.getMessage());
        }
    }
}