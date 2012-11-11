package fi.spanasenko.android.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import fi.spanasenko.android.model.Images;
import fi.spanasenko.android.model.Media;
import fi.spanasenko.android.utils.ImageDownloader;

/**
 * GalleryAdapter
 * The class responsible for displaying images for GridView in ImageGalleryActivity.
 */
public class GalleryAdapter extends BaseAdapter {

    private Media[] mMedia;
    private Context mContext;
    private ImageDownloader mImageDownloader;

    /**
     * Constructor.
     * @param ctx      Parent context.
     * @param pictures Pictures array to be displayed with this adapter.
     */
    public GalleryAdapter(Context ctx, Media[] pictures) {
        mMedia = pictures;
        mContext = ctx;
        mImageDownloader = new ImageDownloader(ctx);
    }

    @Override
    public int getCount() {
        if (mMedia == null) {
            throw new IllegalStateException("Adapter is not initialised yet");
        }

        return mMedia.length;
    }

    @Override
    public Object getItem(int i) {
        if (mMedia == null || mMedia.length <= i) {
            throw new IndexOutOfBoundsException("There is no such item in adapter");
        }

        return mMedia[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Images.Image thumbnail = ((Media) getItem(i)).getImages().getThumbnail();

        ViewHolder holder;
        if (view == null) {
            view = new ImageLoaderView(mContext);
            view.setLayoutParams(new GridView.LayoutParams(thumbnail.getWidth(), thumbnail.getWidth()));
            ((ImageLoaderView) view).getImageView().setScaleType(ImageView.ScaleType.FIT_XY);

            holder = new ViewHolder();
            holder.image = (ImageLoaderView) view;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        mImageDownloader.download(thumbnail.getUrl(), holder.image, 0);

        return view;
    }

    /**
     * ViewHolder
     * Holds view components.
     */
    private static class ViewHolder {
        public ImageLoaderView image;
    }
}
