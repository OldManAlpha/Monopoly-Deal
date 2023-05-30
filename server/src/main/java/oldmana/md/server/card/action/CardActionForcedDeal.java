package oldmana.md.server.card.action;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.net.packet.server.actionstate.PacketActionStatePropertiesSelected;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.card.CardType;
import oldmana.md.server.state.ActionState;
import oldmana.md.server.state.ActionStateTargetSelfPlayerProperty;

public class CardActionForcedDeal extends CardAction
{
	@Override
	public void playCard(Player player)
	{
		getServer().getGameState().addActionState(new ActionStateTargetForcedDeal(player));
	}
	
	@Override
	public boolean canPlayCard(Player player)
	{
		boolean ownerHasProp = false;
		boolean otherHasProp = false;
		
		for (PropertySet set : player.getPropertySets())
		{
			if (set.hasBase())
			{
				ownerHasProp = true;
				break;
			}
		}
		
		for (Player other : getServer().getPlayersExcluding(player))
		{
			for (PropertySet set : other.getPropertySets())
			{
				if (!set.isMonopoly() && set.hasBase())
				{
					otherHasProp = true;
					break;
				}
			}
		}
		return ownerHasProp && otherHasProp;
	}
	
	private static CardType<CardActionForcedDeal> createType()
	{
		CardType<CardActionForcedDeal> type = new CardType<CardActionForcedDeal>(CardActionForcedDeal.class,
				CardActionForcedDeal::new, "Forced Deal");
		CardTemplate template = type.getDefaultTemplate();
		template.put("value", 3);
		template.put("name", "Forced Deal");
		template.putStrings("displayName", "FORCED", "DEAL");
		template.put("fontSize", 8);
		template.put("displayOffsetY", 2);
		template.putStrings("description", "Trade one of your properties for one of another player's properties that is " +
				"not part of a full set. 10-Color property wild cards cannot be stolen with this card.");
		template.put("revocable", true);
		template.put("clearsRevocableCards", false);
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
			getActionOwner().clearRevocableCards();
			replaceState(new ActionStateTradeProperties(self, other));
		}
		
		@Override
		public void onCardUndo(Card card)
		{
			if (card == CardActionForcedDeal.this)
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
