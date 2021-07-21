package oldmana.md.server.state;

import java.util.List;
import java.util.Random;

import oldmana.md.net.packet.server.PacketStatus;
import oldmana.md.net.packet.server.actionstate.PacketUpdateActionStateAccepted;
import oldmana.md.net.packet.server.actionstate.PacketUpdateActionStateRefusal;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.event.ActionStateChangedEvent;
import oldmana.md.server.event.ActionStateChangingEvent;
import oldmana.md.server.event.PlayersWonEvent;
import oldmana.md.server.event.TurnEndEvent;
import oldmana.md.server.event.TurnStartEvent;

public class GameState
{
	private MDServer server;
	
	private Player playerTurn;
	private int turns;
	private boolean waitingDraw;
	
	private ActionState state;
	
	private String status = "";
	
	private boolean winningEnabled = true;
	
	private int deferredWinTurns;
	private Player deferredWinPlayer;
	
	public GameState(MDServer server)
	{
		this.server = server;
		
		waitingDraw = true;
	}
	
	public Player getActivePlayer()
	{
		return playerTurn;
	}
	
	public void undoCard(Card card)
	{
		if (state != null)
		{
			state.onCardUndo(card);
		}
		turns++;
		nextNaturalActionState();
	}
	
	public void startGame()
	{
		playerTurn = server.getPlayers().get(new Random().nextInt(server.getPlayers().size()));
	}
	
	public void endGame()
	{
		if (playerTurn != null)
		{
			playerTurn.clearRevocableCards();
			playerTurn = null;
		}
		for (Player player : server.getPlayers())
		{
			player.clearStatusEffects();
		}
		setActionState(new ActionStateDoNothing());
	}
	
	public boolean isGameStarted()
	{
		return playerTurn != null;
	}
	
	public void nextTurn()
	{
		int numPlayers = server.getPlayers().size();
		if (deferredWinPlayer == playerTurn)
		{
			if (deferredWinTurns == 1)
			{
				server.broadcastMessage("Winning is now possible.");
			}
			deferredWinTurns -= 1;
			if (deferredWinTurns < 1)
			{
				deferredWinPlayer = null;
			}
			else
			{
				server.broadcastMessage("Winning is deferred for " + deferredWinTurns + " turn" + (deferredWinTurns != 1 ? "s" : "") 
						+ ". (After " + deferredWinPlayer.getName() + "'s turn)");
			}
		}
		if (playerTurn != null)
		{
			playerTurn.clearRevocableCards();
			
			server.getEventManager().callEvent(new TurnEndEvent(playerTurn));
		}
		if (playerTurn == null)
		{
			playerTurn = server.getPlayers().get(new Random().nextInt(server.getPlayers().size()));
		}
		else
		{
			playerTurn = server.getPlayers().get((server.getPlayers().indexOf(playerTurn) + 1) % numPlayers);
		}
		
		turns = 3;
		waitingDraw = true;
		System.out.println("New Turn: " + playerTurn.getName() + " (ID: " + playerTurn.getID() + ")");
		if (deferredWinTurns > 0)
		{
			System.out.println("Win deferred: " + deferredWinTurns + " Turns (After " + deferredWinPlayer.getName() + "'s Turn)");
		}
		nextNaturalActionState();
		server.getEventManager().callEvent(new TurnStartEvent(playerTurn));
	}
	
	public void setTurn(Player player, boolean draw)
	{
		playerTurn = player;
		waitingDraw = draw;
	}
	
	public void setTurns(int turns)
	{
		this.turns = turns;
	}
	
	public int getTurnsRemaining()
	{
		return turns;
	}
	
	public void decrementTurn()
	{
		turns--;
		nextNaturalActionState();
	}
	
	public void decrementTurns(int amount)
	{
		turns = Math.max(turns - amount, 0);
		nextNaturalActionState();
	}
	
	public void markDrawn()
	{
		waitingDraw = false;
	}
	
	public ActionState getActionState()
	{
		return state;
	}
	
	public void setActionState(ActionState state)
	{
		if (state instanceof ActionStateDoNothing)
		{
			this.state = state;
			server.broadcastPacket(this.state.constructPacket());
			return;
		}
		
		if (!checkWin())
		{
			ActionStateChangingEvent changingEvent = new ActionStateChangingEvent(this.state, state);
			server.getEventManager().callEvent(changingEvent);
			if (!state.isFinished())
			{
				if (!changingEvent.isCanceled())
				{
					ActionState lastState = this.state;
					this.state = state;
					server.broadcastPacket(this.state.constructPacket());
					for (Player player : state.getAccepted())
					{
						server.broadcastPacket(new PacketUpdateActionStateAccepted(player.getID(), true));
					}
					ActionStateChangedEvent changedEvent = new ActionStateChangedEvent(lastState, state);
					server.getEventManager().callEvent(changedEvent);
				}
			}
		}
		else
		{
			nextNaturalActionState();
		}
	}
	
	public boolean checkWin()
	{
		List<Player> winners = server.findWinners();
		if (!winners.isEmpty() && deferredWinTurns < 1 && isWinningEnabled())
		{
			PlayersWonEvent event = new PlayersWonEvent(winners);
			server.getEventManager().callEvent(event);
			deferWinBy(event.getWinDeferredTurns());
			if (event.isCanceled())
			{
				return false;
			}
		}
		
		if (!winners.isEmpty() && deferredWinTurns < 1 && isWinningEnabled())
		{
			endGame();
			if (winners.size() > 1)
			{
				String statusText = null;
				for (Player player : winners)
				{
					if (statusText == null)
					{
						statusText = "Tie between " + player.getName();
					}
					else
					{
						statusText += ", " + player.getName();
					}
				}
				statusText += "!";
				setStatus(statusText);
			}
			else
			{
				setStatus(winners.get(0).getName() + " has won!");
			}
			return true;
		}
		return false;
	}
	
	public void resendActionState(Player player)
	{
		if (state != null)
		{
			player.sendPacket(state.constructPacket());
			
			for (Player accepted : state.getAccepted())
			{
				player.sendPacket(new PacketUpdateActionStateAccepted(accepted.getID(), true));
			}
			for (Player refused : state.getRefused())
			{
				player.sendPacket(new PacketUpdateActionStateRefusal(refused.getID(), true));
			}
		}
	}
	
	public void resendActionState()
	{
		if (state != null)
		{
			server.broadcastPacket(state.constructPacket());
			
			for (Player accepted : state.getAccepted())
			{
				server.broadcastPacket(new PacketUpdateActionStateAccepted(accepted.getID(), true));
			}
			for (Player refused : state.getRefused())
			{
				server.broadcastPacket(new PacketUpdateActionStateRefusal(refused.getID(), true));
			}
		}
	}
	
	public void nextNaturalActionState()
	{
		Player player = getActivePlayer();
		if (player != null)
		{
			if (waitingDraw)
			{
				setActionState(new ActionStateDraw(player));
			}
			else if (turns > 0)
			{
				setActionState(new ActionStatePlay(player));
			}
			else
			{
				if (player.getHand().getCardCount() > 7)
				{
					setActionState(new ActionStateDiscard(player));
				}
				else
				{
					setActionState(new ActionStateFinishTurn(player));
				}
			}
		}
	}
	
	public void setStatus(String status)
	{
		this.status = status == null ? "" : status;
		server.broadcastPacket(new PacketStatus(this.status));
	}
	
	public void broadcastStatus()
	{
		server.broadcastPacket(new PacketStatus(status));
	}
	
	public void sendStatus(Player player)
	{
		player.sendPacket(new PacketStatus(status));
	}
	
	public void setWinningEnabled(boolean winningEnabled)
	{
		this.winningEnabled = winningEnabled;
	}
	
	public boolean isWinningEnabled()
	{
		return winningEnabled;
	}
	
	public void deferWinBy(int turns)
	{
		deferredWinTurns = turns;
		deferredWinPlayer = playerTurn;
	}
}
