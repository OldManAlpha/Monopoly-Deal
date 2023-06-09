package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.play.PlayArguments;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;

import static oldmana.md.server.card.CardAttributes.*;

public class CardActionPassGo extends CardAction
{
	@Override
	public void doPlay(Player player, PlayArguments args)
	{
		player.drawCards(2);
	}
	
	private static CardType<CardActionPassGo> createType()
	{
		CardType<CardActionPassGo> type = new CardType<CardActionPassGo>(CardActionPassGo.class, CardActionPassGo::new,
				"Pass Go", "Go");
		CardTemplate template = type.getDefaultTemplate();
		template.put(VALUE, 1);
		template.put(NAME, "Pass Go");
		template.putStrings(DISPLAY_NAME, "PASS", "GO");
		template.put(FONT_SIZE, 9);
		template.put(DISPLAY_OFFSET_Y, 2);
		template.putStrings(DESCRIPTION, "Draw two cards.");
		template.put(UNDOABLE, false);
		template.put(CLEARS_UNDOABLE_ACTIONS, false);
		type.setDefaultTemplate(template);
		return type;
	}
}
