package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.MJPacketBuffer;
import oldmana.md.common.net.api.packet.ComplexPacket;
import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.util.DataUtil;

import java.awt.Color;

public class PacketInfoPlate extends Packet implements ComplexPacket
{
	public int player;
	public int id;
	public short priority;
	public String text;
	public Color textColor;
	public Color color;
	public Color borderColor;
	
	public PacketInfoPlate() {}
	
	public PacketInfoPlate(int player, int id, int priority, String text, Color textColor, Color color, Color borderColor)
	{
		this.player = player;
		this.id = id;
		this.priority = (short) priority;
		this.text = text;
		this.textColor = textColor;
		this.color = color;
		this.borderColor = borderColor;
	}
	
	@Override
	public void toBytes(MJPacketBuffer buffer)
	{
		buffer.addInt(player);
		buffer.addInt(id);
		buffer.addShort(priority);
		buffer.addString(text);
		buffer.addByte(DataUtil.convertBooleansToByte(textColor != null, color != null, borderColor != null));
		if (textColor != null)
		{
			buffer.addInt(textColor.getRGB());
		}
		if (color != null)
		{
			buffer.addInt(color.getRGB());
		}
		if (borderColor != null)
		{
			buffer.addInt(borderColor.getRGB());
		}
	}
	
	@Override
	public void fromBytes(MJPacketBuffer buffer)
	{
		player = buffer.getInt();
		id = buffer.getInt();
		priority = buffer.getShort();
		text = buffer.getString();
		boolean[] definedColors = DataUtil.convertByteToBooleans(buffer.getByte());
		if (definedColors[0])
		{
			textColor = new Color(buffer.getInt());
		}
		if (definedColors[1])
		{
			color = new Color(buffer.getInt());
		}
		if (definedColors[2])
		{
			borderColor = new Color(buffer.getInt());
		}
	}
}
