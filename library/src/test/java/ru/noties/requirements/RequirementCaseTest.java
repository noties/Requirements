package ru.noties.requirements;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.M)
public class RequirementCaseTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void no_attached_throws() {

        // all methods that rely on attached state -> will throw if not attached
        //
        // #appContext -> activity -> dispatcher
        // #activity -> dispatcher
        // -#callback // private
        // -#dispatcher // private
        // #deliverResult -> dispatcher
        // #startActivityForResult -> dispatcher
        // #requestPermission -> dispatcher
        // #checkSelfPermission -> dispatcher
        // #shouldShowRequestPermissionRationale -> dispatcher

        final RequirementCase requirementCase = new RequirementCase() {
            @Override
            public boolean meetsRequirement() {
                return false;
            }

            @Override
            public void startResolution() {

            }
        };

        final String message = "This requirement case is not attached: ";

        final Runnable[] runnables = {
                new Runnable() {
                    @Override
                    public void run() {
                        requirementCase.appContext();
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        requirementCase.activity();
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        requirementCase.deliverResult(false);
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        requirementCase.startActivityForResult(null, 0);
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        requirementCase.requestPermission(null, 0);
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        requirementCase.checkSelfPermission(null);
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        requirementCase.shouldShowRequestPermissionRationale(null);
                    }
                }
        };

        for (Runnable runnable : runnables) {
            assertThrows(message, runnable);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static void assertThrows(@NonNull String messageStarts, @NonNull Runnable runnable) {
        try {
            runnable.run();
            fail();
        } catch (Throwable t) {
            assertTrue(t.getMessage(), t.getMessage().startsWith(messageStarts));
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void attached_redirects() {

        final EventDispatcher dispatcher = mock(EventDispatcher.class);
        final RequirementCase.Callback callback = mock(RequirementCase.Callback.class);

        // in order to not receive an NPE:
        when(dispatcher.activity()).thenReturn(mock(Activity.class));

        // cannot match null
        final Intent intent = mock(Intent.class);
        final String permission = "whatever-dude";

        final RequirementCase requirementCase = new RequirementCase() {
            @Override
            public boolean meetsRequirement() {
                return false;
            }

            @Override
            public void startResolution() {

            }
        };
        requirementCase.attach(dispatcher, callback);

        // 6 -> dispatcher (total)
        requirementCase.appContext();
        requirementCase.activity();
        requirementCase.startActivityForResult(intent, 0);
        requirementCase.requestPermission(permission, 0);
        requirementCase.checkSelfPermission(permission);
        requirementCase.shouldShowRequestPermissionRationale(permission);

        // #appContext + #activity
        verify(dispatcher, times(2)).activity();

        verify(dispatcher, times(1)).startActivityForResult((Intent) any(), anyInt());
        verify(dispatcher, times(1)).requestPermission(anyString(), anyInt());
        verify(dispatcher, times(1)).checkSelfPermission(anyString());
        verify(dispatcher, times(1)).shouldShowRequestPermissionRationale(anyString());

        // 2 -> callback
        requirementCase.deliverResult(true);
        requirementCase.deliverResult(true, null);

        verify(callback, times(2)).onRequirementCaseResult(anyBoolean(), (Payload) any());
    }
}