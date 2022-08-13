package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketButton extends Packet
{
	public byte id;
	public int playerID;
	public String name;
	public boolean enabled;
	public byte type;
	public byte color;
	
	public PacketButton() {}

	public PacketButton(int id, int playerID, String name, boolean enabled, byte type, byte color)
	{
		this.id = (byte) id;
		this.playerID = playerID;
		this.name = name;
		this.enabled = enabled;
		this.type = type;
		this.color = color;
	}
}
