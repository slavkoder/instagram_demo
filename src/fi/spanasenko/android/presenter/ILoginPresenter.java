package fi.spanasenko.android.presenter;

import fi.spanasenko.android.view.ILoginView;

/**
 * ILoginPresenter
 * Interface for login presenter. Contains method that could be called from LoginActivty.
 */
public interface ILoginPresenter extends IBasePresenter<ILoginView> {

    /**
     * Checks authorization status and shows next view. If authorized it's prefered view, if not - login view.
     */
    void checkAuthorizationAndShowNextView();

    /**
     * Checks if GPS is turned on prior to showing next view. If turned off asks user if he wants to turn it on.
     */
    void checkGpsStatusAndShowNextView();

    /**
     * Initiates Instagram authorization.
     */
    void authorize();

    /**
     * Logs out from Instagram.
     */
    void logout();


}
