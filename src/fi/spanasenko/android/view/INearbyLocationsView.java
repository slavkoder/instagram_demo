/**
 * File: INearbyLocationsView.java
 * Created: 11/10/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android.view;

import android.content.Context;
import fi.spanasenko.android.model.Location;

/**
 * INearbyLocationsView
 * Nearby locations view interface.
 */
public interface INearbyLocationsView extends IBaseView {

    /**
     * Returns application base context.
     * @return Base context.
     */
    Context getBaseContext();

    /**
     * Updates list view with new location data.
     * @param locations Array of locations to be displayed in the list view.
     */
    public void updateLocations(Location[] locations);

}
