package fi.spanasenko.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * UserSettings
 * Class provides easy access to user settings stored in shared preferences.
 */
public class UserSettings {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private static final String SHARED = "instagram_demo_pref";
    private static final String API_USERNAME = "username";
    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_ACCESS_TOKEN = "access_token";
    private static final String IS_MAP_PREFERRED = "is_map_preferred";

    private static UserSettings _instance;

    /**
     * Default constructor.
     * @param context Parent context.
     */
    private UserSettings(Context context) {
        sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    /**
     * Returns instance of the UserSettings class.
     * @param context Parent context.
     * @return instance of the UserSettings class.
     */
    public static UserSettings getInstance(Context context) {
        if (_instance == null) {
            _instance = new UserSettings(context);
        }

        return _instance;
    }

    /**
     * Stores user access token and related data in the application shared preferences.
     * @param accessToken Access token.
     * @param id Instagram user id.
     * @param username Instagram username.
     * @param fullName Instagram user full name.
     */
    public void storeAccessToken(String accessToken, String id, String username, String fullName) {
        editor.putString(API_ID, id);
        editor.putString(API_NAME, fullName);
        editor.putString(API_ACCESS_TOKEN, accessToken);
        editor.putString(API_USERNAME, username);
        editor.commit();
    }

    /**
     * Stores user access token in the application shared preferences.
     * @param accessToken Access token.
     */
    public void storeAccessToken(String accessToken) {
        editor.putString(API_ACCESS_TOKEN, accessToken);
        editor.commit();
    }

    /**
     * Resets access token and user name.
     */
    public void resetAccessToken() {
        editor.putString(API_ID, null);
        editor.putString(API_NAME, null);
        editor.putString(API_ACCESS_TOKEN, null);
        editor.putString(API_USERNAME, null);
        editor.commit();
    }

    /**
     * Returns user name.
     * @return User name.
     */
    public String getUsername() {
        return sharedPref.getString(API_USERNAME, null);
    }

    /**
     * Returns instagram user id.
     * @return instagram user id.
     */
    public String getId() {
        return sharedPref.getString(API_ID, null);
    }

    /**
     * Returns instagram user full name.
     * @return instagram user full name.
     */
    public String getFullName() {
        return sharedPref.getString(API_NAME, null);
    }

    /**
     * Returns instagram access token.
     * @return Access token.
     */
    public String getAccessToken() {
        return sharedPref.getString(API_ACCESS_TOKEN, null);
    }

    /**
     * Sets whether user prefers map view or not.
     * @param isMapPrefered If true map view is preferred, if false - list view should be shown.
     */
    public void setIsMapPrefered(boolean isMapPrefered) {
        editor.putBoolean(IS_MAP_PREFERRED, isMapPrefered);
        editor.commit();
    }

    /**
     * Returns whether user prefers map view or not.
     * @return If true map view is preferred, if false - list view should be shown.
     */
    public boolean isMapPrefered() {
        return sharedPref.getBoolean(IS_MAP_PREFERRED, true);
    }
}
