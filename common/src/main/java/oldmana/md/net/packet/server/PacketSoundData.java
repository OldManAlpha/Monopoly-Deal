package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketSoundData extends Packet
{
	public String name;
	public byte[] data;
	public int hash;
	
	public PacketSoundData() {}
	
	public PacketSoundData(String name, byte[] data, int hash)
	{
		this.name = name;
		this.data = data;
		this.hash = hash;
	}
}
