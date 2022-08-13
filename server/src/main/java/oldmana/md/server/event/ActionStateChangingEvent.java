package oldmana.md.server.event;

import oldmana.md.server.state.ActionState;

public class ActionStateChangingEvent extends CancelableEvent
{
	private ActionState lastState;
	private ActionState newState;
	
	public ActionStateChangingEvent(ActionState lastState, ActionState newState)
	{
		this.lastState = lastState;
		this.newState = newState;
	}
	
	public ActionState getLastState()
	{
		return lastState;
	}
	
	public ActionState getNewState()
	{
		return newState;
	}
}
