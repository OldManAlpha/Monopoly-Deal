package oldmana.md.server.mod;

public class ModNotFoundException extends RuntimeException
{
	public ModNotFoundException(String modName)
	{
		super("Mod '" + modName + "' is not loaded");
	}
}
