package oldmana.md.client.card.collection;

import oldmana.md.client.Player;
import oldmana.md.client.gui.component.MDInvisibleHand;
import oldmana.md.client.gui.component.large.MDHand;

public class Hand extends CardCollection
{
	public Hand(int id, Player owner)
	{
		super(id, owner);
		//setUI(new MDHand(this));
	}
	
	public Hand(int id, Player owner, int cardCount)
	{
		super(id, owner, cardCount);
		setUI(new MDInvisibleHand(this));
	}
	
	public boolean hasTooManyCards()
	{
		return getCardCount() > 7;
	}
}
