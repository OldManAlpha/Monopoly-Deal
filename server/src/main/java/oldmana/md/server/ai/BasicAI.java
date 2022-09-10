package oldmana.md.server.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
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
import oldmana.md.server.card.action.CardActionPassGo;
import oldmana.md.server.card.action.CardActionRent;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.card.CardType;
import oldmana.md.server.rules.win.PropertySetCondition;
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
import oldmana.md.server.state.ActionStateTargetForcedDeal;
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
		return getPlayer().getBank().getTotalValue() * (getPlayer().getBank().getCardCount() * 0.4);
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
		List<PropertyCombo> combos = getPossibleCombos(getPlayer().getAllPropertyCards());
		for (PropertyCombo combo : combos)
		{
			Map<PropertyColor, List<CardProperty>> winningSets = new HashMap<PropertyColor, List<CardProperty>>();
			combo.forEach((color, props) ->
			{
				if (color != null && color.getMaxProperties() == props.size())
				{
					winningSets.put(color, props);
				}
			});
			if (winningSets.size() >= ((PropertySetCondition) getServer().getGameRules().getWinCondition()).getSetCount())
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
			jsn.playCard(player, state.getActionOwner() == player ? state.getRefused().get(0) : state.getActionOwner());
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
	
	public List<Card> selectRentPayment(Player to, List<Card> cards, int rent)
	{
		cards.removeIf(card -> card.getValue() == 0);
		// Don't go through combinations and return all assets if the rent is less than or equal to what we have
		if (getPlayer().getTotalMonetaryAssets() <= rent)
		{
			return cards;
		}
		
		List<RentCombo> combos = getRentCombos(cards, rent);
		combos.sort(Comparator.comparingInt(combo -> combo.getValue()));
		
		List<RentCombo> propertyless = new ArrayList<RentCombo>();
		for (RentCombo combo : combos)
		{
			if (!combo.hasProperties())
			{
				propertyless.add(combo);
			}
		}
		propertyless.sort(Comparator.comparingInt(combo -> (combo.getValue() * 100) + combo.getCards().size()));
		
		// If no properties must be forfeited, then just return the cheapest payment
		if (!propertyless.isEmpty())
		{
			return propertyless.get(0).getCards();
		}
		
		List<CardProperty> opponentProps = to.getAllPropertyCards();
		List<CardProperty> selfProps = getPlayer().getAllPropertyCards();
		
		int opponentNeeded = getCardsNeededToWin(opponentProps);
		int selfNeeded = getCardsNeededToWin(selfProps);
		
		Map<Integer, List<RentCombo>> netMap = new HashMap<Integer, List<RentCombo>>();
		List<RentCombo> losses = new ArrayList<RentCombo>();
		for (RentCombo combo : combos)
		{
			List<CardProperty> comboProps = combo.getProperties();
			
			// The properties the opponent will have with this combo
			List<CardProperty> comboOpponentProps = new ArrayList<CardProperty>(opponentProps);
			comboOpponentProps.addAll(comboProps);
			int comboOpponentNeeded = getCardsNeededToWin(comboOpponentProps);
			if (comboOpponentNeeded == 0) // The opponent will win if we give up these properties!
			{
				losses.add(combo);
			}
			
			// The properties the self player will have with this combo
			List<CardProperty> comboSelfProps = new ArrayList<CardProperty>(selfProps);
			comboSelfProps.removeAll(comboProps);
			int comboSelfNeeded = getCardsNeededToWin(comboSelfProps);
			
			// Selfish trait could be added here by multiplying self net loss
			int net = (selfNeeded - comboSelfNeeded) + (comboOpponentNeeded - opponentNeeded);
			netMap.computeIfAbsent(net, key -> new ArrayList<RentCombo>());
			netMap.get(net).add(combo);
		}
		for (int i = 0 ; i > -20 ; i--)
		{
			if (netMap.containsKey(i))
			{
				List<RentCombo> bestCombos = netMap.get(i);
				List<RentCombo> betterBestCombos = new ArrayList<RentCombo>();
				for (RentCombo combo : bestCombos)
				{
					if (!losses.contains(combo))
					{
						betterBestCombos.add(combo);
					}
				}
				if (!betterBestCombos.isEmpty())
				{
					// Of the best nets, we're going to forfeit the smallest number of properties
					betterBestCombos.sort(Comparator.comparingInt(combo -> combo.getProperties().size()));
					return betterBestCombos.get(0).getCards();
				}
			}
		}
		// Admit defeat
		System.out.println(getPlayer().getName() + " was forced to give a losing payment");
		return combos.get(0).getCards();
	}
	
	public List<CardProperty> getStealableProperties()
	{
		List<CardProperty> stealable = new ArrayList<CardProperty>();
		for (Player p : getServer().getPlayersExcluding(getPlayer()))
		{
			for (PropertySet set : p.getPropertySets())
			{
				if (!set.isMonopoly())
				{
					for (CardProperty prop : set.getPropertyCards())
					{
						if (prop.isBase())
						{
							stealable.add(prop);
						}
					}
				}
			}
		}
		return stealable;
	}
	
	public List<CardProperty> getTradableProperties()
	{
		List<CardProperty> tradable = new ArrayList<CardProperty>();
		for (PropertySet set : getPlayer().getPropertySets())
		{
			for (CardProperty prop : set.getPropertyCards())
			{
				if (prop.isBase())
				{
					tradable.add(prop);
				}
			}
		}
		return tradable;
	}
	
	public CardProperty getBestCardToSteal()
	{
		int currentNeededToWin = getCardsNeededToWin(getPlayer());
		List<CardProperty> targets = new ArrayList<CardProperty>();
		int targetValue = -1;
		for (CardProperty prop : getStealableProperties())
		{
			if (getCardsNeededToWinWith(getPlayer().getAllPropertyCards(), prop) < currentNeededToWin)
			{
				if (prop.getValue() > targetValue)
				{
					targets.clear();
					targets.add(prop);
					targetValue = prop.getValue();
				}
				else if (prop.getValue() == targetValue)
				{
					targets.add(prop);
				}
			}
		}
		Collections.shuffle(targets);
		// Sort by net color gain
		targets.sort((Comparator.<CardProperty>comparingInt(target -> target.getColors().size())).reversed());
		return targets.size() > 0 ? targets.get(0) : null;
	}
	
	public CardTrade getBestCardTrade()
	{
		int currentNeededToWin = getCardsNeededToWin(getPlayer());
		List<CardTrade> targets = new ArrayList<CardTrade>();
		for (CardProperty stealable : getStealableProperties())
		{
			for (CardProperty tradable : getTradableProperties())
			{
				if (getCardsNeededToWinWithWithout(getPlayer().getAllPropertyCards(), stealable, tradable) < currentNeededToWin)
				{
					if (currentNeededToWin > 1)
					{
						// Skip this trade if the player would win this property
						if (getCardsNeededToWinWithWithout(stealable.getOwner().getAllPropertyCards(), tradable, stealable) == 0)
						{
							continue;
						}
					}
					targets.add(new CardTrade(stealable, tradable));
				}
			}
		}
		Collections.shuffle(targets);
		// Sort by net color gain
		targets.sort(Comparator.comparingInt(trade -> trade.give.getColors().size() - trade.take.getColors().size()));
		return targets.size() > 0 ? targets.get(0) : null;
	}
	
	public static class CardTrade
	{
		public CardProperty take;
		public CardProperty give;
		
		public CardTrade(CardProperty take, CardProperty give)
		{
			this.take = take;
			this.give = give;
		}
	}
	
	private int getTurnsRemaining()
	{
		return getServer().getGameState().getTurnsRemaining();
	}
	
	public void registerDefaultStateHandlers()
	{
		Player player = getPlayer();
		
		registerDesire(CardType.PASS_GO, card ->
		{
			return 51 - (20 * Math.max(0, getHandCount() - 5 - getTurnsRemaining()));
		});
		registerDesire(CardType.PROPERTY, card ->
		{
			if (card.getValue() == 0 && !card.isBase())
			{
				return 100;
			}
			int needed = getCardsNeededToWin(getPlayer());
			int neededWith = getCardsNeededToWinWith(getPlayer().getAllPropertyCards(), card);
			if (neededWith == 0)
			{
				return 100;
			}
			double security = getBankSecurity();
			
			return Math.max(Math.min(security * 5, 50), neededWith < needed ? 65 - (neededWith * (20 - Math.min(security, 16))) : 0);
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
			if (!card.canPlayCard(player))
			{
				return -1;
			}
			int neededToWin = getCardsNeededToWin(player);
			CardProperty bestToSteal = getBestCardToSteal();
			return bestToSteal != null ? 80 - (neededToWin * 12) : 10;
		});
		registerDesire(CardType.FORCED_DEAL, card ->
		{
			if (!card.canPlayCard(player))
			{
				return -1;
			}
			int neededToWin = getCardsNeededToWin(player);
			CardTrade bestTrade = getBestCardTrade();
			if (bestTrade == null)
			{
				return -1;
			}
			return 80 - (neededToWin * 10);
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
				return Math.max(90 - (security * 6), 30);
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
			CardProperty best = getBestCardToSteal();
			if (best != null)
			{
				state.onCardSelected(best);
				return;
			}
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
		
		registerSelfStateHandler(ActionStateTargetForcedDeal.class, state ->
		{
			CardTrade best = getBestCardTrade();
			if (best == null)
			{
				getServer().getGameState().nextNaturalActionState();
				return;
			}
			state.onCardsSelected(best.give, best.take);
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
			List<Card> nonProps = hand.stream()
					.filter(card -> !(card instanceof CardProperty))
					.sorted(Comparator.comparingInt(card -> card.getValue()))
					.collect(Collectors.toList());
			List<Card> preferred = nonProps.stream()
					.filter(card -> !(card instanceof CardAction) || card instanceof CardActionPassGo)
					.collect(Collectors.toList());
			player.discard(preferred.isEmpty() ? (nonProps.isEmpty() ? getRandomCard(hand) : nonProps.get(0)) : preferred.get(0));
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
				
				List<Card> payment = selectRentPayment(state.getActionOwner(), player.getAllTableCards(), rent);
				
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
			return;
		}
		if (!(getServer().getGameRules().getWinCondition() instanceof PropertySetCondition))
		{
			System.out.println("Basic AI only supports Monopoly win conditions! Cannot perform any action!");
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
	
	public static class Plan
	{
		private List<PlanStage> stages = new LinkedList<PlanStage>();
		
		public Plan() {}
		
		public Plan(PlanStage... stages)
		{
			this.stages.addAll(Arrays.asList(stages));
		}
		
		public void addStage(PlanStage stage)
		{
			stages.add(stage);
		}
		
		public PlanStage getNextStage()
		{
			return stages.get(0);
		}
		
		public void completeStage()
		{
			stages.remove(0);
		}
		
		public boolean isDone()
		{
			return stages.isEmpty();
		}
	}
	
	public static class PlanStage
	{
	
	}
}
