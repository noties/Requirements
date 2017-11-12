package ru.noties.requirements.sample.cases;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

import ru.noties.requirements.BuildUtils;

// small implementation for our small needs
class AlertDialogBuilder extends AlertDialog.Builder {

    private DialogInterface.OnDismissListener onDismissListener;

    AlertDialogBuilder(Context context) {
        super(context);
    }

    @Override
    public AlertDialogBuilder setTitle(int titleId) {
        super.setTitle(titleId);
        return this;
    }

    @Override
    public AlertDialogBuilder setTitle(CharSequence title) {
        super.setTitle(title);
        return this;
    }

    @Override
    public AlertDialogBuilder setMessage(int messageId) {
        super.setMessage(messageId);
        return this;
    }

    @Override
    public AlertDialogBuilder setMessage(CharSequence message) {
        super.setMessage(message);
        return this;
    }

    @Override
    public AlertDialogBuilder setPositiveButton(int textId, DialogInterface.OnClickListener listener) {
        super.setPositiveButton(textId, listener);
        return this;
    }

    @Override
    public AlertDialogBuilder setNegativeButton(int textId, DialogInterface.OnClickListener listener) {
        super.setNegativeButton(textId, listener);
        return this;
    }

    public AlertDialogBuilder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        if (BuildUtils.isAtLeast(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            super.setOnDismissListener(onDismissListener);
        } else {
            this.onDismissListener = onDismissListener;
        }
        return this;
    }

    @Override
    public AlertDialog create() {
        final AlertDialog dialog = super.create();
        if (!BuildUtils.isAtLeast(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            dialog.setOnDismissListener(onDismissListener);
        }
        return dialog;
    }

    @Override
    public AlertDialog show() {
        final AlertDialog dialog = create();
        dialog.show();
        return dialog;
    }
}
