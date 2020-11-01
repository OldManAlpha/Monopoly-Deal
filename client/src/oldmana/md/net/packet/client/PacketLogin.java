package oldmana.md.net.packet.client;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketLogin extends Packet
{
	public int protocolVersion;
	public int uid;
	
	public PacketLogin()
	{
		
	}
	
	public PacketLogin(int protocolVersion, int uid)
	{
		this.protocolVersion = protocolVersion;
		this.uid = uid;
	}
	
	public int getProtocolVersion()
	{
		return protocolVersion;
	}
	
	public int getUID()
	{
		return uid;
	}
	
	/*
	@Override
	public void fromBytes(MJPacketBuffer data)
	{
		protocolVersion = data.getInt();
		uid = data.getInt();
	}

	@Override
	public byte[] toBytes(MJPacketBuffer data)
	{
		data.addInt(8);
		data.addInt(protocolVersion);
		data.addInt(uid);
		return data.toByteArray();
	}
	*/
}
