package oldmana.md.server.playerui;

import oldmana.md.common.net.packet.server.PacketInfoPlate;
import oldmana.md.common.playerui.InfoPlateBase;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;

import java.awt.Color;

public class InfoPlate extends InfoPlateBase
{
	private Player owner;
	private String tag;
	private boolean manualSend = false;
	private boolean registered = false;
	
	public InfoPlate(Player owner)
	{
		this.owner = owner;
	}
	
	public InfoPlate(Player owner, String text)
	{
		this(owner);
		setText(text);
	}
	
	public InfoPlate(Player owner, String text, String tag)
	{
		this(owner, text);
		this.tag = tag;
	}
	
	@Override
	public void setPriority(int priority)
	{
		super.setPriority(priority);
		sendAuto();
	}
	
	@Override
	public void setText(String text)
	{
		super.setText(text);
		sendAuto();
	}
	
	@Override
	public void setTextColor(Color textColor)
	{
		super.setTextColor(textColor);
		sendAuto();
	}
	
	@Override
	public void setColor(Color color)
	{
		super.setColor(color);
		sendAuto();
	}
	
	@Override
	public void setBorderColor(Color borderColor)
	{
		super.setBorderColor(borderColor);
		sendAuto();
	}
	
	public Player getOwner()
	{
		return owner;
	}
	
	public boolean hasOwner()
	{
		return owner != null;
	}
	
	/**
	 * Sends this plate to all clients.
	 */
	public void send()
	{
		if (hasOwner() && isRegistered())
		{
			MDServer.getInstance().broadcastPacket(getPacket());
		}
	}
	
	private void sendAuto()
	{
		if (!isManualSend())
		{
			send();
		}
	}
	
	/**
	 * Sends this plate to the specified player.
	 * @param player The player to send the plate to
	 */
	public void send(Player player)
	{
		if (hasOwner() && isRegistered())
		{
			player.sendPacket(getPacket());
		}
	}
	
	/**
	 * Registers the info plate to the player and sends it all clients.
	 */
	public void register()
	{
		owner.registerInfoPlate(this);
	}
	
	/**
	 * Unregisters the info plate and removes it from view.
	 */
	public void remove()
	{
		owner.removeInfoPlate(this);
	}
	
	public void setTag(String tag)
	{
		this.tag = tag;
	}
	
	public String getTag()
	{
		return tag;
	}
	
	public void setManualSend(boolean manualSend)
	{
		this.manualSend = manualSend;
	}
	
	public boolean isManualSend()
	{
		return manualSend;
	}
	
	public void setRegistered(boolean registered)
	{
		this.registered = registered;
	}
	
	public boolean isRegistered()
	{
		return registered;
	}
	
	public PacketInfoPlate getPacket()
	{
		return new PacketInfoPlate(owner.getID(), getId(), getPriority(), getText(), getTextColor(), getColor(),
				getBorderColor());
	}
}
