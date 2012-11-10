/**
 * File: ViewImageActivity.java
 * Created: 11/7/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android;

import android.app.Activity;
import android.os.Bundle;
import fi.spanasenko.android.ui.ImageLoaderView;
import fi.spanasenko.android.utils.ImageDownloader;

/**
 * ViewImageActivity
 * The screen that shows a full size image.
 */
public class ViewImageActivity extends Activity {

    public static final String EXTRA_FULL_SIZE_URL = "fi.spanasenko.android.EXTRA_FULL_SIZE_URL";

    private ImageDownloader mImageDownloader;
    private ImageLoaderView mPhotoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.full_screen_image);
        mPhotoView = (ImageLoaderView) findViewById(R.id.image_loader);

        mImageDownloader = new ImageDownloader(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_FULL_SIZE_URL)) {
            String url = extras.getString(EXTRA_FULL_SIZE_URL);
            mImageDownloader.download(url, mPhotoView, 0);
        } else {
            // Nothing to show, though it might happen only if full size ulr wasn't parsed correctly.
            finish();
        }
    }

}
