package fi.spanasenko.android.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class MyImageView extends ImageView {

    private boolean mBlockLayout = false;
    private boolean mIsFixedSize = false;

    public MyImageView(Context context) {
        super(context);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        blockLayoutIfPossible();
        super.setImageDrawable(drawable);
        mBlockLayout = false;
    }

    @Override
    public void requestLayout() {
        if (!mBlockLayout) {
            super.requestLayout();
        }
    }

    private void blockLayoutIfPossible() {
        if (mIsFixedSize) {
            mBlockLayout = true;
        }
    }

    /**
     * Sets image as fixed size.
     * @param isFixedSize
     */
    public void setIsFixedSize(boolean isFixedSize) {
        this.mIsFixedSize = isFixedSize;
    }
}