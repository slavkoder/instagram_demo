package fi.spanasenko.android.presenter;

import fi.spanasenko.android.view.IBaseView;

/**
 * IBasePresenter
 * Base class for presenters.
 */
public interface IBasePresenter<T extends IBaseView>
{
	public void logout();


	/**
	 * Performs any initialization activities that the presenter needs to do
	 * when first associated with a new view.
	 * 
	 * Android views should call this method in <code>onResume()</code> unless
	 * there is a specific reason not to do so.
	 */
	public void init();

	/**
	 * Performs any deinitialization activities that the presenter should do
	 * when the view is about to be made obsolete.
	 * 
	 * Android views should call this method in <code>onPause()</code> unless
	 * there is a specific reason not to do so.
	 */
	public void deinit();

	public T getView();
	
	public void setView(T view);
}
