package oldmana.md.server.event;

import oldmana.md.server.MDServer;
import oldmana.md.server.rules.GameRules;

public class GameRulesReloadedEvent extends Event
{
	public GameRules getGameRules()
	{
		return MDServer.getInstance().getGameRules();
	}
}
