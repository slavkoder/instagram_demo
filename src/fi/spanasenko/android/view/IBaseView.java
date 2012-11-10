package fi.spanasenko.android.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import fi.spanasenko.android.instagram.OperationCallback;

/**
 * IBaseView.
 * The default view which contains common functionality against all primary views.
 */
public interface IBaseView
{
	void onError(Exception e);

	void notifyUser(int titleId, int messageId);
	
	void showBusyDialog();
	
	void showBusyDialog(int resourceId);
	
	void dismissBusyDialog();
	
	void promptUser(String title, String message, final String positiveButton, final String negativeButton,
            final OperationCallback<String> callback);

    void promptUser(int title, int message, int positiveButton, int negativeButton,
            final OperationCallback<String> callback);
	
	String getStringResource(int resourceId);
	
	int getIntResource(int resourceId);
	
	void startActivity(Intent i);
	
	ComponentName startService(Intent i);

    /**
     * Logs out user from Instagram and closes application or shows initial screen (depending on the current screen).
     */
    void logout();

    /**
     * Returns current activity context.
     * @return Current activity context.
     */
    Context getContext();

}
