package oldmana.md.server.playerui;

import oldmana.md.common.playerui.Button;
import oldmana.md.common.playerui.ButtonColorScheme;
import oldmana.md.net.packet.server.PacketPlayerButton;
import oldmana.md.server.Player;

import java.util.function.Consumer;

public class PlayerButton implements Button
{
	private static int nextID;
	
	private int id;
	
	private Player owner;
	private Player view;
	
	private String text;
	private boolean enabled;
	private ButtonColorScheme color;
	
	private int priority = 100;
	private double maxSize = 0.5;
	
	private Consumer<PlayerButton> listener;
	private ButtonTag tag;
	
	public PlayerButton()
	{
		id = nextID++;
		setBlank();
	}
	
	public PlayerButton(String text)
	{
		id = nextID++;
		this.text = text;
		this.enabled = true;
		this.color = ButtonColorScheme.NORMAL;
	}
	
	public PlayerButton(String text, Consumer<PlayerButton> listener)
	{
		id = nextID++;
		this.text = text;
		this.enabled = true;
		this.color = ButtonColorScheme.NORMAL;
		this.listener = listener;
	}
	
	public PlayerButton(String text, ButtonColorScheme color, Consumer<PlayerButton> listener)
	{
		id = nextID++;
		this.text = text;
		this.enabled = true;
		this.color = color;
		this.listener = listener;
	}
	
	public int getID()
	{
		return id;
	}
	
	public Player getOwner()
	{
		return owner;
	}
	
	public void setOwner(Player owner)
	{
		this.owner = owner;
	}
	
	public Player getView()
	{
		return view;
	}
	
	public void setView(Player view)
	{
		this.view = view;
	}
	
	public void remove()
	{
		owner.removeButton(this);
	}
	
	public void setBlank(boolean enabled)
	{
		text = "";
		this.enabled = enabled;
		color = ButtonColorScheme.NORMAL;
		listener = null;
	}
	
	public void setBlank()
	{
		setBlank(false);
	}
	
	public void build(String name)
	{
		setBlank(true);
		this.text = name;
	}
	
	public void build(String name, ButtonColorScheme color)
	{
		build(name);
		this.color = color;
	}
	
	@Override
	public String getText()
	{
		return text;
	}
	
	@Override
	public void setText(String text)
	{
		this.text = text;
	}
	
	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	@Override
	public ButtonColorScheme getColor()
	{
		return color;
	}
	
	@Override
	public void setColor(ButtonColorScheme color)
	{
		this.color = color;
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
	
	public void setListener(Consumer<PlayerButton> listener)
	{
		this.listener = listener;
	}
	
	public void removeListener()
	{
		listener = null;
	}
	
	public ButtonTag getTag()
	{
		return tag;
	}
	
	public void setTag(ButtonTag tag)
	{
		this.tag = tag;
	}
	
	public void buttonClicked()
	{
		if (listener != null)
		{
			listener.accept(this);
		}
	}
	
	public void register(Player owner, Player view)
	{
		owner.registerButton(this, view);
	}
	
	public void sendUpdate()
	{
		owner.sendPacket(new PacketPlayerButton(getID(), view.getID(), getText(), isEnabled(), (byte) getColor().getID(), getPriority(), getMaxSize()));
	}
	
	public static class ButtonTag
	{
		private String name;
		
		public ButtonTag(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return name;
		}
	}
}
