package oldmana.md.server.ai;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardMoney;
import oldmana.md.server.state.ActionState;
import oldmana.md.server.state.ActionStateDoNothing;
import oldmana.md.server.state.ActionStateDraw;
import oldmana.md.server.state.ActionStateFinishTurn;
import oldmana.md.server.state.ActionStatePlay;
import oldmana.md.server.state.ActionStateRent;

public class BasicAI extends PlayerAI
{
	private static Map<Class<? extends ActionState>, BasicAIStateResponder> defaultHandlers = new HashMap<Class<? extends ActionState>, BasicAIStateResponder>();
	
	private Map<Class<? extends ActionState>, BasicAIStateResponder> handlerOverrides = new HashMap<Class<? extends ActionState>, BasicAIStateResponder>();
	
	public BasicAI(Player player)
	{
		super(player);
	}
	
	public static void registerDefaultStateHandlers()
	{
		registerStateHandler(ActionStateDoNothing.class, (player, state) ->
		{
			
		});
		
		registerStateHandler(ActionStateDraw.class, (player, state) ->
		{
			if (state.getActionOwner() == player)
			{
				player.draw();
			}
		});
		
		registerStateHandler(ActionStatePlay.class, (player, state) ->
		{
			if (state.getActionOwner() == player)
			{
				for (Card card : player.getHand().getCards(true))
				{
					if (card instanceof CardMoney)
					{
						player.playCardBank(card);
						return;
					}
				}
				MDServer.getInstance().getGameState().nextTurn();
			}
		});
		
		registerStateHandler(ActionStateFinishTurn.class, new BasicAIStateResponder()
		{
			@Override
			public void doAction(Player player, ActionState state)
			{
				if (state.getActionOwner() == player)
				{
					MDServer.getInstance().getGameState().nextTurn();
				}
			}
		});
		
		registerStateHandler(ActionStateRent.class, (player, state) ->
		{
			ActionStateRent rent = (ActionStateRent) state;
			if (rent.getActionOwner() == player)
			{
				
			}
		});
	}
	
	public static void registerStateHandler(Class<? extends ActionState> state, BasicAIStateResponder handler)
	{
		defaultHandlers.put(state, handler);
	}
	
	public void overrideStateHandler(Class<? extends ActionState> state, BasicAIStateResponder handler)
	{
		handlerOverrides.put(state, handler);
	}
	
	@Override
	public void doAction()
	{
		ActionState state = getServer().getGameState().getActionState();
		BasicAIStateResponder handler = handlerOverrides.containsKey(state.getClass()) ? handlerOverrides.get(state.getClass()) : 
			defaultHandlers.get(state.getClass());
		if (handler == null)
		{
			System.out.println("Basic AI does not have a handler for " + state.getClass().getSimpleName() + "! Cannot perform any action!");
			return;
		}
		handler.doAction(getPlayer(), state);
	}
	
	@Override
	public double getWinThreat(Player player)
	{
		return 0;
	}
	
	@Override
	public double getRentThreat(Player player)
	{
		return 0;
	}
	
	public static class BasicStateResponder<AS extends ActionState>
	{
		private BasicAI ai;
		
		public BasicStateResponder()
		{
			
		}
		
		public void setAI(BasicAI ai)
		{
			this.ai = ai;
		}
		
		public BasicAI getAI()
		{
			return ai;
		}
		
		public Player getPlayer()
		{
			return ai.getPlayer();
		}
		
		public MDServer getServer()
		{
			return MDServer.getInstance();
		}
		
		public void doAction(AS state) {}
		
		public void doActionOther(AS state) {}
	}
	
	public class StateResponder extends BasicStateResponder<ActionStateRent>
	{
		@Override
		public void doAction(ActionStateRent state)
		{
			
		}
	}
	
	public interface BasicAIStateResponder
	{
		public default Random getRandom()
		{
			return new Random();
		}
		
		public void doAction(Player player, ActionState state);
		
		public default MDServer getServer()
		{
			return MDServer.getInstance();
		}
	}
}
