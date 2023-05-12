package oldmana.general.mjnetworkingapi.packet;

import oldmana.general.mjnetworkingapi.MJPacketBuffer;

public interface ComplexPacket
{
	void toBytes(MJPacketBuffer buffer);
	
	void fromBytes(MJPacketBuffer buffer);
}
