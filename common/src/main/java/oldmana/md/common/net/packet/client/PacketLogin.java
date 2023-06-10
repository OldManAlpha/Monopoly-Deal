package oldmana.md.common.net.packet.client;

import oldmana.md.common.net.api.packet.Packet;

public class PacketLogin extends Packet
{
	public int protocolVersion;
	public String clientVersion;
	public byte[] id;
	public String name;
	
	public PacketLogin() {}
	
	public PacketLogin(int protocolVersion, String clientVersion, byte[] id, String name)
	{
		this.protocolVersion = protocolVersion;
		this.clientVersion = clientVersion;
		this.id = id;
		this.name = name;
	}
}
