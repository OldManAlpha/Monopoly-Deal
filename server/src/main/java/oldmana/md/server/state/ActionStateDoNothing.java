package oldmana.md.server.state;

/**
 * Used for when the game is not running.
 */
public class ActionStateDoNothing extends ActionStateIdle
{
	public ActionStateDoNothing() {}
	
	public ActionStateDoNothing(String status)
	{
		setStatus(status);
	}
	
	@Override
	public boolean isImportant()
	{
		return false;
	}
}
