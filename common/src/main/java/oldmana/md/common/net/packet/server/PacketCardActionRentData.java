package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketCardActionRentData extends Packet
{
	public int id;
	public String name;
	public byte value;
	public byte[] colors;
	
	public String[] displayName;
	public byte fontSize;
	public byte displayOffsetY;
	public short description;
	
	public int outerColor;
	public int innerColor;
	
	public PacketCardActionRentData() {}
	
	public PacketCardActionRentData(int id, String name, int value, byte[] colors, String[] displayName, int fontSize,
	                                int displayOffsetY, int description, int outerColor, int innerColor)
	{
		this.id = id;
		this.name = name;
		this.value = (byte) value;
		this.colors = colors;
		
		this.displayName = displayName;
		this.fontSize = (byte) fontSize;
		this.displayOffsetY = (byte) displayOffsetY;
		
		this.description = (short) description;
		
		this.outerColor = outerColor;
		this.innerColor = innerColor;
	}
}
