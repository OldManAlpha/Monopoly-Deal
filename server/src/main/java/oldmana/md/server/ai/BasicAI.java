package oldmana.md.server.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardMoney;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.PropertyColor;
import oldmana.md.server.card.action.CardActionJustSayNo;
import oldmana.md.server.card.action.CardActionRent;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.card.type.CardType;
import oldmana.md.server.state.ActionState;
import oldmana.md.server.state.ActionStateDiscard;
import oldmana.md.server.state.ActionStateDoNothing;
import oldmana.md.server.state.ActionStateDraw;
import oldmana.md.server.state.ActionStateFinishTurn;
import oldmana.md.server.state.ActionStatePlay;
import oldmana.md.server.state.ActionStateRent;
import oldmana.md.server.state.ActionStateStealMonopoly;
import oldmana.md.server.state.ActionStateStealProperty;
import oldmana.md.server.state.ActionStateTargetDebtCollector;
import oldmana.md.server.state.ActionStateTargetPlayerMonopoly;
import oldmana.md.server.state.ActionStateTargetSlyDeal;
import oldmana.md.server.state.ActionStateTradeProperties;

/**
 * A lazily slapped together mess of an AI
 */
public class BasicAI extends PlayerAI
{
	private Map<Class<? extends ActionState>, BasicStateResponder<?>> selfHandlers =
			new HashMap<Class<? extends ActionState>, BasicStateResponder<?>>();
	private Map<Class<? extends ActionState>, BasicStateResponder<?>> otherHandlers =
			new HashMap<Class<? extends ActionState>, BasicStateResponder<?>>();
	
	private Map<CardType<?>, PlayDesire<?>> typeDesires = new HashMap<CardType<?>, PlayDesire<?>>();
	private PlayDesire<Card> bankDesire;
	
	
	public BasicAI(Player player)
	{
		super(player);
		registerDefaultStateHandlers();
	}
	
	private <T extends Card> T getRandomCard(List<T> cards)
	{
		return cards.get(getRandom().nextInt(cards.size()));
	}
	
	private boolean hasCard(List<Card> cards, CardType<?> type)
	{
		for (Card card : cards)
		{
			if (card.getType() == type)
			{
				return true;
			}
		}
		return false;
	}
	
	private <T extends Card> T getCard(List<Card> cards, CardType<T> type)
	{
		for (Card card : cards)
		{
			if (card.getType() == type)
			{
				return (T) card;
			}
		}
		return null;
	}
	
	private double getBankSecurity()
	{
		return getPlayer().getBank().getTotalValue() * (getPlayer().getBank().getCardCount() * 0.5);
	}
	
	private double getHandCount()
	{
		return getPlayer().getHand().getCardCount();
	}
	
	private boolean shouldPlayRent(int rent, double minimumEffectiveness)
	{
		return getRentEffectiveness(rent) >= minimumEffectiveness;
	}
	
	private double getRentPotential(CardActionRent rent)
	{
		int highestPotential = 1;
		for (PropertyColor color : rent.getRentColors())
		{
			int maxRent = color.getRent(color.getMaxProperties());
			if (maxRent > highestPotential)
			{
				highestPotential = maxRent;
			}
		}
		return getPlayer().getHighestValueRent(rent.getRentColors()) / (double) highestPotential;
	}
	
	private double getRentEffectiveness(int rent)
	{
		double total = 0;
		
		for (Player p : getServer().getPlayersExcluding(getPlayer()))
		{
			total += Math.min(p.getTotalMonetaryAssets(), rent);
		}
		return total / ((getServer().getPlayers().size() - 1) * rent);
	}
	
	private double getSingleRentEffectiveness(int rent)
	{
		double highestTotal = 0;
		
		for (Player p : getServer().getPlayersExcluding(getPlayer()))
		{
			highestTotal = Math.max(Math.min(p.getTotalMonetaryAssets(), rent), highestTotal);
		}
		return highestTotal / rent;
	}
	
	public boolean checkWin()
	{
		List<Map<PropertyColor, List<CardProperty>>> combos = getPossibleCombosNew(getPlayer().getAllPropertyCards());
		for (Map<PropertyColor, List<CardProperty>> combo : combos)
		{
			Map<PropertyColor, List<CardProperty>> winningSets = new HashMap<PropertyColor, List<CardProperty>>();
			combo.forEach((color, props) ->
			{
				if (color.getMaxProperties() == props.size())
				{
					winningSets.put(color, props);
				}
			});
			if (winningSets.size() >= 3)
			{
				winningSets.forEach((color, winningSet) ->
				{
					PropertySet set = getPlayer().getSolidPropertySet(color);
					if (set == null)
					{
						set = getPlayer().createPropertySet();
					}
					for (CardProperty prop : winningSet)
					{
						if (!set.hasCard(prop))
						{
							prop.transfer(set);
						}
					}
					set.setEffectiveColor(color);
				});
				getServer().getGameState().checkWin();
				return true;
			}
		}
		return false;
	}
	
	private boolean playJSN(ActionState state)
	{
		Player player = getPlayer();
		if (hasCard(player.getHand().getCards(), CardType.JUST_SAY_NO))
		{
			CardActionJustSayNo jsn = getCard(player.getHand().getCards(), CardType.JUST_SAY_NO);
			jsn.playCard(player, state.getActionOwner() == player ? state.getRefused().get(0).getID() : state.getActionOwner().getID());
			return true;
		}
		return false;
	}
	
	private void dumbActionStateOwner(ActionState state)
	{
		Player player = getPlayer();
		if (state.getNumberOfRefused() > 0)
		{
			if (!playJSN(state))
			{
				state.removeActionTarget(state.getRefused().get(0));
			}
		}
		if (state.isFinished())
		{
			getServer().getGameState().nextNaturalActionState();
		}
	}
	
	private void dumbActionStateReply(ActionState state)
	{
		Player player = getPlayer();
		if (state.getTargetPlayer() == player && !state.isRefused(player))
		{
			if (!playJSN(state))
			{
				state.setAccepted(player, true);
			}
		}
		if (state.isFinished())
		{
			getServer().getGameState().nextNaturalActionState();
		}
	}
	
	private List<Card> selectRentPayment(List<Card> bank, List<Card> properties, int rent)
	{
		List<Card> payment = new ArrayList<Card>();
		while (rent > 0)
		{
			boolean hasCards = false;
			for (Card c : bank)
			{
				if (c.getValue() > 0)
				{
					hasCards = true;
					break;
				}
			}
			if (!hasCards)
			{
				for (Card c : properties)
				{
					if (c.getValue() > 0)
					{
						hasCards = true;
						break;
					}
				}
			}
			if (!hasCards)
			{
				return payment;
			}
			
			Card match = selectPaymentFrom(bank, rent);
			
			if (match != null)
			{
				rent -= match.getValue();
				payment.add(match);
				bank.remove(match);
				continue;
			}
			
			match = selectPaymentFrom(properties, rent);
			
			if (match != null)
			{
				rent -= match.getValue();
				payment.add(match);
				properties.remove(match);
				continue;
			}
			return payment;
		}
		return payment;
	}
	
	private Card selectPaymentFrom(List<Card> cards, int rent)
	{
		Card match = null;
		for (Card card : cards)
		{
			if (card.getValue() == 0)
			{
				continue;
			}
			
			// Immediately select the card if it's the perfect value
			if (card.getValue() == rent)
			{
				match = card;
				break;
			}
			
			if (match == null)
			{
				match = card;
				continue;
			}
			
			// Choose a lower value card if we're already overpaying
			if (card.getValue() > rent && match.getValue() > card.getValue())
			{
				match = card;
			}
			// Choose a higher value card if we're already underpaying
			else if (card.getValue() < rent && match.getValue() < card.getValue())
			{
				match = card;
			}
		}
		return match;
	}
	
	public void registerDefaultStateHandlers()
	{
		Player player = getPlayer();
		
		registerDesire(CardType.PASS_GO, card ->
		{
			return 51 - (15 * Math.max(0, getHandCount() - 6));
		});
		registerDesire(CardType.PROPERTY, card ->
		{
			if (card.getValue() == 0 && !card.isBase())
			{
				return 100;
			}
			double security = getBankSecurity();
			
			return Math.min(security * 5, 50);
		});
		registerDesire(CardType.ITS_MY_BIRTHDAY, card ->
		{
			return getRentEffectiveness(2) * 60;
		});
		registerDesire(CardType.DEBT_COLLECTOR, card ->
		{
			return getSingleRentEffectiveness(5) * 75;
		});
		registerDesire(CardType.RENT, card ->
		{
			if (!card.canPlayCard(player))
			{
				return -1;
			}
			return getRentEffectiveness(player.getHighestValueRent(card.getRentColors())) *
					Math.min(getRentPotential(card) * 1.4, 1) * 100;
		});
		registerDesire(CardType.SLY_DEAL, card ->
		{
			return card.canPlayCard(player) ? 25 : -1;
		});
		registerDesire(CardType.DEAL_BREAKER, card ->
		{
			return card.canPlayCard(player) ? 60 : -1;
		});
		
		bankDesire = card ->
		{
			double security = getBankSecurity();
			if (card instanceof CardMoney)
			{
				return Math.max(90 - (security * 6), 25);
			}
			else if (card instanceof CardAction)
			{
				if (player.getHand().getCardCount() == 1)
				{
					return 25;
				}
				int stakes = player.getAllPropertyCards().size();
				return Math.max(Math.min(40, stakes * 8) - (security * 6), 1);
			}
			return -1;
		};
		
		registerOtherStateHandler(ActionStateDoNothing.class, state -> {});
		
		registerSelfStateHandler(ActionStateDraw.class, state ->
		{
			getPlayer().draw();
		});
		
		registerSelfStateHandler(ActionStatePlay.class, state ->
		{
			if (checkWin())
			{
				return;
			}
			
			boolean mustPlay = player.getHand().hasTooManyCards();
			
			
			
			Card mostDesired = null;
			double mostDesire = -1;
			boolean toBank = false;
			
			for (Card card : player.getHand())
			{
				PlayDesire playDesire = getDesire(card.getType());
				if (playDesire != null)
				{
					double desire = playDesire.getDesire(card);
					if (desire > mostDesire)
					{
						mostDesired = card;
						mostDesire = desire;
						toBank = false;
					}
				}
				double bankingDesire = bankDesire.getDesire(card);
				if (bankingDesire > mostDesire)
				{
					mostDesired = card;
					mostDesire = bankingDesire;
					toBank = true;
				}
			}
			
			if (mostDesired != null)
			{
				if (getServer().isVerbose())
				{
					System.out.println(getPlayer().getName() + "'s desire on " + mostDesired.getName() +
							(toBank ? " (Bank)" : "") + ": " + mostDesire);
				}
				if (mostDesire >= 25 || mustPlay)
				{
					if (toBank)
					{
						player.playCardBank(mostDesired);
					}
					else
					{
						if (mostDesired instanceof CardAction)
						{
							if (mostDesired instanceof CardActionRent)
							{
								if (getRentPotential((CardActionRent) mostDesired) >= 0.4 &&
										getServer().getGameState().getTurnsRemaining() >= 2)
								{
									if (hasCard(player.getHand().getCards(), CardType.DOUBLE_THE_RENT))
									{
										mostDesired.transfer(getServer().getDiscardPile());
										getCard(player.getHand().getCards(), CardType.DOUBLE_THE_RENT)
												.transfer(getServer().getDiscardPile());
										getServer().getGameState().decrementTurns(2);
										((CardActionRent) mostDesired).playCard(player, 2);
										return;
									}
								}
							}
							player.playCardAction((CardAction) mostDesired);
						}
						else
						{
							player.playCardProperty((CardProperty) mostDesired, -1);
						}
					}
					return;
				}
			}
			player.endTurn();
		});
		
		registerSelfStateHandler(ActionStateTargetSlyDeal.class, state ->
		{
			List<CardProperty> choices = new ArrayList<CardProperty>();
			for (Player p : getServer().getPlayersExcluding(player))
			{
				for (PropertySet set : p.getPropertySets())
				{
					if (!set.isMonopoly())
					{
						for (CardProperty prop : set.getPropertyCards())
						{
							if (prop.isBase())
							{
								choices.add(prop);
							}
						}
					}
				}
			}
			state.onCardSelected(getRandomCard(choices));
		});
		
		registerSelfStateHandler(ActionStateTargetPlayerMonopoly.class, state ->
		{
			List<PropertySet> choices = new ArrayList<PropertySet>();
			for (Player p : getServer().getPlayersExcluding(player))
			{
				for (PropertySet set : p.getPropertySets())
				{
					if (set.isMonopoly())
					{
						choices.add(set);
					}
				}
			}
			state.onSetSelected(choices.get(getRandom().nextInt(choices.size())));
		});
		
		registerSelfStateHandler(ActionStateTargetDebtCollector.class, state ->
		{
			List<Player> candidates = new ArrayList<Player>();
			int highestValue = -1;
			for (Player p : getServer().getPlayersExcluding(player))
			{
				int assets = Math.min(p.getTotalMonetaryAssets(), 5);
				if (assets > highestValue)
				{
					candidates.clear();
					candidates.add(p);
					highestValue = assets;
				}
				else if (assets == highestValue)
				{
					candidates.add(p);
				}
			}
			state.playerSelected(candidates.get(getRandom().nextInt(candidates.size())));
		});
		
		registerSelfStateHandler(ActionStateFinishTurn.class, state ->
		{
			if (!checkWin())
			{
				player.endTurn();
			}
		});
		registerSelfStateHandler(ActionStateDiscard.class, state ->
		{
			List<Card> hand = player.getHand().getCards();
			List<Card> nonProps = hand.stream().filter(card -> !(card instanceof CardProperty)).collect(Collectors.toList());
			player.discard(getRandomCard(nonProps.isEmpty() ? hand : nonProps));
		});
		registerSelfStateHandler(ActionStateTradeProperties.class, state ->
		{
			dumbActionStateOwner(state);
		});
		registerSelfStateHandler(ActionStateStealProperty.class, state ->
		{
			dumbActionStateOwner(state);
		});
		registerSelfStateHandler(ActionStateStealMonopoly.class, state ->
		{
			dumbActionStateOwner(state);
		});
		
		registerSelfStateHandler(ActionStateRent.class, state ->
		{
			dumbActionStateOwner(state);
		});
		
		registerOtherStateHandler(ActionStateRent.class, state ->
		{
			if (state.getTargetPlayers().contains(player) && !state.isRefused(player) && !state.isAccepted(player))
			{
				int rent = state.getPlayerRent(player);
				int bankValue = player.getBank().getTotalValue();
				if (rent > bankValue || (rent >= 10 && (bankValue - rent) < 8))
				{
					if (playJSN(state))
					{
						return;
					}
				}
				
				List<Card> payment = selectRentPayment(player.getBank().getCards(true),
						new ArrayList<Card>(player.getAllPropertyCards()), rent);
				
				state.playerPaid(player, payment);
			}
		});
		registerOtherStateHandler(ActionStateTradeProperties.class, state ->
		{
			dumbActionStateReply(state);
		});
		registerOtherStateHandler(ActionStateStealProperty.class, state ->
		{
			dumbActionStateReply(state);
		});
		registerOtherStateHandler(ActionStateStealMonopoly.class, state ->
		{
			dumbActionStateReply(state);
		});
	}
	
	public <T extends ActionState> void registerSelfStateHandler(Class<T> stateClass, BasicStateResponder<T> responder)
	{
		selfHandlers.put(stateClass, responder);
	}
	
	public <T extends ActionState> void registerOtherStateHandler(Class<T> stateClass, BasicStateResponder<T> responder)
	{
		otherHandlers.put(stateClass, responder);
	}
	
	public <T extends Card> void registerDesire(CardType<T> type, PlayDesire<T> desire)
	{
		typeDesires.put(type, desire);
	}
	
	public <T extends Card> PlayDesire<T> getDesire(CardType<T> type)
	{
		return (PlayDesire<T>) typeDesires.get(type);
	}
	
	@Override
	public void doAction()
	{
		ActionState state = getServer().getGameState().getActionState();
		BasicStateResponder handler = state.getActionOwner() == getPlayer() ?
				selfHandlers.get(state.getClass()) : otherHandlers.get(state.getClass());
		if (handler == null)
		{
			//System.out.println("Basic AI does not have a handler for " + state.getClass().getSimpleName() +
			//		"! Cannot perform any action!");
			return;
		}
		handler.doAction(state);
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
	
	public interface BasicStateResponder<T extends ActionState>
	{
		void doAction(T state);
	}
	
	public interface PlayDesire<T extends Card>
	{
		/**
		 * Normal Range 0-100: If the desire is less than 25, it won't be acted on unless required
		 * -1 indicates the card cannot be played under any circumstance
		 */
		double getDesire(T card);
	}
}
