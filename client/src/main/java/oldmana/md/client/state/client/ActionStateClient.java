package oldmana.md.client.state.client;

import oldmana.md.client.MDClient;
import oldmana.md.client.state.GameState;

public abstract class ActionStateClient
{
	public abstract void setup();
	
	public abstract void cleanup();
	
	public void updateUI() {};
	
	public void removeState()
	{
		getClient().getGameState().setClientActionState(null);
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
