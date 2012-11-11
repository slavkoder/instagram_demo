/**
 * File: LocationsOverlay.java
 * Created: 11/10/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android.ui;

import android.graphics.drawable.Drawable;
import com.google.android.maps.MapView;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import fi.spanasenko.android.model.Location;
import fi.spanasenko.android.presenter.NearbyLocationPresenter;

import java.util.ArrayList;

/**
 * LocationsOverlay
 * Class description
 */
public class LocationsOverlay extends BalloonItemizedOverlay<LocationOverlayItem> {

    private ArrayList<LocationOverlayItem> mOverlays = new ArrayList<LocationOverlayItem>();

    private NearbyLocationPresenter mPresenter;

    public LocationsOverlay(Drawable drawable, MapView mapView, NearbyLocationPresenter presenter) {
        super(boundCenterBottom(drawable), mapView);
        mPresenter = presenter;
    }

    @Override
    protected LocationOverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    @Override
    public int size() {
        return mOverlays.size();
    }

    /**
     * Adds overlay item.
     * @param overlay Location overlay item.
     */
    public void addOverlay(LocationOverlayItem overlay) {
        mOverlays.add(overlay);
        populate();
    }

    /**
     * Removes all current overlays from storage.
     */
    public void clearOverlays() {
        mOverlays.clear();
    }

    @Override
    protected boolean onBalloonTap(int index, LocationOverlayItem item) {
        Location selectedLocation = item.getLocation();
        mPresenter.openLocation(selectedLocation);
        return true;
    }
}
