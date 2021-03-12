package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketSoundData extends Packet
{
	public String name;
	public byte[] data;
	
	public PacketSoundData() {}
	
	public PacketSoundData(String name, byte[] data)
	{
		this.name = name;
		this.data = data;
	}
}
