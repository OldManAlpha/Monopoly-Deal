package oldmana.md.server.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import oldmana.md.net.packet.server.actionstate.PacketActionStatePlayerTurn.TurnState;
import oldmana.md.server.MDScheduler.MDTask;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardAnimationType;
import oldmana.md.server.card.CardBuilding;
import oldmana.md.server.card.CardMoney;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.PropertyColor;
import oldmana.md.server.card.action.CardActionDealBreaker.ActionStateTargetDealBreaker;
import oldmana.md.server.card.action.CardActionJustSayNo;
import oldmana.md.server.card.action.CardActionPassGo;
import oldmana.md.server.card.action.CardActionRent;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.card.CardType;
import oldmana.md.server.rules.win.PropertySetCondition;
import oldmana.md.server.state.ActionState;
import oldmana.md.server.state.ActionStateDoNothing;
import oldmana.md.server.state.ActionStateRent;
import oldmana.md.server.card.action.CardActionDealBreaker.ActionStateStealMonopoly;
import oldmana.md.server.card.action.CardActionSlyDeal.ActionStateStealProperty;
import oldmana.md.server.card.action.CardActionDebtCollector.ActionStateTargetDebtCollector;
import oldmana.md.server.card.action.CardActionForcedDeal.ActionStateTargetForcedDeal;
import oldmana.md.server.state.ActionStateTargetPlayerMonopoly;
import oldmana.md.server.card.action.CardActionSlyDeal.ActionStateTargetSlyDeal;
import oldmana.md.server.card.action.CardActionForcedDeal.ActionStateTradeProperties;
import oldmana.md.server.state.primary.ActionStatePlayerTurn;

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
	
	private int getHandCount()
	{
		return getPlayer().getHand().getCardCount();
	}
	
	private boolean hasRentCard(List<Card> cards, List<PropertyColor> colors)
	{
		return cards.stream().filter(card -> card.getType() == CardType.RENT)
		.map(card -> (CardActionRent) card).anyMatch(card ->
		{
			for (PropertyColor color : card.getRentColors())
			{
				if (colors.contains(color))
				{
					return true;
				}
			}
			return false;
		});
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
		
		for (Player p : getOpponents())
		{
			total += Math.min(p.getTotalMonetaryAssets(), rent);
		}
		return total / ((getServer().getPlayerCount() - 1) * rent);
	}
	
	private double getSingleRentEffectiveness(int rent)
	{
		double highestTotal = 0;
		
		for (Player p : getOpponents())
		{
			highestTotal = Math.max(Math.min(p.getTotalMonetaryAssets(), rent), highestTotal);
		}
		return highestTotal / rent;
	}
	
	private List<PropertyColor> getRentCardColors()
	{
		Set<PropertyColor> colors = new HashSet<PropertyColor>();
		for (Card card : getPlayer().getHand())
		{
			if (card instanceof CardActionRent)
			{
				CardActionRent rent = (CardActionRent) card;
				colors.addAll(rent.getRentColors());
			}
		}
		return new ArrayList<PropertyColor>(colors);
	}
	
	// Temporary while refactoring collections
	private int getHighestValueRent(List<CardProperty> cards)
	{
		List<PropertyColor> colors = getRentCardColors();
		Map<PropertyColor, Integer> colorCounts = new HashMap<PropertyColor, Integer>();
		List<PropertyColor> hasBaseColor = new ArrayList<PropertyColor>();
		for (PropertyColor color : colors)
		{
			colorCounts.put(color, 0);
		}
		for (CardProperty card : cards)
		{
			for (PropertyColor color : card.getColors())
			{
				if (colorCounts.containsKey(color))
				{
					colorCounts.put(color, colorCounts.get(color) + 1);
					if (card.isBase() && !hasBaseColor.contains(color))
					{
						hasBaseColor.add(color);
					}
				}
			}
		}
		int highestValue = -1;
		for (Entry<PropertyColor, Integer> entry : colorCounts.entrySet())
		{
			if (hasBaseColor.contains(entry.getKey()))
			{
				highestValue = Math.max(highestValue, entry.getKey().getRent(Math.min(entry.getValue(), entry.getKey().getMaxProperties())));
			}
		}
		return highestValue;
	}
	
	private int getHighestValueRent()
	{
		return getHighestValueRent(getPlayer().getAllPropertyCards());
	}
	
	private int getHighestValueRent(CardProperty with)
	{
		List<CardProperty> props = getPlayer().getAllPropertyCards();
		props.add(with);
		return getHighestValueRent(props);
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
		getServer().getGameState().proceed();
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
		getServer().getGameState().proceed();
	}
	
	public void calculateRentPayment(Player to, List<Card> cards, int rent, Consumer<List<Card>> paymentAction)
	{
		cards.removeIf(card -> card.getValue() == 0);
		// Don't go through combinations and return all assets if the rent is less than or equal to what we have
		if (getPlayer().getTotalMonetaryAssets() <= rent)
		{
			paymentAction.accept(cards);
			return;
		}
		
		final Iterator<RentCombo> it = getRentCombosIterator(cards, rent);
		
		final List<CardProperty> opponentProps = to.getAllPropertyCards();
		final List<CardProperty> selfProps = getPlayer().getAllPropertyCards();
		final int opponentNeeded = getCardsNeededToWin(opponentProps);
		final int selfNeeded = getCardsNeededToWin(selfProps);
		
		getServer().getScheduler().scheduleTask(1, true, new Consumer<MDTask>()
		{
			private RentCombo bestCandidiate;
			private int bestNet;
			
			@Override
			public void accept(MDTask task)
			{
				for (int i = 0 ; i < 1000 ; i++)
				{
					if (!it.hasNext())
					{
						task.cancel();
						paymentAction.accept(bestCandidiate.getCards());
						return;
					}
					RentCombo candidate = it.next();
					if (bestCandidiate == null)
					{
						setBestCandidiate(candidate);
						continue;
					}
					if (bestCandidiate.hasProperties() && !candidate.hasProperties())
					{
						setBestCandidiate(candidate);
						continue;
					}
					
				}
			}
			
			private void setBestCandidiate(RentCombo combo)
			{
				setBestCandidiate(combo, -1);
			}
			
			private void setBestCandidiate(RentCombo combo, int net)
			{
				this.bestCandidiate = combo;
				this.bestNet = net;
			}
		});
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
		for (Player p : getOpponents())
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
		return getServer().getGameState().getMovesRemaining();
	}
	
	public void registerDefaultStateHandlers()
	{
		Player player = getPlayer();
		
		registerDesire(CardType.PASS_GO, card ->
		{
			return 51 - (20 * Math.max(0, getHandCount() - (getServer().getGameRules().getMaxCardsInHand() - 2) - getTurnsRemaining()));
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
			
			int rentWithout = getHighestValueRent();
			int rentWith = getHighestValueRent(card);
			
			double effectiveness = getRentEffectiveness(rentWith);
			
			double aggressiveDesire = 0;
			if (rentWith > rentWithout)
			{
				aggressiveDesire = rentWith * effectiveness * 10 * Math.max(8 - security, 1);
				aggressiveDesire = Math.min(getTurnsRemaining() > 1 ? 90 : 40, aggressiveDesire);
				System.out.println("Aggressive: " + aggressiveDesire);
			}
			
			return Math.max(aggressiveDesire, Math.max(Math.min(security * 5, 50),
					neededWith < needed ? 65 - (neededWith * (20 - Math.min(security, 16))) : 0));
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
			double insecurity = (Math.max(6 - getBankSecurity(), 0) / 12) + 1;
			return Math.min(getRentEffectiveness(player.getHighestValueRent(card.getRentColors())) * insecurity, 100) *
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
			return Math.max(bestToSteal != null ? 80 - (neededToWin * 12) : 10, getHandCount() == 1 ? 26 : 0);
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
			if (card instanceof CardMoney || card instanceof CardBuilding) // Bots think of buildings only as money, for now
			{
				return Math.max(90 - (security * 6), 30);
			}
			else if (card instanceof CardAction)
			{
				if (player.getHand().getCardCount() == 1)
				{
					return 25;
				}
				if (card instanceof CardActionJustSayNo)
				{
					return 0;
				}
				int stakes = player.getAllPropertyCards().size();
				return Math.max(Math.min(40, stakes * 8) - (security * 6), 1);
			}
			return -1;
		};
		
		registerOtherStateHandler(ActionStateDoNothing.class, state -> {});
		
		registerSelfStateHandler(ActionStatePlayerTurn.class, state ->
		{
			TurnState turnState = state.getTurnState();
			if (turnState == TurnState.DRAW)
			{
				getPlayer().draw();
			}
			else if (turnState == TurnState.PLAY)
			{
				if (checkWin())
				{
					return;
				}
				
				if (state.getMoves() == 0)
				{
					player.endTurn();
					return;
				}
				
				boolean mustPlay = player.getHand().hasTooManyCards();
				
				
				Map<Card, Boolean> mostDesired = new HashMap<Card, Boolean>();
				double mostDesire = -1;
				
				for (Card card : player.getHand())
				{
					PlayDesire playDesire = getDesire(card.getType());
					double bankingDesire = bankDesire.getDesire(card);
					if (playDesire != null)
					{
						double desire = playDesire.getDesire(card);
						if (desire > mostDesire)
						{
							mostDesired.clear();
							mostDesire = desire;
						}
						if (desire == mostDesire)
						{
							mostDesired.put(card, false);
						}
					}
					if (bankingDesire > mostDesire)
					{
						mostDesired.clear();
						mostDesire = bankingDesire;
					}
					if (bankingDesire == mostDesire && !mostDesired.containsKey(card))
					{
						mostDesired.put(card, true);
					}
				}
				
				if (!mostDesired.isEmpty())
				{
					Card chosenCard = getRandomCard(new ArrayList<Card>(mostDesired.keySet()));
					boolean toBank = mostDesired.get(chosenCard);
					if (getServer().isVerbose())
					{
						System.out.println(getPlayer().getName() + "'s desire on " + chosenCard.getName() +
								(toBank ? " (Bank)" : "") + ": " + mostDesire);
					}
					if (mostDesire >= 25 || mustPlay)
					{
						if (toBank)
						{
							player.playCardBank(chosenCard);
						}
						else
						{
							if (chosenCard instanceof CardAction)
							{
								if (chosenCard instanceof CardActionRent)
								{
									if (getRentPotential((CardActionRent) chosenCard) >= 0.4 &&
											getServer().getGameState().getMovesRemaining() >= 2)
									{
										if (hasCard(player.getHand().getCards(), CardType.DOUBLE_THE_RENT))
										{
											chosenCard.transfer(getServer().getDiscardPile());
											getCard(player.getHand().getCards(), CardType.DOUBLE_THE_RENT)
													.transfer(getServer().getDiscardPile(), -1, CardAnimationType.IMPORTANT);
											getServer().getGameState().decrementMoves(2);
											((CardActionRent) chosenCard).playCard(player, 2);
											return;
										}
									}
								}
								player.playCardAction((CardAction) chosenCard);
							}
							else
							{
								player.playCardProperty((CardProperty) chosenCard, -1);
							}
						}
						return;
					}
				}
				player.endTurn();
			}
			else if (turnState == TurnState.DISCARD)
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
			}
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
			for (Player p : getOpponents())
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
				System.out.println("AI cannot proceed: No forced deal target.");
				return;
			}
			state.onCardsSelected(best.give, best.take);
		});
		
		registerSelfStateHandler(ActionStateTargetDealBreaker.class, state ->
		{
			List<PropertySet> choices = new ArrayList<PropertySet>();
			for (Player p : getOpponents())
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
			for (Player p : getOpponents())
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
				if ((rent > bankValue && player.getTotalPropertyValue() > 0) || (rent >= 10 && (bankValue - rent) < 8))
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
