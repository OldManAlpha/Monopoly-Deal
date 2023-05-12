package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;
import oldmana.md.server.state.ActionStateRent;
import oldmana.md.server.state.ActionStateTargetPlayer;

public class CardActionDebtCollector extends CardAction
{
	@Override
	public void playCard(Player player)
	{
		if (getServer().getPlayerCount() == 2)
		{
			player.clearRevocableCards();
			getServer().getGameState().addActionState(new ActionStateRent(player, getServer().getPlayersExcluding(player).get(0), 5));
		}
		else
		{
			getServer().getGameState().addActionState(new ActionStateTargetDebtCollector(player));
		}
	}
	
	private static CardType<CardActionDebtCollector> createType()
	{
		CardType<CardActionDebtCollector> type = new CardType<CardActionDebtCollector>(CardActionDebtCollector.class,
				CardActionDebtCollector::new, "Debt Collector");
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
	
	public class ActionStateTargetDebtCollector extends ActionStateTargetPlayer
	{
		public ActionStateTargetDebtCollector(Player player)
		{
			super(player);
			setStatus(player.getName() + " used Debt Collector");
		}
		
		@Override
		public void playerSelected(Player player)
		{
			getActionOwner().clearRevocableCards();
			replaceState(new ActionStateRent(getActionOwner(), player, 5));
		}
		
		@Override
		public void onCardUndo(Card card)
		{
			if (card == CardActionDebtCollector.this)
			{
				removeState();
			}
		}
	}
}
