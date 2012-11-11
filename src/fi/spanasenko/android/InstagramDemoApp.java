package fi.spanasenko.android;

import android.app.Application;
import android.content.Context;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * InstagramDemoApp
 * An instance of this class is available during the application lifecycle. Used to init crash-reporting lib here.
 */
@ReportsCrashes(formKey = "dFhuYTBzMkxjeENmOE84QVlwcXNGaHc6MQ")
public class InstagramDemoApp extends Application {

    private boolean isLoggingOut;

    @Override
    public void onCreate() {
        ACRA.init(this);
        super.onCreate();

        // Initialize location library and allow every location update.
        LocationLibrary.initialiseLibrary(getBaseContext(), true, getPackageName());
    }

    public static InstagramDemoApp getInstance(Context ctx) {
        return (InstagramDemoApp) ctx.getApplicationContext();
    }

    public boolean isLoggingOut() {
        return isLoggingOut;
    }

    public void setLoggingOut(boolean loggingOut) {
        isLoggingOut = loggingOut;
    }
}
