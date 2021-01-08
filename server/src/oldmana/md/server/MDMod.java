package oldmana.md.server;

public abstract class MDMod
{
	private String name;
	
	public MDMod(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public MDServer getServer()
	{
		return MDServer.getInstance();
	}
	
	public abstract void onLoad();
}
