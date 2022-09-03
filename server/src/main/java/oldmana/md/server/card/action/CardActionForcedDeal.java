package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.card.CardType;
import oldmana.md.server.state.ActionStateTargetForcedDeal;

public class CardActionForcedDeal extends CardAction
{
	@Override
	public void playCard(Player player)
	{
		getServer().getGameState().setActionState(new ActionStateTargetForcedDeal(player));
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
	
	private static CardType<CardActionForcedDeal> createType()
	{
		CardType<CardActionForcedDeal> type = new CardType<CardActionForcedDeal>(CardActionForcedDeal.class, "Forced Deal");
		CardTemplate template = type.getDefaultTemplate();
		template.put("value", 3);
		template.put("name", "Forced Deal");
		template.putStrings("displayName", "FORCED", "DEAL");
		template.put("fontSize", 8);
		template.put("displayOffsetY", 2);
		template.putStrings("description", "Trade one of your properties for one of another player's properties that is " +
				"not part of a full set. 10-Color property wild cards cannot be stolen with this card.");
		template.put("revocable", true);
		template.put("clearsRevocableCards", false);
		return type;
	}
}
