package oldmana.md.server.card;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.playerui.CardButtonType;
import oldmana.md.common.net.packet.server.PacketCardPropertyData;
import oldmana.md.server.Player;
import oldmana.md.server.card.play.argument.PropertySetArgument;
import oldmana.md.server.card.play.PlayArguments;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.card.control.CardButton;
import oldmana.md.server.card.control.CardControls;

import static oldmana.md.server.card.CardAttributes.*;

public class CardProperty extends Card
{
	public static final String COLORS = "colors";
	public static final String BASE = "base";
	public static final String STEALABLE = "stealable";
	
	public static CardTemplate RAINBOW_WILD;
	
	private List<PropertyColor> colors;
	private boolean base;
	private boolean stealable;
	
	@Override
	public void applyTemplate(CardTemplate template, boolean soft)
	{
		super.applyTemplate(template, soft);
		if (!template.has(INNER_COLOR))
		{
			setInnerColor(Color.WHITE);
		}
		colors = template.getColorList("colors");
		base = template.getBoolean("base");
		stealable = template.getBoolean("stealable");
	}
	
	@Override
	protected CardControls createControls()
	{
		CardControls actions = super.createControls();
		CardButton play = actions.getButtonByText("Play");
		play.setType(CardButtonType.PROPERTY);
		play.setListener((player, card, data) ->
				card.play(PlayArguments.ofPropertySet(player.getPropertySet(data))));
		return actions;
	}
	
	/**
	 * Because the act of playing a property card may entail more complicated operations other than simply moving a card,
	 * this is in the playing logic rather than the moving logic.
	 */
	@Override
	public void doPlay(Player player, PlayArguments args)
	{
		PropertySet set = args.hasArgument(PropertySetArgument.class) ?
				args.getArgument(PropertySetArgument.class).getTargetSet() : null;
		if (set == null)
		{
			if (isSingleColor() && player.hasSolidPropertySet(getColor()))
			{
				set = player.getSolidPropertySet(getColor());
			}
			else
			{
				set = player.createPropertySet();
			}
		}
		transfer(set, getPlayAnimation());
		set.checkLegality();
	}
	
	@Override
	public boolean canBank(Player player)
	{
		return super.canBank(player) && getServer().getGameRules().canBankPropertyCards();
	}
	
	public boolean isSingleColor()
	{
		return colors.size() == 1;
	}
	
	public boolean isBiColor()
	{
		return colors.size() == 2;
	}
	
	/**If a property is a base, its colors may be used as a foundation for a set's color.
	 * 
	 * @return True if card is a base
	 */
	public boolean isBase()
	{
		return base;
	}
	
	/**If a property is stealable, it may be targeted by Sly Deals and Forced Deals.
	 *
	 * @return True if the card can be stolen
	 */
	public boolean isStealable()
	{
		return stealable;
	}
	
	/**If there are multiple colors on the card, the first color is returned.
	 * 
	 * @return First color of the card
	 */
	public PropertyColor getColor()
	{
		return colors.get(0);
	}
	
	public List<PropertyColor> getColors()
	{
		return colors;
	}
	
	public boolean hasColor(PropertyColor color)
	{
		return colors.contains(color);
	}
	
	public void setColors(PropertyColor... colors)
	{
		this.colors = new ArrayList<PropertyColor>(Arrays.asList(colors));
	}
	
	@Override
	public Packet getCardDataPacket()
	{
		List<PropertyColor> colors = getColors();
		byte[] types = new byte[colors.size()];
		for (int i = 0 ; i < types.length ; i++)
		{
			types[i] = colors.get(i).getID();
		}
		return new PacketCardPropertyData(getID(), getName(), getValue(), types, isBase(), isStealable(),
				getDescription().getID(), getOuterColor().getRGB(), getInnerColor().getRGB());
	}
	
	@Override
	public String toString()
	{
		String str = "CardProperty (" + colors.size() + " Color" + (colors.size() != 1 ? "s" : "") + ": ";
		for (PropertyColor color : colors)
		{
			str += color.getLabel() + "/";
		}
		str = str.substring(0, str.length() - 1);
		str += ") (Base: " + isBase() + ")";
		str += " (Stealable: " + isStealable() + ")";
		str += " (Name: " + getName() + ")";
		str += " (" + getValue() + "M)";
		return str;
	}
	
	
	/**
	 * Shortcut utility for creating properties easily.
	 */
	public static CardProperty create(int value, String name, PropertyColor... colors)
	{
		return CardType.PROPERTY.createCard(createTemplate(value, name, colors));
	}
	
	/**
	 * Shortcut utility for creating properties easily.
	 */
	public static CardProperty create(int value, String name, boolean base, PropertyColor... colors)
	{
		return CardType.PROPERTY.createCard(createTemplate(value, name, base, colors));
	}
	
	/**
	 * Shortcut utility for creating properties easily.
	 */
	public static CardProperty create(int value, String name, boolean base, boolean stealable, PropertyColor... colors)
	{
		return CardType.PROPERTY.createCard(createTemplate(value, name, base, stealable, colors));
	}
	
	public static CardTemplate createTemplate(int value, String name, PropertyColor... colors)
	{
		return createTemplate(value, name, true, colors);
	}
	
	public static CardTemplate createTemplate(int value, String name, boolean base, PropertyColor... colors)
	{
		return createTemplate(value, name, base, true, colors);
	}
	
	public static CardTemplate createTemplate(int value, String name, boolean base, boolean stealable, PropertyColor... colors)
	{
		CardTemplate template = CardType.PROPERTY.getDefaultTemplate();
		template.put(VALUE, value);
		template.put(NAME, name);
		template.putColors(COLORS, colors);
		template.put(BASE, base);
		template.put(STEALABLE, stealable);
		return template;
	}
	
	public static List<CardProperty> getPropertyCards(int[] ids)
	{
		List<CardProperty> props = new ArrayList<CardProperty>();
		List<Card> cards = Card.getCards(ids);
		for (Card card : cards)
		{
			props.add((CardProperty) card);
		}
		return props;
	}
	
	private static CardType<CardProperty> createType()
	{
		CardType<CardProperty> type = new CardType<CardProperty>(CardProperty.class, CardProperty::new, false, "Property");
		type.addExemptReduction(COLORS, false);
		type.addExemptReduction(VALUE, false);
		CardTemplate dt = type.getDefaultTemplate();
		dt.put(NAME, "Generic Property");
		dt.putStrings(DESCRIPTION, "Property cards can be placed down, allowing you to charge rent to other players " +
				"using Rent cards. Properties that are placed down can also be used to pay rent with.");
		dt.put(UNDOABLE, true);
		dt.put(CLEARS_UNDOABLE_ACTIONS, false);
		dt.putColors(COLORS, PropertyColor.RAILROAD);
		dt.put(BASE, true);
		dt.put(STEALABLE, true);
		dt.put(CONSUME_MOVES_STAGE, CardPlayStage.AFTER_PLAY);
		dt.put(MOVE_STAGE, CardPlayStage.MANUAL);
		type.setDefaultTemplate(dt);
		
		RAINBOW_WILD = new CardTemplate(dt);
		RAINBOW_WILD.put(VALUE, 0);
		RAINBOW_WILD.put(NAME, "Property Wild Card");
		RAINBOW_WILD.putStrings(DESCRIPTION, "A property card that can be paired with any color. " +
				"It cannot be used for rent if you don't have another property with the color. "
				+ "This card cannot be stolen with Sly Deals, Forced Deals, or rent.");
		RAINBOW_WILD.putColors("colors", PropertyColor.getVanillaColors());
		RAINBOW_WILD.put(BASE, false);
		RAINBOW_WILD.put(STEALABLE, false);
		type.addTemplate(RAINBOW_WILD, "Rainbow Wild");
		return type;
	}
}
