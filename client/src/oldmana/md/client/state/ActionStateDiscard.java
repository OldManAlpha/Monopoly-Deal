package oldmana.md.client.state;

import oldmana.md.client.Player;

public class ActionStateDiscard extends ActionState
{
	public ActionStateDiscard(Player player)
	{
		super(player);
	}
	
	@Override
	public void setup()
	{
		getGameState().setWhoseTurn(getActionOwner());
		getGameState().setTurns(0);
	}
}
