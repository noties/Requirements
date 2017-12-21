package ru.noties.requirements;

import android.content.Intent;
import android.support.annotation.NonNull;

import ru.noties.listeners.Listeners;

class EventSourceImpl extends EventSource {

    private final Listeners<Listener> listeners = Listeners.create(3);

    EventSourceImpl() {
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

        boolean result = false;

        for (Listener listener : listeners.begin()) {
            result |= listener.onActivityResult(requestCode, resultCode, data);
        }

        return result;
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean result = false;

        for (Listener listener : listeners.begin()) {
            result |= listener.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        return result;
    }

    @NonNull
    @Override
    public Subscription subscribe(@NonNull Listener listener) {
        return new SubscriptionImpl(listener);
    }

    private class SubscriptionImpl implements Subscription {

        private Listener listener;

        private SubscriptionImpl(@NonNull Listener listener) {
            this.listener = listener;
            listeners.add(listener);
        }

        @Override
        public void unsubscribe() {
            // null check in case called unsubscribe multiple times
            if (listener != null) {
                listeners.remove(listener);
                listener = null;
            }
        }
    }
}
