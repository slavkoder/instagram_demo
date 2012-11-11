package fi.spanasenko.android.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import fi.spanasenko.android.R;
import fi.spanasenko.android.instagram.OperationCallback;
import fi.spanasenko.android.instagram.VoidOperationCallback;

/**
 * UiUtils
 * Holds implementation for common UI methods.
 */
public class UiUtils {

    public static void displayError(Context context, Exception exception) {
        displayError(context, exception, null);
    }

    public static void displayError(Context context, Exception exception, final VoidOperationCallback callback) {
        notifyUser(context, R.string.error_dialog_title, R.string.unexpected_error_message, callback);
    }

    public static void notifyUser(Context context, String message) {
        String defaultTitle = context.getResources().getString(R.string.info_dialog_title);
        notifyUser(context, defaultTitle, message, null);
    }

    public static void notifyUser(Context context, String title, String message) {
        notifyUser(context, title, message, null);
    }

    public static void notifyUser(Context context, int messageId) {
        String message = context.getResources().getString(messageId);
        notifyUser(context, message);
    }

    public static void notifyUser(Context context, int titleId, int messageId) {
        notifyUser(context, titleId, messageId, null);
    }

    public static void notifyUser(Context context, int titleId, int messageId, final VoidOperationCallback callback) {
        String title = context.getResources().getString(titleId);
        String message = context.getResources().getString(messageId);
        notifyUser(context, title, message, callback);
    }

    public static void notifyUser(Context context, String title, String message, final VoidOperationCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(message).setTitle(title).setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (callback != null) {
                            callback.notifyCompleted();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void promptUser(Context context, int titleId, int messageId, final String positiveButton,
            final String negativeButton, final OperationCallback<String> callback) {
        String title = context.getResources().getString(titleId);
        String message = context.getResources().getString(messageId);
        promptUser(context, title, message, positiveButton, negativeButton, callback);
    }

    public static void promptUser(Context context, String title, String message, final String positiveButton,
            final String negativeButton, final OperationCallback<String> callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setMessage(message).setTitle(title).setCancelable(false)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (callback != null) {
                            callback.notifyCompleted(positiveButton);
                        }
                    }
                }).setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.notifyCompleted(negativeButton);
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

}
