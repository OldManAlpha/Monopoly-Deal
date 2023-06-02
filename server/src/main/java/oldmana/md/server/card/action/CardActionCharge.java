package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;
import oldmana.md.server.state.ActionStateRent;
import oldmana.md.server.state.ActionStateTargetPlayer;

/**
 * Charge cards are used a foundation for Birthday and Debt Collector cards. This card type can be used to easily create
 * custom charge cards in custom decks.
 */
public class CardActionCharge extends CardAction
{
	private boolean chargesAll;
	private int charge;
	
	@Override
	public void applyTemplate(CardTemplate template)
	{
		super.applyTemplate(template);
		chargesAll = template.getBoolean("chargesAll");
		charge = template.getInt("charge");
	}
	
	@Override
	public void playCard(Player player)
	{
		if (chargesAll || getServer().getPlayerCount() <= 2)
		{
			player.clearRevocableCards();
			getServer().getGameState().addActionState(new ActionStateRent(player, getServer().getPlayersExcluding(player), charge));
		}
		else
		{
			getServer().getGameState().addActionState(new ActionStateTargetCharge(player, this));
		}
	}
	
	public boolean doesChargeAll()
	{
		return chargesAll;
	}
	
	public int getCharge()
	{
		return charge;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " (Charges All: " + chargesAll + ") (Charge: " + charge + "M) (" + getValue() + "M)";
	}
	
	private static CardType<CardActionCharge> createType()
	{
		CardType<CardActionCharge> type = new CardType<CardActionCharge>(CardActionCharge.class,
				CardActionCharge::new, "Charge");
		type.addExemptReduction("value", false);
		type.addExemptReduction("chargesAll", false);
		type.addExemptReduction("charge", false);
		CardTemplate template = type.getDefaultTemplate();
		template.put("value", 1);
		template.put("revocable", true);
		template.put("clearsRevocableCards", false);
		template.put("chargesAll", false);
		template.put("charge", 1);
		type.setDefaultTemplate(template);
		return type;
	}
	
	public static class ActionStateTargetCharge extends ActionStateTargetPlayer
	{
		private CardActionCharge card;
		
		public ActionStateTargetCharge(Player player, CardActionCharge card)
		{
			super(player);
			this.card = card;
			setStatus(player.getName() + " used " + card.getName());
		}
		
		public CardActionCharge getCard()
		{
			return card;
		}
		
		public int getCharge()
		{
			return card.getCharge();
		}
		
		@Override
		public void playerSelected(Player player)
		{
			getActionOwner().clearRevocableCards();
			replaceState(new ActionStateRent(getActionOwner(), player, getCharge()));
		}
		
		@Override
		public void onCardUndo(Card card)
		{
			if (card == this.card)
			{
				removeState();
			}
		}
	}
}
