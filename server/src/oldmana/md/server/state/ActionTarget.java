package oldmana.md.server.state;

import oldmana.md.server.Player;

public class ActionTarget
{
	private Player target;
	private boolean refused;
	private boolean accepted;
	
	public ActionTarget(Player target)
	{
		this.target = target;
	}
	
	public Player getPlayer()
	{
		return target;
	}
	
	public boolean isRefused()
	{
		return refused;
	}
	
	public void setRefused(boolean refused)
	{
		this.refused = refused;
	}
	
	public boolean isAccepted()
	{
		return accepted;
	}
	
	public void setAccepted(boolean accepted)
	{
		this.accepted = accepted;
	}
}
