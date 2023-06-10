package oldmana.md.client.state.primary;

import oldmana.md.client.Player;
import oldmana.md.client.state.ActionState;
import oldmana.md.client.state.ActionStateDiscard;
import oldmana.md.client.state.ActionStateDraw;
import oldmana.md.client.state.ActionStateFinishTurn;
import oldmana.md.client.state.ActionStatePlay;
import oldmana.md.common.net.packet.server.actionstate.PacketActionStatePlayerTurn.TurnState;

public class ActionStatePlayerTurn extends ActionState
{
	private TurnState turnState;
	private int moves;
	
	public ActionStatePlayerTurn(Player player, TurnState turnState, int moves)
	{
		super(player);
		this.turnState = turnState;
		this.moves = moves;
	}
	
	public TurnState getTurnState()
	{
		return turnState;
	}
	
	public int getMoves()
	{
		return moves;
	}
	
	public ActionState createClientState()
	{
		switch (turnState)
		{
			case DRAW: return new ActionStateDraw(getActionOwner());
			case PLAY:
			{
				if (moves > 0)
				{
					return new ActionStatePlay(getActionOwner(), moves);
				}
				return new ActionStateFinishTurn(getActionOwner());
			}
			case DISCARD: return new ActionStateDiscard(getActionOwner());
			default: return null;
		}
	}
}
