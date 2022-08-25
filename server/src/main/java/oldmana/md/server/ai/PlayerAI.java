package oldmana.md.server.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.PropertyColor;

public abstract class PlayerAI
{
	private Player player;
	
	private Random rand = new Random();
	
	public PlayerAI(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	protected Random getRandom()
	{
		return rand;
	}
	
	protected MDServer getServer()
	{
		return MDServer.getInstance();
	}
	
	public List<Map<PropertyColor, Integer>> getPossibleCombos(Player player)
	{
		return getPossibleCombos(player.getAllPropertyCards());
	}
	
	/**
	 * A terrible, lazy method to get the possible combos given a list of cards. Randomly generates 100 combos,
	 * perhaps missing possible combos.
	 */
	public List<Map<PropertyColor, List<CardProperty>>> getPossibleCombosNew(List<CardProperty> cards)
	{
		List<CardProperty> wilds = new ArrayList<CardProperty>();
		Map<PropertyColor, List<CardProperty>> solidProps = new HashMap<PropertyColor, List<CardProperty>>();
		List<PropertyColor> validColors = new ArrayList<PropertyColor>();
		for (CardProperty card : cards)
		{
			if (card.isSingleColor())
			{
				PropertyColor color = card.getColor();
				solidProps.computeIfAbsent(color, key -> new ArrayList<CardProperty>());
				solidProps.get(color).add(card);
			}
			else
			{
				wilds.add(card);
			}
			// Add colors as valid if the card is a base
			if (card.isBase())
			{
				for (PropertyColor color : card.getColors())
				{
					if (!validColors.contains(color))
					{
						validColors.add(color);
					}
				}
			}
		}
		List<Map<PropertyColor, List<CardProperty>>> possibleCombos = new ArrayList<Map<PropertyColor, List<CardProperty>>>();
		
		if (validColors.isEmpty())
		{
			return possibleCombos;
		}
		
		Random r = getRandom();
		for (int iter = 0 ; iter < 100 ; iter++)
		{
			Map<PropertyColor, List<CardProperty>> combo = new HashMap<PropertyColor, List<CardProperty>>();
			solidProps.forEach((color, props) -> combo.put(color, new ArrayList<CardProperty>(props)));
			for (PropertyColor color : validColors)
			{
				combo.computeIfAbsent(color, key -> new ArrayList<CardProperty>());
			}
			
			List<CardProperty> shuffledWilds = new ArrayList<CardProperty>(wilds);
			Collections.shuffle(shuffledWilds);
			
			for (CardProperty wild : shuffledWilds)
			{
				List<PropertyColor> possibleColors = new ArrayList<PropertyColor>();
				for (PropertyColor color : wild.getColors())
				{
					if (combo.containsKey(color))
					{
						List<CardProperty> colorProps = combo.get(color);
						if (colorProps.size() < color.getMaxProperties() && (wild.isBase() || !colorProps.isEmpty()))
						{
							possibleColors.add(color);
						}
					}
				}
				if (!possibleColors.isEmpty())
				{
					combo.get(possibleColors.get(r.nextInt(possibleColors.size()))).add(wild);
				}
			}
			for (Map<PropertyColor, List<CardProperty>> prevCombo : possibleCombos)
			{
				// TODO: Don't add combo if it's exactly the same as an already created one
			}
			possibleCombos.add(combo);
		}
		return possibleCombos;
	}
	
	public List<Map<PropertyColor, Integer>> getPossibleCombos(List<CardProperty> cards)
	{
		List<CardProperty> wilds = new ArrayList<CardProperty>();
		Map<PropertyColor, Integer> baseProps = new HashMap<PropertyColor, Integer>();
		List<PropertyColor> validColors = new ArrayList<PropertyColor>();
		for (CardProperty card : cards)
		{
			if (card.isSingleColor())
			{
				PropertyColor color = card.getColor();
				if (!baseProps.containsKey(color))
				{
					baseProps.put(color, 1);
				}
				else
				{
					baseProps.put(color, baseProps.get(color) + 1);
				}
			}
			else
			{
				wilds.add(card);
			}
			// Add colors as valid if the card is a base
			if (card.isBase())
			{
				for (PropertyColor color : card.getColors())
				{
					if (!validColors.contains(color))
					{
						validColors.add(color);
					}
				}
			}
		}
		
		List<Map<PropertyColor, Integer>> possibleCombos = new ArrayList<Map<PropertyColor, Integer>>();
		
		Map<CardProperty, Integer> prog = new HashMap<CardProperty, Integer>();
		
		for (CardProperty card : wilds)
		{
			prog.put(card, 0);
		}
		
		while (true)
		{
			Map<PropertyColor, Integer> possibility = new HashMap<PropertyColor, Integer>(baseProps);
			
			boolean validCombo = true;
			// Iterate through all wild cards and their current color in iteration
			for (Entry<CardProperty, Integer> wild : prog.entrySet())
			{
				PropertyColor color = wild.getKey().getColors().get(wild.getValue());
				if (!validColors.contains(color))
				{
					validCombo = false;
					break;
				}
				possibility.put(color, possibility.containsKey(color) ? possibility.get(color) + 1 : 1);
			}
			if (validCombo)
			{
				possibleCombos.add(possibility);
			}
			
			// Find next color combination
			boolean complete = true;
			@SuppressWarnings("unchecked")
			Entry<CardProperty, Integer>[] sets = (Entry<CardProperty, Integer>[]) prog.entrySet().toArray(new Entry[prog.size()]);
			for (int i = 0 ; i < sets.length ; i++)
			{ // TODO: BUSTED: Need to account for non-base
				CardProperty prop = sets[i].getKey();
				int pos = sets[i].getValue();
				// If not already at last color for the wild card, otherwise continue to next card
				if (prop.getColors().size() != pos + 1)
				{
					// Add 1 to color index
					sets[i].setValue(pos + 1);
					// Set previous color indices to 0
					if (i >= 0)
					{
						for (i-- ; i >= 0 ; i--)
						{
							sets[i].setValue(0);
						}
					}
					complete = false;
					break;
				}
			}
			
			if (complete)
			{
				break;
			}
		}
		return possibleCombos;
	}
	
	public int getCardsNeededToWin(Player player)
	{
		List<Map<PropertyColor, Integer>> combos = getPossibleCombos(player);
		
		int leastCardsNeeded = 1000;
		for (Map<PropertyColor, Integer> combo : combos)
		{
			Map<PropertyColor, Integer> neededCards = new HashMap<PropertyColor, Integer>();
			for (Entry<PropertyColor, Integer> entry : combo.entrySet())
			{
				neededCards.put(entry.getKey(), Math.max(0, entry.getKey().getMaxProperties() - entry.getValue()));
			}
			// TODO: Check real win condition
			int win = 3;//getServer().getGameRules().getMonopoliesRequiredToWin();
			int[] comboLeastNeeded = new int[win];
			for (int i = 0 ; i < comboLeastNeeded.length ; i++)
			{
				comboLeastNeeded[i] = 2;
			}
			for (Entry<PropertyColor, Integer> entry : neededCards.entrySet())
			{
				for (int i = 0 ; i < comboLeastNeeded.length ; i++)
				{
					if (comboLeastNeeded[i] > entry.getValue())
					{
						comboLeastNeeded[i] = entry.getValue();
						break;
					}
				}
			}
			int neededCardsCount = 0;
			for (int i = 0 ; i < comboLeastNeeded.length ; i++)
			{
				neededCardsCount += comboLeastNeeded[i];
			}
			if (leastCardsNeeded > neededCardsCount)
			{
				leastCardsNeeded = neededCardsCount;
			}
		}
		
		getServer().broadcastMessage("COMBOS: " + combos.size());
		for (Map<PropertyColor, Integer> combo : combos)
		{
			System.out.println("--- Combo:");
			for (Entry<PropertyColor, Integer> e : combo.entrySet())
			{
				System.out.println(e.getKey().toString() + ": " + e.getValue());
			}
		}
		
		
		return leastCardsNeeded;
	}
	
	
	
	public abstract void doAction();
	
	public abstract double getWinThreat(Player player);
	
	public abstract double getRentThreat(Player player);
}
