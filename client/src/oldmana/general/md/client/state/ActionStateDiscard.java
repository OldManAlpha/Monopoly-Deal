package oldmana.general.md.client.state;

import oldmana.general.md.client.Player;

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
