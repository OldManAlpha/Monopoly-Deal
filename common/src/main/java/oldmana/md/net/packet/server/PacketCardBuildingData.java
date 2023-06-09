package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketCardBuildingData extends Packet
{
	public int id;
	public String name;
	public int value;
	public byte tier;
	public byte rentAddition;
	
	public String[] displayName;
	public byte fontSize;
	public byte displayOffsetY;
	public short description;
	
	public int outerColor;
	public int innerColor;
	
	public PacketCardBuildingData() {}
	
	public PacketCardBuildingData(int id, String name, int value, int tier, int rentAddition,
			String[] displayName, int fontSize, int displayOffsetY, int description, int outerColor, int innerColor)
	{
		this.id = id;
		this.name = name;
		this.value = value;
		this.tier = (byte) tier;
		this.rentAddition = (byte) rentAddition;
		
		this.displayName = displayName;
		this.fontSize = (byte) fontSize;
		this.displayOffsetY = (byte) displayOffsetY;
		this.description = (short) description;
		
		this.outerColor = outerColor;
		this.innerColor = innerColor;
	}
}
