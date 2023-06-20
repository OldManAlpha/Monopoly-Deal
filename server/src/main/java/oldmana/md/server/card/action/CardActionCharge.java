package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.play.PlayArguments;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;
import oldmana.md.server.history.UndoableAction;
import oldmana.md.server.state.ActionStateRent;
import oldmana.md.server.state.ActionStateTargetPlayer;

import static oldmana.md.server.card.CardAttributes.*;

/**
 * Charge cards are used a foundation for Birthday and Debt Collector cards. This card type can be used to easily create
 * custom charge cards in custom decks.
 */
public class CardActionCharge extends CardAction
{
	public static final String CHARGES_ALL = "chargesAll";
	public static final String CHARGE = "charge";
	
	private boolean chargesAll;
	private int charge;
	
	@Override
	public void applyTemplate(CardTemplate template)
	{
		super.applyTemplate(template);
		chargesAll = template.getBoolean(CHARGES_ALL);
		charge = template.getInt(CHARGE);
	}
	
	@Override
	public void doPlay(Player player, PlayArguments args)
	{
		if (chargesAll || getServer().getPlayerCount() <= 2)
		{
			player.clearUndoableActions();
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
				CardActionCharge::new, false, "Charge");
		type.addExemptReduction(VALUE, false);
		type.addExemptReduction(CHARGES_ALL, false);
		type.addExemptReduction(CHARGE, false);
		CardTemplate template = type.getDefaultTemplate();
		template.put(VALUE, 1);
		template.put(UNDOABLE, true);
		template.put(CLEARS_UNDOABLE_ACTIONS, false);
		template.put(CHARGES_ALL, false);
		template.put(CHARGE, 1);
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
			getActionOwner().clearUndoableActions();
			replaceState(new ActionStateRent(getActionOwner(), player, getCharge()));
		}
		
		@Override
		public void onUndo(UndoableAction action)
		{
			if (action.hasCard(card))
			{
				removeState();
			}
		}
	}
}
