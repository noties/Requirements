package ru.noties.requirements.fragment;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.app.Fragment;

import java.util.concurrent.CopyOnWriteArrayList;

import ru.noties.requirements.EventController;
import ru.noties.requirements.EventDispatcher;
import ru.noties.requirements.EventSource;

// note that this Fragment is not retained
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class EventControllerFragment extends Fragment implements EventController {

    private final EventDispatcher eventDispatcher = new FragmentDispatcher(this);
    private final EventSourceImpl eventSource = new EventSourceImpl();

    @NonNull
    @Override
    public EventDispatcher eventDispatcher() {
        return eventDispatcher;
    }

    @NonNull
    @Override
    public EventSource eventSource() {
        return eventSource;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (EventSource.Listener listener : eventSource.listeners) {
            listener.onActivityResult(requestCode, resultCode, data);
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (EventSource.Listener listener : eventSource.listeners) {
            listener.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private class EventSourceImpl extends EventSource {

        private final CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();

        @Override
        public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
            throw new IllegalStateException("Unexpected method call");
        }

        @Override
        public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            throw new IllegalStateException("Unexpected method call");
        }

        @NonNull
        @Override
        public Subscription subscribe(@NonNull final Listener listener) {
            listeners.add(listener);
            return new Subscription() {
                @Override
                public void unsubscribe() {
                    listeners.remove(listener);
                }
            };
        }
    }
}
