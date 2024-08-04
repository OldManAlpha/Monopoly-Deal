package oldmana.md.server.event.state;

import oldmana.md.common.state.TargetState;
import oldmana.md.server.Player;
import oldmana.md.server.event.CancellableEvent;
import oldmana.md.server.state.ActionState;

/**
 * Called when a player's targeted state changes in an action state.
 */
public class ActionStateTargetStateChangeEvent extends CancellableEvent
{
	private ActionState state;
	private Player changedPlayer;
	private TargetState newState;
	
	public ActionStateTargetStateChangeEvent(ActionState state, Player changedPlayer, TargetState newState)
	{
		this.state = state;
		this.changedPlayer = changedPlayer;
		this.newState = newState;
	}
	
	public ActionState getState()
	{
		return state;
	}
	
	public Player getChangedPlayer()
	{
		return changedPlayer;
	}
	
	public TargetState getNewState()
	{
		return newState;
	}
	
	public TargetState getOldState()
	{
		return state.getTargetState(changedPlayer);
	}
}
