package fi.spanasenko.android;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import fi.spanasenko.android.instagram.InstagramApi;
import fi.spanasenko.android.instagram.OperationCallback;
import fi.spanasenko.android.instagram.OperationCallbackBase;
import fi.spanasenko.android.instagram.VoidOperationCallback;
import fi.spanasenko.android.model.Media;
import fi.spanasenko.android.ui.GalleryAdapter;
import fi.spanasenko.android.utils.UiUtils;

/**
 * ImageGalleryActivity
 * Represents image gallery screen with media fetched from Instagram.
 */
public class ImageGalleryActivity extends BaseActivity {

    public static final String EXTRA_LOCATION_ID = "fi.spanasenko.android.EXTRA_LOCATION_ID";

    private static final int NUM_COLUMNS_PORTRAIT = 3;
    private static final int NUM_COLUMNS_LANDSCAPE = 5;

    private GridView mGridView;
    private InstagramApi mInstagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_gallery);

        Bundle extra = getIntent().getExtras();
        if (extra == null || !extra.containsKey(EXTRA_LOCATION_ID)) {
            // There is nothing to display.
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
        showBusyDialog(R.string.wait_media);
        mInstagram.fetchRecentMedia(locationId,
                new OperationCallback<Media[]>(OperationCallbackBase.DispatchType.MainThread) {
                    @Override
                    protected void onCompleted(Media[] result) {
                        dismissBusyDialog();
                        updateGridView(result);
                    }

                    @Override
                    protected void onError(Exception error) {
                        dismissBusyDialog();
                        UiUtils.displayError(ImageGalleryActivity.this, error);
                    }
                });
    }

    /**
     * Updates grid view with new data by creating a new adapter.
     * @param media Media array with data to be displayed in the grid view.
     */
    private void updateGridView(Media[] media) {
        if (media != null && media.length > 0) {
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
        } else {
            UiUtils.notifyUser(this, R.string.no_media_title, R.string.no_media_message, new VoidOperationCallback() {
                @Override
                protected void onCompleted() {
                    finish();
                }

                @Override
                protected void onError(Exception error) {
                    finish();
                }
            });

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mGridView.setNumColumns(NUM_COLUMNS_LANDSCAPE);
        } else {
            mGridView.setNumColumns(NUM_COLUMNS_PORTRAIT);
        }
    }
}
