/**
 * File: NearbyLocationsActivity.java
 * Created: 11/8/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import fi.spanasenko.android.model.Location;
import fi.spanasenko.android.presenter.NearbyLocationPresenter;
import fi.spanasenko.android.ui.LocationListAdapter;
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

        setContentView(R.layout.login_screen);

        mLocationList = (ListView) findViewById(android.R.id.list);
        mPresenter = new NearbyLocationPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPresenter.registerObserver();
        mPresenter.checkAuthorizationAndLoadLocations();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mPresenter.unregisterObserver();
    }

    @Override
    public void updateLocations(Location[] locations) {
        LocationListAdapter adapter = new LocationListAdapter(this, locations);
        adapter.setUserLocation(new LocationInfo(getBaseContext()));
        mLocationList.setAdapter(adapter);
        mLocationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Location location = (Location) adapterView.getItemAtPosition(position);
                mPresenter.openLocation(location);
            }
        });
    }

}
