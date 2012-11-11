package fi.spanasenko.android.instagram;

import android.os.Handler;
import android.os.Looper;

/**
 * OperationCallbackBase
 * Abstract class used as a super class for operation callbacks.
 */
public abstract class OperationCallbackBase implements Runnable {

	public enum DispatchType { CurrentThread, MainThread, NewThread }

    private static Handler mainHandler;
	
	protected void DispatchToNewThread()
	{
		Thread thread = new Thread(this);
		thread.start();
	}
	
	protected void DispatchToMainThread()
	{
        if (mainHandler == null)
        {
            mainHandler = new Handler(Looper.getMainLooper());
        }

        mainHandler.post(this);
	}
}
