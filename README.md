# Requirements

Small utility library for Android to evaluate requirements in order for some action to proceed. For example: network connection, permissions (API 23), system services (location, bluetooth, ...), etc.

[![Maven Central](https://img.shields.io/maven-central/v/ru.noties/requirements.svg)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%requirements%22)

```gradle
implementation 'ru.noties:requirements:1.0.1'
```


## Overview

In order to correctly react to a user action we must make sure that all requirements are satisfied for this action to proceed. Sometimes it's just _network connection_. Sometimes it's _location services_ and, on devices starting Marshmallow, the _location permission_. The list goes forth. So does the code in an Activity or a Fragment.

The aim of this library is to detach requirements' resolution process from the code and give flexibility to add/remove requirements on-the-go.

```java
final Requirement requirement = RequirementBuilder.create()
        .add(new NetworkCase())
        .addIf(BuildUtils.isAtLeast(Build.VERSION_CODES.M), new LocationPermissionCase())
        .add(new LocationServicesCase())
        .build(this, eventSource);
```

The pivot point of this library is the `RequirementCase`. It encapsulates one single case that must be met before an action can go further. It will receive `onActivityResult` and `onRequestPermissionsResult` events and can react to them if needed. In general: `RequirementCase` is state-less container that validates if requirement case is met and, if not, **starts resolution**.

There are 2 API methods in `RequirementCase` that must be implemented:
* `boolean meetsRequirement()`
* `void startResolution()`

For example in case of network connection one might do this:
```java
@Override
public boolean meetsRequirement() {
    final ConnectivityManager manager
            = (ConnectivityManager) appContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    final NetworkInfo info = manager != null
            ? manager.getActiveNetworkInfo()
            : null;
    return info != null && info.isConnected();
}
```

If `meetRequirement` returns `false`, then `startResolution` will be called

```java
@Override
public void startResolution() {
    new AlertDialog.Builder(activity())
            // configuration of a dialog
            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    startActivityForResult(
                            new Intent(Settings.ACTION_WIRELESS_SETTINGS),
                            requestCode
                    );
            })
            .show();
}
```

```java
@Override
public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    if (this.requestCode == requestCode) {
        deliverResult(meetsRequirement());
        return true;
    }
    return false;
}
```

### Event source

In order for `RequirementCase` to react to Activity events, `EventSource` must be used. Obtain an instance of it by calling: `EventSource.create()`. Then redirect `onActivityResult` and `onRequestPermissionsResult` to it. Each method returns a boolean indicating if event was consumed.

```java

private final EventSource eventSource = EventSource.create();

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
```

### Dialogs in resolution

It's aboslutely crucial that after `startResolution` is called `RequirementCase` must deliver success or cancellation event (it can be postponed for example until `onActivityResult` or `onRequestPermissionsResult` is delivered). Otheriwse the requirements chain will break.

In case of showing a dialog in `startResolution`, it's advicable to track the dismiss state of a dialog. Library provides utility class `MutableBool` that can help keep track of dialog state:

```java

final MutableBool bool = new MutableBool();

new AlertDialogBuilder(activity())
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

				// mark as success
                bool.setValue(true);

                // for example, start activity for result
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
```

### Activity lifecycle

Internally `Requirement` listens for supplied activity lifecycle events and disposes itself when attached Activity went through `onDestroy`. If Activity was destroyed whilst requirement resolution process is still in progress that process will be lost.

### Multiple listeners

Please note that if a `Requirement#validate` is called and `Requirement#isInProgress` is true, supplied listener won't trigger the whole validation process again but unstead will subscribe for the final result (with other listeners).

### Request code

Sometimes our creative abilities give us a hard time and we sit hours thinking of ideal request code:

```
private static final int LOCATION_PERMISSION_REQUEST_CODE = 72;
```

It's great time and I won't trade it for anything. Still, if you wish, there is an utility class to do this amazing job for you:

```java
RequestCode.createRequestCode(String);
RequestCode.createRequestCode(Class<?>);
```