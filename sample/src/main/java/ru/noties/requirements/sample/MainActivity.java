package ru.noties.requirements.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import ru.noties.requirements.BuildUtils;
import ru.noties.requirements.EventSource;
import ru.noties.requirements.Payload;
import ru.noties.requirements.Requirement;
import ru.noties.requirements.sample.cases.LocationPermissionCase;
import ru.noties.requirements.sample.cases.LocationServicesCase;
import ru.noties.requirements.sample.cases.NetworkCase;

public class MainActivity extends Activity {

    private final EventSource eventSource = EventSource.create();

    private Requirement requirement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final View view = findViewById(R.id.button);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ensureRequirement();
                requirement.validate(new Requirement.Listener() {
                    @Override
                    public void onRequirementSuccess() {
                        // can proceed now
                    }

                    @Override
                    public void onRequirementFailure(@Nullable Payload payload) {
                        // cannot
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!eventSource.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!eventSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void ensureRequirement() {

        if (requirement != null) {
            return;
        }

        requirement = Requirement.builder()
                .add(new NetworkCase())
                .addIf(BuildUtils.isAtLeast(Build.VERSION_CODES.M), new LocationPermissionCase())
                .add(new LocationServicesCase())
                .build(this, eventSource);
    }
}
