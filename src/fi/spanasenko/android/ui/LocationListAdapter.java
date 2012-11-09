/**
 * File: LocationListAdapter.java
 * Created: 11/9/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import fi.spanasenko.android.R;
import fi.spanasenko.android.model.Location;
import fi.spanasenko.android.utils.Utils;

import java.text.DecimalFormat;

/**
 * LocationListAdapter
 * List adapter implementation for nearby locations.
 */
public class LocationListAdapter extends BaseAdapter {

    private Location[] mLocations;
    private LayoutInflater mInflater;
    private LocationInfo mUserLocation;
    private Context mContext;

    /**
     * Constructor.
     * @param ctx Parent context.
     * @param locations Locations array to be displayed with this adapter.
     */
    public LocationListAdapter(Context ctx, Location[] locations) {
        mLocations = locations;
        mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = ctx;
    }

    @Override
    public int getCount() {
        if (mLocations == null) {
            throw new IllegalStateException("Adapter is not initialised yet");
        }

        return mLocations.length;
    }

    @Override
    public Object getItem(int i) {
        if (mLocations == null || mLocations.length <= i) {
            throw new IndexOutOfBoundsException("There is no such item in adapter");
        }

        return mLocations[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Sets user location to determine distance.
     * @param userLocation LocationInfo object that holds data about user location.
     */
    public void setUserLocation(LocationInfo userLocation) {
        this.mUserLocation = userLocation;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.location_item, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Location current = (Location) getItem(i);
        holder.name.setText(current.getName());

        if (mUserLocation != null) {
            // Calculate distance between user and current location
            float distance = Utils.calculateDistance(mUserLocation.lastLat, mUserLocation.lastLong,
                    current.getLatitude(), current.getLongitude());

            // Format distance to be displayed nicely
            DecimalFormat distanceFormat = new DecimalFormat("#.## ");
            distanceFormat.setDecimalSeparatorAlwaysShown(false);
            String distanceKm = distanceFormat.format(distance) + mContext.getString(R.string.distance_km);

            holder.distance.setText(distanceKm);
            holder.distance.setVisibility(View.VISIBLE);
        } else {
            holder.distance.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * ViewHolder
     * Holds view elements used for item of this adapter.
     */
    private class ViewHolder {
        protected TextView name;
        protected TextView distance;

        /**
         * Constructor.
         * @param v Parent view.
         */
        public ViewHolder(View v) {
            name = (TextView) v.findViewById(R.id.location_name);
            distance = (TextView) v.findViewById(R.id.location_distance);
        }
    }

}
