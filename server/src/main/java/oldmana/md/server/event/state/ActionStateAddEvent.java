package oldmana.md.server.event.state;

import oldmana.md.server.event.CancellableEvent;
import oldmana.md.server.state.ActionState;

/**
 * Called when an action state is being added to the queue.
 */
public class ActionStateAddEvent extends CancellableEvent
{
	private ActionState state;
	private boolean lowPriority;
	
	public ActionStateAddEvent(ActionState state, boolean lowPriority)
	{
		this.state = state;
		this.lowPriority = lowPriority;
	}
	
	public ActionState getState()
	{
		return state;
	}
	
	public boolean isLowPriority()
	{
		return lowPriority;
	}
}
