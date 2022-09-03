package oldmana.md.server.card.action;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardActionRentData;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.PropertyColor;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.control.CardButton;
import oldmana.md.server.card.control.CardControls;
import oldmana.md.server.card.CardType;
import oldmana.md.server.state.ActionStateRent;
import oldmana.md.server.state.ActionStateTargetRent;

public class CardActionRent extends CardAction
{
	public static CardTemplate RAINBOW;
	public static CardTemplate BROWN_LIGHT_BLUE;
	public static CardTemplate MAGENTA_ORANGE;
	public static CardTemplate RED_YELLOW;
	public static CardTemplate GREEN_DARK_BLUE;
	public static CardTemplate RAILROAD_UTILITY;
	
	
	private PropertyColor[] colors;
	
	public CardActionRent() {}
	
	@Override
	public void applyTemplate(CardTemplate template)
	{
		super.applyTemplate(template);
		this.colors = template.getColorArray("colors");
		setName(getName(colors));
	}
	
	@Override
	public CardControls createControls()
	{
		CardControls controls = super.createControls();
		CardButton doubleButton = new CardButton("Double Rent", CardButton.CENTER);
		doubleButton.setCondition((player, card) ->
		{
			if (!player.canPlayCards() || getServer().getGameState().getTurnsRemaining() < 2)
			{
				return false;
			}
			for (Card c : player.getHand())
			{
				if (c instanceof CardActionDoubleTheRent)
				{
					return true;
				}
			}
			return false;
		});
		doubleButton.setListener((player, card, data) ->
		{
			Card doubleRent = null;
			for (Card c : player.getHand())
			{
				if (c instanceof CardActionDoubleTheRent)
				{
					doubleRent = c;
					break;
				}
			}
			if (doubleRent == null)
			{
				player.resendActionState();
				return;
			}
			card.transfer(getServer().getDiscardPile());
			doubleRent.transfer(getServer().getDiscardPile());
			getServer().getGameState().decrementTurns(2);
			((CardActionRent) card).playCard(player, 2);
		});
		controls.addButton(doubleButton);
		return controls;
	}
	
	public PropertyColor[] getRentColors()
	{
		return colors;
	}
	
	@Override
	public void playCard(Player player)
	{
		playCard(player, 1);
	}
	
	public void playCard(Player player, double multiplier)
	{
		int rent = (int) (player.getHighestValueRent(colors) * multiplier);
		if (getServer().getGameRules().doesRentChargeAll() || getServer().getPlayers().size() == 2)
		{
			getServer().getGameState().setActionState(new ActionStateRent(player, getServer().getPlayersExcluding(player), rent));
		}
		else
		{
			getServer().getGameState().setActionState(new ActionStateTargetRent(player, rent));
		}
	}
	
	@Override
	public boolean canPlayCard(Player player)
	{
		return player.hasRentableProperties(colors);
	}
	
	@Override
	public Packet getCardDataPacket()
	{
		byte[] types = new byte[colors.length];
		for (int i = 0 ; i < types.length ; i++)
		{
			types[i] = colors[i].getID();
		}
		return new PacketCardActionRentData(getID(), getName(), getValue(), types, getDescription().getID());
	}
	
	private static String getName(PropertyColor[] colors)
	{
		switch (colors.length)
		{
			case 1:
				return colors[0].getName() + " Rent";
			case 2:
				return colors[0].getName() + "/" + colors[1].getName() + " Rent";
			default:
				return colors.length + "-Color Rent";
		}
	}
	
	@Override
	public String toString()
	{
		String str = "CardActionRent (" + colors.length + " Color" + (colors.length != 1 ? "s" : "") + ": ";
		for (PropertyColor color : colors)
		{
			str += color.getLabel() + "/";
		}
		str = str.substring(0, str.length() - 1);
		str += ") (" + getValue() + "M)";
		return str;
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
		CardTemplate template = CardType.PROPERTY.getDefaultTemplate().clone();
		template.put("value", value);
		template.putColors("colors", colors);
		return template;
	}
	
	private static CardType<CardActionRent> createType()
	{
		CardType<CardActionRent> type = new CardType<CardActionRent>(CardActionRent.class, "Rent");
		type.addExemptReduction("colors");
		type.addExemptReduction("value");
		CardTemplate dt = type.getDefaultTemplate();
		dt.put("value", 3);
		dt.put("name", "Rent");
		dt.putStrings("displayName", "RENT");
		dt.putStrings("description", "Charge rent to all players on the properties that match the colors on the card. "
				+ "Only properties put down on your table are valid to rent on. Highest rent possible is automatically calculated.");
		dt.putColors("colors", PropertyColor.getVanillaColors());
		dt.put("revocable", false);
		dt.put("clearsRevocableCards", true);
		RAINBOW = dt;
		
		CardTemplate oneMilValue = new CardTemplate(dt);
		oneMilValue.put("value", 1);
		
		BROWN_LIGHT_BLUE = new CardTemplate(oneMilValue);
		BROWN_LIGHT_BLUE.putColors("colors", PropertyColor.BROWN, PropertyColor.LIGHT_BLUE);
		type.addTemplate(BROWN_LIGHT_BLUE, "Brown/Light Blue Rent", "B/LB Rent");
		
		MAGENTA_ORANGE = new CardTemplate(oneMilValue);
		MAGENTA_ORANGE.putColors("colors", PropertyColor.MAGENTA, PropertyColor.ORANGE);
		type.addTemplate(MAGENTA_ORANGE, "Magenta/Orange Rent", "M/O Rent");
		
		RED_YELLOW = new CardTemplate(oneMilValue);
		RED_YELLOW.putColors("colors", PropertyColor.RED, PropertyColor.YELLOW);
		type.addTemplate(RED_YELLOW, "Red/Yellow Rent", "R/Y Rent");
		
		GREEN_DARK_BLUE = new CardTemplate(oneMilValue);
		GREEN_DARK_BLUE.putColors("colors", PropertyColor.GREEN, PropertyColor.DARK_BLUE);
		type.addTemplate(GREEN_DARK_BLUE, "Green/Dark Blue Rent", "G/DB Rent");
		
		RAILROAD_UTILITY = new CardTemplate(oneMilValue);
		RAILROAD_UTILITY.putColors("colors", PropertyColor.RAILROAD, PropertyColor.UTILITY);
		type.addTemplate(RAILROAD_UTILITY, "Railroad/Utility Rent", "R/U Rent");
		
		return type;
	}
}
