package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketCardBuildingData extends Packet
{
	public int id;
	public String name;
	public int value;
	public byte tier;
	public byte rentAddition;
	public boolean revocable;
	public boolean marksPreviousUnrevocable;
	
	public String[] displayName;
	public byte fontSize;
	public byte displayOffsetY;
	public short description;
	
	public PacketCardBuildingData() {}
	
	public PacketCardBuildingData(int id, String name, int value, int tier, int rentAddition, boolean revocable, boolean marksPreviousUnrevocable, 
			String[] displayName, int fontSize, int displayOffsetY, int description)
	{
		this.id = id;
		this.name = name;
		this.value = value;
		this.tier = (byte) tier;
		this.rentAddition = (byte) rentAddition;
		this.revocable = revocable;
		this.marksPreviousUnrevocable = marksPreviousUnrevocable;
		
		this.displayName = displayName;
		this.fontSize = (byte) fontSize;
		this.displayOffsetY = (byte) displayOffsetY;
		this.description = (short) description;
	}
}
