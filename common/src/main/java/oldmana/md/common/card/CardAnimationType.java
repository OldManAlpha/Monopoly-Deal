package oldmana.md.common.card;

public enum CardAnimationType
{
	NORMAL(0), IMPORTANT(1);
	
	private final int id;
	
	CardAnimationType(int id)
	{
		this.id = id;
	}
	
	public int getID()
	{
		return id;
	}
	
	public static CardAnimationType fromID(int id)
	{
		for (CardAnimationType type : values())
		{
			if (type.getID() == id)
			{
				return type;
			}
		}
		return null;
	}
}
