package fi.spanasenko.android.instagram;

public abstract class VoidOperationCallback extends OperationCallbackBase
{
	private DispatchType dispatchType;
	private Exception error;

	public VoidOperationCallback()
	{
		dispatchType = DispatchType.CurrentThread;
	}

	public VoidOperationCallback(DispatchType dispatchType)
	{
		this.dispatchType = dispatchType;
	}

	protected abstract void onCompleted();

	protected abstract void onError(Exception error);

	public void notifyCompleted()
	{
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
			onCompleted();
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
			onCompleted();
		}
	}
}
