package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketCardData extends Packet
{
	public int id;
	public String name;
	public int value;
	public byte type;
	
	public String[] displayName;
	public byte fontSize;
	public byte displayOffsetY;
	public short description;
	
	public int outerColor;
	public int innerColor;
	
	public PacketCardData() {}
	
	public PacketCardData(int id, String name, int value, int type, String[] displayName,
			int fontSize, int displayOffsetY, int description, int outerColor, int innerColor)
	{
		this.id = id;
		this.name = name;
		this.value = value;
		this.type = (byte) type;
		
		this.displayName = displayName;
		this.fontSize = (byte) fontSize;
		this.displayOffsetY = (byte) displayOffsetY;
		this.description = (short) description;
		
		this.outerColor = outerColor;
		this.innerColor = innerColor;
	}
}
