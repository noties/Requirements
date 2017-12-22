package ru.noties.requirements.sample.cases;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;

import ru.noties.requirements.Flag;
import ru.noties.requirements.RequestCode;
import ru.noties.requirements.RequirementCase;
import ru.noties.requirements.sample.R;

public class NetworkCase extends RequirementCase<Activity> {

    private static final int REQUEST_CODE = RequestCode.createRequestCode(NetworkCase.class);

    @Override
    public boolean meetsRequirement() {
        return hasConnection(appContext());
    }

    @Override
    public void startResolution() {

        // let's present user with explanation dialog

        // helper to keep track of dismissed dialog state
        final Flag flag = Flag.create();

        new AlertDialogBuilder(activity())
                .setTitle(R.string.case_network_title)
                .setMessage(R.string.case_network_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        flag.mark();

                        final Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivityForResult(intent, REQUEST_CODE);
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
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE == requestCode) {
            deliverResult(meetsRequirement());
            return true;
        }
        return false;
    }

    private static boolean hasConnection(@NonNull Context context) {
        final ConnectivityManager manager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = manager != null
                ? manager.getActiveNetworkInfo()
                : null;
        return info != null && info.isConnected();
    }
}
