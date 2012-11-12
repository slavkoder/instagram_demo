package fi.spanasenko.android.view;

import android.content.Context;
import android.content.Intent;
import fi.spanasenko.android.instagram.OperationCallback;
import fi.spanasenko.android.instagram.VoidOperationCallback;

/**
 * IBaseView.
 * The default view which contains common functionality against all primary views.
 */
public interface IBaseView {

    /**
     * Handles error.
     * @param e Exception caused error.
     */
    void onError(Exception e);

    /**
     * Shows notification to user.
     * @param titleId   String resource with dialog title.
     * @param messageId String resource with dialog message.
     */
    void notifyUser(int titleId, int messageId);

    /**
     * Shows progress dialog.
     */
    void showBusyDialog();

    /**
     * Shows progress dialog with a message.
     * @param resourceId Id of string resource with the message to be shown.
     */
    void showBusyDialog(int resourceId);

    /**
     * Hides progress dialog.
     */
    void dismissBusyDialog();

    /**
     * Shows dialog with two buttons to user.
     * @param title          Title of the dialog.
     * @param message        Dialog message.
     * @param positiveButton Positive button label.
     * @param negativeButton Negative button label.
     * @param callback       Callback object responsible for handling result, where string represents pressed button label.
     */
    void promptUser(String title, String message, final String positiveButton, final String negativeButton,
            final OperationCallback<String> callback);

    /**
     * Shows dialog with two buttons to user, instead of strings uses resource ids.
     * @param title          Title of the dialog.
     * @param message        Dialog message.
     * @param positiveButton Positive button label.
     * @param negativeButton Negative button label.
     * @param callback       Callback object responsible for handling result, where string represents pressed button label.
     */
    void promptUser(int title, int message, int positiveButton, int negativeButton,
            final OperationCallback<String> callback);

    /**
     * Returns string resource.
     * @param resourceId Id of the string resource.
     * @return String from resources.
     */
    String getStringResource(int resourceId);

    /**
     * Starts activity for given intent.
     * @param intent Intent to start.
     */
    void startActivity(Intent intent);

    /**
     * Starts activity for result.
     * @param intent Intent to start.
     * @param callback Callback called in on result.
     */
    void startActivityForResult(Intent intent, VoidOperationCallback callback);

    /**
     * Returns current activity context.
     * @return Current activity context.
     */
    Context getContext();

}
