package oldmana.md.common.playerui;

public enum CardButtonType
{
	NORMAL(0), PROPERTY(1), ACTION_COUNTER(2), BUILDING(3);
	
	private final int id;
	
	CardButtonType(int id)
	{
		this.id = id;
	}
	
	public byte getID()
	{
		return (byte) id;
	}
	
	public static CardButtonType fromID(int id)
	{
		for (CardButtonType type : values())
		{
			if (type.getID() == id)
			{
				return type;
			}
		}
		return null;
	}
}
