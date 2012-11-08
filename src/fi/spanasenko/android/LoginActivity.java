/**
 * File: LoginActivity.java
 * Created: 11/8/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import fi.spanasenko.android.instagram.InstagramApi;
import fi.spanasenko.android.instagram.VoidOperationCallback;
import fi.spanasenko.android.utils.UserSettings;

/**
 * LoginActivity
 * Class description
 */
public class LoginActivity extends Activity {

    private InstagramApi mApp;
    private UserSettings mSettings;
    private Button btnConnect;
    private TextView tvSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        mApp = InstagramApi.getInstance(this);

        tvSummary = (TextView) findViewById(R.id.tvSummary);

        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mApp.hasAccessToken()) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(
                            LoginActivity.this);
                    builder.setMessage("Disconnect from Instagram?")
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            mApp.logout();
                                            btnConnect.setText("Connect");
                                            tvSummary.setText("Not connected");
                                        }
                                    })
                            .setNegativeButton("No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    final AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    mApp.authorize(new VoidOperationCallback() {
                        @Override
                        protected void onCompleted() {
                            Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        protected void onError(Exception error) {

                        }
                    });
                }
            }
        });

        if (mApp.hasAccessToken()) {
            tvSummary.setText("Connected as " + UserSettings.getInstance(this).getUsername());
            btnConnect.setText("Disconnect");
        }

    }

}
