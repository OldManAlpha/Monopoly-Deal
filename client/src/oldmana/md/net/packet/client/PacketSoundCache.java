package oldmana.md.net.packet.client;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketSoundCache extends Packet
{
	public String[] cachedSounds;
	public int[] soundHashes;
	
	public PacketSoundCache() {}
	
	public PacketSoundCache(String[] cachedSounds, int[] soundHashes)
	{
		this.cachedSounds = cachedSounds;
		this.soundHashes = soundHashes;
	}
}
