package oldmana.md.server.card.control.condition;

import oldmana.md.server.MDServer;
import oldmana.md.server.card.control.ButtonCondition;

/**
 * The descendants of this class might be used for serializable conditions in the future.
 */
public abstract class AbstractButtonCondition implements ButtonCondition
{
	protected MDServer getServer()
	{
		return MDServer.getInstance();
	}
}
