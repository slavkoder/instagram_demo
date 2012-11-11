/**
 * File: INearbyLocationsPresenter.java
 * Created: 11/10/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android.presenter;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import fi.spanasenko.android.model.Location;
import fi.spanasenko.android.view.INearbyLocationsView;

/**
 * INearbyLocationsPresenter
 * Class description
 */
public interface INearbyLocationsPresenter extends IBasePresenter<INearbyLocationsView> {

    /**
     * Initiates loading locations from Instagram.
     */
    void loadLocations();

    /**
     *
     */
    void registerObserver();

    /**
     *
     */
    void unregisterObserver();

    /**
     * Opens selected location.
     * @param location Selected location.
     */
    void openLocation(Location location);

    /**
     * Returns last known location info.
     * @return last known location info.
     */
    LocationInfo getLastKnownLocation();

}
