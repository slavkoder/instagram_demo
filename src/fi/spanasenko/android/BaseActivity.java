package fi.spanasenko.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import com.flurry.android.FlurryAgent;
import fi.spanasenko.android.instagram.OperationCallback;
import fi.spanasenko.android.instagram.VoidOperationCallback;
import fi.spanasenko.android.utils.UiUtils;

public class BaseActivity extends Activity {

    private static final String FLURRY_API_KEY = "2BCNGBQ4RBJTV934MDXQ";
    private static final String FLURRY_ERROR_ID = "general_error";

    private ProgressDialog _busyDialog;

    protected boolean isBusyDialogVisible;
    protected boolean isNotifyUserVisible;
    protected boolean isDisplayErrorVisible;

    private String busyDialogMessage;
    private String notifyUserTitle;
    private String notifyUserMessage;

    protected Exception displayErrorException;

    private static final String BUSY_DIALOG_VISIBLE_INSTANCE_STATE_KEY = "isBusyDialogVisible";
    private static final String BUSY_DIALOG_MESSAGE_INSTANCE_STATE_KEY = "busyDialogMessage";
    private static final String NOTIFY_USER_VISIBLE_INSTANCE_STATE_KEY = "isNotifyUserVisible";
    private static final String NOTIFY_USER_TITLE_INSTANCE_STATE_KEY = "notifyUserTitle";
    private static final String NOTIFY_USER_MESSAGE_INSTANCE_STATE_KEY = "notifyUserMessage";
    private static final String DISPLAY_ERROR_VISIBLE_INSTANCE_STATE_KEY = "isDisplayErrorVisible";
    private static final String DISPLAY_ERROR_EXCEPTION_INSTANCE_STATE_KEY = "displayErrorException";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean(BUSY_DIALOG_VISIBLE_INSTANCE_STATE_KEY, isBusyDialogVisible);
        state.putString(BUSY_DIALOG_MESSAGE_INSTANCE_STATE_KEY, busyDialogMessage);
        state.putBoolean(NOTIFY_USER_VISIBLE_INSTANCE_STATE_KEY, isNotifyUserVisible);
        state.putString(NOTIFY_USER_TITLE_INSTANCE_STATE_KEY, notifyUserTitle);
        state.putString(NOTIFY_USER_MESSAGE_INSTANCE_STATE_KEY, notifyUserMessage);
        state.putBoolean(DISPLAY_ERROR_VISIBLE_INSTANCE_STATE_KEY, isDisplayErrorVisible);
        state.putSerializable(DISPLAY_ERROR_EXCEPTION_INSTANCE_STATE_KEY, displayErrorException);
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        isBusyDialogVisible = state.getBoolean(BUSY_DIALOG_VISIBLE_INSTANCE_STATE_KEY);
        busyDialogMessage = state.getString(BUSY_DIALOG_MESSAGE_INSTANCE_STATE_KEY);
        isNotifyUserVisible = state.getBoolean(NOTIFY_USER_VISIBLE_INSTANCE_STATE_KEY);
        notifyUserTitle = state.getString(NOTIFY_USER_TITLE_INSTANCE_STATE_KEY);
        notifyUserMessage = state.getString(NOTIFY_USER_MESSAGE_INSTANCE_STATE_KEY);
        isDisplayErrorVisible = state.getBoolean(DISPLAY_ERROR_VISIBLE_INSTANCE_STATE_KEY);
        displayErrorException = (Exception) state.getSerializable(DISPLAY_ERROR_EXCEPTION_INSTANCE_STATE_KEY);

        if (isBusyDialogVisible) {
            showBusyDialog(busyDialogMessage);
        }

        if (isNotifyUserVisible) {
            notifyUser(notifyUserTitle, notifyUserMessage);
        }

        if (isDisplayErrorVisible) {
            onError(displayErrorException);
        }
    }

    public void onError(Exception e) {
        onError(e, new VoidOperationCallback() {

            @Override
            protected void onCompleted() {
                isDisplayErrorVisible = false;
                displayErrorException = null;
            }

            @Override
            protected void onError(Exception error) {
                isDisplayErrorVisible = false;
                displayErrorException = null;
            }

        });
    }

    protected void onError(Exception e, VoidOperationCallback cb) {
        displayErrorException = e;
        UiUtils.displayError(this, e, cb);
        isDisplayErrorVisible = true;

        // Log the error to flurry
        FlurryAgent.onError(FLURRY_ERROR_ID, e.getMessage(), this.getClass().getSimpleName());
    }

    public void notifyUser(int titleId, int messageId) {
        notifyUserTitle = getStringResource(titleId);
        notifyUserMessage = getStringResource(messageId);
        notifyUser(notifyUserTitle, notifyUserMessage);
    }

    public void notifyUser(String title, String message) {
        notifyUserTitle = title;
        notifyUserMessage = message;
        UiUtils.notifyUser(this, notifyUserTitle, notifyUserMessage, new VoidOperationCallback() {

            @Override
            protected void onCompleted() {
                isNotifyUserVisible = false;
                notifyUserTitle = null;
                notifyUserMessage = null;
            }

            @Override
            protected void onError(Exception error) {
                isNotifyUserVisible = false;
                notifyUserTitle = null;
                notifyUserMessage = null;
            }

        });

        isNotifyUserVisible = true;
    }

    public void showBusyDialog() {
        showBusyDialog(R.string.busy_dialog_default_message);
    }

    public void showBusyDialog(int resourceId) {
        showBusyDialog(getResources().getString(resourceId));
    }

    public void showBusyDialog(String message) {
        busyDialogMessage = message;

        if (_busyDialog == null) {
            _busyDialog = ProgressDialog.show(this, "", busyDialogMessage, true);
        } else {
            _busyDialog.setMessage(message);
            _busyDialog.show();
        }

        isBusyDialogVisible = true;
    }

    public void dismissBusyDialog() {
        if (_busyDialog != null) {
            try {
                _busyDialog.dismiss();
            } catch (IllegalArgumentException err) {
            }
        }
        isBusyDialogVisible = false;
        busyDialogMessage = null;
    }

    public void promptUser(int titleId, int messageId, int positiveButtonId, int negativeButtonId,
            final OperationCallback<String> callback) {
        UiUtils.promptUser(this, titleId, messageId, getString(positiveButtonId), getString(negativeButtonId),
                callback);
    }

    public void promptUser(String title, String message, final String positiveButton, final String negativeButton,
                           final OperationCallback<String> callback) {
        UiUtils.promptUser(this, title, message, positiveButton, negativeButton, callback);
    }

    public String getStringResource(int resourceId) {
        return getResources().getString(resourceId);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Note that since we have permissions to access location, Flurry will automatically gather user location data.
        // FlurryAgent.setReportLocation(false);
        FlurryAgent.onStartSession(this, FLURRY_API_KEY);
    }

    @Override
    public void onStop() {
        super.onStop();

        FlurryAgent.onEndSession(this);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();

        // dialog must be closed during onPause or it will leak the activity on rotation
        dismissBusyDialog();
    }


}
