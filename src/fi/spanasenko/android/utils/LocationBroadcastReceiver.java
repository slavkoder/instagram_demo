/**
 * File: LocationBroadcastReceiver.java
 * Created: 11/10/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;

/**
 * LocationBroadcastReceiver
 * Class description
 */
public class LocationBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION = "fi.spanasenko.android.littlefluffylocationlibrary.LOCATION_CHANGED";
    private static final String EXTRA_LOCATION = "com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo";

    private LocationChangedListener mListener;

    /**
     * Constructor.
     * @param listener Object that implements listener interface.
     */
    public LocationBroadcastReceiver(LocationChangedListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extra = intent.getExtras();
        if (extra != null && extra.containsKey(EXTRA_LOCATION)) {
            mListener.onLocationReceived((LocationInfo) extra.getSerializable(EXTRA_LOCATION));
        }
    }

    /**
     * LocationChangedListener
     * Interface for location receiver.
     */
    public static interface LocationChangedListener {
        void onLocationReceived(LocationInfo locationInfo);
    }
}
