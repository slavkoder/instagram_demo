/**
 * File: InstagramDemoApp.java
 * Created: 11/8/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android;

import android.app.Application;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;

/**
 * InstagramDemoApp
 * Class description
 */
public class InstagramDemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize location library
        LocationLibrary.initialiseLibrary(getBaseContext(), getPackageName());
    }
}
