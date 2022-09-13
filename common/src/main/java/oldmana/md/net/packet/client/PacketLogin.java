package oldmana.md.net.packet.client;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketLogin extends Packet
{
	public int protocolVersion;
	public byte[] id;
	public String name;
	
	public PacketLogin() {}
	
	public PacketLogin(int protocolVersion, byte[] id, String name)
	{
		this.protocolVersion = protocolVersion;
		this.id = id;
		this.name = name;
	}
}
