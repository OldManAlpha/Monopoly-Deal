package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardAnimationType;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;
import oldmana.md.server.state.ActionStateTargetPlayerMonopoly;

public class CardActionDealBreaker extends CardAction
{
	@Override
	public void playCard(Player player)
	{
		getServer().getGameState().setActionState(new ActionStateTargetPlayerMonopoly(player));
	}
	
	@Override
	public boolean canPlayCard(Player player)
	{
		for (Player other : getServer().getPlayersExcluding(player))
		{
			if (other.getMonopolyCount() > 0)
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public CardAnimationType getPlayAnimation()
	{
		return CardAnimationType.IMPORTANT;
	}
	
	private static CardType<CardActionDealBreaker> createType()
	{
		CardType<CardActionDealBreaker> type = new CardType<CardActionDealBreaker>(CardActionDealBreaker.class, "Deal Breaker");
		CardTemplate template = type.getDefaultTemplate();
		template.put("value", 5);
		template.put("name", "Deal Breaker");
		template.putStrings("displayName", "DEAL", "BREAKER");
		template.put("fontSize", 7);
		template.put("displayOffsetY", 2);
		template.putStrings("description", "Steal an entire full property set from another player. Cannot be used to steal partial sets.");
		template.put("revocable", true);
		template.put("clearsRevocableCards", false);
		return type;
	}
}
