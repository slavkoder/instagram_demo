/**
 * File: INearbyLocationsPresenter.java
 * Created: 11/10/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android.presenter;

import fi.spanasenko.android.model.Location;
import fi.spanasenko.android.view.INearbyLocationsView;

/**
 * INearbyLocationsPresenter
 * Class description
 */
public interface INearbyLocationsPresenter extends IBasePresenter<INearbyLocationsView> {

    /**
     *
     */
    void checkAuthorizationAndLoadLocations();

    /**
     * Checks gps status and prompt a user to turn it on if not. When checked starts location update.
     */
    void checkGpsStatusAndFetchLocations();

    /**
     * Initiates Instagram authorization.
     */
    void authorize();

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

}
