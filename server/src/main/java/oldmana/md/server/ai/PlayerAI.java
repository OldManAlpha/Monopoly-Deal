package oldmana.md.server.ai;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.PropertyColor;
import oldmana.md.server.rules.win.PropertySetCondition;
import oldmana.md.server.rules.win.WinCondition;

public abstract class PlayerAI
{
	private Player player;
	
	private Random rand = new Random();
	
	private static Map<List<CardProperty>, List<PropertyCombo>> comboCache = new HashMap<List<CardProperty>, List<PropertyCombo>>();
	private static Queue<List<CardProperty>> comboCacheOrder = new ArrayDeque<List<CardProperty>>();
	
	private int responseTime;
	
	public PlayerAI(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public List<Player> getOpponents()
	{
		return getServer().getPlayersExcluding(getPlayer());
	}
	
	protected Random getRandom()
	{
		return rand;
	}
	
	public int getResponseTime()
	{
		return responseTime;
	}
	
	public void triggerResponse()
	{
		if (responseTime > 0)
		{
			responseTime = 40;
		}
	}
	
	public boolean willRespondSoon()
	{
		return responseTime > 0;
	}
	
	public boolean decrementResponseTime()
	{
		return --responseTime == 0;
	}
	
	protected MDServer getServer()
	{
		return MDServer.getInstance();
	}
	
	
	/**
	 * Get a List of possible set combinations that the given property list can produce. Note that the sets returned
	 * might be over the maximum properties for a given color.
	 * @param cards The cards to arrange
	 * @return An exhaustive List of combinations
	 */
	public List<PropertyCombo> getPossibleCombos(List<CardProperty> cards)
	{
		if (comboCache.containsKey(cards))
		{
			return comboCache.get(cards);
		}
		
		List<PropertyCombo> combos = new LinkedList<PropertyCombo>();
		Iterator<PropertyCombo> it = getPossibleCombosIterator(cards);
		while (it.hasNext())
		{
			combos.add(it.next());
		}
		List<PropertyCombo> arrayCombos = new ArrayList<PropertyCombo>(combos);
		comboCache.put(cards, arrayCombos);
		comboCacheOrder.offer(cards);
		if (comboCacheOrder.size() > 200)
		{
			comboCache.remove(comboCacheOrder.poll());
		}
		return arrayCombos;
		
		/*
		List<PropertyCombo> possibleCombos = new ArrayList<PropertyCombo>();
		
		List<CardProperty> solids = new ArrayList<CardProperty>();
		List<CardProperty> basedWilds = new ArrayList<CardProperty>();
		List<CardProperty> unbasedWilds = new ArrayList<CardProperty>();
		
		for (CardProperty prop : cards)
		{
			if (prop.isSingleColor())
			{
				solids.add(prop);
			}
			else if (prop.isBase())
			{
				basedWilds.add(prop);
			}
			else
			{
				unbasedWilds.add(prop);
			}
		}
		
		PropertyCombo baseCombo = new PropertyCombo();
		
		if (solids.isEmpty() && basedWilds.isEmpty())
		{
			baseCombo.addProperties(null, unbasedWilds);
			possibleCombos.add(baseCombo);
			return possibleCombos;
		}
		
		// Create the base combo, which only has solids colors
		for (CardProperty prop : solids)
		{
			baseCombo.addProperty(prop.getColor(), prop);
		}
		possibleCombos.add(baseCombo);
		
		applyCombos(possibleCombos, basedWilds, true);
		applyCombos(possibleCombos, unbasedWilds, false);
		
		comboCache.put(cards, possibleCombos);
		comboCacheOrder.offer(cards);
		if (comboCacheOrder.size() > 200)
		{
			comboCache.remove(comboCacheOrder.poll());
		}
		return possibleCombos;
		
		 */
	}
	
	public void applyCombos(List<PropertyCombo> combos, List<CardProperty> properties, boolean base)
	{
		for (CardProperty prop : properties)
		{
			int originalSize = combos.size();
			// Add the based wild to all previous combos, for each valid color it could be
			for (PropertyColor color : prop.getColors())
			{
				for (int i = 0 ; i < originalSize ; i++)
				{
					PropertyCombo combo = combos.get(i);
					if (!base && !combo.hasColor(color))
					{
						continue;
					}
					PropertyCombo comboCopy = new PropertyCombo(combo);
					comboCopy.addProperty(color, prop);
					combos.add(comboCopy);
					if (combos.size() >= 100000)
					{
						System.out.println("WARNING: AI didn't complete calculating all property combinations because " +
								"there's over 100,000!");
						return;
					}
				}
			}
		}
	}
	
	public Iterator<PropertyCombo> getPossibleCombosIterator(List<CardProperty> cards)
	{
		PropertyCombo baseCombo = new PropertyCombo();
		List<CardProperty> wilds = new ArrayList<CardProperty>();
		for (CardProperty prop : cards)
		{
			if (prop.isSingleColor())
			{
				baseCombo.addProperty(prop.getColor(), prop);
			}
			else
			{
				wilds.add(prop);
			}
		}
		wilds.sort(Comparator.comparingInt(p -> (p.isBase() ? -1 : 1)));
		return new Iterator<PropertyCombo>()
		{
			private ComboStack stack = new ComboStack(baseCombo, wilds);
			
			private PropertyCombo nextCached;
			
			@Override
			public boolean hasNext()
			{
				if (nextCached == null)
				{
					nextCached = stack.next();
				}
				return nextCached != null;
			}
			
			@Override
			public PropertyCombo next()
			{
				PropertyCombo combo = nextCached != null ? nextCached : stack.next();
				nextCached = null;
				return combo;
			}
		};
	}
	
	public static class ComboStack
	{
		private ComboStack innerStack;
		
		private PropertyCombo combo;
		private List<CardProperty> properties;
		private int currentProperty = 0;
		private int currentColor = -1;
		
		private boolean exhausted;
		
		public ComboStack(PropertyCombo combo, List<CardProperty> properties)
		{
			this.combo = combo;
			this.properties = properties;
		}
		
		public PropertyCombo next()
		{
			if (innerStack != null)
			{
				PropertyCombo innerCombo = innerStack.next();
				if (innerCombo != null)
				{
					//exhausted = true; // The base combo here is redundant if there's a combo with more properties
					return innerCombo;
				}
			}
			
			if (canAdvanceColor())
			{
				PropertyColor color = advanceColor();
				if (!isCursorCompatible())
				{
					return next();
				}
				PropertyCombo newCombo = new PropertyCombo(combo, color, getCurrentProperty());
				innerStack = new ComboStack(newCombo, getPropertiesMinusCursor());
				return next();
			}
			
			if (canAdvanceProperty())
			{
				advanceProperty();
				return next();
			}
			
			if (exhausted || combo.getMap().isEmpty())
			{
				return null;
			}
			exhausted = true;
			return combo;
		}
		
		private List<CardProperty> getPropertiesMinusCursor()
		{
			return properties.subList(currentProperty + 1, properties.size());
		}
		
		public CardProperty getCurrentProperty()
		{
			return properties.get(currentProperty);
		}
		
		public PropertyColor getCurrentColor()
		{
			return getCurrentProperty().getColors().get(currentColor);
		}
		
		public boolean canAdvanceProperty()
		{
			return properties.size() > currentProperty + 1;
		}
		
		public void advanceProperty()
		{
			currentProperty++;
			currentColor = -1;
		}
		
		public boolean canAdvanceColor()
		{
			return !properties.isEmpty() && getCurrentProperty().getColors().size() > currentColor + 1;
		}
		
		public PropertyColor advanceColor()
		{
			currentColor++;
			return getCurrentColor();
		}
		
		public boolean isCursorCompatible()
		{
			CardProperty prop = getCurrentProperty();
			return prop.isBase() || combo.hasColor(getCurrentColor());
		}
	}
	
	public int getCardsNeededToWin(Player player)
	{
		return getCardsNeededToWin(player.getAllPropertyCards());
	}
	
	public int getCardsNeededToWin(List<CardProperty> properties)
	{
		List<PropertyCombo> combos = getPossibleCombos(properties);
		int leastNeededCombo = 7;
		
		for (PropertyCombo combo : combos)
		{
			Map<PropertyColor, Integer> needed = new HashMap<PropertyColor, Integer>();
			for (PropertyColor color : PropertyColor.getAllColors())
			{
				needed.put(color, color.getMaxProperties());
			}
			combo.forEach((color, props) ->
			{
				if (color == null)
				{
					return;
				}
				needed.put(color, Math.max(color.getMaxProperties() - props.size(), 0));
			});
			WinCondition condition = getServer().getGameRules().getWinCondition();
			int setsNeeded = condition instanceof PropertySetCondition ?
					((PropertySetCondition) condition).getSetCount() : 3;
			
			Map<PropertyColor, Integer> leastNeeded = new HashMap<PropertyColor, Integer>();
			// Iterate through all colors
			needed.forEach((color, amount) ->
			{
				Entry<PropertyColor, Integer> mostLeastNeeded = null;
				for (Entry<PropertyColor, Integer> entry : leastNeeded.entrySet())
				{
					if (mostLeastNeeded == null || entry.getValue() > mostLeastNeeded.getValue())
					{
						mostLeastNeeded = entry;
					}
				}
				if (setsNeeded > leastNeeded.size())
				{
					leastNeeded.put(color, amount);
				}
				else if (mostLeastNeeded.getValue() > amount)
				{
					leastNeeded.remove(mostLeastNeeded.getKey());
					leastNeeded.put(color, amount);
				}
			});
			int comboNeeded = 0;
			for (int n : leastNeeded.values())
			{
				comboNeeded += n;
			}
			if (comboNeeded < leastNeededCombo)
			{
				leastNeededCombo = comboNeeded;
			}
		}
		return leastNeededCombo;
	}
	
	public int getCardsNeededToWinWith(List<CardProperty> properties, CardProperty with)
	{
		properties.add(with);
		return getCardsNeededToWin(properties);
	}
	
	public int getCardsNeededToWinWith(List<CardProperty> properties, List<CardProperty> with)
	{
		List<CardProperty> all = new ArrayList<CardProperty>(properties);
		all.addAll(with);
		return getCardsNeededToWin(all);
	}
	
	public int getCardsNeededToWinWithout(List<CardProperty> properties, List<CardProperty> without)
	{
		List<CardProperty> all = new ArrayList<CardProperty>(properties);
		all.removeAll(without);
		return getCardsNeededToWin(all);
	}
	
	public int getCardsNeededToWinWithWithout(List<CardProperty> properties, CardProperty with, CardProperty without)
	{
		properties.add(with);
		properties.remove(without);
		return getCardsNeededToWin(properties);
	}
	
	
	public List<RentCombo> getRentCombosFull(List<Card> cards, int rent)
	{
		return getRentCombosFullInternal(cards.stream().filter(card -> card.getValue() > 0)
				.collect(Collectors.toCollection(ArrayList::new)), new ArrayList<Card>(), rent);
	}
	
	public List<RentCombo> getRentCombosFullInternal(List<Card> cards, List<Card> selected, int rent)
	{
		List<RentCombo> combos = new ArrayList<RentCombo>();
		for (int i = 0 ; i < cards.size() ; i++)
		{
			Card card = cards.get(i);
			RentCombo combo = new RentCombo(selected, card);
			if (combo.getValue() >= rent)
			{
				combos.add(combo);
			}
			else if (cards.size() > 1)
			{
				combos.addAll(getRentCombosFullInternal(cards.subList(i + 1, cards.size()), combo.getCards(), rent));
			}
		}
		return combos;
	}
	
	public List<RentCombo> getRentCombos(List<Card> cards, int rent)
	{
		return getRentCombosInternal(cards.stream().filter(card -> card.getValue() > 0)
				.collect(Collectors.toCollection(ArrayList::new)), new ArrayList<Card>(), rent);
	}
	
	public List<RentCombo> getRentCombosInternal(List<Card> cards, List<Card> selected, int rent)
	{
		List<Card> accepted = new ArrayList<Card>();
		for (Card card : cards)
		{
			// If none of the currently accepted are the same value or are a property with the same colors, add to accepted
			if (accepted.stream().noneMatch(c -> (c.getValue() == card.getValue() && !(c instanceof CardProperty)) ||
					(c instanceof CardProperty && c.getValue() == card.getValue() && card instanceof CardProperty &&
							((CardProperty) c).isBase() == ((CardProperty) card).isBase() &&
							((CardProperty) c).getColors().equals(((CardProperty) card).getColors()))))
			{
				accepted.add(card);
			}
		}
		List<RentCombo> combos = new ArrayList<RentCombo>();
		for (int i = 0 ; i < cards.size() ; i++)
		{
			Card card = cards.get(i);
			if (!accepted.contains(card))
			{
				continue;
			}
			RentCombo combo = new RentCombo(selected, card);
			if (combo.getValue() >= rent)
			{
				combos.add(combo);
			}
			else if (cards.size() > 1)
			{
				combos.addAll(getRentCombosInternal(cards.subList(i + 1, cards.size()), combo.getCards(), rent));
			}
		}
		return combos;
	}
	
	public Iterator<RentCombo> getRentCombosIterator(List<Card> cards, int rent)
	{
		return new Iterator<RentCombo>()
		{
			private RentStack stack = new RentStack(cards.stream().filter(card -> card.getValue() > 0)
					.collect(Collectors.toCollection(ArrayList::new)), new ArrayList<Card>(), rent);
			
			private RentCombo nextCached;
			
			@Override
			public boolean hasNext()
			{
				if (nextCached == null)
				{
					nextCached = stack.next();
				}
				return nextCached != null;
			}
			
			@Override
			public RentCombo next()
			{
				RentCombo combo = nextCached != null ? nextCached : stack.next();
				nextCached = null;
				return combo;
			}
		};
	}
	
	public static class RentStack
	{
		private List<Card> cards;
		private List<Card> accepted;
		private List<Card> selected;
		private int rent;
		
		private int pos;
		private RentStack innerStack;
		
		public RentStack(List<Card> cards, List<Card> selected, int rent)
		{
			this.cards = cards;
			accepted = new ArrayList<Card>();
			for (Card card : cards)
			{
				// If none of the currently accepted are the same value or are a property with the same colors, add to accepted
				if (accepted.stream().noneMatch(c -> (c.getValue() == card.getValue() && !(c instanceof CardProperty)) ||
						(c instanceof CardProperty && c.getValue() == card.getValue() && card instanceof CardProperty &&
								((CardProperty) c).isBase() == ((CardProperty) card).isBase() &&
								((CardProperty) c).getColors().equals(((CardProperty) card).getColors()))))
				{
					accepted.add(card);
				}
			}
			this.selected = selected;
			this.rent = rent;
		}
		
		public RentCombo next()
		{
			if (innerStack != null)
			{
				RentCombo combo = innerStack.next();
				if (combo != null)
				{
					return combo;
				}
				innerStack = null;
			}
			if (pos == cards.size())
			{
				return null;
			}
			Card card = cards.get(pos);
			pos++;
			if (!accepted.contains(card))
			{
				return next();
			}
			RentCombo combo = new RentCombo(selected, card);
			if (combo.getValue() >= rent)
			{
				return combo;
			}
			else if (cards.size() > 1)
			{
				innerStack = new RentStack(cards.subList(pos, cards.size()), combo.getCards(), rent);
				return innerStack.next();
			}
			return null;
		}
	}
	
	
	public abstract void doAction();
	
	public abstract double getWinThreat(Player player);
	
	public abstract double getRentThreat(Player player);
	
	public static class PropertyCombo
	{
		private Map<PropertyColor, List<CardProperty>> combo = new HashMap<PropertyColor, List<CardProperty>>();
		
		public PropertyCombo() {}
		
		public PropertyCombo(PropertyCombo toCopy)
		{
			toCopy.getMap().forEach((color, props) -> combo.put(color, new ArrayList<CardProperty>(props)));
		}
		
		public PropertyCombo(PropertyCombo base, PropertyColor addColor, CardProperty addProperty)
		{
			this(base);
			addProperty(addColor, addProperty);
		}
		
		public Map<PropertyColor, List<CardProperty>> getMap()
		{
			return combo;
		}
		
		public void addProperty(PropertyColor color, CardProperty property)
		{
			combo.computeIfAbsent(color, key -> new ArrayList<CardProperty>());
			combo.get(color).add(property);
		}
		
		public void addProperties(PropertyColor color, List<CardProperty> properties)
		{
			combo.computeIfAbsent(color, key -> new ArrayList<CardProperty>());
			combo.get(color).addAll(properties);
		}
		
		public List<CardProperty> getProperties(PropertyColor color)
		{
			return combo.get(color);
		}
		
		public boolean hasColor(PropertyColor color)
		{
			return combo.containsKey(color);
		}
		
		public int getAmount(PropertyColor color)
		{
			return combo.get(color).size();
		}
		
		public void forEach(BiConsumer<PropertyColor, List<CardProperty>> action)
		{
			combo.forEach(action);
		}
	}
	
	public static class RentCombo
	{
		private List<Card> cards;
		
		public RentCombo()
		{
			cards = new ArrayList<Card>();
		}
		
		public RentCombo(List<Card> base, Card addition)
		{
			cards = new ArrayList<Card>(base);
			cards.add(addition);
		}
		
		public List<Card> getCards()
		{
			return cards;
		}
		
		public void addCard(Card card)
		{
			cards.add(card);
		}
		
		public boolean hasProperties()
		{
			for (Card card : cards)
			{
				if (card instanceof CardProperty)
				{
					return true;
				}
			}
			return false;
		}
		
		public List<CardProperty> getProperties()
		{
			List<CardProperty> properties = new ArrayList<CardProperty>();
			for (Card card : cards)
			{
				if (card instanceof CardProperty)
				{
					properties.add((CardProperty) card);
				}
			}
			return properties;
		}
		
		public int getValue()
		{
			int value = 0;
			for (Card card : cards)
			{
				value += card.getValue();
			}
			return value;
		}
	}
}
