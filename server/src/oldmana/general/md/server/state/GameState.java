package oldmana.general.md.server.state;

import java.util.List;
import java.util.Random;

import oldmana.general.md.net.packet.server.PacketStatus;
import oldmana.general.md.net.packet.server.actionstate.PacketUpdateActionStateAccepted;
import oldmana.general.md.server.MDServer;
import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.Card;
import oldmana.general.md.server.card.action.CardActionCorruptedGame;
import oldmana.general.md.server.card.action.CardActionPoliticalFavor;

public class GameState
{
	private MDServer server;
	
	private Player playerTurn;
	private int turns;
	private boolean waitingDraw;
	
	private ActionState state;
	
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
		playerTurn = null;
		setCurrentActionState(new ActionStateDoNothing());
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
			deferredWinTurns -= 1;
			if (deferredWinTurns < 1)
			{
				deferredWinPlayer = null;
			}
		}
		if (playerTurn != null)
		{
			playerTurn.clearRevokableCards();
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
		if (deferredWinTurns > 0)
		{
			System.out.println("Win deferred: " + deferredWinTurns + " (" + playerTurn.getName() + ")");
		}
		System.out.println("ACTIVE PLAYER: " + playerTurn.getID() + " (" + playerTurn.getName() + ")");
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
	
	public void markDrawn()
	{
		waitingDraw = false;
	}
	
	public ActionState getCurrentActionState()
	{
		return state;
	}
	
	public void setCurrentActionState(ActionState state)
	{
		if (!checkWin() && !state.isFinished())
		{
			this.state = state;
			server.broadcastPacket(this.state.constructPacket());
			for (Player player : state.getAccepted())
			{
				server.broadcastPacket(new PacketUpdateActionStateAccepted(player.getID(), true));
			}
			// Political favor
			for (ActionTarget target : state.getActionTargets())
			{
				Player player = target.getTarget();
				for (Card card : player.getBank().getCards(true))
				{
					if (card instanceof CardActionPoliticalFavor)
					{
						state.setRefused(player, true);
						card.transfer(state.getActionOwner().getBank());
						break;
					}
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
		if (!winners.isEmpty() && deferredWinTurns < 1)
		{
			// Corrupted Game Check
			for (Player player : server.getPlayersExcluding(winners))
			{
				for (Card card : player.getHand().getCards(true))
				{
					if (card instanceof CardActionCorruptedGame)
					{
						card.transfer(server.getDiscardPile());
						if (player.getHand().getCardCount() == 0)
						{
							server.getDeck().drawCards(player, 5, 1.2);
						}
						deferWinBy(2);
					}
				}
			}
		}
		if (!winners.isEmpty() && deferredWinTurns < 1)
		{
			playerTurn = null;
			this.state = new ActionStateDoNothing();
			server.broadcastPacket(this.state.constructPacket());
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
				server.broadcastPacket(new PacketStatus(statusText));
			}
			else
			{
				server.broadcastPacket(new PacketStatus(winners.get(0).getName() + " has won!"));
			}
			return true;
		}
		return false;
	}
	
	public void resendActionState(Player player)
	{
		player.sendPacket(state.constructPacket());
	}
	
	public void resendActionState()
	{
		server.broadcastPacket(state.constructPacket());
	}
	
	public void nextNaturalActionState()
	{
		Player player = getActivePlayer();
		if (player != null)
		{
			if (waitingDraw)
			{
				setCurrentActionState(new ActionStateDraw(player));
			}
			else if (turns > 0)
			{
				setCurrentActionState(new ActionStatePlay(player));
			}
			else
			{
				if (player.getHand().getCardCount() > 7)
				{
					setCurrentActionState(new ActionStateDiscard(player));
				}
				else
				{
					setCurrentActionState(new ActionStateFinishTurn(player));
				}
			}
		}
	}
	
	public void deferWinBy(int turns)
	{
		deferredWinTurns = turns;
		deferredWinPlayer = playerTurn;
	}
}
