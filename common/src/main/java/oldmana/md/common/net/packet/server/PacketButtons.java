package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.MJPacketBuffer;
import oldmana.md.common.net.api.packet.ComplexPacket;
import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.playerui.ClientButtonType;

import java.util.ArrayList;
import java.util.List;

public class PacketButtons extends Packet implements ComplexPacket
{
	public String[] text;
	public boolean[] enabled;
	public byte[] color;
	public byte[] type;
	
	public PacketButtons() {}
	
	public PacketButtons(String[] text)
	{
	
	}
	
	public List<ClientButtonType> getTypes()
	{
		List<ClientButtonType> buttonTypes = new ArrayList<ClientButtonType>(type.length);
		for (byte b : type)
		{
			buttonTypes.add(ClientButtonType.fromID(b));
		}
		return buttonTypes;
	}
	
	@Override
	public void toBytes(MJPacketBuffer buffer)
	{
		byte len = (byte) text.length;
		buffer.addByte(len);
		for (int i = 0 ; i < len ; i++)
		{
			buffer.addString(text[i]);
			buffer.addBoolean(enabled[i]);
			buffer.addByte(color[i]);
			buffer.addByte(type[i]);
		}
	}
	
	@Override
	public void fromBytes(MJPacketBuffer buffer)
	{
		byte len = buffer.getByte();
		text = new String[len];
		enabled = new boolean[len];
		color = new byte[len];
		for (int i = 0 ; i < len ; i++)
		{
			text[i] = buffer.getString();
			enabled[i] = buffer.getBoolean();
			color[i] = buffer.getByte();
			type[i] = buffer.getByte();
		}
	}
}
