package oldmana.md.common.net.packet.client;

import oldmana.md.common.net.api.packet.Packet;

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
