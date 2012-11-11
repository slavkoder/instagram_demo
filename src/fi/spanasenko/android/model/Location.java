package fi.spanasenko.android.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.Gson;

/**
 * Location
 * Class representing location item from Instagram API.
 */
public class Location {

    private String id;
    private float latitude;
    private float longitude;
    private String name;

    public Location() {
        super();
    }

    public Location(String id, float latitude, float longitude, String name) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            String json = in.readString();
            Gson gson = new Gson();
            return gson.fromJson(json, User.class);
        }

        @Override
        public User[] newArray(int i) {
            return new User[i];
        }

    };
}
