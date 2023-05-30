package oldmana.md.server.card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardPropertyData;
import oldmana.md.server.card.control.CardButton;
import oldmana.md.server.card.control.CardButton.CardButtonType;
import oldmana.md.server.card.control.CardControls;

public class CardProperty extends Card
{
	public static CardTemplate RAINBOW_WILD;
	
	private List<PropertyColor> colors;
	private boolean base;
	
	@Override
	public void applyTemplate(CardTemplate template)
	{
		super.applyTemplate(template);
		colors = template.getColorList("colors");
		base = template.getBoolean("base");
	}
	
	@Override
	public CardControls createControls()
	{
		CardControls actions = super.createControls();
		CardButton play = new CardButton("Play", CardButton.TOP, CardButtonType.PROPERTY);
		play.setCondition((player, card) -> player.canPlayCards());
		play.setListener((player, card, data) ->
		{
			if (!player.getHand().hasCard(card))
			{
				player.resendActionState();
				return;
			}
			player.playCardProperty((CardProperty) card, data);
		});
		actions.addButton(play);
		
		CardButton bank = new CardButton("Bank", CardButton.BOTTOM);
		bank.setCondition((player, card) -> getServer().getGameRules().canBankPropertyCards() && player.canPlayCards());
		bank.setListener((player, card, data) -> player.playCardBank(card));
		actions.addButton(bank);
		return actions;
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
		byte[] types = new byte[colors.size()];
		for (int i = 0 ; i < types.length ; i++)
		{
			types[i] = colors.get(i).getID();
		}
		return new PacketCardPropertyData(getID(), getName(), getValue(), types, isBase(), getDescription().getID());
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
	
	public static CardTemplate createTemplate(int value, String name, PropertyColor... colors)
	{
		return createTemplate(value, name, true, colors);
	}
	
	public static CardTemplate createTemplate(int value, String name, boolean base, PropertyColor... colors)
	{
		CardTemplate template = CardType.PROPERTY.getDefaultTemplate().clone();
		template.put("value", value);
		template.put("name", name);
		template.putColors("colors", colors);
		template.put("base", base);
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
		CardType<CardProperty> type = new CardType<CardProperty>(CardProperty.class, CardProperty::new, "Property");
		type.addExemptReduction("colors");
		type.addExemptReduction("base");
		type.addExemptReduction("value");
		CardTemplate dt = type.getDefaultTemplate();
		dt.put("name", "Generic Property");
		dt.putStrings("description", "Property cards can be placed down, allowing you to charge rent to other players " +
				"using Rent cards. Properties that are placed down can also be used to pay rent with.");
		dt.put("revocable", true);
		dt.put("clearsRevocableCards", false);
		dt.putColors("colors", PropertyColor.RAILROAD);
		dt.put("base", true);
		type.setDefaultTemplate(dt);
		
		RAINBOW_WILD = new CardTemplate(dt);
		RAINBOW_WILD.put("value", 0);
		RAINBOW_WILD.put("name", "Property Wild Card");
		RAINBOW_WILD.putStrings("description", "A property card that can be paired with any color. " +
				"It cannot be used for rent if you don't have another property with the color. "
				+ "This card cannot be stolen with Sly Deals, Forced Deals, or rent.");
		RAINBOW_WILD.putColors("colors", PropertyColor.getVanillaColors());
		RAINBOW_WILD.put("base", false);
		type.addTemplate(RAINBOW_WILD, "Rainbow Wild");
		return type;
	}
}
