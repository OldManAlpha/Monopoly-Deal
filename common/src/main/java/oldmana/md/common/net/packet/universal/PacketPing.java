package oldmana.md.common.net.packet.universal;

import oldmana.md.common.net.api.packet.Packet;

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
