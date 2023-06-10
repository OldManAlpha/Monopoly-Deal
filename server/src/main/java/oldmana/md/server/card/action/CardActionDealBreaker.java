package oldmana.md.server.card.action;

import oldmana.md.common.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.common.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardAction;
import oldmana.md.common.card.CardAnimationType;
import oldmana.md.server.card.play.PlayArguments;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;
import oldmana.md.server.history.UndoableAction;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.state.ActionStatePropertySetTargeted;
import oldmana.md.server.state.ActionStateTargetPlayerMonopoly;

import static oldmana.md.server.card.CardAttributes.*;

public class CardActionDealBreaker extends CardAction
{
	@Override
	public void doPlay(Player player, PlayArguments args)
	{
		getServer().getGameState().addActionState(new ActionStateTargetDealBreaker(player));
	}
	
	@Override
	public boolean canPlay(Player player)
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
	
	private static CardType<CardActionDealBreaker> createType()
	{
		CardType<CardActionDealBreaker> type = new CardType<CardActionDealBreaker>(CardActionDealBreaker.class,
				CardActionDealBreaker::new, "Deal Breaker");
		CardTemplate template = type.getDefaultTemplate();
		template.put(VALUE, 5);
		template.put(NAME, "Deal Breaker");
		template.putStrings(DISPLAY_NAME, "DEAL", "BREAKER");
		template.put(FONT_SIZE, 7);
		template.put(DISPLAY_OFFSET_Y, 2);
		template.putStrings(DESCRIPTION, "Steal an entire full property set from another player. Cannot be used to steal partial sets.");
		template.put(UNDOABLE, true);
		template.put(CLEARS_UNDOABLE_ACTIONS, false);
		template.put(PLAY_ANIMATION, CardAnimationType.IMPORTANT);
		type.setDefaultTemplate(template);
		return type;
	}
	
	public class ActionStateTargetDealBreaker extends ActionStateTargetPlayerMonopoly
	{
		public ActionStateTargetDealBreaker(Player player)
		{
			super(player);
			setStatus(player.getName() + " used Deal Breaker");
		}
		
		@Override
		public void onSetSelected(PropertySet set)
		{
			getActionOwner().clearUndoableActions();
			replaceState(new ActionStateStealMonopoly(getActionOwner(), set));
		}
		
		@Override
		public void onUndo(UndoableAction action)
		{
			if (action.hasCard(CardActionDealBreaker.this))
			{
				removeState();
			}
		}
	}
	
	public static class ActionStateStealMonopoly extends ActionStatePropertySetTargeted
	{
		public ActionStateStealMonopoly(Player player, PropertySet targetSet)
		{
			super(player, targetSet);
			setStatus(player.getName() + " used Deal Breaker against " + getTargetPlayer().getName());
		}
		
		@Override
		public void setAccepted(Player player, boolean accepted)
		{
			if (accepted)
			{
				getServer().broadcastPacket(new PacketActionStateBasic(-1, BasicActionState.DO_NOTHING, 0)); // Bandaid Fix For Glitched Cards
				if (getServer().getGameRules().doDealBreakersDiscardSets())
				{
					for (Card prop : getTargetSet().getCardsInReverse())
					{
						prop.transfer(getServer().getDiscardPile());
					}
				}
				else
				{
					getTargetSet().transferSet(getActionOwner());
				}
			}
			super.setAccepted(player, accepted);
		}
	}
}
