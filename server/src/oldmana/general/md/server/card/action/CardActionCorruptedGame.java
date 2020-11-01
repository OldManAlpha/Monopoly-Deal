package oldmana.general.md.server.card.action;

import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.CardSpecial;

public class CardActionCorruptedGame extends CardSpecial
{
	public CardActionCorruptedGame()
	{
		super(10, "Corrupted Game");
		setDisplayName("CORRUPTED", "GAME");
		setFontSize(6);
		setDisplayOffsetY(3);
		setRevocable(false);
		setMarksPreviousUnrevocable(false);
	}
	
	public void playCard(Player player)
	{
		
	}
}
