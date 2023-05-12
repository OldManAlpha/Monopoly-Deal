package oldmana.md.net.packet.server.actionstate;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionStateBasic extends Packet
{
	public int player;
	public byte type;
	public int data;
	
	public PacketActionStateBasic() {}
	
	public PacketActionStateBasic(int player, BasicActionState type, int data)
	{
		this.player = player;
		this.type = (byte) type.getID();
		this.data = data;
	}
	
	public BasicActionState getType()
	{
		return BasicActionState.fromID(type);
	}
	
	public enum BasicActionState
	{
		NO_STATE(-1), DO_NOTHING(0), TARGET_PLAYER(4),
		TARGET_SELF_PROPERTY(5), TARGET_PLAYER_PROPERTY(6), TARGET_SELF_PLAYER_PROPERTY(7), TARGET_ANY_PROPERTY(8),
		TARGET_PLAYER_MONOPOLY(9), PLAYER_TARGETED(10);
		
		private final int id;
		
		BasicActionState(int id)
		{
			this.id = id;
		}
		
		public int getID()
		{
			return id;
		}
		
		public static BasicActionState fromID(int id)
		{
			for (BasicActionState type : values())
			{
				if (type.getID() == id)
				{
					return type;
				}
			}
			return null;
		}
	}
}
