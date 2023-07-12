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
import oldmana.md.server.state.ActionStateTargetPlayerProperty;

import static oldmana.md.server.card.CardAttributes.*;

public class CardActionSlyDeal extends CardAction
{
	@Override
	public void doPlay(Player player, PlayArguments args)
	{
		getServer().getGameState().addActionState(new ActionStateTargetSlyDeal(player));
	}
	
	@Override
	public boolean canPlay(Player player)
	{
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
	
	private static CardType<CardActionSlyDeal> createType()
	{
		CardType<CardActionSlyDeal> type = new CardType<CardActionSlyDeal>(CardActionSlyDeal.class,
				CardActionSlyDeal::new, "Sly Deal");
		CardTemplate template = type.getDefaultTemplate();
		template.put(VALUE, 3);
		template.put(NAME, "Sly Deal");
		template.putStrings(DISPLAY_NAME, "SLY", "DEAL");
		template.put(FONT_SIZE, 9);
		template.put(DISPLAY_OFFSET_Y, 2);
		template.putStrings(DESCRIPTION, "Steal a property from another player that is not part of a full set. " +
				"This card might not be able to steal some properties.");
		template.put(UNDOABLE, true);
		template.put(CLEARS_UNDOABLE_ACTIONS, false);
		type.setDefaultTemplate(template);
		return type;
	}
	
	public class ActionStateTargetSlyDeal extends ActionStateTargetPlayerProperty
	{
		public ActionStateTargetSlyDeal(Player player)
		{
			super(player);
			setStatus(player.getName() + " used Sly Deal");
		}
		
		@Override
		public void onCardSelected(CardProperty card)
		{
			if (!card.isStealable())
			{
				getActionOwner().sendMessage(ChatColor.PREFIX_ALERT + "That card is not stealable!");
				getActionOwner().resendActionState();
				return;
			}
			getActionOwner().clearUndoableActions();
			replaceState(new ActionStateStealProperty(getActionOwner(), card));
		}
		
		@Override
		public void onUndo(UndoableAction action)
		{
			if (action.hasCard(CardActionSlyDeal.this))
			{
				removeState();
			}
		}
	}
	
	public static class ActionStateStealProperty extends ActionState
	{
		private CardProperty targetCard;
		
		public ActionStateStealProperty(Player player, CardProperty targetCard)
		{
			super(player, targetCard.getOwner());
			this.targetCard = targetCard;
			setStatus(player.getName() + " used Sly Deal against " + getTargetPlayer().getName());
		}
		
		@Override
		public void setAccepted(Player player, boolean accepted)
		{
			if (accepted)
			{
				getServer().broadcastPacket(new PacketActionStateBasic(-1, BasicActionState.DO_NOTHING, 0)); // Bandaid Fix For Glitched Cards
				getActionOwner().safelyGrantProperty(targetCard);
			}
			super.setAccepted(player, accepted);
		}
		
		@Override
		public Packet constructPacket()
		{
			return new PacketActionStatePropertiesSelected(getActionOwner().getID(), getTargetPlayer().getID(), new int[] {targetCard.getID()});
		}
	}
}
