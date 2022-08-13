package oldmana.md.net.packet.universal;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketPing extends Packet
{
	public int protocolVersion;
	public String version;
	
	public PacketPing() {}
	
	public PacketPing(int protocolVersion, String version)
	{
		this.protocolVersion = protocolVersion;
		this.version = version;
	}
}
