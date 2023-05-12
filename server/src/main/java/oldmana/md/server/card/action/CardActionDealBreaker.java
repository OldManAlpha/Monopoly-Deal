package oldmana.md.server.card.action;

import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardAnimationType;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.state.ActionStatePropertySetTargeted;
import oldmana.md.server.state.ActionStateTargetPlayerMonopoly;

public class CardActionDealBreaker extends CardAction
{
	@Override
	public void playCard(Player player)
	{
		getServer().getGameState().addActionState(new ActionStateTargetDealBreaker(player));
	}
	
	@Override
	public boolean canPlayCard(Player player)
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
	
	@Override
	public CardAnimationType getPlayAnimation()
	{
		return CardAnimationType.IMPORTANT;
	}
	
	private static CardType<CardActionDealBreaker> createType()
	{
		CardType<CardActionDealBreaker> type = new CardType<CardActionDealBreaker>(CardActionDealBreaker.class,
				CardActionDealBreaker::new, "Deal Breaker");
		CardTemplate template = type.getDefaultTemplate();
		template.put("value", 5);
		template.put("name", "Deal Breaker");
		template.putStrings("displayName", "DEAL", "BREAKER");
		template.put("fontSize", 7);
		template.put("displayOffsetY", 2);
		template.putStrings("description", "Steal an entire full property set from another player. Cannot be used to steal partial sets.");
		template.put("revocable", true);
		template.put("clearsRevocableCards", false);
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
			getActionOwner().clearRevocableCards();
			replaceState(new ActionStateStealMonopoly(getActionOwner(), set));
		}
		
		@Override
		public void onCardUndo(Card card)
		{
			if (card == CardActionDealBreaker.this)
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
