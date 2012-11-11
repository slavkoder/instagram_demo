package fi.spanasenko.android.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.Gson;

/**
 * User
 * Representation of a user class from Instagram API.
 */
public class User {

    private String id;
    private String username;
    private String full_name;
    private String bio;
    private String access_token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return full_name;
    }

    public void setFullName(String fullName) {
        this.full_name = fullName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String accessToken) {
        this.access_token = accessToken;
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
