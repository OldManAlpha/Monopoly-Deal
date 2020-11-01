package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardSpecial;

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
