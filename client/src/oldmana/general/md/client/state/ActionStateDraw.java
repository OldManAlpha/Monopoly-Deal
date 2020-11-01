package oldmana.general.md.client.state;

import oldmana.general.md.client.Player;

public class ActionStateDraw extends ActionState
{
	public ActionStateDraw(Player player)
	{
		super(player);
	}
	
	@Override
	public void setup()
	{
		getGameState().setWhoseTurn(getActionOwner());
	}
}
