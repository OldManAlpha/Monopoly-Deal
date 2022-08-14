package oldmana.md.client.gui.component;

import oldmana.md.client.Player;
import oldmana.md.net.packet.client.action.PacketActionButtonClick;

public class MDClientButton extends MDButton
{
	private Player view;
	private int id;
	private int priority;
	private double maxSize;
	
	public MDClientButton(Player view, int id)
	{
		super("");
		this.view = view;
		this.id = id;
		setListener(() ->
		{
			if (isEnabled() && !getClient().isInputBlocked())
			{
				getClient().sendPacket(new PacketActionButtonClick(id));
				setEnabled(false);
			}
		});
	}
	
	public Player getView()
	{
		return view;
	}
	
	public int getID()
	{
		return id;
	}
	
	public int getPriority()
	{
		return priority;
	}
	
	public void setPriority(int priority)
	{
		this.priority = priority;
	}
	
	public double getMaxSize()
	{
		return maxSize;
	}
	
	public void setMaxSize(double maxSize)
	{
		this.maxSize = maxSize;
	}
}
