package oldmana.md.client.state;

import oldmana.md.client.Player;

public class ActionStateDraw extends ActionState
{
	public ActionStateDraw(Player player)
	{
		super(player);
	}
	
	@Override
	public boolean isTurnState()
	{
		return true;
	}
	
	@Override
	public void setup()
	{
	
	}
}
