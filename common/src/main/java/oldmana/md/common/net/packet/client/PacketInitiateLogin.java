package oldmana.md.common.net.packet.client;

import oldmana.md.common.net.api.packet.Packet;

public class PacketInitiateLogin extends Packet
{
	public int protocolVersion;
	
	public PacketInitiateLogin() {}
	
	public PacketInitiateLogin(int protocolVersion)
	{
		this.protocolVersion = protocolVersion;
	}
}
