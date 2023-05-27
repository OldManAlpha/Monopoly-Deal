package oldmana.md.client.state;

import oldmana.md.client.MDClient;
import oldmana.md.client.MDSoundSystem;
import oldmana.md.client.Player;
import oldmana.md.client.ThePlayer;
import oldmana.md.client.gui.component.MDChat;
import oldmana.md.client.state.client.ActionStateClient;
import oldmana.md.client.state.primary.ActionStatePlayerTurn;

import javax.swing.SwingUtilities;

public class GameState
{
	private ActionStatePlayerTurn turn;
	
	private ActionState state;
	private ActionStateClient clientState;
	
	public Player getActivePlayer()
	{
		return state.getActionOwner();
	}
	
	public Player getWhoseTurn()
	{
		return turn != null ? turn.getActionOwner() : null;
	}
	
	public int getMoves()
	{
		return turn != null ? turn.getMoves() : 0;
	}
	
	public ActionState getActionState()
	{
		return state;
	}
	
	public void setPlayerTurn(ActionStatePlayerTurn state)
	{
		turn = state;
		if (state == null)
		{
			if (this.state != null && this.state.isTurnState())
			{
				setActionState(null);
			}
			return;
		}
		if (this.state == null || this.state.isTurnState())
		{
			setActionState(state.createClientState());
		}
	}
	
	public ActionStatePlayerTurn getPlayerTurn()
	{
		return turn;
	}
	
	public void setActionState(ActionState state)
	{
		ActionState prevState = this.state;
		if (this.state != null)
		{
			this.state.cleanup();
		}
		if (state == null)
		{
			if (turn == null)
			{
				return;
			}
			this.state = turn.createClientState();
			state = this.state;
			System.out.println(this.state.getClass().getName());
		}
		else
		{
			System.out.println(state.getClass().getName());
			this.state = state;
		}
		MDClient client = MDClient.getInstance();
		if (state.isTarget(client.getThePlayer()) || state.getActionOwner() == client.getThePlayer())
		{
			if (state.isTarget(client.getThePlayer()))
			{
				client.getTableScreen().getTopbar().triggerAlert();
			}
			if (state.isTarget(client.getThePlayer()) || state.getActionOwner() == client.getThePlayer())
			{
				client.getWindow().setAlert(true);
			}
		}
		setClientActionState(null);
		state.setup();
		if (state.getActionOwner() instanceof ThePlayer && state instanceof ActionStateDraw)
		{
			if (prevState == null || (prevState.getActionOwner() == state.getActionOwner() && prevState instanceof ActionStateDraw))
			{
				return;
			}
			MDSoundSystem.playSound("DrawAlert");
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
		SwingUtilities.invokeLater(() ->
		{
			if (state != null)
			{
				state.updateUI();
			}
			if (clientState != null)
			{
				clientState.updateUI();
			}
			MDClient.getInstance().getTableScreen().repaint();
		});
	}
}
