package oldmana.general.md.client.state;

import oldmana.general.md.client.Player;
import oldmana.general.md.client.card.CardProperty;

public class ActionStatePropertiesSelected extends ActionState
{
	private CardProperty[] cards;
	
	public ActionStatePropertiesSelected(Player player, Player target, CardProperty[] cards)
	{
		super(player, target);
		this.cards = cards;
	}
	
	
}
