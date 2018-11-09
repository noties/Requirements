package ru.noties.requirements;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import java.util.concurrent.CopyOnWriteArrayList;

class EventSourceImpl extends EventSource {

    // generally it's a good idea to take _smallest_ interface possible,
    // but here just let's make it explicit what this collection is for
    private final CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();

    EventSourceImpl() {
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

        boolean result = false;

        for (Listener listener : listeners) {
            result |= listener.onActivityResult(requestCode, resultCode, data);
        }

        return result;
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.M)
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean result = false;

        for (Listener listener : listeners) {
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
