package fi.spanasenko.android.ui;

import com.google.android.maps.OverlayItem;
import fi.spanasenko.android.model.Location;
import fi.spanasenko.android.utils.Utils;

/**
 * LocationOverlayItem
 * Overlay item that has location data.
 */
public class LocationOverlayItem extends OverlayItem {

    private Location mLocation;

    /**
     * Constructor.
     * @param location Location object representing this overlay item.
     */
    public LocationOverlayItem(Location location) {
        super(Utils.getGeoPoint(location.getLatitude(), location.getLongitude()), location.getName(), "");

        mLocation = location;
    }

    /**
     * Returns location object associated with this overlay item.
     * @return location object associated with this overlay item.
     */
    public Location getLocation() {
        return mLocation;
    }
}
