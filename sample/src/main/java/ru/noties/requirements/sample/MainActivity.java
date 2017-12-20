package ru.noties.requirements.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import ru.noties.requirements.BuildUtils;
import ru.noties.requirements.EventDispatcher;
import ru.noties.requirements.EventSource;
import ru.noties.requirements.Payload;
import ru.noties.requirements.Requirement;
import ru.noties.requirements.RequirementBuilder;
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
                        Log.e("requirements", "success");
                    }

                    @Override
                    public void onRequirementFailure(@Nullable Payload payload) {
                        Log.e("requirements", "failure");
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

    @SuppressLint("NewApi")
    private void ensureRequirement() {

        if (requirement != null) {
            return;
        }

        requirement = RequirementBuilder.create(EventDispatcher.create((Activity) this), eventSource)
                .add(new NetworkCase())
                .addIf(BuildUtils.isAtLeast(Build.VERSION_CODES.M), new LocationPermissionCase())
                .add(new LocationServicesCase())
                .build();
    }
}
