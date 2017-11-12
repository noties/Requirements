package ru.noties.requirements;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class RequirementImpl extends Requirement
        implements EventSource.Listener, RequirementCase.Callback {

    private Activity activity;

    private EventSource eventSource;

    private List<RequirementCase> requirementCases;

    private final Deque<RequirementCase> deque;

    private final ListenerSource listenerSource;

    private EventSource.Subscription subscription;

    RequirementImpl(
            @NonNull Activity activity,
            @NonNull EventSource eventSource,
            @NonNull List<RequirementCase> requirementCases
    ) {
        this.activity = activity;
        this.eventSource = eventSource;
        this.requirementCases = requirementCases;
        this.deque = new ArrayDeque<>(requirementCases.size() + 1);
        this.listenerSource = new ListenerSource();

        // register listener to be notified about activity destroyed event
        // so we can release everything
        activity.getApplication().registerActivityLifecycleCallbacks(new ActivityDestroyedListener());
    }

    @Override
    public void validate(@NonNull Listener listener) {

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

    @Nullable
    private RequirementCase currentCase() {
        return deque.peek();
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        final RequirementCase requirementCase = currentCase();
        return requirementCase != null && requirementCase.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        final RequirementCase requirementCase = currentCase();
        return requirementCase != null && requirementCase.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void validate() {
        final RequirementCase current = currentCase();
        if (current != null) {
            current.attach(activity, this);
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

        deque.clear();

        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }

        if (success) {
            listenerSource.onRequirementSuccess();
        }

        listenerSource.clear();
    }

    @Override
    public void onRequirementCaseResult(@NonNull RequirementCase.Result result, @Nullable Payload payload) {

        final RequirementCase current = currentCase();

        if (current != null) {

            current.detach();

            switch (result) {

                case SUCCESS:
                    deque.pop();
                    validate();
                    break;

                case FAILURE:
                    listenerSource.onRequirementFailure(payload);
                    end(false);
                    break;

                default:
                    throw new IllegalStateException("Unknown type of result: " + result);
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

                a.getApplication().unregisterActivityLifecycleCallbacks(this);

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

                activity = null;
                eventSource = null;
                requirementCases = null;
            }
        }
    }

    private static class ListenerSource implements Listener {

        private final List<Listener> listeners;

        ListenerSource() {
            this.listeners = new ArrayList<>(3);
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

        void add(@NonNull Listener listener) {
            listeners.add(listener);
        }

        void clear() {
            listeners.clear();
        }
    }
}
