/**
 * File: LocationsMapActivity.java
 * Created: 11/10/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import com.flurry.android.FlurryAgent;
import com.google.android.maps.*;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;
import fi.spanasenko.android.instagram.OperationCallback;
import fi.spanasenko.android.instagram.VoidOperationCallback;
import fi.spanasenko.android.model.Location;
import fi.spanasenko.android.presenter.NearbyLocationPresenter;
import fi.spanasenko.android.ui.LocationOverlayItem;
import fi.spanasenko.android.ui.LocationsOverlay;
import fi.spanasenko.android.utils.UiUtils;
import fi.spanasenko.android.utils.UserSettings;
import fi.spanasenko.android.utils.Utils;
import fi.spanasenko.android.view.INearbyLocationsView;

import java.util.List;

/**
 * LocationsMapActivity
 * Screen with Instagram locations showed as pins. Taping a pin shows balloon, taping balloon shows photos for selected
 * location.
 */
public class LocationsMapActivity extends MapActivity implements INearbyLocationsView {

    private NearbyLocationPresenter mPresenter;
    private TapControlledMapView mMapView;
    private List<Overlay> mMapOverlays;
    private LocationsOverlay mItemizedOverlay;
    private MyLocationOverlay mMyLocationOverlay;

    // Ugly copypaste from BaseActivity
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

    private static final String SELECTED_OVERLAY_ITEM_INDEX = "overlay_item_index";
    private static final int DEFAULT_ZOOM_LEVEL = 16;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locations_map_screen);

        if (!UserSettings.getInstance(this).isMapPrefered()) {
            // Show list activity immediately
            startActivity(new Intent(this, NearbyLocationsActivity.class));
            finish();
            return;
        }

        mMapView = (TapControlledMapView) findViewById(R.id.map_view);
        mMapView.setBuiltInZoomControls(true);

        mMapOverlays = mMapView.getOverlays();

        // dismiss balloon upon single tap of MapView (iOS behavior)
        mMapView.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public boolean onSingleTap(MotionEvent e) {
                if (mItemizedOverlay != null) {
                    mItemizedOverlay.hideAllBalloons();
                    return true;
                }

                return false;
            }
        });

        mPresenter = new NearbyLocationPresenter(this);

        Drawable drawable = this.getResources().getDrawable(R.drawable.map_marker);
        mItemizedOverlay = new LocationsOverlay(drawable, mMapView, mPresenter);
        mItemizedOverlay.setBalloonBottomOffset(drawable.getMinimumHeight());
        mItemizedOverlay.setShowClose(false);
        mItemizedOverlay.setShowDisclosure(true);

        mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        mMyLocationOverlay.enableMyLocation();
        mMapOverlays.add(mMyLocationOverlay);

        if (savedInstanceState == null) {
            final MapController mc = mMapView.getController();

            GeoPoint myLocation = mMyLocationOverlay.getMyLocation();
            if (myLocation == null) {
                LocationInfo lastKnown = mPresenter.getLastKnownLocation();
                myLocation = Utils.getGeoPoint(lastKnown.lastLat, lastKnown.lastLong);
            }

            mc.animateTo(myLocation);
            mc.setZoom(DEFAULT_ZOOM_LEVEL);
        }

        mPresenter.loadLocations();
    }

    @Override
    public void updateLocations(Location[] locations) {
        mItemizedOverlay.clearOverlays();

        for (Location loc : locations) {
            mItemizedOverlay.addOverlay(new LocationOverlayItem(loc));
        }

        mMapOverlays.add(mItemizedOverlay);
        mMapView.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // This is an ugly hack to prevent back stack from staying after logout.
        if (InstagramDemoApp.getInstance(this).isLoggingOut()) {
            finish();
        }

        mPresenter.registerObserver();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mPresenter.unregisterObserver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_logout: {
                BaseActivity.logout(this);
                return true;
            }
            case R.id.menu_show_list: {
                // Save user view preference
                UserSettings.getInstance(this).setIsMapPrefered(false);

                // Show list view
                Intent showList = new Intent(this, NearbyLocationsActivity.class);
                startActivity(showList);
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
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
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean(BUSY_DIALOG_VISIBLE_INSTANCE_STATE_KEY, isBusyDialogVisible);
        state.putString(BUSY_DIALOG_MESSAGE_INSTANCE_STATE_KEY, busyDialogMessage);
        state.putBoolean(NOTIFY_USER_VISIBLE_INSTANCE_STATE_KEY, isNotifyUserVisible);
        state.putString(NOTIFY_USER_TITLE_INSTANCE_STATE_KEY, notifyUserTitle);
        state.putString(NOTIFY_USER_MESSAGE_INSTANCE_STATE_KEY, notifyUserMessage);
        state.putBoolean(DISPLAY_ERROR_VISIBLE_INSTANCE_STATE_KEY, isDisplayErrorVisible);
        state.putSerializable(DISPLAY_ERROR_EXCEPTION_INSTANCE_STATE_KEY, displayErrorException);

        // example saving focused state of overlays
        if (mItemizedOverlay != null && mItemizedOverlay.getFocus() != null) {
            state.putInt(SELECTED_OVERLAY_ITEM_INDEX, mItemizedOverlay.getLastFocusedIndex());
        }
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

        if (mItemizedOverlay != null) {
            int focused = state.getInt(SELECTED_OVERLAY_ITEM_INDEX, -1);
            if (focused >= 0 && focused < mItemizedOverlay.size()) {
                mItemizedOverlay.setFocus(mItemizedOverlay.getItem(focused));
            }
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

    @Override
    public void showBusyDialog() {
        showBusyDialog(R.string.busy_dialog_default_message);
    }

    @Override
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
        return getString(resourceId);
    }

    @Override
    public int getIntResource(int resourceId) {
        return getIntResource(resourceId);
    }

    @Override
    public Context getContext() {
        return this;
    }

}
