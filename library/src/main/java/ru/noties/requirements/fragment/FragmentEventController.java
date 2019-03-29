package ru.noties.requirements.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import ru.noties.requirements.EventController;

/**
 * An {@link EventController} that uses invisible fragment to dispatch and receive events.
 * Please note that fragment is not retained and will be recreated after configuration change
 * or a process death. This is intentional. As one should evaluate the whole chain of
 * {@link ru.noties.requirements.RequirementCase}s on such occasion.
 *
 * @see #create(FragmentActivity)
 * @see #create(Fragment)
 * @see #create(FragmentManager)
 * @since 2.0.0
 */
public abstract class FragmentEventController {

    @NonNull
    public static EventController create(@NonNull FragmentActivity activity) {
        return create(activity.getSupportFragmentManager());
    }

    @NonNull
    public static EventController create(@NonNull Fragment fragment) {
        return create(fragment.requireFragmentManager());
    }

    @NonNull
    public static EventController create(@NonNull FragmentManager manager) {
        EventControllerFragment eventControllerFragment = (EventControllerFragment) manager.findFragmentByTag(TAG);
        if (eventControllerFragment == null) {
            eventControllerFragment = new EventControllerFragment();
            manager.beginTransaction()
                    .add(eventControllerFragment, TAG)
                    .commitNowAllowingStateLoss();
        }
        return eventControllerFragment;
    }

    private static final String TAG = FragmentEventController.class.getName() + ".TAG";

    private FragmentEventController() {
    }
}
