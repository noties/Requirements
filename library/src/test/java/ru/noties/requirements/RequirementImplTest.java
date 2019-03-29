package ru.noties.requirements;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.M)
public class RequirementImplTest {

    private EventDispatcher mockDispatcher;
    private EventSource source;
    private EventSource.Subscription subscription;

    @Before
    public void before() {
        // just a simple dispatcher that mocks activity/application
        mockDispatcher = mock(EventDispatcher.class);

        final Application application = mock(Application.class);
        final Activity activity = mock(Activity.class);
        when(activity.getApplication()).thenReturn(application);

        when(mockDispatcher.activity()).thenReturn(activity);

        source = mock(EventSource.class);
        subscription = mock(EventSource.Subscription.class);
        when(source.subscribe(any(EventSource.Listener.class))).thenReturn(subscription);
    }

    @Test
    public void validate_triggers_event_source_subscription() {
        final RequirementImpl impl = new RequirementImpl(
                mockDispatcher,
                source,
                Collections.<RequirementCase>emptyList());
        impl.validate(mock(Requirement.Listener.class));
        verify(source, times(1)).subscribe(any(EventSource.Listener.class));
    }

    @Test
    public void validate_when_in_progress_does_not_trigger_event_source_subscription() {

        final RequirementImpl impl = new RequirementImpl(
                mockDispatcher,
                source,
                // we will use mock case so it will _hang_ requirement resolution process
                Collections.singletonList(mock(RequirementCase.class)));

        impl.validate(mock(Requirement.Listener.class));
        impl.validate(mock(Requirement.Listener.class));

        verify(source, times(1)).subscribe(any(EventSource.Listener.class));
    }

    @Test
    public void empty_requirement_cases_list_success() {

        // when there are no cases success will be delivered

        final RequirementImpl impl = new RequirementImpl(
                mockDispatcher,
                source,
                Collections.<RequirementCase>emptyList());

        // check for synchronous method
        assertTrue(impl.isValid());

        final Requirement.Listener listener = mock(Requirement.Listener.class);
        impl.validate(listener);

        verify(listener, times(1)).onRequirementSuccess();
        verify(listener, times(1)).onComplete();
        verify(listener, times(0)).onRequirementFailure(any(Payload.class));
    }

    @Test
    public void multiple_listeners_for_single_resolution() {

        // only one event source subscription will be created, but both listeners will be notified

        final class Case extends RequirementCase {

            @Override
            public boolean meetsRequirement() {
                return false;
            }

            @Override
            public void startResolution() {

            }
        }

        final Case requirementCase = new Case();

        final RequirementImpl impl = new RequirementImpl(
                mockDispatcher,
                source,
                Collections.singletonList((RequirementCase) requirementCase));

        final Requirement.Listener listener1 = mock(Requirement.Listener.class);
        final Requirement.Listener listener2 = mock(Requirement.Listener.class);

        impl.validate(listener1);

        assertTrue(impl.isInProgress());

        impl.validate(listener2);

        assertTrue(impl.isInProgress());

        verify(source, times(1)).subscribe(any(EventSource.Listener.class));

        requirementCase.deliverSuccess();

        assertFalse(impl.isInProgress());

        verify(listener1, times(1)).onRequirementSuccess();
        verify(listener1, times(1)).onComplete();

        verify(listener2, times(1)).onRequirementSuccess();
        verify(listener2, times(1)).onComplete();
    }

    @Test
    public void is_valid_attach_detaches_no_subscription() {

        // only one case will be attached at a time
        // each one will go through attach/detach
        // no event source subscription will be triggered

        final List<RequirementCase> cases = new ArrayList<>();
        final AtomicInteger count = new AtomicInteger();

        final class Case extends RequirementCase {

            @Override
            public boolean meetsRequirement() {

                count.incrementAndGet();

                // we must be attached
                assertTrue(isAttached());

                // all other _must_ be detached
                for (RequirementCase requirementCase : cases) {
                    if (requirementCase != this) {
                        assertFalse(requirementCase.isAttached());
                    }
                }

                return true;
            }

            @Override
            public void startResolution() {
                throw new RuntimeException();
            }
        }
        for (int i = 0; i < 10; i++) {
            cases.add(new Case());
        }

        final RequirementImpl impl = new RequirementImpl(
                mockDispatcher,
                source,
                cases);

        assertTrue(impl.isValid());
        assertEquals(10, count.get());

        verify(source, times(0)).subscribe(any(EventSource.Listener.class));
    }

    @Test
    public void is_valid_first_false_breaks() {

        // first false breaks (no cases will be queried after first failure)
        final AtomicInteger count = new AtomicInteger();

        final class Case extends RequirementCase {

            private final boolean meets;

            private Case(boolean meets) {
                this.meets = meets;
            }

            @Override
            public boolean meetsRequirement() {
                count.incrementAndGet();
                return meets;
            }

            @Override
            public void startResolution() {
                throw new RuntimeException();
            }
        }

        final List<RequirementCase> cases = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            cases.add(new Case(i < 5));
        }

        final RequirementImpl impl = new RequirementImpl(
                mockDispatcher,
                source,
                cases);

        assertFalse(impl.isValid());
        assertEquals(6, count.get()); // first false increments
    }

    @Test
    public void cancel_detaches_notifies_failure_unsubscribes() {
        assertCancel(new Cancel() {
            @Override
            public void doCancel(@NonNull RequirementImpl impl) {
                impl.cancel();
            }
        });
    }

    @Test
    public void cancel_payload_detaches_notifies_failure_unsubscribes() {
        assertCancel(new Cancel() {
            @Override
            public void doCancel(@NonNull RequirementImpl impl) {
                impl.cancel(null);
            }
        });
    }

    private interface Cancel {
        void doCancel(@NonNull RequirementImpl impl);
    }

    private void assertCancel(@NonNull Cancel cancel) {

        final class SubscriptionImpl implements EventSource.Subscription {

            private boolean isUnsubscribed;

            @Override
            public void unsubscribe() {
                isUnsubscribed = true;
            }
        }
        final SubscriptionImpl subscription = new SubscriptionImpl();
        when(source.subscribe(any(EventSource.Listener.class))).thenReturn(subscription);

        final List<RequirementCase> cases = Arrays.asList(mock(RequirementCase.class), mock(RequirementCase.class));

        final RequirementImpl impl = new RequirementImpl(
                mockDispatcher,
                source,
                cases);

        final Requirement.Listener listener = mock(Requirement.Listener.class);

        impl.validate(listener);

        assertTrue(impl.isInProgress());
        assertFalse(subscription.isUnsubscribed);

        verify(source, times(1)).subscribe(any(EventSource.Listener.class));

        cancel.doCancel(impl);

        assertFalse(impl.isInProgress());
        assertTrue(subscription.isUnsubscribed);

        for (RequirementCase requirementCase : cases) {
            assertFalse(requirementCase.isAttached());
        }

        verify(listener, times(1)).onRequirementFailure((Payload) any());
        verify(listener, times(1)).onComplete();
    }

    @Test
    public void activity_destroyed_detach_unsubscribe_cleanup() {
        assertDestroy(new Destroy() {
            @Override
            public void doDestroy(@NonNull Activity activity, @NonNull RequirementImpl impl, @NonNull Application.ActivityLifecycleCallbacks callbacks) {
                callbacks.onActivityDestroyed(activity);
            }
        });
    }

    @Test
    public void manual_destroy_detach_unsubscribe_cleanup() {
        assertDestroy(new Destroy() {
            @Override
            public void doDestroy(@NonNull Activity activity, @NonNull RequirementImpl impl, @NonNull Application.ActivityLifecycleCallbacks callbacks) {
                impl.destroy();
            }
        });
    }

    private void assertDestroy(@NonNull Destroy destroy) {

        final class SubscriptionImpl implements EventSource.Subscription {

            private boolean isUnsubscribed;

            @Override
            public void unsubscribe() {
                isUnsubscribed = true;
            }
        }
        final SubscriptionImpl subscription = new SubscriptionImpl();

        final ArgumentCaptor<Application.ActivityLifecycleCallbacks> captor =
                ArgumentCaptor.forClass(Application.ActivityLifecycleCallbacks.class);

        final Application application = mock(Application.class);
        final Activity activity = mock(Activity.class);
        when(activity.getApplication()).thenReturn(application);
        final EventDispatcher dispatcher = mock(EventDispatcher.class);
        when(dispatcher.activity()).thenReturn(activity);

        when(source.subscribe(any(EventSource.Listener.class))).thenReturn(subscription);

        final RequirementCase requirementCase = mock(RequirementCase.class);

        final RequirementImpl impl = new RequirementImpl(
                dispatcher,
                source,
                Collections.singletonList(requirementCase));

        final Requirement.Listener listener = mock(Requirement.Listener.class);

        impl.validate(listener);

        assertTrue(impl.isInProgress());
        assertTrue(requirementCase.isAttached());
        assertFalse(subscription.isUnsubscribed);
        assertFalse(impl.isDestroyed());

        verify(application, times(1)).registerActivityLifecycleCallbacks(captor.capture());

        // must be activity which dispatcher returns
        final Application.ActivityLifecycleCallbacks callbacks = captor.getValue();
        destroy.doDestroy(activity, impl, callbacks);

        verify(application, times(1)).unregisterActivityLifecycleCallbacks(captor.capture());
        assertSame(callbacks, captor.getValue());

        assertFalse(impl.isInProgress());
        assertFalse(requirementCase.isAttached());
        assertTrue(subscription.isUnsubscribed);
        assertTrue(impl.isDestroyed());

        // these methods won't throw
        impl.cancel();
        impl.cancel(null);
        impl.isInProgress();
        impl.destroy();

        // now, all methods will throw
        final Runnable[] runnables = {
                new Runnable() {
                    @Override
                    public void run() {
                        impl.validate(mock(Requirement.Listener.class));
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        impl.isValid();
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        impl.onActivityResult(0, 0, null);
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        impl.onRequestPermissionsResult(0, null, null);
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        impl.onRequirementCaseResult(true, null);
                    }
                }
        };

        final String message = "This Requirement instance has been destroyed";
        for (Runnable runnable : runnables) {
            try {
                runnable.run();
                fail();
            } catch (IllegalStateException e) {
                assertTrue(e.getMessage(), e.getMessage().startsWith(message));
            }
        }
    }

    @Test
    public void on_requirement_result_when_no_pending_throws() {

        final RequirementCase requirementCase = mock(RequirementCase.class);

        final RequirementImpl impl = new RequirementImpl(
                mockDispatcher,
                source,
                Collections.singletonList(requirementCase));

        assertFalse(impl.isInProgress());
        assertFalse(requirementCase.isAttached());

        try {
            impl.onRequirementCaseResult(false, null);
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage(), e.getMessage()
                    .contains("Unexpected state inside Requirement. RequirementCase delivered a result "));
        }
    }

    @Test
    public void cancel_payload_delivered() {

        final RequirementCase requirementCase = mock(RequirementCase.class);

        final RequirementImpl impl = new RequirementImpl(
                mockDispatcher,
                source,
                Collections.singletonList(requirementCase));

        final Requirement.Listener listener = mock(Requirement.Listener.class);

        impl.validate(listener);

        assertTrue(impl.isInProgress());
        assertTrue(requirementCase.isAttached());

        final Payload payload = mock(Payload.class);

        impl.cancel(payload);

        assertFalse(impl.isInProgress());
        assertFalse(requirementCase.isAttached());

        final ArgumentCaptor<Payload> captor = ArgumentCaptor.forClass(Payload.class);

        verify(listener, times(1)).onRequirementFailure(captor.capture());

        assertSame(payload, captor.getValue());
    }

    @Test
    public void case_payload_delivered() {

        final class MyPayload implements Payload {
        }
        final MyPayload myPayload = new MyPayload();

        final RequirementCase requirementCase = new RequirementCase() {
            @Override
            public boolean meetsRequirement() {
                return false;
            }

            @Override
            public void startResolution() {
                // no op
            }
        };

        final RequirementImpl impl = new RequirementImpl(
                mockDispatcher,
                source,
                Collections.singletonList(requirementCase));

        final Requirement.Listener listener = mock(Requirement.Listener.class);
        final ArgumentCaptor<Payload> captor = ArgumentCaptor.forClass(Payload.class);

        impl.validate(listener);

        requirementCase.deliverFailure(myPayload);

        verify(listener, times(1)).onRequirementFailure(captor.capture());
        verify(listener, times(1)).onComplete();

        assertSame(myPayload, captor.getValue());
    }

    private interface Destroy {
        void doDestroy(@NonNull Activity activity, @NonNull RequirementImpl impl, @NonNull Application.ActivityLifecycleCallbacks callbacks);
    }
}