package oldmana.md.net.packet.client;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketInitiateLogin extends Packet
{
	public int protocolVersion;
	
	public PacketInitiateLogin() {}
	
	public PacketInitiateLogin(int protocolVersion)
	{
		this.protocolVersion = protocolVersion;
	}
}
