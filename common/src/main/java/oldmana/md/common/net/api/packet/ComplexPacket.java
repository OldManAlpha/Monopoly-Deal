package oldmana.md.common.net.api.packet;

import oldmana.md.common.net.api.MJPacketBuffer;

public interface ComplexPacket
{
	void toBytes(MJPacketBuffer buffer);
	
	void fromBytes(MJPacketBuffer buffer);
}
