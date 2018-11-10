package ru.noties.requirements;

import android.app.Activity;
import android.app.Application;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.M)
public class RequirementImplTest {

    private EventDispatcher mockDispatcher;

    @Before
    public void before() {
        // just a simple dispatcher that mocks activity/application
        mockDispatcher = mock(EventDispatcher.class);

        final Application application = mock(Application.class);
        final Activity activity = mock(Activity.class);
        when(activity.getApplication()).thenReturn(application);

        when(mockDispatcher.activity()).thenReturn(activity);
    }

    @Test
    public void validate_triggers_event_source_subscription() {
        final EventSource source = mock(EventSource.class);
        final RequirementImpl impl = new RequirementImpl(
                mockDispatcher,
                source,
                Collections.<RequirementCase>emptyList());
        impl.validate(mock(Requirement.Listener.class));
        verify(source, times(1)).subscribe(any(EventSource.Listener.class));
    }

    @Test
    public void validate_when_in_progress_does_not_trigger_event_source_subscription() {

        final EventSource source = mock(EventSource.class);
        // we should mock this method so we return non-null
        when(source.subscribe(any(EventSource.Listener.class))).thenReturn(mock(EventSource.Subscription.class));

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
                mock(EventSource.class),
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

        final EventSource source = mock(EventSource.class);
        when(source.subscribe(any(EventSource.Listener.class))).thenReturn(mock(EventSource.Subscription.class));

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
    public void single_case() {
        throw null;
    }

    @Test
    public void multiple_cases() {
        throw null;
    }

    @Test
    public void is_valid_attach_detaches_no_subscription() {
        // only one case will be attached at a time
        // each one will go through attach/detach
        // no event source subscription will be triggered
        // first false breaks (no cases will be queried after first failure)
        throw null;
    }

    @Test
    public void cancel_detaches_notifies_failure_unsubscribes() {
        throw null;
    }

    @Test
    public void activity_destroyed_detach_unsubscribe_cleanup() {

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

        final EventSource source = mock(EventSource.class);
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
        callbacks.onActivityDestroyed(activity);

        verify(application, times(1)).unregisterActivityLifecycleCallbacks(captor.capture());
        assertSame(callbacks, captor.getValue());

        assertFalse(impl.isInProgress());
        assertFalse(requirementCase.isAttached());
        assertTrue(subscription.isUnsubscribed);
        assertTrue(impl.isDestroyed());

        // now, all methods will throw
    }

    @Test
    public void on_requirement_result_when_no_pending_throws() {
        throw null;
    }

    @Test
    public void on_requirement_result_success_next() {
        throw null;
    }
}