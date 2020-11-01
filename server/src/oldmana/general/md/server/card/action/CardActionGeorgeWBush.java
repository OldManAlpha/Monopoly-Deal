package oldmana.general.md.server.card.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.Card;
import oldmana.general.md.server.card.CardAction;
import oldmana.general.md.server.card.collection.PropertySet;
import oldmana.general.md.server.card.property.CardPropertySaudiArabia;

public class CardActionGeorgeWBush extends CardAction
{
	public CardActionGeorgeWBush()
	{
		super(3, "George W Bush");
		setDisplayName("GEORGE", "W BUSH");
		setFontSize(7);
		setDisplayOffsetY(2);
		setRevocable(false);
		setMarksPreviousUnrevocable(true);
	}
	
	@Override
	public void playCard(Player player)
	{
		List<Card> allProperties = new ArrayList<Card>();
		for (Player p : getServer().getPlayers())
		{
			boolean isTarget = true;
			SetLoop:
			for (PropertySet set : p.getPropertySets())
			{
				for (Card card : set.getCards())
				{
					if (card instanceof CardPropertySaudiArabia)
					{
						isTarget = false;
						break SetLoop;
					}
				}
			}
			if (isTarget)
			{
				for (PropertySet set : p.getPropertySets())
				{
					for (Card card : set.getCards())
					{
						allProperties.add(card);
					}
				}
			}
		}
		Random r = new Random();
		
		int propCount = allProperties.size();
		for (int i = 0 ; i < Math.min(2, propCount) ; i++)
		{
			Card card = allProperties.remove(r.nextInt(allProperties.size()));
			card.transfer(getServer().getDiscardPile(), -1, 0.8);
		}
	}
}
