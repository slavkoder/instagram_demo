/**
 * File: ILoginPresenter.java
 * Created: 11/11/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android.presenter;

import fi.spanasenko.android.view.ILoginView;

/**
 * ILoginPresenter
 * Class description
 */
public interface ILoginPresenter extends IBasePresenter<ILoginView> {

    /**
     *
     */
    void checkAuthorizationAndShowNextView();

    /**
     *
     */
    void checkGpsStatusAndShowNextView();

    /**
     * Initiates Instagram authorization.
     */
    void authorize();

    /**
     *
     */
    void logout();


}
