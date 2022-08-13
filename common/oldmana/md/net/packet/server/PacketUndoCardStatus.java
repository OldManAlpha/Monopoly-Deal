package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketUndoCardStatus extends Packet
{
	public int cardId;
	
	public PacketUndoCardStatus() {}
	
	public PacketUndoCardStatus(int cardId)
	{
		this.cardId = cardId;
	}
}
