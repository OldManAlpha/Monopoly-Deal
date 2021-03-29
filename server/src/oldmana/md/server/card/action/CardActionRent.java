package oldmana.md.server.card.action;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardActionRentData;
import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardProperty.PropertyColor;
import oldmana.md.server.state.ActionStateRent;
import oldmana.md.server.state.ActionStateTargetRent;

public class CardActionRent extends CardAction
{
	private PropertyColor[] colors;
	
	public CardActionRent(int value, PropertyColor... colors)
	{
		super(value, "Rent");
		
		this.colors = colors;
		
		setRevocable(false);
		setClearsRevocableCards(true);
		setDescription("Charge rent to all players on the properties that match the colors on the card. "
				+ "Only properties put down on your table are valid to rent on. Highest rent possible is automatically calculated.");
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
		return new PacketCardActionRentData(getID(), getValue(), types, getDescription().getID());
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
}
