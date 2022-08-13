package oldmana.md.server.event;

public class CancelableEvent extends Event
{
	private boolean canceled;
	
	public void setCanceled(boolean canceled)
	{
		this.canceled = canceled;
	}
	
	public boolean isCanceled()
	{
		return canceled;
	}
}
