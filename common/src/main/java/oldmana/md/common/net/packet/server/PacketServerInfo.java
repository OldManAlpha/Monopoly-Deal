package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

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
