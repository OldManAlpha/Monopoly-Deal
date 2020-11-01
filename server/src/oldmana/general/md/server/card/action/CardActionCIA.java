package oldmana.general.md.server.card.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.Card;
import oldmana.general.md.server.card.CardAction;
import oldmana.general.md.server.card.collection.Deck;

public class CardActionCIA extends CardAction
{
	public CardActionCIA()
	{
		super(3, "CIA");
		setDisplayName("CIA");
		setFontSize(10);
		setDisplayOffsetY(1);
		setRevocable(false);
		setMarksPreviousUnrevocable(false);
	}
	
	@Override
	public void playCard(Player player)
	{
		Deck deck = getServer().getDeck();
		List<Card> peek = new ArrayList<Card>(4);
		for (int i = 0 ; i < 4 ; i++)
		{
			if (deck.isEmpty())
			{
				break;
			}
			Card card = deck.getCardAt(0);
			card.transfer(player.getHand(), 0, i < 3 ? 0.8 : 0.5);
			peek.add(card);
		}
		Collections.reverse(peek);
		for (int i = 0 ; i < peek.size() ; i++)
		{
			Card card = peek.get(i);
			card.transfer(deck, 0, i == 0 ? 0.5 : 0.8);
		}
	}
}
