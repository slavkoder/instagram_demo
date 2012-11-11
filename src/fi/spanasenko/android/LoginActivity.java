package fi.spanasenko.android;

import android.os.Bundle;
import android.view.View;
import fi.spanasenko.android.presenter.LoginPresenter;
import fi.spanasenko.android.view.ILoginView;

/**
 * LoginActivity
 * Simple login screen activity which is responsible for checking login status and showing login dialog.
 */
public class LoginActivity extends BaseActivity implements ILoginView {

    private LoginPresenter mPresenter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.login_screen);

        mPresenter = new LoginPresenter(this);

        Bundle extra = getIntent().getExtras();
        if (extra != null && extra.getBoolean(BaseActivity.EXTRA_LOGOUT)) {
            mPresenter.logout();
        } else {
            mPresenter.checkAuthorizationAndShowNextView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Handles login button click.
     * @param v Reference to the caller view.
     */
    public void onLoginClicked(View v) {
        mPresenter.authorize();
        InstagramDemoApp.getInstance(this).setLoggingOut(false);
    }
}
