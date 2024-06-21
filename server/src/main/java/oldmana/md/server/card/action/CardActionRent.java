package oldmana.md.server.card.action;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.net.packet.server.PacketCardActionRentData;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.ModifierCard;
import oldmana.md.server.card.play.argument.CardArgument;
import oldmana.md.server.card.play.argument.ConsumeModifierArgument;
import oldmana.md.server.card.play.PlayArguments;
import oldmana.md.server.card.PropertyColor;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.history.UndoableAction;
import oldmana.md.server.history.BasicUndoableAction;
import oldmana.md.server.card.CardType;
import oldmana.md.server.rules.GameRules;
import oldmana.md.server.state.ActionStateRent;
import oldmana.md.server.state.ActionStateTargetPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static oldmana.md.server.card.CardAttributes.*;

public class CardActionRent extends CardAction
{
	public static final String COLORS = "colors";
	public static final String CHARGES_ALL = "chargesAll";
	
	public static CardTemplate RAINBOW;
	public static CardTemplate BROWN_LIGHT_BLUE;
	public static CardTemplate MAGENTA_ORANGE;
	public static CardTemplate RED_YELLOW;
	public static CardTemplate GREEN_DARK_BLUE;
	public static CardTemplate RAILROAD_UTILITY;
	
	private static final String DEFAULT_NAME = "Rent";
	
	private List<PropertyColor> colors;
	
	private RentChargeTarget rentChargeTarget = RentChargeTarget.DEFAULT;
	
	@Override
	public void applyTemplate(CardTemplate template, boolean soft)
	{
		super.applyTemplate(template, soft);
		this.colors = template.getColorList(COLORS);
		String name = template.getString(NAME);
		setName(name.equals(DEFAULT_NAME) ? getName(colors) : name); // If it's the default name, dynamically set the name
		if (template.has(CHARGES_ALL))
		{
			Object val = template.getObject(CHARGES_ALL);
			if (val instanceof String && val.equals("default"))
			{
				rentChargeTarget = RentChargeTarget.DEFAULT;
			}
			else
			{
				rentChargeTarget = template.getBoolean(CHARGES_ALL) ? RentChargeTarget.ALL : RentChargeTarget.SINGLE;
			}
		}
	}
	
	public List<PropertyColor> getRentColors()
	{
		return colors;
	}
	
	public RentChargeTarget getChargeTarget()
	{
		return rentChargeTarget;
	}
	
	@Override
	public void doPlay(Player player, PlayArguments args)
	{
		List<RentModifierCard> modifiers = args.getArguments(CardArgument.class).stream()
				.map(CardArgument::getCard)
				.filter(card -> card instanceof RentModifierCard)
				.map(card -> (RentModifierCard) card)
				.collect(Collectors.toList());
		int baseRent = player.getHighestValueRent(getRentColors());
		double currentRent = baseRent;
		for (RentModifierCard modifier : modifiers)
		{
			currentRent = modifier.modifyRent(baseRent, currentRent);
			((Card) modifier).play(new ConsumeModifierArgument(this));
		}
		
		int rent = (int) Math.round(currentRent);
		GameRules rules = getServer().getGameRules();
		if (getChargeTarget() == RentChargeTarget.ALL || getServer().getPlayerCount() <= 2 || (getChargeTarget() == RentChargeTarget.DEFAULT &&
				(colors.size() <= 2 && rules.doesTwoColorRentChargeAll()) ||
				(colors.size() > 2 && rules.doesMultiColorRentChargeAll())))
		{
			player.clearUndoableActions();
			getServer().getGameState().addActionState(new ActionStateRent(player, getServer().getPlayersExcluding(player), rent));
		}
		else
		{
			getServer().getGameState().addActionState(new ActionStateTargetRent(player, rent));
		}
	}
	
	@Override
	protected void playStageAddUndo(Player player, PlayArguments args)
	{
		if (getServer().getGameRules().isUndoAllowed())
		{
			List<Card> cards = args.getArguments(CardArgument.class).stream()
					.map(CardArgument::getCard)
					.filter(card -> card instanceof RentModifierCard)
					.collect(Collectors.toCollection(ArrayList::new));
			cards.add(0, this);
			int moveCost = cards.stream().reduce(0, (moves, card) -> moves + card.getMoveCost(), Integer::sum);
			player.addUndoableAction(new BasicUndoableAction(cards, player, moveCost));
		}
	}
	
	@Override
	public boolean canPlay(Player player)
	{
		return player.hasRentableProperties(colors);
	}
	
	@Override
	public Packet getCardDataPacket()
	{
		byte[] types = new byte[colors.size()];
		for (int i = 0 ; i < types.length ; i++)
		{
			types[i] = colors.get(i).getID();
		}
		return new PacketCardActionRentData(getID(), getName(), getValue(), types, getDescription().getID(),
				getOuterColor().getRGB(), getInnerColor().getRGB());
	}
	
	@Override
	public String toString()
	{
		String str = getClass().getSimpleName() + " (RentType: " + rentChargeTarget.name() + ") (" + colors.size() + " Color" +
				(colors.size() != 1 ? "s" : "") + ": ";
		for (PropertyColor color : colors)
		{
			str += color.getLabel() + "/";
		}
		str = str.substring(0, str.length() - 1);
		str += ") (" + getValue() + "M)";
		return str;
	}
	
	private static String getName(List<PropertyColor> colors)
	{
		switch (colors.size())
		{
			case 1:
				return colors.get(0).getName() + " Rent";
			case 2:
				return colors.get(0).getName() + "/" + colors.get(1).getName() + " Rent";
			default:
				return colors.size() + "-Color Rent";
		}
	}
	
	/**
	 * Shortcut utility for creating rent cards easily.
	 */
	public static CardActionRent create(int value, PropertyColor... colors)
	{
		return CardType.RENT.createCard(createTemplate(value, colors));
	}
	
	public static CardTemplate createTemplate(int value, PropertyColor... colors)
	{
		CardTemplate template = CardType.RENT.getDefaultTemplate();
		template.put(VALUE, value);
		template.putColors(COLORS, colors);
		return template;
	}
	
	private static CardType<CardActionRent> createType()
	{
		CardType<CardActionRent> type = new CardType<CardActionRent>(CardActionRent.class, CardActionRent::new, false, "Rent");
		type.addExemptReduction(COLORS, false);
		type.addExemptReduction(VALUE, false);
		CardTemplate dt = type.getDefaultTemplate();
		dt.put(VALUE, 3);
		dt.put(NAME, DEFAULT_NAME);
		dt.putStrings(DISPLAY_NAME, "RENT");
		dt.putStrings(DESCRIPTION, "Charge rent using your placed down properties that match the colors on this Rent card. "
				+ "Refer to your properties to find the amount of rent you can charge.");
		dt.putColors(COLORS, PropertyColor.getVanillaColors());
		dt.put(UNDOABLE, true);
		dt.put(CLEARS_UNDOABLE_ACTIONS, false);
		type.setDefaultTemplate(dt);
		
		RAINBOW = dt.clone();
		type.addTemplate(RAINBOW, "Rainbow Rent");
		
		CardTemplate oneMilValue = new CardTemplate(dt);
		oneMilValue.put(VALUE, 1);
		
		BROWN_LIGHT_BLUE = new CardTemplate(oneMilValue);
		BROWN_LIGHT_BLUE.putColors(COLORS, PropertyColor.BROWN, PropertyColor.LIGHT_BLUE);
		type.addTemplate(BROWN_LIGHT_BLUE, "Brown/Light Blue Rent", "B/LB Rent");
		
		MAGENTA_ORANGE = new CardTemplate(oneMilValue);
		MAGENTA_ORANGE.putColors(COLORS, PropertyColor.MAGENTA, PropertyColor.ORANGE);
		type.addTemplate(MAGENTA_ORANGE, "Magenta/Orange Rent", "M/O Rent");
		
		RED_YELLOW = new CardTemplate(oneMilValue);
		RED_YELLOW.putColors(COLORS, PropertyColor.RED, PropertyColor.YELLOW);
		type.addTemplate(RED_YELLOW, "Red/Yellow Rent", "R/Y Rent");
		
		GREEN_DARK_BLUE = new CardTemplate(oneMilValue);
		GREEN_DARK_BLUE.putColors(COLORS, PropertyColor.GREEN, PropertyColor.DARK_BLUE);
		type.addTemplate(GREEN_DARK_BLUE, "Green/Dark Blue Rent", "G/DB Rent");
		
		RAILROAD_UTILITY = new CardTemplate(oneMilValue);
		RAILROAD_UTILITY.putColors(COLORS, PropertyColor.RAILROAD, PropertyColor.UTILITY);
		type.addTemplate(RAILROAD_UTILITY, "Railroad/Utility Rent", "R/U Rent");
		
		return type;
	}
	
	public class ActionStateTargetRent extends ActionStateTargetPlayer
	{
		private int rent;
		
		public ActionStateTargetRent(Player player, int rent)
		{
			super(player);
			this.rent = rent;
			setStatus(player.getName() + " is using a Rent card");
		}
		
		public int getRent()
		{
			return rent;
		}
		
		@Override
		public void playerSelected(Player player)
		{
			getActionOwner().clearUndoableActions();
			replaceState(new ActionStateRent(getActionOwner(), player, rent));
		}
		
		@Override
		public void onUndo(UndoableAction action)
		{
			if (action.hasCard(CardActionRent.this))
			{
				removeState();
			}
		}
	}
	
	/**
	 * The rent type specifies how many players can be charged by a rent card.
	 */
	public enum RentChargeTarget
	{
		/** Fall back to the game rules for direction. */
		DEFAULT,
		/** Charge one player rent, regardless of game rules. */
		SINGLE,
		/** Charge all players rent, regardless of game rules. */
		ALL
	}
	
	/**
	 * Cards implementing this interface are indicated to be modifiers for rent cards.
	 */
	public interface RentModifierCard extends ModifierCard
	{
		/**
		 * Modifies the rent value when playing a rent card. The final rent will be rounded to the nearest integer.
		 * @param baseRent The original rent without any modifiers
		 * @param currentRent The current rent that may have been modified by other modifiers
		 * @return The rent with this modifier applied
		 */
		double modifyRent(int baseRent, double currentRent);
	}
}
