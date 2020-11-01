package oldmana.md.client.card.collection;

import oldmana.md.client.Player;
import oldmana.md.client.card.Card;
import oldmana.md.client.gui.component.MDBank;

public class Bank extends CardCollection
{
	public Bank(int id, Player owner)
	{
		super(id, owner);
		setUI(new MDBank(this));
	}
	
	public int getTotalValue()
	{
		int value = 0;
		for (Card card : getCards())
		{
			value += card.getValue();
		}
		return value;
	}
}
