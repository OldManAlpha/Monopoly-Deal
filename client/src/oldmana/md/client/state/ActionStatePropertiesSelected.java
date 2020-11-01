package oldmana.md.client.state;

import oldmana.md.client.Player;
import oldmana.md.client.card.CardProperty;

public class ActionStatePropertiesSelected extends ActionState
{
	private CardProperty[] cards;
	
	public ActionStatePropertiesSelected(Player player, Player target, CardProperty[] cards)
	{
		super(player, target);
		this.cards = cards;
	}
	
	
}
