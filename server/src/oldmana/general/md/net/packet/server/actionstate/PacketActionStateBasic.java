package oldmana.general.md.net.packet.server.actionstate;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionStateBasic extends Packet
{
	public int player;
	public int type;
	public int data;
	
	public PacketActionStateBasic() {}
	
	public PacketActionStateBasic(int player, BasicActionState type, int data)
	{
		this.player = player;
		this.type = type.getID();
		this.data = data;
	}
	
	public static enum BasicActionState
	{
		DO_NOTHING(-1), DRAW(0), PLAY(1), DISCARD(2), FINISH_TURN(3), TARGET_PLAYER(4), TARGET_PLAYER_PROPERTY(5), TARGET_SELF_PLAYER_PROPERTY(6), 
		TARGET_PLAYER_MONOPOLY(7);
		
		int id;
		
		BasicActionState(int id)
		{
			this.id = id;
		}
		
		public int getID()
		{
			return id;
		}
	}
}
