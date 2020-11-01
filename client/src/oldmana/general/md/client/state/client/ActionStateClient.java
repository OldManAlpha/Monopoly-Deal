package oldmana.general.md.client.state.client;

import oldmana.general.md.client.MDClient;
import oldmana.general.md.client.state.GameState;

public abstract class ActionStateClient
{
	public abstract void setup();
	
	public abstract void cleanup();
	
	public void removeState()
	{
		getClient().getGameState().setCurrentClientActionState(null);
	}
	
	public GameState getGameState()
	{
		return getClient().getGameState();
	}
	
	public MDClient getClient()
	{
		return MDClient.getInstance();
	}
}
