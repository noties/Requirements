package ru.noties.requirements.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import ru.noties.requirements.EventController;

/**
 * @since 2.0.0
 */
public abstract class FragmentEventController {

    @NonNull
    public static EventController get(@NonNull FragmentActivity activity) {
        final FragmentManager manager = activity.getSupportFragmentManager();
        EventControllerFragment eventControllerFragment = (EventControllerFragment) manager.findFragmentByTag(TAG);
        if (eventControllerFragment == null) {
            eventControllerFragment = new EventControllerFragment();
            manager.beginTransaction()
                    .add(eventControllerFragment, TAG)
                    .commitNow();
        }
        return eventControllerFragment;
    }

    private static final String TAG = FragmentEventController.class.getName() + ".TAG";

    private FragmentEventController() {
    }
}
