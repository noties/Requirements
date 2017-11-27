package ru.noties.requirements.sample.cases;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;

import ru.noties.requirements.MutableBool;
import ru.noties.requirements.RequestCode;
import ru.noties.requirements.RequirementCase;
import ru.noties.requirements.sample.R;

public class LocationServicesCase extends RequirementCase {

    private static final int REQUEST_CODE = RequestCode.createRequestCode(LocationServicesCase.class);

    @Override
    public boolean meetsRequirement() {
        return servicesEnabled(appContext());
    }

    @Override
    public void startResolution() {

        final MutableBool bool = new MutableBool();

        new AlertDialogBuilder(activity())
                .setTitle(R.string.case_location_services_title)
                .setMessage(R.string.case_location_services_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bool.setValue(true);
                        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (!bool.value()) {
                            deliverResult(false);
                        }
                    }
                })
                .show();
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE == requestCode) {
            deliverResult(meetsRequirement());
            return true;
        }
        return false;
    }

    private static boolean servicesEnabled(@NonNull Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null
                && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }
}
