package oldmana.md.server.card;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardData;
import oldmana.md.server.Player;
import oldmana.md.server.card.control.CardControls;
import oldmana.md.server.card.control.CardButton;

public class CardAction extends Card
{
	public CardAction() {}
	
	public void playCard(Player player) {}
	
	public boolean canPlayCard(Player player)
	{
		return true;
	}
	
	@Override
	public CardControls createControls()
	{
		CardControls actions = super.createControls();
		
		CardButton play = new CardButton("Play", CardButton.TOP);
		play.setCondition((player, card) -> player.canPlayCards() && ((CardAction) card).canPlayCard(player));
		play.setListener((player, card, data) -> player.playCardAction((CardAction) card));
		actions.addButton(play);
		
		CardButton bank = new CardButton("Bank", CardButton.BOTTOM);
		bank.setCondition((player, card) -> player.canPlayCards());
		bank.setListener((player, card, data) -> player.playCardBank(card));
		actions.addButton(bank);
		
		return actions;
	}
	
	@Override
	public Packet getCardDataPacket()
	{
		return new PacketCardData(getID(), getName(), getValue(), 0, isRevocable(), clearsRevocableCards(),
				getDisplayName(), (byte) getFontSize(), (byte) getDisplayOffsetY(), getDescription().getID());
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " (" + getValue() + "M)";
	}
	
	private static CardType<CardAction> createType()
	{
		return new CardType<CardAction>(CardAction.class, "Action Card", false);
	}
}
