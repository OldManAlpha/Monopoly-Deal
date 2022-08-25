package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.card.type.CardType;
import oldmana.md.server.state.ActionStateTargetSlyDeal;

public class CardActionSlyDeal extends CardAction
{
	@Override
	public void playCard(Player player)
	{
		getServer().getGameState().setActionState(new ActionStateTargetSlyDeal(player));
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
	
	private static CardType<CardActionSlyDeal> createType()
	{
		CardType<CardActionSlyDeal> type = new CardType<CardActionSlyDeal>(CardActionSlyDeal.class, "Sly Deal");
		CardTemplate template = type.getDefaultTemplate();
		template.put("value", 3);
		template.put("name", "Sly Deal");
		template.putStrings("displayName", "SLY", "DEAL");
		template.put("fontSize", 9);
		template.put("displayOffsetY", 2);
		template.putStrings("description", "Steal a property from another player that is not part of a full set. " +
				"10-Color property wild cards cannot be stolen with this card.");
		template.put("revocable", false);
		template.put("clearsRevocableCards", true);
		return type;
	}
}
