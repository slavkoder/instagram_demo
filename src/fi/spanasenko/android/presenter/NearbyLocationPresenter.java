/**
 * File: NearbyLocationPresenter.java
 * Created: 11/10/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.provider.Settings;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import fi.spanasenko.android.ImageGalleryActivity;
import fi.spanasenko.android.R;
import fi.spanasenko.android.instagram.InstagramApi;
import fi.spanasenko.android.instagram.OperationCallback;
import fi.spanasenko.android.instagram.OperationCallbackBase;
import fi.spanasenko.android.instagram.VoidOperationCallback;
import fi.spanasenko.android.model.Location;
import fi.spanasenko.android.utils.LocationBroadcastReceiver;
import fi.spanasenko.android.view.INearbyLocationsView;

/**
 * NearbyLocationPresenter
 * Class description
 */
public class NearbyLocationPresenter extends PresenterBase<INearbyLocationsView> implements INearbyLocationsPresenter,
        LocationBroadcastReceiver.LocationChangedListener {

    private InstagramApi mInstagram;
    private BroadcastReceiver mLocationReceiver;
    private LocationInfo mLastKnownLocation;

    /**
     * Constructor matching parent.
     * @param view Parent view interface.
     */
    public NearbyLocationPresenter(INearbyLocationsView view) {
        super(view, view.getContext());

        mInstagram = InstagramApi.getInstance(getContext());
        mLocationReceiver = new LocationBroadcastReceiver(this);
    }

    @Override
    public void checkAuthorizationAndLoadLocations() {
        // Check authorization status
        if (!mInstagram.hasAccessToken()) {
            authorize();
        } else {
            loadLocations();
        }
    }

    @Override
    public void authorize() {
        getView().showBusyDialog(R.string.wait_access_token);

        mInstagram.authorize(new VoidOperationCallback(OperationCallbackBase.DispatchType.MainThread) {
            @Override
            protected void onCompleted() {
                getView().dismissBusyDialog();

                // To avoid bother user this check will be issued once per authorization.
                checkGpsStatusAndFetchLocations();
            }

            @Override
            protected void onError(Exception error) {
                getView().dismissBusyDialog();
                getView().onError(error);
            }
        });
    }

    @Override
    public void checkGpsStatusAndFetchLocations() {
        final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getView().promptUser(R.string.gps_title, R.string.gps_message, android.R.string.yes, android.R.string.no,
                    new OperationCallback<String>() {
                        @Override
                        protected void onCompleted(String result) {
                            if (result.equals(getView().getStringResource(android.R.string.yes))) {
                                // Show device settings
                                openActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }

                            // Decision is made, now we can load locations
                            loadLocations();
                        }

                        @Override
                        protected void onError(Exception error) {
                            // Anyway load that damn locations!
                            loadLocations();
                        }
                    });
        } else {
            // No prompt needed, go fetching locations from Instagram.
            loadLocations();
        }
    }

    @Override
    public void loadLocations() {
        getView().showBusyDialog(R.string.wait_locations);

        // Returns last known location
        if (mLastKnownLocation == null) {
            mLastKnownLocation = new LocationInfo(getView().getBaseContext());
        }

        mInstagram.fetchNearbyLocations(mLastKnownLocation.lastLat, mLastKnownLocation.lastLong,
                new OperationCallback<Location[]>(OperationCallbackBase.DispatchType.MainThread) {
                    @Override
                    protected void onCompleted(Location[] result) {
                        getView().dismissBusyDialog();
                        getView().updateLocations(result);
                    }

                    @Override
                    protected void onError(Exception error) {
                        getView().dismissBusyDialog();
                        onError(error);
                    }
                });
    }

    @Override
    public void registerObserver() {
        // Register for location broadcast
        getContext().registerReceiver(mLocationReceiver, new IntentFilter(LocationBroadcastReceiver.ACTION));

    }

    @Override
    public void unregisterObserver() {
        getContext().unregisterReceiver(mLocationReceiver);
    }

    @Override
    public void openLocation(Location location) {
        Intent showRecentMedia = new Intent(getContext(), ImageGalleryActivity.class);
        showRecentMedia.putExtra(ImageGalleryActivity.EXTRA_LOCATION_ID, location.getId());
        openActivity(showRecentMedia);
    }

    @Override
    public void logout() {
        mInstagram.logout();
        getView().logout();
    }

    @Override
    public void onLocationReceived(LocationInfo locationInfo) {
        LocationInfo oldLocation = mLastKnownLocation;
        mLastKnownLocation = locationInfo;
        if (locationInfo.lastAccuracy < oldLocation.lastAccuracy
                || locationInfo.lastLat != oldLocation.lastLat
                || locationInfo.lastLong != oldLocation.lastLong) {

            // Only update if location changed reasonably
            loadLocations();
        }
    }
}
