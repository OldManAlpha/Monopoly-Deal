package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardSpecial;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.type.CardType;
import oldmana.md.server.state.ActionState;

public class CardActionJustSayNo extends CardSpecial
{
	@Override
	public void playCard(Player player, int data)
	{
		Player target = getServer().getPlayerByID(data);
		ActionState state = getServer().getGameState().getActionState();
		if (state.getActionOwner() == player && state.isTarget(target) && state.getActionTarget(target).isRefused())
		{
			state.setRefused(target, false);
			transfer(getServer().getDiscardPile());
		}
		else if (state.isTarget(player) && !state.getActionTarget(player).isRefused())
		{
			state.setRefused(player, true);
			transfer(getServer().getDiscardPile());
		}
		player.checkEmptyHand();
		if (state.isFinished())
		{
			getServer().getGameState().nextNaturalActionState();
		}
	}
	
	@Override
	public CardTypeLegacy getTypeLegacy()
	{
		return CardTypeLegacy.ACTION_COUNTER;
	}
	
	@Override
	public String toString()
	{
		return "CardActionJustSayNo (" + getValue() + "M)";
	}
	
	private static CardType<CardActionJustSayNo> createType()
	{
		CardType<CardActionJustSayNo> type = new CardType<CardActionJustSayNo>(CardActionJustSayNo.class, "Just Say No!", "JSN");
		CardTemplate template = type.getDefaultTemplate();
		template.put("value", 4);
		template.put("name", "Just Say No!");
		template.putStrings("displayName", "JUST", "SAY NO!");
		template.put("fontSize", 8);
		template.put("displayOffsetY", 1);
		template.putStrings("description", "Use to stop an action played against you. Can be played against another " +
				"Just Say No to cancel it.");
		template.put("revocable", false);
		template.put("clearsRevocableCards", false);
		return type;
	}
}
