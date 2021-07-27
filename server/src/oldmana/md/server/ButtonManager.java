package oldmana.md.server;

import java.util.HashMap;
import java.util.Map;

import oldmana.md.net.packet.server.PacketButton;

public class ButtonManager
{
	private MDServer server;
	
	private Map<Integer, PlayerButtonSlot> slots = new HashMap<Integer, PlayerButtonSlot>();
	
	public ButtonManager(MDServer server)
	{
		this.server = server;
		
		slots.put(0, new PlayerButtonSlot(0)); // Action button slot reserved by the server
	}
	
	public PlayerButtonSlot requestButtonSlot()
	{
		for (int i = 0 ; i < 3 ; i++)
		{
			if (!slots.containsKey(i))
			{
				PlayerButtonSlot slot = new PlayerButtonSlot(i);
				slots.put(i, slot);
				return slot;
			}
		}
		return null;
	}
	
	public void relinquishButtonSlot(int index)
	{
		slots.remove(index);
	}
	
	public PlayerButtonSlot getExistingButtonSlot(int index)
	{
		return slots.get(index);
	}
	
	private MDServer getServer()
	{
		return server;
	}
	
	public class PlayerButtonSlot
	{
		private int index;
		
		public PlayerButtonSlot(int index)
		{
			this.index = index;
		}
		
		public int getIndex()
		{
			return index;
		}
		
		public PlayerButton getButton(Player player, Player view)
		{
			return player.getButtonView(view, index);
		}
		
		public void clearButtons()
		{
			for (Player player : getServer().getPlayers())
			{
				player.clearButtons(index);
			}
		}
		
		public void sendUpdate()
		{
			for (Player player : getServer().getPlayers())
			{
				player.sendButtonPackets(index);
			}
		}
	}
	
	public static class BaseButton
	{
		private String text;
		private boolean enabled;
		private ButtonColorScheme color;
		
		private ButtonListener listener;
		
		public BaseButton()
		{
			setBlank();
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
		
		public void setListener(ButtonListener listener)
		{
			this.listener = listener;
		}
		
		public void removeListener()
		{
			listener = null;
		}
		
		public void buttonClicked()
		{
			if (listener != null)
			{
				listener.buttonClicked();
			}
		}
	}
	
	public static class PlayerButton extends BaseButton
	{
		private Player player;
		private Player view;
		private int index;
		
		private PlayerButtonType type;
		
		public PlayerButton(Player player, Player view, int index)
		{
			this.player = player;
			this.view = view;
			this.index = index;
			setBlank();
		}
		
		public Player getPlayer()
		{
			return player;
		}
		
		public Player getView()
		{
			return view;
		}
		
		@Override
		public void setBlank(boolean enabled)
		{
			super.setBlank(enabled);
			type = PlayerButtonType.NORMAL;
		}
		
		public void setAcceptRefuseText(String accept, String refuse)
		{
			setText(accept + "`" + refuse);
		}
		
		public PlayerButtonType getType()
		{
			return type;
		}
		
		public void setType(PlayerButtonType type)
		{
			this.type = type;
		}
		
		public void sendUpdate()
		{
			player.sendPacket(new PacketButton(index, view.getID(), getText(), isEnabled(), getType().getID(), getColor().getID()));
		}
	}
	
	public static interface ButtonListener
	{
		public void buttonClicked();
	}
	
	public static enum PlayerButtonType
	{
		NORMAL(0), RENT(1), REFUSABLE(2);
		
		private int id;
		
		PlayerButtonType(int id)
		{
			this.id = id;
		}
		
		public byte getID()
		{
			return (byte) id;
		}
	}
	
	public static enum ButtonColorScheme
	{
		NORMAL(0), ALERT(1);
		
		private int id;
		
		ButtonColorScheme(int id)
		{
			this.id = id;
		}
		
		public byte getID()
		{
			return (byte) id;
		}
	}
}
