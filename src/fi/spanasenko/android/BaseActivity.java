package fi.spanasenko.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.flurry.android.FlurryAgent;
import fi.spanasenko.android.instagram.OperationCallback;
import fi.spanasenko.android.instagram.VoidOperationCallback;
import fi.spanasenko.android.utils.UiUtils;
import fi.spanasenko.android.view.IBaseView;

import java.util.HashMap;

/**
 * BaseActivity
 * Encapsulates methods commonly used by other activities, such as showing busy dialog.
 */
public class BaseActivity extends Activity implements IBaseView {

    private ProgressDialog _busyDialog;

    protected boolean isBusyDialogVisible;
    protected boolean isNotifyUserVisible;
    protected boolean isDisplayErrorVisible;

    private String busyDialogMessage;
    private String notifyUserTitle;
    private String notifyUserMessage;

    private HashMap<Integer, VoidOperationCallback> resultCallbacks = new HashMap<Integer, VoidOperationCallback>();
    private int lastActivityCode = 0;

    protected Exception displayErrorException;

    public static final String EXTRA_LOGOUT = "fi.spanasenko.android.EXTRA_LOGOUT";

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

    @Override
    protected void onResume() {
        super.onResume();

        // This is an ugly hack to prevent back stack from staying after logout.
        if (InstagramDemoApp.getInstance(this).isLoggingOut() && !(this instanceof LoginActivity)) {
            finish();
        }
    }

    @Override
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
        FlurryAgent.onError(getString(R.string.flurry_error_code), e.getMessage(), this.getClass().getSimpleName());
    }

    @Override
    public void notifyUser(int titleId, int messageId) {
        notifyUserTitle = getStringResource(titleId);
        notifyUserMessage = getStringResource(messageId);
        notifyUser(notifyUserTitle, notifyUserMessage);
    }

    protected void notifyUser(String title, String message) {
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

    @Override
    public void showBusyDialog() {
        showBusyDialog(R.string.busy_dialog_default_message);
    }

    @Override
    public void showBusyDialog(int resourceId) {
        showBusyDialog(getResources().getString(resourceId));
    }

    protected void showBusyDialog(String message) {
        busyDialogMessage = message;

        if (_busyDialog == null) {
            _busyDialog = ProgressDialog.show(this, "", busyDialogMessage, true);
        } else {
            _busyDialog.setMessage(message);
            _busyDialog.show();
        }

        isBusyDialogVisible = true;
    }

    @Override
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

    @Override
    public void promptUser(int titleId, int messageId, int positiveButtonId, int negativeButtonId,
            final OperationCallback<String> callback) {
        UiUtils.promptUser(this, titleId, messageId, getString(positiveButtonId), getString(negativeButtonId),
                callback);
    }

    @Override
    public void promptUser(String title, String message, final String positiveButton, final String negativeButton,
            final OperationCallback<String> callback) {
        UiUtils.promptUser(this, title, message, positiveButton, negativeButton, callback);
    }

    @Override
    public String getStringResource(int resourceId) {
        return getResources().getString(resourceId);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Note that since we have permissions to access location, Flurry will automatically gather user location data.
        // FlurryAgent.setReportLocation(false);
        FlurryAgent.onStartSession(this, getString(R.string.flurry_api_key));
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
    public void startActivityForResult(Intent intent, VoidOperationCallback callback) {
        startActivityForResult(intent, lastActivityCode);
        resultCallbacks.put(lastActivityCode, callback);
        lastActivityCode++;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        VoidOperationCallback callback = resultCallbacks.get(requestCode);
        if (callback != null) {
            resultCallbacks.remove(requestCode);
            callback.notifyCompleted();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Dialog must be closed during onPause or it will leak the activity on rotation.
        dismissBusyDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Logs out user from Instagram using current activity as refering point.
     * @param activity User's current activity.
     */
    public static void logout(Activity activity) {
        // Send the user to the first view cleaning all the activities in the back stack
        Intent login = new Intent(activity, LoginActivity.class);
        login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        login.putExtra(EXTRA_LOGOUT, true);
        activity.startActivity(login);

        // This is an ugly hack to prevent back stack from staying after logout.
        InstagramDemoApp.getInstance(activity).setLoggingOut(true);
    }

    @Override
    public Context getContext() {
        return this;
    }

}
