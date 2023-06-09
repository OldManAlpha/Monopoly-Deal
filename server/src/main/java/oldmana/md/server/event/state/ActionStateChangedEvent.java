package oldmana.md.server.event.state;

import oldmana.md.server.event.Event;
import oldmana.md.server.state.ActionState;

public class ActionStateChangedEvent extends Event
{
	private ActionState lastState;
	private ActionState newState;
	
	public ActionStateChangedEvent(ActionState lastState, ActionState newState)
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
