package oldmana.md.server.card;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardData;
import oldmana.md.server.card.control.CardButton;
import oldmana.md.server.card.control.CardControls;

public class CardMoney extends Card
{
	public static CardTemplate ONE_MIL;
	public static CardTemplate TWO_MIL;
	public static CardTemplate THREE_MIL;
	public static CardTemplate FOUR_MIL;
	public static CardTemplate FIVE_MIL;
	public static CardTemplate TEN_MIL;
	
	@Override
	public void applyTemplate(CardTemplate template)
	{
		super.applyTemplate(template);
		setName(getValue() + "M");
		setDisplayName(getValue() + "M");
	}
	
	@Override
	public CardControls createControls()
	{
		CardControls actions = super.createControls();
		
		CardButton bank = new CardButton("Bank", CardButton.BOTTOM);
		bank.setCondition((player, card) -> player.canPlayCards());
		bank.setListener((player, card, data) -> player.playCardBank(card));
		actions.addButton(bank);
		
		return actions;
	}
	
	@Override
	public Packet getCardDataPacket()
	{
		return new PacketCardData(getID(), getName(), getValue(), 1, isRevocable(), clearsRevocableCards(),
				getDisplayName(), (byte) getFontSize(), (byte) getDisplayOffsetY(), getDescription().getID());
	}
	
	@Override
	public String toString()
	{
		return "Money (" + getValue() + "M)";
	}
	
	private static CardType<CardMoney> createType()
	{
		CardType<CardMoney> type = new CardType<CardMoney>(CardMoney.class, "Money");
		type.addExemptReduction("value");
		CardTemplate dt = type.getDefaultTemplate();
		dt.put("value", 1);
		dt.put("name", "Money");
		dt.putStrings("description", "Money can be banked to protect your properties from rent.");
		dt.put("revocable", true);
		dt.put("clearsRevocableCards", false);
		
		ONE_MIL = new CardTemplate(dt);
		ONE_MIL.put("value", 1);
		type.addTemplate(ONE_MIL, "1M");
		
		TWO_MIL = new CardTemplate(dt);
		TWO_MIL.put("value", 2);
		type.addTemplate(TWO_MIL, "2M");
		
		THREE_MIL = new CardTemplate(dt);
		THREE_MIL.put("value", 3);
		type.addTemplate(THREE_MIL, "3M");
		
		FOUR_MIL = new CardTemplate(dt);
		FOUR_MIL.put("value", 4);
		type.addTemplate(FOUR_MIL, "4M");
		
		FIVE_MIL = new CardTemplate(dt);
		FIVE_MIL.put("value", 5);
		type.addTemplate(FIVE_MIL, "5M");
		
		TEN_MIL = new CardTemplate(dt);
		TEN_MIL.put("value", 10);
		type.addTemplate(TEN_MIL, "10M");
		
		return type;
	}
}
