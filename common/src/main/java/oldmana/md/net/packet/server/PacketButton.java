package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketButton extends Packet
{
	public int id;
	public int playerID;
	public String name;
	public boolean enabled;
	public byte type;
	public byte color;
	public short priority;
	public float maxSize;
	
	public PacketButton() {}

	public PacketButton(int id, int playerID, String name, boolean enabled, byte color, int priority, double maxSize)
	{
		this.id = id;
		this.playerID = playerID;
		this.name = name;
		this.enabled = enabled;
		this.color = color;
		this.priority = (short) priority;
		this.maxSize = (float) maxSize;
	}
}
