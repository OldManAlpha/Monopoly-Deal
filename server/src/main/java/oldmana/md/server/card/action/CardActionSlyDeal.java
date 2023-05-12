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
import oldmana.md.server.state.ActionStateTargetPlayerProperty;

public class CardActionSlyDeal extends CardAction
{
	@Override
	public void playCard(Player player)
	{
		getServer().getGameState().addActionState(new ActionStateTargetSlyDeal(player));
	}
	
	@Override
	public boolean canPlayCard(Player player)
	{
		for (Player other : getServer().getPlayersExcluding(player))
		{
			for (PropertySet set : other.getPropertySets())
			{
				if (!set.isMonopoly() && set.hasBase())
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
		template.put("value", 3);
		template.put("name", "Sly Deal");
		template.putStrings("displayName", "SLY", "DEAL");
		template.put("fontSize", 9);
		template.put("displayOffsetY", 2);
		template.putStrings("description", "Steal a property from another player that is not part of a full set. " +
				"10-Color property wild cards cannot be stolen with this card.");
		template.put("revocable", true);
		template.put("clearsRevocableCards", false);
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
			getActionOwner().clearRevocableCards();
			replaceState(new ActionStateStealProperty(getActionOwner(), card));
		}
		
		@Override
		public void onCardUndo(Card card)
		{
			if (card == CardActionSlyDeal.this)
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
