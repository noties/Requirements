package ru.noties.requirements;

import android.content.Intent;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.noties.requirements.EventSource.Listener;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.M)
public class EventSourceImplTest {

    private EventSourceImpl impl;

    @Before
    public void before() {
        impl = new EventSourceImpl();
    }

    @Test
    public void empty() {
        assertFalse(impl.onActivityResult(0, 0, null));
        assertFalse(impl.onRequestPermissionsResult(0, null, null));
    }

    @Test
    public void on_activity_result_single() {

        final Listener listener = mock(Listener.class);

        final Intent intent = mock(Intent.class);

        // if any(...) used then mocked method is not called, wtf
        // maybe not mocked intent had to do something with it?
        when(listener.onActivityResult(eq(12), eq(0), eq(intent))).thenReturn(true);

        assertNotNull(impl.subscribe(listener));

        assertFalse(impl.onActivityResult(11, 0, intent));
        assertTrue(impl.onActivityResult(12, 0, intent));
        assertTrue(impl.onActivityResult(12, 0, intent));

        verify(listener, times(2)).onActivityResult(eq(12), eq(0), eq(intent));
        verify(listener, times(0)).onRequestPermissionsResult(anyInt(), any(String[].class), any(int[].class));
    }

    @Test
    public void on_activity_result_multiple() {

        final Listener first = mock(Listener.class);
        final Listener second = mock(Listener.class);

        final Intent intent = mock(Intent.class);

        when(first.onActivityResult(eq(12), eq(0), eq(intent))).thenReturn(true);
        when(second.onActivityResult(eq(13), eq(0), eq(intent))).thenReturn(true);

        assertNotNull(impl.subscribe(first));
        assertNotNull(impl.subscribe(second));

        assertFalse(impl.onActivityResult(11, 0, intent));
        assertTrue(impl.onActivityResult(12, 0, intent));
        assertTrue(impl.onActivityResult(13, 0, intent));
        assertFalse(impl.onActivityResult(14, 0, intent));

        verify(first, times(4)).onActivityResult(anyInt(), anyInt(), eq(intent));
        verify(second, times(4)).onActivityResult(anyInt(), anyInt(), eq(intent));

        verify(first, times(0)).onRequestPermissionsResult(anyInt(), any(String[].class), any(int[].class));
        verify(second, times(0)).onRequestPermissionsResult(anyInt(), any(String[].class), any(int[].class));
    }

    @Test
    public void on_request_permission_result() {

        final Listener first = mock(Listener.class);
        final Listener second = mock(Listener.class);

        final String[] permissions = new String[0];
        final int[] results = new int[0];

        // it looks like a bug in mockito (lack of testing?!) that when `any` used mocked
        // method is not invoked
        when(first.onRequestPermissionsResult(eq(12), eq(permissions), eq(results))).thenReturn(true);
        when(second.onRequestPermissionsResult(eq(13), eq(permissions), eq(results))).thenReturn(true);

        assertNotNull(impl.subscribe(first));
        assertNotNull(impl.subscribe(second));

        assertFalse(impl.onRequestPermissionsResult(11, permissions, results));
        assertTrue(impl.onRequestPermissionsResult(12, permissions, results));
        assertTrue(impl.onRequestPermissionsResult(13, permissions, results));
        assertFalse(impl.onRequestPermissionsResult(14, permissions, results));

        verify(first, times(0)).onActivityResult(anyInt(), anyInt(), any(Intent.class));
        verify(second, times(0)).onActivityResult(anyInt(), anyInt(), any(Intent.class));

        verify(first, times(4)).onRequestPermissionsResult(anyInt(), any(String[].class), any(int[].class));
        verify(second, times(4)).onRequestPermissionsResult(anyInt(), any(String[].class), any(int[].class));
    }
}