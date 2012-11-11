package fi.spanasenko.android.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

/**
 * LocationBroadcastReceiver
 * Receives location update broadcasts from Little Fluffy Location Library.
 */
public class LocationBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION = "fi.spanasenko.android.littlefluffylocationlibrary.LOCATION_CHANGED";

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
        if (extra != null && extra.containsKey(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO)) {
            mListener.onLocationReceived((LocationInfo) extra.getSerializable(
                    LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO));
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
