package fi.spanasenko.android.instagram;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import fi.spanasenko.android.R;
import fi.spanasenko.android.model.User;
import fi.spanasenko.android.utils.UserSettings;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Thiago Locatelli <thiago.locatelli@gmail.com>
 * @author Lorensius W. L T <lorenz@londatiga.net>
 */
public class InstagramApi {

    private static final String TAG = InstagramApi.class.getSimpleName();

    private static InstagramApi _instance;

    private UserSettings mSession;

    private String mAuthUrl;
    private String mTokenUrl;
    private String mAccessToken;
    private Context mCtx;
    private ProgressDialog mProgress;

    // Instagram API endpoints
    private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    private static final String API_URL = "https://api.instagram.com/v1";

    // Instagram API keys
    public static final String CLIENT_ID = "ab11cf78383844adba1c73ce9363f2de";
    public static final String CLIENT_SECRET = "8350a30c3cfe47fea5e0dd6248ffa89f";
    public static final String CALLBACK_URL = "instagram://connection";

    /**
     * Default constructor.
     * @param context Parent context.
     */
    private InstagramApi(Context context) {

        mCtx = context;
        mSession = UserSettings.getInstance(context);
        mAccessToken = mSession.getAccessToken();

        mTokenUrl = TOKEN_URL + "?client_id=" + CLIENT_ID + "&client_secret="
                + CLIENT_SECRET + "&redirect_uri=" + CALLBACK_URL + "&grant_type=authorization_code";
        mAuthUrl = AUTH_URL + "?client_id=" + CLIENT_ID + "&redirect_uri="
                + CALLBACK_URL + "&response_type=code&display=touch&scope=likes+comments+relationships";
    }

    /**
     * Returns an instance of InstagramApi class or creates if not created.
     * @param context Parent context.
     * @return Instance of InstagramApi
     */
    public static InstagramApi getInstance(Context context) {
        if (_instance == null) {
            _instance = new InstagramApi(context);
        }

        return _instance;
    }

    /**
     * Logs user out by wiping access token and other user data data.
     */
    public void logout() {
        if (mAccessToken != null) {
            mSession.resetAccessToken();
            mAccessToken = null;
        }
    }

    /**
     * Returns whether the instance has access token or not.
     * @return True if there is a valid access token or false if not.
     */
    public boolean hasAccessToken() {
        return (mAccessToken == null) ? false : true;
    }

    public void authorize(final VoidOperationCallback callback) {
        InstagramDialog.OAuthDialogListener listener = new InstagramDialog.OAuthDialogListener() {
            @Override
            public void onComplete(String code) {
                mProgress.setMessage(mCtx.getString(R.string.wait_access_token));
                mProgress.show();

                getAccessToken(code, new OperationCallback<User>() {
                    @Override
                    protected void onCompleted(User result) {
                        // Store access token and user data
                        mSession.storeAccessToken(result.getAccessToken(), result.getId(), result.getUsername(),
                                result.getFullName());

                        // Notify caller application
                        callback.onCompleted();
                    }

                    @Override
                    protected void onError(Exception error) {
                        mProgress.dismiss();
                        callback.onError(error);
                    }
                });
            }

            @Override
            public void onError(String error) {
                callback.onError(new Exception(error));
            }
        };

        InstagramDialog dialog = new InstagramDialog(mCtx, mAuthUrl, listener);
        dialog.show();

        mProgress = new ProgressDialog(mCtx);
        mProgress.setCancelable(false);
    }

    /**
     * Returns callback URL.
     * @return Returns callback url.
     */
    protected String getCallbackUrl() {
        return CALLBACK_URL;
    }

    /**
     * Requests access token for given code.
     * @param code Code to be used for access token request.
     * @param callback Callback object responsible for handling completion or error.
     */
    private void getAccessToken(final String code, final OperationCallback<User> callback) {

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Getting access token");
                try {
                    String postData = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET +
                            "&grant_type=authorization_code" + "&redirect_uri=" + CALLBACK_URL + "&code=" + code;
                    String response = postRequest(TOKEN_URL, postData);

                    Log.i(TAG, "response " + response);
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();

                    mAccessToken = jsonObj.getString("access_token");
                    Log.i(TAG, "Got access token: " + mAccessToken);

                    Gson gson = new Gson();
                    User user = gson.fromJson(jsonObj.getJSONObject("user").toString(), User.class);
                    user.setAccessToken(mAccessToken);

                    callback.notifyCompleted(user);
                } catch (Exception ex) {
                    callback.notifyError(ex);
                    Log.e(TAG, "Error getting access token", ex);
                }

            }
        }.start();
    }

//    private void fetchUserName() {
//
//        new Thread() {
//            @Override
//            public void run() {
//                Log.i(TAG, "Fetching user info");
//                int what = WHAT_FINALIZE;
//                try {
//                    String url = API_URL + "/users/" + mSession.getId() + "/?access_token=" + mAccessToken;
//                    String response = getRequest(url);
//                    System.out.println(response);
//
//                    JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
//                    String name = jsonObj.getJSONObject("data").getString("full_name");
//                    String bio = jsonObj.getJSONObject("data").getString("bio");
//                    Log.i(TAG, "Got name: " + name + ", bio [" + bio + "]");
//                } catch (Exception ex) {
//                    what = WHAT_ERROR;
//                    ex.printStackTrace();
//                }
//
//                mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
//            }
//        }.start();
//
//    }

    /**
     * Reads stream to string.
     * @param is InpurStream to read data from.
     * @return String with data from given stream.
     * @throws IOException
     */
    private String streamToString(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }

    /**
     * Issues GET request to the given url and returns response.
     * @param requestUrl Api endpoint with parameters.
     * @return Server response.
     * @throws IOException
     */
    private String getRequest(String requestUrl) throws IOException {
        URL url = new URL(requestUrl);

        Log.d(TAG, "Opening URL " + url.toString());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoInput(true);
        urlConnection.connect();
        String response = streamToString(urlConnection.getInputStream());

        return response;
    }

    /**
     * Issues POST request to the given url and writes given data from string.
     * @param requestUrl Api endpoint for POST request.
     * @param postData Data to be posted.
     * @return Server response.
     * @throws IOException
     */
    private String postRequest(String requestUrl, String postData) throws IOException {
        URL url = new URL(requestUrl);

        Log.i(TAG, "Opening Token URL " + url.toString());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
        writer.write(postData);
        writer.flush();
        String response = streamToString(urlConnection.getInputStream());

        return response;
    }

}