/**
 * File: InstagramDemoApp.java
 * Created: 11/8/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android;

import android.app.Application;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * InstagramDemoApp
 * An instance of this class is available during the application lifecycle. Used to init crash-reporting lib here.
 */
@ReportsCrashes(formKey = "dFhuYTBzMkxjeENmOE84QVlwcXNGaHc6MQ")
public class InstagramDemoApp extends Application {

    @Override
    public void onCreate() {
        ACRA.init(this);
        super.onCreate();

        // Initialize location library
        LocationLibrary.initialiseLibrary(getBaseContext(), getPackageName());
    }

}
