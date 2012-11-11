package fi.spanasenko.android.model;

/**
 * Media
 * Represents a media object from Instagram API. Only limited set of attributes was included for clarity.
 */
public class Media {

    private String id;
    private Location location;
    private Images images;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }
}
