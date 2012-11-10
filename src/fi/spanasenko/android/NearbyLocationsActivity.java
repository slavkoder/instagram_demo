/**
 * File: NearbyLocationsActivity.java
 * Created: 11/8/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import fi.spanasenko.android.instagram.InstagramApi;
import fi.spanasenko.android.instagram.OperationCallback;
import fi.spanasenko.android.instagram.OperationCallbackBase;
import fi.spanasenko.android.instagram.VoidOperationCallback;
import fi.spanasenko.android.model.Location;
import fi.spanasenko.android.ui.LocationListAdapter;
import fi.spanasenko.android.utils.LocationBroadcastReceiver;
import fi.spanasenko.android.utils.UiUtils;

/**
 * NearbyLocationsActivity
 * Screen which shows nearby locations list for Instagram user. Calls authentication if needed.
 */
public class NearbyLocationsActivity extends BaseActivity implements LocationBroadcastReceiver.LocationChangedListener {

    private InstagramApi mInstagram;
    private ListView mLocationList;
    private BroadcastReceiver mLocationReceiver;
    private LocationInfo mLastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_screen);

        mLocationList = (ListView) findViewById(android.R.id.list);

        mInstagram = InstagramApi.getInstance(this);
        mLocationReceiver = new LocationBroadcastReceiver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register for location broadcast
        registerReceiver(mLocationReceiver, new IntentFilter(LocationBroadcastReceiver.ACTION));

        // Check authorization status
        if (!mInstagram.hasAccessToken()) {
            authorize();
        } else {
            loadLocations();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister location update receiver
        unregisterReceiver(mLocationReceiver);
    }

    /**
     * Initiates Instagram authorization.
     */
    private void authorize() {
        if (isFinishing()) {
            return;
        }

        showBusyDialog(R.string.wait_access_token);

        mInstagram.authorize(new VoidOperationCallback(OperationCallbackBase.DispatchType.MainThread) {
            @Override
            protected void onCompleted() {
                dismissBusyDialog();
                Toast.makeText(NearbyLocationsActivity.this, "Authorized successfully!", Toast.LENGTH_LONG).show();

                // To avoid bother user this check will be issued once per authorization.
                checkGpsStatusAndFetchLocations();
            }

            @Override
            protected void onError(Exception error) {
                dismissBusyDialog();
                UiUtils.displayError(NearbyLocationsActivity.this, error);
            }
        });
    }

    /**
     * Checks gps status and prompt a user to turn it on if not. When checked starts location update.
     */
    private void checkGpsStatusAndFetchLocations() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            promptUser(R.string.gps_title, R.string.gps_message, android.R.string.yes, android.R.string.no,
                    new OperationCallback<String>() {
                        @Override
                        protected void onCompleted(String result) {
                            if (result.equals(getString(android.R.string.yes))) {
                                // Show device settings
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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

    /**
     * Initiates loading locations from Instagram.
     */
    private void loadLocations() {
        showBusyDialog(R.string.wait_locations);

        // Returns last known location
        if (mLastKnownLocation == null) {
            mLastKnownLocation = new LocationInfo(getBaseContext());
        }

        mInstagram.fetchNearbyLocations(mLastKnownLocation.lastLat, mLastKnownLocation.lastLong,
                new OperationCallback<Location[]>(OperationCallbackBase.DispatchType.MainThread) {
                    @Override
                    protected void onCompleted(Location[] result) {
                        dismissBusyDialog();
                        updateList(result);
                    }

                    @Override
                    protected void onError(Exception error) {
                        dismissBusyDialog();
                        UiUtils.displayError(NearbyLocationsActivity.this, error);
                    }
                });
    }

    /**
     * Updates list view with new location data.
     *
     * @param locations Array of locations to be displayed in the list view.
     */
    private void updateList(Location[] locations) {
        LocationListAdapter adapter = new LocationListAdapter(this, locations);
        adapter.setUserLocation(new LocationInfo(getBaseContext()));
        mLocationList.setAdapter(adapter);
        mLocationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Location location = (Location) adapterView.getItemAtPosition(position);

                Intent showRecentMedia = new Intent(NearbyLocationsActivity.this, ImageGalleryActivity.class);
                showRecentMedia.putExtra(ImageGalleryActivity.EXTRA_LOCATION_ID, location.getId());
                startActivity(showRecentMedia);
            }
        });
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
