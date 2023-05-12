package oldmana.md.client;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.collection.Hand;

public class ThePlayer extends Player
{
	public ThePlayer(MDClient client, int id, String name)
	{
		super(client, id, name);
	}
	
	@Override
	public void setHand(Hand hand)
	{
		this.hand = hand;
		client.getTableScreen().setHand(hand);
	}
	
	@Override
	public boolean hasAllPropertiesInHand()
	{
		for (Card card : getHand().getCards())
		{
			if (!(card instanceof CardProperty))
			{
				return false;
			}
		}
		return true;
	}
}
