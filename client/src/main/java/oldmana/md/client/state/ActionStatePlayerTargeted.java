package oldmana.md.client.state;

import oldmana.md.client.Player;

public class ActionStatePlayerTargeted extends ActionState
{
	public ActionStatePlayerTargeted(Player actionOwner, Player actionTarget)
	{
		super(actionOwner, actionTarget);
	}
}
