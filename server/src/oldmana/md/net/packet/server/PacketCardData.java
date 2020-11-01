package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketCardData extends Packet
{
	public int id;
	public String name;
	public int value;
	public byte type;
	public boolean revocable;
	public boolean marksPreviousUnrevocable;
	
	public String[] displayName;
	public byte fontSize;
	public byte displayOffsetY;
	
	public PacketCardData() {}
	
	public PacketCardData(int id, String name, int value, int type, boolean revocable, boolean marksPreviousUnrevocable, String[] displayName, 
			byte fontSize, byte displayOffsetY)
	{
		this.id = id;
		this.name = name;
		this.value = value;
		this.type = (byte) type;
		this.revocable = revocable;
		this.marksPreviousUnrevocable = marksPreviousUnrevocable;
		
		this.displayName = displayName;
		this.fontSize = fontSize;
		this.displayOffsetY = displayOffsetY;
	}
}
