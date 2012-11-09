/**
 * File: ImageGalleryActivity.java
 * Created: 11/9/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import fi.spanasenko.android.instagram.InstagramApi;
import fi.spanasenko.android.instagram.OperationCallback;
import fi.spanasenko.android.instagram.OperationCallbackBase;
import fi.spanasenko.android.model.Media;
import fi.spanasenko.android.ui.GalleryAdapter;
import fi.spanasenko.android.utils.UiUtils;

/**
 * ImageGalleryActivity
 * Class description
 */
public class ImageGalleryActivity extends Activity {

    public static final String EXTRA_LOCATION_ID = "fi.spanasenko.android.EXTRA_LOCATION_ID";

    private GridView mGridView;
    private InstagramApi mInstagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_gallery);

        Bundle extra = getIntent().getExtras();
        if (extra == null || !extra.containsKey(EXTRA_LOCATION_ID)) {
            // There is nothing to display
            finish();
            return;
        }

        mGridView = (GridView) findViewById(R.id.grid_view);
        mInstagram = InstagramApi.getInstance(this);

        String locationId = extra.getString(EXTRA_LOCATION_ID);
        fetchMediaForLocation(locationId);
    }

    /**
     * Requests media from the Instagram API.
     * @param locationId Id of location to request recent media from.
     */
    private void fetchMediaForLocation(String locationId) {
        mInstagram.fetchRecentMedia(locationId, new OperationCallback<Media[]>(OperationCallbackBase.DispatchType.MainThread) {
            @Override
            protected void onCompleted(Media[] result) {
                updateGridView(result);
            }

            @Override
            protected void onError(Exception error) {
                UiUtils.displayError(ImageGalleryActivity.this, error);
            }
        });
    }

    /**
     * Updates grid view with new data by creating a new adapter.
     * @param media Media array with data to be displayed in the grid view.
     */
    private void updateGridView(Media[] media) {
        GalleryAdapter adapter = new GalleryAdapter(this, media);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Media media = (Media) adapterView.getItemAtPosition(position);

                Intent showFullSizeImage = new Intent(ImageGalleryActivity.this, ViewImageActivity.class);
                showFullSizeImage.putExtra(ViewImageActivity.EXTRA_FULL_SIZE_URL,
                        media.getImages().getStandardResolution().getUrl());
                startActivity(showFullSizeImage);
            }
        });
    }
}
