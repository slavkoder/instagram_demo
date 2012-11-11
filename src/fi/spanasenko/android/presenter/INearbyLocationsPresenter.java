package fi.spanasenko.android.presenter;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import fi.spanasenko.android.model.Location;
import fi.spanasenko.android.view.INearbyLocationsView;

/**
 * INearbyLocationsPresenter
 * Interface for nearby locations presenter which. Responsible for listening for location changes and requesting
 * locations from Instagram according to user's position.
 */
public interface INearbyLocationsPresenter extends IBasePresenter<INearbyLocationsView> {

    /**
     * Initiates loading locations from Instagram.
     */
    void loadLocations();

    /**
     * Registers observer for location changes.
     */
    void registerLocationObserver();

    /**
     * Unregisters location changes observer.
     */
    void unregisterLocationObserver();

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
