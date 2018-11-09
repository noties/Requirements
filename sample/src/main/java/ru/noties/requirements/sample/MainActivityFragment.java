package ru.noties.requirements.sample;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import ru.noties.debug.Debug;
import ru.noties.requirements.BuildUtils;
import ru.noties.requirements.Payload;
import ru.noties.requirements.Requirement;
import ru.noties.requirements.RequirementBuilder;
import ru.noties.requirements.fragment.FragmentEventController;
import ru.noties.requirements.sample.cases.LocationPermissionCase;
import ru.noties.requirements.sample.cases.LocationServicesCase;
import ru.noties.requirements.sample.cases.NetworkCase;

public class MainActivityFragment extends FragmentActivity {

    private Requirement requirement;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requirement = createRequirement();

        final View view = findViewById(R.id.button);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requirement.validate(new Requirement.Listener() {
                    @Override
                    public void onRequirementSuccess() {
                        Debug.i();
                    }

                    @Override
                    public void onRequirementFailure(@Nullable Payload payload) {
                        Debug.e("payload: %s", payload);
                    }
                });
            }
        });
    }

    @SuppressLint("NewApi")
    @NonNull
    private Requirement createRequirement() {
        return RequirementBuilder.create(FragmentEventController.get(this))
                .add(new NetworkCase())
                .addIf(BuildUtils.isAtLeast(Build.VERSION_CODES.M), new LocationPermissionCase())
                .add(new LocationServicesCase())
                .build();
    }
}
