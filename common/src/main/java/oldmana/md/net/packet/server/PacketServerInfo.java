package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketServerInfo extends Packet
{
	public int protocolVersion;
	public byte[] serverKey;
	
	public PacketServerInfo() {}
	
	public PacketServerInfo(int protocolVersion, byte[] serverKey)
	{
		this.protocolVersion = protocolVersion;
		this.serverKey = serverKey;
	}
}
