package fi.spanasenko.android.instagram;

public abstract class OperationCallback<T> extends OperationCallbackBase
{
	private DispatchType dispatchType;
	private T result;
	private Exception error;

	public OperationCallback()
	{
		dispatchType = DispatchType.CurrentThread;
	}

	public OperationCallback(DispatchType dispatchType)
	{
		this.dispatchType = dispatchType;
	}

	protected abstract void onCompleted(T result);

	protected abstract void onError(Exception error);

	public void notifyCompleted(T result)
	{
		this.result = result;

		if (dispatchType == DispatchType.MainThread)
		{
			DispatchToMainThread();
		}
		else if (dispatchType == DispatchType.NewThread)
		{
			DispatchToNewThread();
		}
		else
		{
			onCompleted(result);
		}
	}

	public void notifyError(Exception error)
	{
		this.error = error;

		if (dispatchType == DispatchType.MainThread)
		{
			DispatchToMainThread();
		}
		else if (dispatchType == DispatchType.NewThread)
		{
			DispatchToNewThread();
		}
		else
		{
			onError(error);
		}
	}

	public void run()
	{
		if (this.error != null)
		{
			onError(this.error);
		}
		else
		{
			onCompleted(this.result);
		}
	}
}
