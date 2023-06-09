package oldmana.md.server.state.primary;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.actionstate.PacketActionStatePlayerTurn;
import oldmana.md.net.packet.server.actionstate.PacketActionStatePlayerTurn.TurnState;
import oldmana.md.server.Player;
import oldmana.md.server.state.ActionState;

public class ActionStatePlayerTurn extends ActionState
{
	private TurnState state = TurnState.DRAW;
	private int moves;
	
	private int lastSentMoves = -1;
	
	public ActionStatePlayerTurn(Player player)
	{
		super(player);
		setStatus(player.getName() + "'s Turn: Draw");
		moves = getServer().getGameRules().getMovesPerTurn();
	}
	
	public TurnState getTurnState()
	{
		return state;
	}
	
	public void updateState()
	{
		updateState(false);
	}
	
	public void updateState(boolean mustSendState)
	{
		if (state == TurnState.DRAW)
		{
			return;
		}
		TurnState prevState = state;
		if (moves == 0 && getActionOwner().getHand().hasTooManyCards())
		{
			state = TurnState.DISCARD;
			setStatus("Waiting for " + getActionOwner().getName() + " to discard");
		}
		else
		{
			state = TurnState.PLAY;
			if (moves > 0)
			{
				setStatus(getActionOwner().getName() + "'s Turn");
			}
			else
			{
				setStatus("Waiting for " + getActionOwner().getName() + " to finish their turn");
			}
		}
		if (mustSendState || state != prevState || moves != lastSentMoves)
		{
			lastSentMoves = moves;
			getServer().getGameState().setStateChanged();
			getServer().broadcastPacket(constructPacket());
		}
	}
	
	public boolean canPlayCards()
	{
		return state == TurnState.PLAY && moves > 0;
	}
	
	public boolean isDrawing()
	{
		return state == TurnState.DRAW;
	}
	
	public void setDrawn()
	{
		if (state == TurnState.DRAW)
		{
			state = TurnState.PLAY;
			updateState(); // Make sure we shouldn't discard
		}
	}
	
	public int getMoves()
	{
		return moves;
	}
	
	public void setMoves(int moves)
	{
		this.moves = moves;
		updateState();
	}
	
	public void incrementMoves()
	{
		incrementMoves(1);
	}
	
	public void incrementMoves(int inc)
	{
		if (inc == 0)
		{
			return;
		}
		moves += inc;
		updateState();
	}
	
	public void decrementMoves()
	{
		decrementMoves(1);
	}
	
	public void decrementMoves(int dec)
	{
		if (dec == 0)
		{
			return;
		}
		moves = Math.max(moves - dec, 0);
		updateState();
	}
	
	@Override
	public boolean isFinished()
	{
		return false;
	}
	
	@Override
	public Packet constructPacket()
	{
		return new PacketActionStatePlayerTurn(getActionOwner().getID(), state, moves);
	}
}
