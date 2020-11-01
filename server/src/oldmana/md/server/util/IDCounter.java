package oldmana.md.server.util;

public class IDCounter
{
	public static int lastPlayerID = -1;
	public static int lastCardID = -1;
	public static int lastCollectionID = -1;
	
	public static int nextPlayerID()
	{
		return ++lastPlayerID;
	}
	
	public static int nextCardID()
	{
		return ++lastCardID;
	}
	
	public static int nextCollectionID()
	{
		return ++lastCollectionID;
	}
}
