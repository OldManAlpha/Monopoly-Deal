package oldmana.md.server.card.action;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardActionRentData;
import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardProperty.PropertyColor;
import oldmana.md.server.state.ActionStateListener;
import oldmana.md.server.state.ActionStateRent;
import oldmana.md.server.state.ActionStateTargetRent;

public class CardActionRent extends CardAction implements ActionStateListener
{
	private PropertyColor[] colors;
	
	private ActionStateRent rent;
	
	public CardActionRent(int value, PropertyColor... colors)
	{
		super(value, "Rent");
		
		this.colors = colors;
		
		setRevocable(false);
		setMarksPreviousUnrevocable(true);
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
		if (getServer().doesRentChargeAll() || getServer().getPlayers().size() == 2)
		{
			getServer().getGameState().setCurrentActionState(new ActionStateRent(player, getServer().getPlayersExcluding(player), rent));
		}
		else
		{
			getServer().getGameState().setCurrentActionState(new ActionStateTargetRent(player, rent));
		}
	}
	
	@Override
	public boolean canPlayCard(Player player)
	{
		return player.hasRentableProperties(colors);
	}
	
	@Override
	public boolean onActionStateUpdate()
	{
		if (rent.getNumberOfTargets() == rent.getNumberOfAccepted())
		{
			getServer().getGameState().nextNaturalActionState();
		}
		return true;
	}
	
	@Override
	public Packet getCardDataPacket()
	{
		byte[] types = new byte[colors.length];
		for (int i = 0 ; i < types.length ; i++)
		{
			types[i] = colors[i].getID();
		}
		return new PacketCardActionRentData(getID(), getValue(), types);
	}
	
	@Override
	public String toString()
	{
		String str = "CardActionRent (" + colors.length + " Colors: ";
		for (PropertyColor color : colors)
		{
			str += color.getLabel() + "/";
		}
		str = str.substring(0, str.length() - 1);
		str += ") (" + getValue() + "M)";
		return str;
	}
}
