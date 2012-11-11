package fi.spanasenko.android.presenter;

import fi.spanasenko.android.view.IBaseView;

/**
 * IBasePresenter
 * Base class for presenters.
 */
public interface IBasePresenter<T extends IBaseView> {

    public T getView();

    public void setView(T view);
}
