package oldmana.md.server.card.action;

import oldmana.md.server.card.CardAction;

public class CardActionDoubleTheRent extends CardAction
{
	
	public CardActionDoubleTheRent()
	{
		super(1, "Double The Rent");
		setDisplayName("DOUBLE", "THE RENT!");
		setFontSize(7);
		setDisplayOffsetY(2);
		setRevocable(false);
		setMarksPreviousUnrevocable(true);
	}
	
	@Override
	public CardType getType()
	{
		return CardType.DOUBLE_THE_RENT;
	}
}
