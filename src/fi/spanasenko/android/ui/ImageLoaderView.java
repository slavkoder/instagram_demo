package fi.spanasenko.android.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import fi.spanasenko.android.R;

/**
 * ImageLoaderView
 * Implementation of the view that contains ProgressBar and ImageView. Used to show loading indicator while downloading
 * images from remote/local source to ImageView.
 */
public class ImageLoaderView extends LinearLayout {

    private Context mContext;
    private ProgressBar mSpinner;
    private MyImageView mImage;

    /**
     * This is used when creating the view in XML.
     * @param context Parent context.
     * @param attrSet XML attributes.
     */
    public ImageLoaderView(final Context context, final AttributeSet attrSet) {
        super(context, attrSet);
        instantiate(context);
    }

    /**
     * This is used when creating the view programatically.
     * @param context the Activity context
     */
    public ImageLoaderView(final Context context) {
        super(context);
        instantiate(context);
    }

    /**
     * First time loading of the LoaderImageView
     * Sets up the LayoutParams of the view, you can change these to
     * get the required effects you want
     */
    private void instantiate(final Context context) {
        mContext = context;
        setGravity(Gravity.CENTER);

        mImage = new MyImageView(mContext);
        mImage.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        mSpinner = new ProgressBar(mContext);
        mSpinner.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        mSpinner.setIndeterminate(true);

        addView(mSpinner);
        addView(mImage);
    }

    public void startLoading() {
        mSpinner.setVisibility(View.VISIBLE);
        mImage.setVisibility(View.GONE);
    }

    /**
     * Sets image bitmap and hides waiting indicator if successful.
     * @param isLoadedSuccessfully True if image loaded fully, false if image drawable is DownloadedDrawable
     */
    public void setImageDrawable(Drawable image, boolean isLoadedSuccessfully) {
        mImage.setImageDrawable(image);

        if (isLoadedSuccessfully) {
            mSpinner.setVisibility(View.GONE);
            mImage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sets image bitmap and hides waiting indicator.
     * @param isLoadedSuccessfully True if loaded successfully, false otherwise. If false error icon displayed.
     */
    public void setImageBitmap(Bitmap image, boolean isLoadedSuccessfully) {
        mImage.setImageBitmap(image);

        mSpinner.setVisibility(View.GONE);
        mImage.setVisibility(View.VISIBLE);

        if (!isLoadedSuccessfully) {
            mImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
        }
    }

    /**
     * Returns ImageView associated with this instance.
     * @return ImageView associated with this instance.
     */
    public MyImageView getImageView() {
        return mImage;
    }
}
