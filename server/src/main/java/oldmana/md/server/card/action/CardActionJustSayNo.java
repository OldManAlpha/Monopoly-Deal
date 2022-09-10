package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardAnimationType;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.control.CardButton;
import oldmana.md.server.card.control.CardButton.CardButtonType;
import oldmana.md.server.card.control.CardControls;
import oldmana.md.server.card.CardType;
import oldmana.md.server.state.ActionState;

public class CardActionJustSayNo extends CardAction
{
	public void playCard(Player player, Player target)
	{
		ActionState state = getServer().getGameState().getActionState();
		if (state.getActionOwner() == player && state.isTarget(target) && state.getActionTarget(target).isRefused())
		{
			state.setRefused(target, false);
			transfer(getServer().getDiscardPile(), -1, CardAnimationType.IMPORTANT);
		}
		else if (state.isTarget(player) && !state.getActionTarget(player).isRefused())
		{
			state.setRefused(player, true);
			transfer(getServer().getDiscardPile(), -1, CardAnimationType.IMPORTANT);
		}
		player.checkEmptyHand();
		if (state.isFinished())
		{
			getServer().getGameState().nextNaturalActionState();
		}
	}
	
	@Override
	public boolean canPlayCard(Player player)
	{
		return false;
	}
	
	@Override
	public CardControls createControls()
	{
		CardControls controls = super.createControls();
		CardButton play = new CardButton("Play", CardButton.TOP, CardButtonType.ACTION_COUNTER);
		play.setCondition((player, card) -> getServer().getGameState().getActionState().canRefuseAny(player));
		play.setListener((player, card, data) ->
		{
			Player target = getServer().getPlayerByID(data);
			ActionState state = getServer().getGameState().getActionState();
			if (state.canRefuse(player, target))
			{
				((CardActionJustSayNo) card).playCard(player, target);
			}
		});
		controls.addButton(play);
		
		return controls;
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
