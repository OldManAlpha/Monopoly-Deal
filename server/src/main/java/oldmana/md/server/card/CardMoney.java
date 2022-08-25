package oldmana.md.server.card;

import oldmana.md.server.card.type.CardType;

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
