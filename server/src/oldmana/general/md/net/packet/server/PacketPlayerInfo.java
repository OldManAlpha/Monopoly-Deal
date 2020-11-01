package oldmana.general.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketPlayerInfo extends Packet
{
	public int id;
	public String name;
	public boolean connected;
	
	public PacketPlayerInfo() {}
	
	public PacketPlayerInfo(int id, String name, boolean connected)
	{
		this.id = id;
		this.name = name;
		this.connected = connected;
	}
	
	/*
	@Override
	public void fromBytes(MJPacketBuffer data)
	{
		id = data.getInt();
		name = data.getString();
	}

	@Override
	public byte[] toBytes(MJPacketBuffer data)
	{
		data.addInt(4 + name.getBytes().length);
		data.addInt(id);
		data.addString(name);
		return data.toByteArray();
	}
	*/
}
