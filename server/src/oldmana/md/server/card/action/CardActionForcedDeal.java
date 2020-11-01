package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.state.ActionStateTargetSelfPlayerProperty;

public class CardActionForcedDeal extends CardAction
{
	public CardActionForcedDeal()
	{
		super(3, "Forced Deal");
		setDisplayName("FORCED", "DEAL");
		setFontSize(8);
		setDisplayOffsetY(2);
	}
	
	@Override
	public void playCard(Player player)
	{
		getServer().getGameState().setCurrentActionState(new ActionStateTargetSelfPlayerProperty(player, this));
	}
	
	@Override
	public boolean canPlayCard(Player player)
	{
		boolean ownerHasProp = false;
		boolean otherHasProp = false;
		
		for (PropertySet set : player.getPropertySets())
		{
			if (!set.isMonopoly() && set.hasBase())
			{
				ownerHasProp = true;
				break;
			}
		}
		
		for (Player other : getServer().getPlayersExcluding(player))
		{
			for (PropertySet set : other.getPropertySets())
			{
				if (!set.isMonopoly() && set.hasBase())
				{
					otherHasProp = true;
					break;
				}
			}
		}
		return ownerHasProp && otherHasProp;
	}
}
