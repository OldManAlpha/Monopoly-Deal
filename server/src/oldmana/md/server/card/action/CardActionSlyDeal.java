package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.state.ActionStateTargetPlayerProperty;

public class CardActionSlyDeal extends CardAction
{
	public CardActionSlyDeal()
	{
		super(3, "Sly Deal");
		setDisplayName("SLY", "DEAL");
		setFontSize(9);
		setDisplayOffsetY(2);
	}
	
	@Override
	public void playCard(Player player)
	{
		getServer().getGameState().setCurrentActionState(new ActionStateTargetPlayerProperty(player));
	}
	
	@Override
	public boolean canPlayCard(Player player)
	{
		for (Player other : getServer().getPlayersExcluding(player))
		{
			for (PropertySet set : other.getPropertySets())
			{
				if (!set.isMonopoly() && set.hasBase())
				{
					return true;
				}
			}
		}
		return false;
	}
}
