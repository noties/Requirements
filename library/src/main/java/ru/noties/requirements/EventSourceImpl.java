package ru.noties.requirements;

import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

class EventSourceImpl extends EventSource {

    private final List<Listener> listeners = new ArrayList<>(3);

    EventSourceImpl() {
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

        boolean result = false;

        for (int i = listeners.size() - 1; i >= 0; i--) {
            result |= listeners.get(i).onActivityResult(requestCode, resultCode, data);
        }

        return result;
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean result = false;

        for (int i = listeners.size() - 1; i >= 0; i--) {
            result |= listeners.get(i).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        return result;
    }

    @NonNull
    @Override
    public Subscription subscribe(@NonNull Listener listener) {
        listeners.add(listener);
        return new SubscriptionImpl(listener);
    }

    private class SubscriptionImpl implements Subscription {

        private Listener listener;

        private SubscriptionImpl(@NonNull Listener listener) {
            this.listener = listener;
        }

        @Override
        public void unsubscribe() {
            if (listener != null) {
                listeners.remove(listener);
                listener = null;
            }
        }
    }
}
