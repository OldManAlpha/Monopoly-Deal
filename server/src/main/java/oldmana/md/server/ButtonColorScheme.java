package oldmana.md.server;

public enum ButtonColorScheme
{
	NORMAL(0), ALERT(1);
	
	private final int id;
	
	ButtonColorScheme(int id)
	{
		this.id = id;
	}
	
	public byte getID()
	{
		return (byte) id;
	}
}
