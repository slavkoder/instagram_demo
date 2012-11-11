package fi.spanasenko.android.model;

/**
 * Images
 * This class represents an image set from Instagram API. It also included implementation of simple Image class.
 */
public class Images {

    private Image low_resolution;
    private Image thumbnail;
    private Image standard_resolution;

    public Images(Image lowResolution, Image thumbnail, Image standardResolution) {
        this.low_resolution = lowResolution;
        this.thumbnail = thumbnail;
        this.standard_resolution = standardResolution;
    }

    public Image getLowResolution() {
        return low_resolution;
    }

    public void setLowResolution(Image lowResolution) {
        this.low_resolution = lowResolution;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Image getStandardResolution() {
        return standard_resolution;
    }

    public void setStandardResolution(Image standardResolution) {
        this.standard_resolution = standardResolution;
    }

    /**
     * Image
     * This class represents an image from Instagram API.
     */
    public static class Image {

        private String url;
        private int width;
        private int height;

        public Image(String url, int width, int height) {
            this.url = url;
            this.width = width;
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

}
