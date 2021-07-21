package oldmana.md.client.card.collection;

import java.util.List;

import oldmana.md.client.card.Card;
import oldmana.md.client.gui.component.collection.MDDiscardPile;

public class DiscardPile extends CardCollection
{
	public DiscardPile(int id, List<Card> cards)
	{
		super(id, null);
		for (Card card : cards)
		{
			addCard(card);
		}
	}
	
	@Override
	public void addCardAtIndex(Card card, int index)
	{
		super.addCardAtIndex(card, index);
		if (getUI() != null)
		{
			((MDDiscardPile) getUI()).cardAdded();
		}
	}
	
	@Override
	public void removeCard(Card card)
	{
		super.removeCard(card);
		if (getUI() != null)
		{
			((MDDiscardPile) getUI()).cardRemoved();
		}
	}
}
