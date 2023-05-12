package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;
import oldmana.md.server.state.ActionStateRent;

public class CardActionItsMyBirthday extends CardAction
{
	@Override
	public void playCard(Player player)
	{
		getServer().getGameState().addActionState(new ActionStateRent(player, getServer().getPlayersExcluding(player), 2));
	}
	
	private static CardType<CardActionItsMyBirthday> createType()
	{
		CardType<CardActionItsMyBirthday> type = new CardType<CardActionItsMyBirthday>(CardActionItsMyBirthday.class,
				CardActionItsMyBirthday::new, "It's My Birthday",
				"Birthday");
		CardTemplate template = type.getDefaultTemplate();
		template.put("value", 2);
		template.put("name", "It's My Birthday");
		template.putStrings("displayName", "IT'S MY", "BIRTHDAY");
		template.put("fontSize", 7);
		template.put("displayOffsetY", 0);
		template.putStrings("description", "Charge all other players 2M.");
		template.put("revocable", false);
		template.put("clearsRevocableCards", true);
		return type;
	}
}
