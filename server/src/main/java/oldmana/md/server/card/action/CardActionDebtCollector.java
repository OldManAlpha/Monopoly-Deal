package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;
import oldmana.md.server.state.ActionStateRent;
import oldmana.md.server.state.ActionStateTargetDebtCollector;

public class CardActionDebtCollector extends CardAction
{
	@Override
	public void playCard(Player player)
	{
		if (getServer().getPlayers().size() == 2)
		{
			player.clearRevocableCards();
			getServer().getGameState().setActionState(new ActionStateRent(player, getServer().getPlayersExcluding(player).get(0), 5));
		}
		else
		{
			getServer().getGameState().setActionState(new ActionStateTargetDebtCollector(player));
		}
	}
	
	private static CardType<CardActionDebtCollector> createType()
	{
		CardType<CardActionDebtCollector> type = new CardType<CardActionDebtCollector>(CardActionDebtCollector.class, "Debt Collector");
		CardTemplate template = type.getDefaultTemplate();
		template.put("value", 3);
		template.put("name", "Debt Collector");
		template.putStrings("displayName", "DEBT", "COLLECTOR");
		template.put("fontSize", 6);
		template.put("displayOffsetY", 1);
		template.putStrings("description", "Select a player and charge 5M against them.");
		template.put("revocable", true);
		template.put("clearsRevocableCards", false);
		return type;
	}
}
