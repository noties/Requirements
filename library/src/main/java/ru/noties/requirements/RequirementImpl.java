package ru.noties.requirements;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class RequirementImpl extends Requirement
        implements EventSource.Listener, RequirementCase.Callback {

    private EventDispatcher eventDispatcher;

    private Activity activity;

    private EventSource eventSource;

    private List<RequirementCase> requirementCases;

    private final Deque<RequirementCase> deque;

    private final ListenerSource listenerSource;

    private EventSource.Subscription subscription;

    // @since 2.0.0
    private boolean isDestroyed;

    // @since 2.0.0 made field to allow manual #destroy
    private ActivityDestroyedListener activityDestroyedListener;

    RequirementImpl(
            @NonNull EventDispatcher eventDispatcher,
            @NonNull EventSource eventSource,
            @NonNull List<RequirementCase> requirementCases
    ) {
        this.eventDispatcher = eventDispatcher;
        this.eventSource = eventSource;
        this.requirementCases = requirementCases;
        this.deque = new ArrayDeque<>(requirementCases.size() + 1);
        this.listenerSource = new ListenerSource();

        // register listener to be notified about activity destroyed event
        // so we can release everything
        this.activity = eventDispatcher.activity();
        this.activityDestroyedListener = new ActivityDestroyedListener();
        this.activity.getApplication().registerActivityLifecycleCallbacks(activityDestroyedListener);
    }

    @Override
    public void validate(@NonNull Listener listener) {

        // @since 2.0.0
        checkState();

        listenerSource.add(listener);

        // if we have subscription it means that we are in progress -> just add supplied listener
        // to our list of listeners

        if (subscription == null) {

            deque.clear();
            deque.addAll(requirementCases);

            subscription = eventSource.subscribe(this);

            validate();
        }
    }

    @Override
    public boolean isValid() {

        // @since 2.0.0
        checkState();

        boolean result = true;
        for (RequirementCase requirementCase : requirementCases) {
            //noinspection unchecked
            requirementCase.attach(eventDispatcher, this);
            result = requirementCase.meetsRequirement();
            requirementCase.detach();
            if (!result) {
                break;
            }
        }
        return result;
    }

    @Override
    public void cancel() {

        // no need to throw if we are destroyed at this point
        // @since 2.0.0
        if (isDestroyed()) {
            return;
        }

        cancel(null);
    }

    @Override
    public void cancel(@Nullable Payload payload) {

        // no need to throw if we are destroyed at this point
        // @since 2.0.0
        if (isDestroyed()) {
            return;
        }

        final RequirementCase current = currentCase();
        if (current != null) {
            current.detach();
        }

        listenerSource.onRequirementFailure(payload);

        end(false);
    }

    @Override
    public boolean isInProgress() {
        // no check for state here
        return subscription != null;
    }

    @Override
    public void destroy() {

        // if we are already destroyed -> silently ignore
        if (isDestroyed()) {
            return;
        }

        isDestroyed = true;

        activity.getApplication().unregisterActivityLifecycleCallbacks(activityDestroyedListener);

        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }

        final RequirementCase requirementCase = currentCase();
        if (requirementCase != null) {
            requirementCase.detach();
        }

        deque.clear();

        listenerSource.clear();

        eventDispatcher = null;
        activity = null;
        activityDestroyedListener = null;
        eventSource = null;
        requirementCases = null;
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    @Nullable
    private RequirementCase currentCase() {
        return deque.peek();
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

        // @since 2.0.0
        checkState();

        final RequirementCase requirementCase = currentCase();
        return requirementCase != null && requirementCase.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // @since 2.0.0
        checkState();

        final RequirementCase requirementCase = currentCase();
        return requirementCase != null && requirementCase.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void validate() {

        // @since 2.0.0
        checkState();

        final RequirementCase current = currentCase();
        if (current != null) {
            //noinspection unchecked
            current.attach(eventDispatcher, this);
            if (!current.meetsRequirement()) {
                current.startResolution();
            } else {
                current.detach();
                deque.pop();
                if (currentCase() != null) {
                    validate();
                } else {
                    end(true);
                }
            }
        } else {
            end(true);
        }
    }

    private void end(boolean success) {

        // @since 2.0.0
        checkState();

        deque.clear();

        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }

        if (success) {
            listenerSource.onRequirementSuccess();
        }

        // @since 2.0.0
        listenerSource.onComplete();

        listenerSource.clear();
    }

    @Override
    public void onRequirementCaseResult(boolean result, @Nullable Payload payload) {

        // @since 2.0.0
        checkState();

        final RequirementCase current = currentCase();

        if (current != null) {

            current.detach();

            if (result) {
                deque.pop();
                validate();
            } else {
                listenerSource.onRequirementFailure(payload);
                end(false);
            }

        } else {
            throw new IllegalStateException("Unexpected state inside Requirement. RequirementCase delivered a result " +
                    "whilst this Requirement has no pending cases");
        }
    }

    private class ActivityDestroyedListener extends ActivityLifecycleCallbacksAdapter {

        @Override
        public void onActivityDestroyed(Activity a) {
            if (activity == a) {
                destroy();
            }
        }
    }

    private static class ListenerSource extends Listener {

        private final CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();

        ListenerSource() {
        }

        @Override
        public void onRequirementSuccess() {
            for (Listener listener : listeners) {
                listener.onRequirementSuccess();
            }
        }

        @Override
        public void onRequirementFailure(@Nullable Payload payload) {
            for (Listener listener : listeners) {
                listener.onRequirementFailure(payload);
            }
        }

        @Override
        public void onComplete() {
            for (Listener listener : listeners) {
                listener.onComplete();
            }
        }

        void add(@NonNull Listener listener) {
            listeners.add(listener);
        }

        void clear() {
            listeners.clear();
        }
    }

    private void checkState() {
        if (isDestroyed) {
            throw new IllegalStateException("This Requirement instance has been destroyed via " +
                    "natural Activity `#onDestroy` lifecycle event or via manual call to `Requirement#destroy`.");
        }
    }
}
