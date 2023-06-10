package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketUndoCardStatus extends Packet
{
	public int cardId;
	
	public PacketUndoCardStatus() {}
	
	public PacketUndoCardStatus(int cardId)
	{
		this.cardId = cardId;
	}
}
