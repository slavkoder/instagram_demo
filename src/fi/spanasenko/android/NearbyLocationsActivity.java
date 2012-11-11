package fi.spanasenko.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import fi.spanasenko.android.model.Location;
import fi.spanasenko.android.presenter.NearbyLocationPresenter;
import fi.spanasenko.android.ui.LocationListAdapter;
import fi.spanasenko.android.utils.UserSettings;
import fi.spanasenko.android.view.INearbyLocationsView;

/**
 * NearbyLocationsActivity
 * Screen which shows nearby locations list for Instagram user. Calls authentication if needed.
 */
public class NearbyLocationsActivity extends BaseActivity implements INearbyLocationsView {

    private NearbyLocationPresenter mPresenter;

    private ListView mLocationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.locations_list_screen);

        mLocationList = (ListView) findViewById(android.R.id.list);
        mPresenter = new NearbyLocationPresenter(this);

        mPresenter.loadLocations();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPresenter.registerLocationObserver();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mPresenter.unregisterLocationObserver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_show_map: {
                // Save user view preference.
                UserSettings.getInstance(this).setIsMapPrefered(true);

                // Show list view.
                Intent showMap = new Intent(this, LocationsMapActivity.class);
                startActivity(showMap);
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void updateLocations(Location[] locations) {
        // Create and set adapter to show locations.
        LocationListAdapter adapter = new LocationListAdapter(this, locations);
        adapter.setUserLocation(new LocationInfo(getBaseContext()));
        mLocationList.setAdapter(adapter);

        // When user selects an item open media gallery for selected location.
        mLocationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Location location = (Location) adapterView.getItemAtPosition(position);
                mPresenter.openLocation(location);
            }
        });
    }

}
