package oldmana.md.server.card.action;

import oldmana.md.common.playerui.CardButtonType;
import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.common.card.CardAnimationType;
import oldmana.md.server.card.CardPlayStage;
import oldmana.md.server.card.play.argument.PlayerArgument;
import oldmana.md.server.card.play.PlayArguments;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.control.CardButton;
import oldmana.md.server.card.control.CardControls;
import oldmana.md.server.card.CardType;

import static oldmana.md.server.card.CardAttributes.*;

public class CardActionJustSayNo extends CardAction
{
	@Override
	protected CardControls createControls()
	{
		CardControls controls = super.createControls();
		CardButton play = controls.getButtonByText("Play");
		play.setType(CardButtonType.ACTION_COUNTER);
		play.setListener((player, card, data) ->
				play(PlayArguments.ofPlayer(getServer().getPlayerByID(data))));
		return controls;
	}
	
	@Override
	public void doPlay(Player player, PlayArguments args)
	{
		if (!args.hasArgument(PlayerArgument.class))
		{
			System.out.println("Failed to play " + getName() + ", player argument not present.");
			return;
		}
		Player target = args.getArgument(PlayerArgument.class).getPlayer();
		getServer().getGameState().getActionState().refuse(player, target);
	}
	
	@Override
	public boolean canPlay(Player player)
	{
		return getServer().getGameState().getActionState().canRefuseAny(player);
	}
	
	@Override
	public boolean canPlayNow()
	{
		return getServer().getGameState().getActionState().canRefuseAny(getOwner());
	}
	
	private static CardType<CardActionJustSayNo> createType()
	{
		CardType<CardActionJustSayNo> type = new CardType<CardActionJustSayNo>(CardActionJustSayNo.class,
				CardActionJustSayNo::new, "Just Say No!", "JSN");
		CardTemplate template = type.getDefaultTemplate();
		template.put(VALUE, 4);
		template.put(NAME, "Just Say No!");
		template.putStrings(DISPLAY_NAME, "JUST", "SAY NO!");
		template.put(FONT_SIZE, 8);
		template.put(DISPLAY_OFFSET_Y, 1);
		template.putStrings(DESCRIPTION, "Use to stop an action played against you. Can be played against another " +
				"Just Say No to cancel it.");
		template.put(UNDOABLE, false);
		template.put(CLEARS_UNDOABLE_ACTIONS, false);
		template.put(PLAY_ANIMATION, CardAnimationType.IMPORTANT);
		template.put(MOVE_COST, 0);
		template.put(MOVE_STAGE, CardPlayStage.BEFORE_PLAY);
		type.setDefaultTemplate(template);
		return type;
	}
}
