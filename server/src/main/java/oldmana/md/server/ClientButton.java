package oldmana.md.server;

import oldmana.md.net.packet.server.PacketButton;

import java.util.function.Consumer;

public class ClientButton
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
	
	private Consumer<ClientButton> listener;
	private ButtonTag tag;
	
	public ClientButton()
	{
		id = nextID++;
		setBlank();
	}
	
	public ClientButton(String text, ButtonColorScheme color, Consumer<ClientButton> listener)
	{
		id = nextID++;
		this.text = text;
		this.enabled = true;
		this.color = color;
		this.listener = listener;
	}
	
	public ClientButton(String text, Consumer<ClientButton> listener)
	{
		id = nextID++;
		this.text = text;
		this.enabled = true;
		this.color = ButtonColorScheme.NORMAL;
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
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public ButtonColorScheme getColor()
	{
		return color;
	}
	
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
	
	public void setListener(Consumer<ClientButton> listener)
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
		owner.sendPacket(new PacketButton(getID(), view.getID(), getText(), isEnabled(), getColor().getID(), getPriority(), getMaxSize()));
	}
	
	public enum ButtonColorScheme
	{
		NORMAL(0), ALERT(1);
		
		private final int id;
		
		ButtonColorScheme(int id)
		{
			this.id = id;
		}
		
		public byte getID()
		{
			return (byte) id;
		}
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
