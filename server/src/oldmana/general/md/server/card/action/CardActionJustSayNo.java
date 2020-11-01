package oldmana.general.md.server.card.action;

import oldmana.general.md.server.card.CardSpecial;

public class CardActionJustSayNo extends CardSpecial
{
	public CardActionJustSayNo()
	{
		super(4, "JUST SAY NO!");
		setDisplayName("JUST", "SAY NO!");
		setDisplayOffsetY(1);
	}
	
	@Override
	public String toString()
	{
		return "CardActionJustSayNo (" + getValue() + "M)";
	}
}
