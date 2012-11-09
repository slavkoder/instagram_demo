/**
 * File: NearbyLocationsActivity.java
 * Created: 11/8/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android;

import android.app.Activity;
import android.app.ProgressDialog;
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
public class NearbyLocationsActivity extends Activity {

    private InstagramApi mInstagram;

    private ProgressDialog mProgress;

    private ListView mLocationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        mLocationList = (ListView) findViewById(android.R.id.list);

        mProgress = new ProgressDialog(this);
        mProgress.setCancelable(false);

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
        mProgress.setMessage(getString(R.string.wait_access_token));
        mProgress.show();

        mInstagram.authorize(new VoidOperationCallback(OperationCallbackBase.DispatchType.MainThread) {
            @Override
            protected void onCompleted() {
                mProgress.dismiss();
                Toast.makeText(NearbyLocationsActivity.this, "Authorized successfully!", Toast.LENGTH_LONG).show();
                loadLocations();
            }

            @Override
            protected void onError(Exception error) {
                mProgress.dismiss();
                UiUtils.displayError(NearbyLocationsActivity.this, error);
            }
        });
    }

    /**
     * Initiates loading locations from Instagram.
     */
    private void loadLocations() {
        mProgress.setMessage(getString(R.string.wait_locations));
        mProgress.show();

        // Returns last known location
        LocationInfo lastKnown = new LocationInfo(getBaseContext());

        mInstagram.fetchNearbyLocations(lastKnown.lastLat, lastKnown.lastLong,
                new OperationCallback<Location[]>(OperationCallbackBase.DispatchType.MainThread) {
            @Override
            protected void onCompleted(Location[] result) {
                mProgress.dismiss();
                updateList(result);
            }

            @Override
            protected void onError(Exception error) {
                mProgress.dismiss();
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
