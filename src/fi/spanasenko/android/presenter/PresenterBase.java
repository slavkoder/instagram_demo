package fi.spanasenko.android.presenter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import fi.spanasenko.android.view.IBaseView;

import java.lang.ref.WeakReference;

/**
 * PresenterBase
 * Base class for presenters. Holds implementation of the common methods used in other presenters.
 */
public abstract class PresenterBase<T extends IBaseView> implements IBasePresenter<T> {
    private WeakReference<T> _weakView;
    private Context _context;

    public PresenterBase(T view, Context context) {
        _weakView = new WeakReference<T>(view);
        _context = context;
    }

    public T getView() {
        return _weakView.get();
    }

    public Context getContext() {
        return _context;
    }


    public void setView(T view) {
        _weakView.clear();

        _weakView = new WeakReference<T>(view);
    }

    protected void openActivity(Class<? extends Activity> clazz) {
        openActivity(clazz, 0);
    }

    protected void openActivity(Class<? extends Activity> clazz, int flags) {
        // Activate an intent from a Class while still being decoupled from the UI context. Much safer.
        Package p = clazz.getPackage();
        String name = clazz.getName();

        ComponentName cn = new ComponentName(p.getName(), name);

        Intent i = new Intent();
        i.setComponent(cn);
        i.setFlags(flags);

        openActivity(i);
    }

    protected void openActivity(Intent intent) {
        getView().startActivity(intent);
    }

}
