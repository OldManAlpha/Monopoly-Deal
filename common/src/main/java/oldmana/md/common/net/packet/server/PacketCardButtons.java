package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.MJPacketBuffer;
import oldmana.md.common.net.api.packet.ComplexPacket;
import oldmana.md.common.net.api.packet.Packet;

public class PacketCardButtons extends Packet implements ComplexPacket
{
	public int cardID;
	public String[] text;
	public byte[] id;
	public byte[] type;
	public byte[] color;
	public short[] x;
	public short[] y;
	public short[] width;
	public short[] height;
	
	public PacketCardButtons() {}
	
	public PacketCardButtons(int cardID, String[] text, byte[] id, byte[] type, byte[] color, short[] x, short[] y, short[] width, short[] height)
	{
		this.cardID = cardID;
		this.text = text;
		this.id = id;
		this.type = type;
		this.color = color;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void toBytes(MJPacketBuffer buffer)
	{
		buffer.addInt(cardID);
		byte len = (byte) text.length;
		buffer.addByte(len);
		for (int i = 0 ; i < len ; i++)
		{
			buffer.addString(text[i]);
			buffer.addByte(id[i]);
			buffer.addByte(type[i]);
			buffer.addByte(color[i]);
			buffer.addShort(x[i]);
			buffer.addShort(y[i]);
			buffer.addShort(width[i]);
			buffer.addShort(height[i]);
		}
	}
	
	@Override
	public void fromBytes(MJPacketBuffer buffer)
	{
		cardID = buffer.getInt();
		byte len = buffer.getByte();
		text = new String[len];
		id = new byte[len];
		type = new byte[len];
		color = new byte[len];
		x = new short[len];
		y = new short[len];
		width = new short[len];
		height = new short[len];
		for (int i = 0 ; i < len ; i++)
		{
			text[i] = buffer.getString();
			id[i] = buffer.getByte();
			type[i] = buffer.getByte();
			color[i] = buffer.getByte();
			x[i] = buffer.getShort();
			y[i] = buffer.getShort();
			width[i] = buffer.getShort();
			height[i] = buffer.getShort();
		}
	}
}
