package oldmana.md.server.state;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import oldmana.md.net.packet.server.PacketStatus;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.net.packet.server.actionstate.PacketActionStatePlayerTurn;
import oldmana.md.net.packet.server.actionstate.PacketActionStatePlayerTurn.TurnState;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.collection.Deck;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.event.ActionStateChangedEvent;
import oldmana.md.server.event.GameEndEvent;
import oldmana.md.server.event.GameStartEvent;
import oldmana.md.server.event.PlayersWonEvent;
import oldmana.md.server.event.TurnEndEvent;
import oldmana.md.server.event.TurnStartEvent;
import oldmana.md.server.state.primary.ActionStatePlayerTurn;

public class GameState
{
	private MDServer server;
	
	private ActionStatePlayerTurn turnState;
	private List<ActionState> states = new LinkedList<ActionState>();
	private ActionState lastSentState;
	
	private TurnOrder turnOrder;
	
	private boolean gameRunning;
	private boolean clean;
	
	private boolean winningEnabled = true;
	
	private int deferredWinCycles;
	private Player deferredWinPlayer;
	
	private boolean stateChanged;
	
	public GameState(MDServer server)
	{
		this.server = server;
		
		turnOrder = new TurnOrder();
	}
	
	public TurnOrder getTurnOrder()
	{
		return turnOrder;
	}
	
	public Player getActivePlayer()
	{
		return turnOrder.getActivePlayer();
	}
	
	public void undoCard(Card card)
	{
		if (getActionState() != null)
		{
			getActionState().onCardUndo(card);
		}
		getTurnState().incrementMoves();
	}
	
	public boolean isGameRunning()
	{
		return gameRunning;
	}
	
	public boolean isClean()
	{
		return clean;
	}
	
	public void startGame()
	{
		startGame(false);
	}
	
	public void startGame(boolean ignoreCleanup)
	{
		if (!isClean() && !ignoreCleanup)
		{
			cleanup();
		}
		gameRunning = true;
		clean = false;
		server.getEventManager().callEvent(new GameStartEvent());
		for (int i = 0 ; i < server.getGameRules().getCardsDealt() ; i++)
		{
			for (Player player : server.getPlayers())
			{
				server.getDeck().drawCard(player, 0.6);
			}
		}
		nextTurn();
	}
	
	public void endGame()
	{
		endGame("");
	}
	
	public void endGame(String status)
	{
		gameRunning = false;
		server.getEventManager().callEvent(new GameEndEvent());
		if (getActivePlayer() != null)
		{
			getActivePlayer().clearRevocableCards();
			//activePlayer = null;
		}
		for (Player player : server.getPlayers())
		{
			player.clearStatusEffects();
		}
		states.clear();
		removeTurnState();
		addActionState(new ActionStateDoNothing(status));
		System.out.println("Game Ended: " + status);
	}
	
	/**
	 * Puts all cards in the playing field back to their original place.
	 */
	public void cleanup()
	{
		Deck deck = server.getDeck();
		for (Player player : server.getPlayers())
		{
			for (Card card : player.getBank().getCardsInReverse())
			{
				putCardAway(card);
			}
			List<PropertySet> sets = player.getPropertySets(true);
			Collections.reverse(sets);
			for (PropertySet set : sets)
			{
				for (Card card : set.getCardsInReverse())
				{
					putCardAway(card);
				}
			}
			for (Card card : player.getHand().getCardsInReverse())
			{
				putCardAway(card);
			}
		}
		for (Card card : server.getDiscardPile().getCardsInReverse())
		{
			putCardAway(card);
		}
		
		for (Card card : deck.getCards(true))
		{
			if (!deck.getDeckStack().hasCard(card))
			{
				putCardAway(card);
			}
		}
		
		for (Card card : deck.getDeckStack().getCards())
		{
			if (!deck.hasCard(card))
			{
				putCardAway(card);
			}
		}
		
		deck.shuffle();
		
		clean = true;
	}
	
	private void putCardAway(Card card)
	{
		Deck deck = server.getDeck();
		if (deck.getDeckStack().hasCard(card))
		{
			card.transfer(deck, -1, 0.25);
		}
		else
		{
			card.transfer(server.getVoidCollection(), -1, 0.25);
		}
	}
	
	public void nextTurn()
	{
		if (deferredWinPlayer != null && deferredWinPlayer == getActivePlayer())
		{
			deferredWinCycles--;
			if (deferredWinCycles < 1)
			{
				deferredWinPlayer = null;
				server.broadcastMessage("Winning is now possible.", true);
			}
			else
			{
				server.broadcastMessage("Winning is deferred for " + deferredWinCycles + " turn cycle" + (deferredWinCycles != 1 ? "s" : "")
						+ ". (After " + deferredWinPlayer.getName() + "'s turn)", true);
			}
		}
		
		Player player = getActivePlayer();
		if (player != null)
		{
			player.clearRevocableCards();
			server.getEventManager().callEvent(new TurnEndEvent(player));
		}
		
		if (checkWin())
		{
			return;
		}
		
		states.clear();
		turnState = turnOrder.nextTurn();
		player = getActivePlayer();
		
		System.out.println("New Turn: " + player.getName() + " (ID: " + player.getID() + ")");
		checkCurrentState();
		server.getEventManager().callEvent(new TurnStartEvent(player));
	}
	
	public void setTurn(Player player, boolean draw)
	{
		states.clear();
		turnState = turnOrder.setTurn(player);
		turnState.sendState();
		broadcastStatus();
		setStateChanged();
	}
	
	public void setMoves(int moves)
	{
		getTurnState().setMoves(moves);
	}
	
	public int getMovesRemaining()
	{
		return getTurnState().getMoves();
	}
	
	public void decrementMoves()
	{
		getTurnState().decrementMoves();
	}
	
	public void decrementMoves(int amount)
	{
		getTurnState().decrementMoves(amount);
	}
	
	public void setDrawn()
	{
		getTurnState().setDrawn();
	}
	
	/**
	 * Get the focused action state, or the turn state if there's no other states in queue.
	 * @return The action state currently being processed
	 */
	public ActionState getActionState()
	{
		return states.isEmpty() ? turnState : states.get(0);
	}
	
	public ActionStatePlayerTurn getTurnState()
	{
		return turnState;
	}
	
	public boolean hasTurnState()
	{
		return turnState != null;
	}
	
	/**
	 * Removing the turn state causes the game to halt, effectively ending the game.
	 */
	private void removeTurnState()
	{
		turnState = null;
		server.broadcastPacket(new PacketActionStatePlayerTurn(-1, TurnState.REMOVE_STATE, 0));
	}
	
	/**
	 * Add an action state to the top of the queue of execution.
	 * @param state The state to append
	 */
	public void addActionState(ActionState state)
	{
		if (!(state instanceof ActionStateDoNothing) && checkWin())
		{
			return;
		}
		if (states.contains(state) || state.isFinished())
		{
			return;
		}
		states.removeIf(s -> !s.isImportant());
		states.add(0, state);
		checkCurrentState();
	}
	
	/**
	 * Add an action state to the top of the queue, changing the currently focused action state.
	 * @param state The state to immediately run
	 */
	public void addLowPriorityActionState(ActionState state)
	{
		if (checkWin())
		{
			return;
		}
		if (states.contains(state) || state.isFinished())
		{
			return;
		}
		states.removeIf(s -> !s.isImportant());
		states.add(state);
		checkCurrentState();
	}
	
	/**
	 * Promotes an already existing action state to the top of the execution queue.
	 * @param state The state to promote
	 */
	public void promoteActionState(ActionState state)
	{
		if (checkWin())
		{
			return;
		}
		states.remove(state);
		addActionState(state);
		checkCurrentState();
	}
	
	/**
	 * Replace the old action state with the new action state provided, maintaining the same action state priority.
	 * @param oldState The action state to take out
	 * @param newState The action state to put in place of the old one
	 */
	public void swapActionState(ActionState oldState, ActionState newState)
	{
		if (checkWin())
		{
			return;
		}
		int index = states.indexOf(oldState);
		if (index < 0)
		{
			throw new IllegalArgumentException("Old state does not exist!");
		}
		states.remove(index);
		if (index == 0)
		{
			addActionState(newState);
		}
		else
		{
			states.add(index, newState);
		}
		checkCurrentState();
	}
	
	public void removeActionState(ActionState state)
	{
		if (checkWin())
		{
			return;
		}
		states.remove(state);
		checkCurrentState();
	}
	
	private void checkCurrentState()
	{
		ActionState state = getActionState();
		if (state != lastSentState)
		{
			if (state != null)
			{
				state.sendState();
			}
			
			if (states.isEmpty())
			{
				server.broadcastPacket(new PacketActionStateBasic(-1, BasicActionState.NO_STATE, 0));
			}
			broadcastStatus();
			ActionState lastState = lastSentState;
			lastSentState = state;
			setStateChanged();
			server.getEventManager().callEvent(new ActionStateChangedEvent(lastState != null ? lastState : getTurnState(),
					state != null ? state : getTurnState()));
			for (Player player : server.getPlayers())
			{
				player.checkEmptyHand();
			}
		}
	}
	
	public boolean checkWin()
	{
		List<Player> winners = server.getGameRules().getWinCondition().getWinners();
		if (!winners.isEmpty() && deferredWinCycles < 1 && isWinningEnabled())
		{
			PlayersWonEvent event = new PlayersWonEvent(winners);
			server.getEventManager().callEvent(event);
			if (event.getWinDeferredTurns() > 0)
			{
				deferWinBy(event.getWinDeferredTurns());
			}
			if (event.isCancelled())
			{
				return false;
			}
			winners = event.getWinners();
			
			if (!winners.isEmpty() && deferredWinCycles < 1 && isWinningEnabled())
			{
				String statusText;
				if (winners.size() > 1)
				{
					statusText = "Tie between " + winners.get(0).getName();
					for (int i = 1 ; i < winners.size() ; i++)
					{
						statusText += ", " + winners.get(i).getName();
					}
					statusText += "!";
				}
				else
				{
					statusText = winners.get(0).getName() + " has won!";
				}
				endGame(statusText);
				return true;
			}
		}
		return false;
	}
	
	public void resendActionState(Player player)
	{
		ActionState state = getActionState();
		state.sendState(player);
		sendStatus(player);
	}
	
	public void resendActionState()
	{
		ActionState state = getActionState();
		state.sendState();
		server.broadcastPacket(new PacketStatus(state.getStatus()));
	}
	
	public void resendTurnState(Player player)
	{
		if (turnState != null)
		{
			player.sendPacket(turnState.constructPacket());
		}
	}
	
	/**
	 * Removes all finished action states from the queue and sends the new current state to clients, if changed.
	 */
	public void proceed()
	{
		states.removeIf(state -> state.isFinished());
		checkCurrentState();
	}
	
	/**
	 * Updates the turn state and resends it to clients.
	 */
	public void updateTurnState()
	{
		turnState.updateState(true);
	}
	
	public String getStatus()
	{
		return getActionState().getStatus();
	}
	
	public void broadcastStatus()
	{
		server.broadcastPacket(new PacketStatus(getStatus()));
	}
	
	public void sendStatus(Player player)
	{
		player.sendPacket(new PacketStatus(getStatus()));
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
		if (turnState == null && states.isEmpty())
		{
			nextTurn();
		}
		if (stateChanged)
		{
			if (turnState != null)
			{
				turnState.updateState();
			}
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
		deferredWinCycles = turns;
		deferredWinPlayer = getActivePlayer();
	}
}
