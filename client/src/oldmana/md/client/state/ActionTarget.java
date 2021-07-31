package oldmana.md.client.state;

import javax.swing.JComponent;

import oldmana.md.client.Player;

public class ActionTarget
{
	private Player target;
	private boolean refused;
	private boolean accepted;
	
	private JComponent overlay;
	
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
	
	public JComponent getOverlay()
	{
		return overlay;
	}
	
	public boolean hasOverlay()
	{
		return overlay != null;
	}
	
	public void setOverlay(JComponent overlay)
	{
		this.overlay = overlay;
	}
}
