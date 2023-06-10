package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketPlayerButton extends Packet
{
	public int id;
	public int playerID;
	public String name;
	public boolean enabled;
	public byte type;
	public byte color;
	public short priority;
	public float maxSize;
	
	public PacketPlayerButton() {}

	public PacketPlayerButton(int id, int playerID, String name, boolean enabled, byte color, int priority, double maxSize)
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
