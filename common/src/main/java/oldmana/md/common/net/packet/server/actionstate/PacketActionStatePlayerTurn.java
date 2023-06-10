package oldmana.md.common.net.packet.server.actionstate;

import oldmana.md.common.net.api.packet.Packet;

public class PacketActionStatePlayerTurn extends Packet
{
	public int player;
	public byte state;
	public int moves;
	
	public PacketActionStatePlayerTurn() {}
	
	public PacketActionStatePlayerTurn(int player, TurnState state, int moves)
	{
		this.player = player;
		this.state = (byte) state.ordinal();
		this.moves = moves;
	}
	
	public TurnState getTurnState()
	{
		return TurnState.values()[state];
	}
	
	public enum TurnState
	{
		DRAW, PLAY, DISCARD, REMOVE_STATE;
	}
}
