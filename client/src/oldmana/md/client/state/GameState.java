package oldmana.md.client.state;

import oldmana.md.client.MDClient;
import oldmana.md.client.Player;
import oldmana.md.client.state.client.ActionStateClient;

public class GameState
{
	private Player turn;
	private int turns;
	
	private ActionState state;
	private ActionStateClient clientState;
	
	public Player getActivePlayer()
	{
		return state.getActionOwner();
	}
	
	public Player getWhoseTurn()
	{
		return turn;
	}
	
	public void setWhoseTurn(Player player)
	{
		turn = player;
	}
	
	public int getTurns()
	{
		return turns;
	}
	
	public void setTurns(int turns)
	{
		this.turns = turns;
	}
	
	public ActionState getActionState()
	{
		return state;
	}
	
	public void setActionState(ActionState state)
	{
		if (this.state != null)
		{
			this.state.cleanup();
		}
		this.state = state;
		MDClient client = MDClient.getInstance();
		if (state.isTarget(client.getThePlayer()) || state.getActionOwner() == client.getThePlayer())
		{
			client.getWindow().setAlert(true);
		}
		setClientActionState(null);
		if (state != null)
		{
			state.setup();
		}
	}
	
	public ActionStateClient getClientActionState()
	{
		return clientState;
	}
	
	public void setClientActionState(ActionStateClient state)
	{
		if (clientState != null)
		{
			clientState.cleanup();
		}
		clientState = state;
		if (clientState != null)
		{
			clientState.setup();
		}
	}
	
	public void updateUI()
	{
		if (state != null)
		{
			state.updateUI();
		}
		if (clientState != null)
		{
			clientState.updateUI();
		}
	}
}
