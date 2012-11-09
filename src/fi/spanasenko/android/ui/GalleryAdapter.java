/**
 * File: GalleryAdapter.java
 * Created: 11/9/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import fi.spanasenko.android.R;
import fi.spanasenko.android.model.Media;
import fi.spanasenko.android.utils.ImageDownloader;

/**
 * GalleryAdapter
 * The class responsible for displaying images for GridView in ImageGalleryActivity.
 */
public class GalleryAdapter extends BaseAdapter {

    private Media[] mMedia;
    private LayoutInflater mInflater;
    private ImageDownloader mImageDownloader;

    /**
     * Constructor.
     * @param ctx Parent context.
     * @param locations Locations array to be displayed with this adapter.
     */
    public GalleryAdapter(Context ctx, Media[] locations) {
        mMedia = locations;
        mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        ImageView image;
        if (view == null) {
            image = (ImageView) mInflater.inflate(R.layout.media_item, viewGroup, false);
        } else {
            image = (ImageView) view;
        }

        Media currentItem = (Media) getItem(i);
        mImageDownloader.download(currentItem.getImages().getThumbnail().getUrl(), image, 0);

        return image;
    }
}
