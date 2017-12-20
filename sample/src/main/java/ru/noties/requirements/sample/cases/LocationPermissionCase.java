package ru.noties.requirements.sample.cases;

import android.Manifest;
import android.content.DialogInterface;

import ru.noties.requirements.Flag;
import ru.noties.requirements.PermissionCase;
import ru.noties.requirements.sample.R;

public class LocationPermissionCase extends PermissionCase {

    public LocationPermissionCase() {
        super(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    protected void showPermissionRationale() {

        final Flag flag = Flag.create();

        new AlertDialogBuilder(activity())
                .setTitle(R.string.case_location_permission_title)
                .setMessage(R.string.case_location_permission_rationale_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        flag.mark();
                        requestPermission();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (!flag.isSet()) {
                            deliverResult(false);
                        }
                    }
                })
                .show();
    }

    @Override
    protected void showExplanationOnNever() {

        final Flag flag = Flag.create();

        new AlertDialogBuilder(activity())
                .setTitle(R.string.case_location_permission_title)
                .setMessage(R.string.case_location_permission_never_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        flag.mark();
                        navigateToSettingsScreen();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (!flag.isSet()) {
                            deliverResult(false);
                        }
                    }
                })
                .show();
    }
}
