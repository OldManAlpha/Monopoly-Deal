package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketCardPropertyData extends Packet
{
	public int id;
	public String name;
	public int value;
	public byte[] colors;
	public boolean base;
	public boolean stealable;
	
	public short description;
	
	public int outerColor;
	public int innerColor;
	
	public PacketCardPropertyData() {}
	
	public PacketCardPropertyData(int id, String name, int value, byte[] colors, boolean base, boolean stealable, int description,
	                              int outerColor, int innerColor)
	{
		this.id = id;
		this.name = name;
		this.value = value;
		this.colors = colors;
		this.base = base;
		this.stealable = stealable;
		
		this.description = (short) description;
		
		this.outerColor = outerColor;
		this.innerColor = innerColor;
	}
}
