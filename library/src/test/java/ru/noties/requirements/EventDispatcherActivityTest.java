package ru.noties.requirements;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.M)
public class EventDispatcherActivityTest {

    @Test
    public void all_methods_redirected() {

        final Activity activity = mock(Activity.class);
        final EventDispatcherActivity dispatcher = EventDispatcher.activity(activity);

        assertEquals(activity, dispatcher.activity());

        final String permission = "permission";

        dispatcher.checkSelfPermission(permission);
        dispatcher.requestPermission(permission, 12);
        dispatcher.shouldShowRequestPermissionRationale(permission);
        dispatcher.startActivityForResult(mock(Intent.class), 13);

        // `requestPermission` is final... but test is not failing.. did robolectric make changes?
        // it looks they did, here this method is not final (it's good!)
//        try {
//            final Method method = Activity.class.getDeclaredMethod("requestPermissions", String[].class, int.class);
//            System.out.printf("isFinal: %s%n", Modifier.isFinal(method.getModifiers()));
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }

        verify(activity, times(1)).checkSelfPermission(anyString());
        verify(activity, times(1)).requestPermissions(any(String[].class), anyInt());
        verify(activity, times(1)).shouldShowRequestPermissionRationale(anyString());
        verify(activity, times(1)).startActivityForResult(any(Intent.class), anyInt());
    }

}