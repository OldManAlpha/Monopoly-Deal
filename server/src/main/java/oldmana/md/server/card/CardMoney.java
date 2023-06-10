package oldmana.md.server.card;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.net.packet.server.PacketCardData;
import oldmana.md.server.Player;

import static oldmana.md.server.card.CardAttributes.*;

public class CardMoney extends Card
{
	public static CardTemplate ONE_MIL;
	public static CardTemplate TWO_MIL;
	public static CardTemplate THREE_MIL;
	public static CardTemplate FOUR_MIL;
	public static CardTemplate FIVE_MIL;
	public static CardTemplate TEN_MIL;
	
	private static final String DEFAULT_NAME = "Money";
	
	@Override
	public void applyTemplate(CardTemplate template)
	{
		super.applyTemplate(template);
		if (template.getString(NAME).equals(DEFAULT_NAME))
		{
			// If it's the default name, dynamically set the name
			setName(getValue() + "M");
			setDisplayName(getValue() + "M");
		}
	}
	
	/**
	 * Money cards cannot be played, only banked.
	 * @return False
	 */
	@Override
	public boolean canPlay(Player player)
	{
		return false;
	}
	
	@Override
	public Packet getCardDataPacket()
	{
		return new PacketCardData(getID(), getName(), getValue(), 1,
				getDisplayName(), (byte) getFontSize(), (byte) getDisplayOffsetY(), getDescription().getID(),
				getOuterColor().getRGB(), getInnerColor().getRGB());
	}
	
	@Override
	public String toString()
	{
		return "Money (" + getValue() + "M)";
	}
	
	private static CardType<CardMoney> createType()
	{
		CardType<CardMoney> type = new CardType<CardMoney>(CardMoney.class, CardMoney::new, "Money");
		type.addExemptReduction("value");
		CardTemplate dt = type.getDefaultTemplate();
		dt.put(VALUE, 1);
		dt.put(NAME, DEFAULT_NAME);
		dt.putStrings(DESCRIPTION, "Money can be banked to protect your properties from rent.");
		dt.put(UNDOABLE, true);
		dt.put(CLEARS_UNDOABLE_ACTIONS, false);
		type.setDefaultTemplate(dt);
		
		ONE_MIL = new CardTemplate(dt);
		ONE_MIL.put(VALUE, 1);
		type.addTemplate(ONE_MIL, "1M");
		
		TWO_MIL = new CardTemplate(dt);
		TWO_MIL.put(VALUE, 2);
		type.addTemplate(TWO_MIL, "2M");
		
		THREE_MIL = new CardTemplate(dt);
		THREE_MIL.put(VALUE, 3);
		type.addTemplate(THREE_MIL, "3M");
		
		FOUR_MIL = new CardTemplate(dt);
		FOUR_MIL.put(VALUE, 4);
		type.addTemplate(FOUR_MIL, "4M");
		
		FIVE_MIL = new CardTemplate(dt);
		FIVE_MIL.put(VALUE, 5);
		type.addTemplate(FIVE_MIL, "5M");
		
		TEN_MIL = new CardTemplate(dt);
		TEN_MIL.put(VALUE, 10);
		type.addTemplate(TEN_MIL, "10M");
		
		return type;
	}
	
	public static CardMoney create(int value)
	{
		switch (value)
		{
			case 1: return ONE_MIL.createCard();
			case 2: return TWO_MIL.createCard();
			case 3: return THREE_MIL.createCard();
			case 4: return FOUR_MIL.createCard();
			case 5: return FIVE_MIL.createCard();
			case 10: return TEN_MIL.createCard();
			default: return new CardTemplate(CardType.MONEY.getDefaultTemplate()).put("value", value).createCard();
		}
	}
}
