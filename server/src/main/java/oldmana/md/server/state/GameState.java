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
	
	private Player activePlayer;
	private int turns;
	private boolean waitingDraw;
	
	private ActionState state;
	
	private String status = "";
	
	private boolean winningEnabled = true;
	
	private int deferredWinTurns;
	private Player deferredWinPlayer;
	
	private boolean stateChanged;
	
	public GameState(MDServer server)
	{
		this.server = server;
		
		waitingDraw = true;
	}
	
	public Player getActivePlayer()
	{
		return activePlayer;
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
		activePlayer = server.getPlayers().get(new Random().nextInt(server.getPlayers().size()));
	}
	
	public void endGame()
	{
		if (activePlayer != null)
		{
			activePlayer.clearRevocableCards();
			activePlayer = null;
		}
		for (Player player : server.getPlayers())
		{
			player.clearStatusEffects();
		}
		setStatus("");
		setActionState(new ActionStateDoNothing());
	}
	
	public boolean isGameStarted()
	{
		return activePlayer != null;
	}
	
	public void nextTurn()
	{
		List<Player> players = server.getPlayers();
		if (activePlayer != null && deferredWinPlayer == activePlayer)
		{
			deferredWinTurns -= 1;
			if (deferredWinTurns < 1)
			{
				deferredWinPlayer = null;
				server.broadcastMessage("Winning is now possible.", true);
			}
			else
			{
				server.broadcastMessage("Winning is deferred for " + deferredWinTurns + " turn" + (deferredWinTurns != 1 ? "s" : "") 
						+ ". (After " + deferredWinPlayer.getName() + "'s turn)", true);
			}
		}
		if (activePlayer != null)
		{
			activePlayer.clearRevocableCards();
			server.getEventManager().callEvent(new TurnEndEvent(activePlayer));
			activePlayer = players.get((players.indexOf(activePlayer) + 1) % players.size());
		}
		else
		{
			activePlayer = players.get(new Random().nextInt(players.size()));
		}
		
		turns = 3;
		waitingDraw = true;
		System.out.println("New Turn: " + activePlayer.getName() + " (ID: " + activePlayer.getID() + ")");
		nextNaturalActionState();
		server.getEventManager().callEvent(new TurnStartEvent(activePlayer));
	}
	
	public void setTurn(Player player, boolean draw)
	{
		activePlayer = player;
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
		setStateChanged();
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
			else
			{
				nextNaturalActionState();
			}
		}
		else
		{
			nextNaturalActionState();
		}
	}
	
	public boolean checkWin()
	{
		List<Player> winners = server.getGameRules().getWinCondition().getWinners();
		if (!winners.isEmpty() && deferredWinTurns < 1 && isWinningEnabled())
		{
			PlayersWonEvent event = new PlayersWonEvent(winners);
			server.getEventManager().callEvent(event);
			if (event.getWinDeferredTurns() > 0)
			{
				deferWinBy(event.getWinDeferredTurns());
			}
			if (event.isCanceled())
			{
				return false;
			}
			
			if (deferredWinTurns < 1 && isWinningEnabled())
			{
				endGame();
				if (winners.size() > 1)
				{
					String statusText = "Tie between " + winners.get(0).getName();
					for (int i = 1 ; i < winners.size() ; i++)
					{
						statusText += ", " + winners.get(i).getName();
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
	
	/**
	 * Indicates if something important has changed during this game tick. Currently, this only causes a reevaluation
	 * of card buttons at the end of a tick.
	 */
	public boolean hasStateChanged()
	{
		return stateChanged;
	}
	
	/**
	 * Mark that something important in the game has changed this tick.
	 */
	public void setStateChanged()
	{
		stateChanged = true;
	}
	
	public void tick()
	{
		if (stateChanged)
		{
			for (Player player : server.getPlayers())
			{
				player.getHand().updateCardButtons();
			}
			stateChanged = false;
		}
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
		deferredWinPlayer = activePlayer;
	}
}
