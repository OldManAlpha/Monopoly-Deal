package oldmana.md.server.card.action;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.common.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.common.net.packet.server.actionstate.PacketActionStatePropertiesSelected;
import oldmana.md.server.ChatColor;
import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.play.PlayArguments;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.history.UndoableAction;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.card.CardType;
import oldmana.md.server.state.ActionState;
import oldmana.md.server.state.ActionStateTargetSelfPlayerProperty;

import static oldmana.md.server.card.CardAttributes.*;

public class CardActionForcedDeal extends CardAction
{
	@Override
	public void doPlay(Player player, PlayArguments args)
	{
		getServer().getGameState().addActionState(new ActionStateTargetForcedDeal(player));
	}
	
	@Override
	public boolean canPlay(Player player)
	{
		boolean ownerHasProp = false;
		
		for (PropertySet set : player.getPropertySets())
		{
			if (set.hasBase())
			{
				ownerHasProp = true;
				break;
			}
		}
		if (!ownerHasProp)
		{
			return false;
		}
		
		for (Player other : getServer().getPlayersExcluding(player))
		{
			for (PropertySet set : other.getPropertySets())
			{
				if (!set.isMonopoly() && set.hasStealable())
				{
					return true;
				}
			}
		}
		return false;
	}
	
	private static CardType<CardActionForcedDeal> createType()
	{
		CardType<CardActionForcedDeal> type = new CardType<CardActionForcedDeal>(CardActionForcedDeal.class,
				CardActionForcedDeal::new, "Forced Deal");
		CardTemplate template = type.getDefaultTemplate();
		template.put(VALUE, 3);
		template.put(NAME, "Forced Deal");
		template.putStrings(DISPLAY_NAME, "FORCED", "DEAL");
		template.put(FONT_SIZE, 8);
		template.put(DISPLAY_OFFSET_Y, 2);
		template.putStrings(DESCRIPTION, "Trade one of your properties for one of another player's properties that is " +
				"not part of a full set. This card might not be able to steal some properties.");
		template.put(UNDOABLE, true);
		template.put(CLEARS_UNDOABLE_ACTIONS, false);
		type.setDefaultTemplate(template);
		return type;
	}
	
	public class ActionStateTargetForcedDeal extends ActionStateTargetSelfPlayerProperty
	{
		public ActionStateTargetForcedDeal(Player player)
		{
			super(player);
			setStatus(player.getName() + " used Forced Deal");
		}
		
		@Override
		public void onCardsSelected(CardProperty self, CardProperty other)
		{
			if (!other.isStealable())
			{
				getActionOwner().sendMessage(ChatColor.PREFIX_ALERT + "That card is not stealable!");
				getActionOwner().resendActionState();
				return;
			}
			getActionOwner().clearUndoableActions();
			replaceState(new ActionStateTradeProperties(self, other));
		}
		
		@Override
		public void onUndo(UndoableAction action)
		{
			if (action.hasCard(CardActionForcedDeal.this))
			{
				removeState();
			}
		}
	}
	
	public static class ActionStateTradeProperties extends ActionState
	{
		private CardProperty ownerCard;
		private CardProperty targetCard;
		
		public ActionStateTradeProperties(CardProperty ownerCard, CardProperty targetCard)
		{
			super(ownerCard.getOwner(), targetCard.getOwner());
			this.ownerCard = ownerCard;
			this.targetCard = targetCard;
			setStatus(getActionOwner().getName() + " used Forced Deal against " + getTargetPlayer().getName());
		}
		
		@Override
		public void setAccepted(Player player, boolean accepted)
		{
			if (accepted)
			{
				getServer().broadcastPacket(new PacketActionStateBasic(-1, BasicActionState.DO_NOTHING, 0)); // Bandaid Fix For Glitched Cards
				PropertySet set = (PropertySet) ownerCard.getOwningCollection();
				getActionOwner().safelyGrantProperty(targetCard);
				player.safelyGrantProperty(ownerCard);
				set.checkLegality();
			}
			super.setAccepted(player, accepted);
		}
		
		@Override
		public Packet constructPacket()
		{
			return new PacketActionStatePropertiesSelected(getActionOwner().getID(), getTargetPlayer().getID(),
					new int[] {ownerCard.getID(), targetCard.getID()});
		}
	}
}
