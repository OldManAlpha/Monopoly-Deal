package oldmana.md.server.event.player;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.event.CancellableEvent;
import oldmana.md.server.history.UndoableAction;

public class PlayerUndoActionEvent extends CancellableEvent
{
	private Player player;
	private UndoableAction action;
	
	public PlayerUndoActionEvent(Player player, UndoableAction action)
	{
		this.player = player;
		this.action = action;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public UndoableAction getAction()
	{
		return action;
	}
}
