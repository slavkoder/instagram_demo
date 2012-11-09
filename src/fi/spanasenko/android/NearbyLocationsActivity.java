/**
 * File: NearbyLocationsActivity.java
 * Created: 11/8/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android;

import android.content.Intent;
import android.os.Bundle;
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
import fi.spanasenko.android.utils.UiUtils;

/**
 * NearbyLocationsActivity
 * Screen which shows nearby locations list for Instagram user. Calls authentication if needed.
 */
public class NearbyLocationsActivity extends BaseActivity {

    private InstagramApi mInstagram;
    private ListView mLocationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        mLocationList = (ListView) findViewById(android.R.id.list);

        mInstagram = InstagramApi.getInstance(this);
        if (!mInstagram.hasAccessToken()) {
            authorize();
        } else {
            loadLocations();
        }
    }

    /**
     * Initiates Instagram authorization.
     */
    private void authorize() {
        showBusyDialog(R.string.wait_access_token);

        mInstagram.authorize(new VoidOperationCallback(OperationCallbackBase.DispatchType.MainThread) {
            @Override
            protected void onCompleted() {
                dismissBusyDialog();
                Toast.makeText(NearbyLocationsActivity.this, "Authorized successfully!", Toast.LENGTH_LONG).show();
                loadLocations();
            }

            @Override
            protected void onError(Exception error) {
                dismissBusyDialog();
                UiUtils.displayError(NearbyLocationsActivity.this, error);
            }
        });
    }

    /**
     * Initiates loading locations from Instagram.
     */
    private void loadLocations() {
        showBusyDialog(R.string.wait_locations);

        // Returns last known location
        LocationInfo lastKnown = new LocationInfo(getBaseContext());

        mInstagram.fetchNearbyLocations(lastKnown.lastLat, lastKnown.lastLong,
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

}
