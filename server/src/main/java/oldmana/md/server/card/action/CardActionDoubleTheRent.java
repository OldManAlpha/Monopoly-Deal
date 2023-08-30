package oldmana.md.server.card.action;

import oldmana.md.common.card.CardAnimationType;
import oldmana.md.server.ChatColor;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.play.argument.CardArgument;
import oldmana.md.server.card.play.PlayArguments;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;
import oldmana.md.server.card.action.CardActionRent.RentModifierCard;
import oldmana.md.server.card.play.argument.ConsumeModifierArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static oldmana.md.server.card.CardAttributes.*;

public class CardActionDoubleTheRent extends CardAction implements RentModifierCard
{
	@Override
	public double modifyRent(int baseRent, double currentRent)
	{
		return getServer().getGameRules().getDoubleRentPolicy().doubleRent(baseRent, currentRent);
	}
	
	@Override
	public void doPlay(Player player, PlayArguments args)
	{
		if (args.hasArgument(ConsumeModifierArgument.class))
		{
			return; // This is being consumed, so we're not performing the card logic
		}
		List<Card> selected = args.getArguments(CardArgument.class).stream()
				.map(CardArgument::getCard)
				.collect(Collectors.toList());
		selected.add(this);
		boolean canPlayMoreModifiers = getServer().getGameRules().isMultipleRentModifiersAllowed();
		int currentMoveCost = selected.stream()
				.mapToInt(Card::getMoveCost)
				.reduce(Integer::sum)
				.getAsInt();
		List<Card> options = new ArrayList<Card>();
		for (Card card : player.getHand())
		{
			if (selected.contains(card) || currentMoveCost + card.getMoveCost() > player.getMoves())
			{
				continue;
			}
			
			if ((card instanceof CardActionRent || (canPlayMoreModifiers && card instanceof RentModifierCard)) && card.canPlay(player))
			{
				options.add(card);
			}
		}
		if (!options.isEmpty())
		{
			player.promptCardCombo(selected, options);
		}
		else
		{
			player.sendMessage(ChatColor.PREFIX_ALERT + "You don't have enough moves to play that with another card.");
			player.resendActionState();
		}
	}
	
	@Override
	public boolean canPlay(Player player)
	{
		for (Card card : player.getHand())
		{
			if (card instanceof CardActionRent && card.canPlayNow() &&
					card.getMoveCost() + getMoveCost() <= player.getMoves())
			{
				return true;
			}
		}
		return false;
	}
	
	private static CardType<CardActionDoubleTheRent> createType()
	{
		CardType<CardActionDoubleTheRent> type = new CardType<CardActionDoubleTheRent>(CardActionDoubleTheRent.class,
				CardActionDoubleTheRent::new, "Double The Rent!", "Double Rent");
		CardTemplate template = type.getDefaultTemplate();
		template.put(VALUE, 1);
		template.put(NAME, "Double The Rent!");
		template.putStrings(DISPLAY_NAME, "DOUBLE", "THE RENT!");
		template.put(FONT_SIZE, 7);
		template.put(DISPLAY_OFFSET_Y, 2);
		template.putStrings(DESCRIPTION, "Can be played with a Rent card to double the charge against players. Counts as a move.");
		template.put(UNDOABLE, false);
		template.put(CLEARS_UNDOABLE_ACTIONS, false);
		template.put(PLAY_ANIMATION, CardAnimationType.IMPORTANT);
		type.setDefaultTemplate(template);
		return type;
	}
}
