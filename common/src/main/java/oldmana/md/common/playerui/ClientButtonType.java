package oldmana.md.common.playerui;

/**
 * Idea on the back burner..
 */
public enum ClientButtonType
{
	/** Normal buttons simply notify the server when they're clicked **/
	NORMAL,
	/** Special buttons perform special client-sided actions when clicked, such as displaying the rent screen **/
	SPECIAL,
	/** Displays the current undoable card when hovered over, otherwise a normal button **/
	UNDO;
	
	public int getID()
	{
		return ordinal();
	}
	
	public static ClientButtonType fromID(int id)
	{
		return values()[id];
	}
}
