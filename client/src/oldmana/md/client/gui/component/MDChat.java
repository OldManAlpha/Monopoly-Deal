package oldmana.md.client.gui.component;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import oldmana.md.client.MDScheduler.MDTask;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;
import oldmana.md.net.packet.universal.PacketChat;

public class MDChat extends MDComponent
{
	private List<Message> messages = new ArrayList<Message>();
	private int scroll;
	
	private StringBuilder typed = new StringBuilder();
	private int typeOffset;
	private int typeDisplayOffset;
	
	private boolean chatOpen = false;
	
	public MDChat()
	{
		getClient().getScheduler().scheduleTask(new MDTask(1, true)
		{
			@Override
			public void run()
			{
				boolean update = false;
				if (chatOpen)
				{
					update = true;
				}
				for (Message m : messages)
				{
					if (m.tick())
					{
						update = true;
					}
				}
				if (update)
				{
					repaint();
				}
			}
		});
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher()
		{
			@Override
			public boolean dispatchKeyEvent(KeyEvent event)
			{
				if (event.getKeyCode() == KeyEvent.VK_T && !chatOpen)
				{
					chatOpen = true;
					requestFocus();
					return true;
				}
				return false;
			}
		});
		addKeyListener(new KeyListener()
		{
			@Override
			public void keyPressed(KeyEvent event)
			{
				if (!chatOpen)
				{
					return;
				}
				if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE)
				{
					int index = typed.length() - typeOffset - 1;
					if (index > -1)
					{
						typed.deleteCharAt(typed.length() - typeOffset - 1);
					}
				}
				else if (event.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if (typed.length() > 0)
					{
						getClient().sendPacket(new PacketChat(typed.toString()));
						typed = new StringBuilder();
					}
					typeOffset = 0;
					scroll = 0;
					chatOpen = false;
					repaint();
				}
				else if (event.getKeyCode() == KeyEvent.VK_LEFT)
				{
					typeOffset = Math.min(typeOffset + 1, typed.length());
				}
				else if (event.getKeyCode() == KeyEvent.VK_RIGHT)
				{
					typeOffset = Math.max(typeOffset - 1, 0);
				}
				else if (event.getKeyCode() == KeyEvent.VK_UP)
				{
					scroll = Math.min(Math.max(messages.size() - 12, 0), scroll + 1);
				}
				else if (event.getKeyCode() == KeyEvent.VK_DOWN)
				{
					scroll = Math.max(0, scroll - 1);
				}
				else if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					chatOpen = false;
					typed = new StringBuilder();
					typeOffset = 0;
					scroll = 0;
					repaint();
				}
				else if (event.getKeyChar() != '\uFFFF' && event.getKeyCode() != KeyEvent.VK_DELETE)
				{
					typed.insert(typed.length() - typeOffset, event.getKeyChar());
				}
			}

			@Override
			public void keyReleased(KeyEvent event)
			{
				
			}

			@Override
			public void keyTyped(KeyEvent event)
			{
				
			}
		});
		/*
		addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent event)
			{
				int amt = event.getUnitsToScroll();
				if (amt < 0)
				{
					scroll = Math.min(Math.max(messages.size() - 12, 0), scroll + 1);
				}
				else if (amt > 0)
				{
					scroll = Math.max(0, scroll - 1);
				}
			}
		});
		*/
	}
	
	public void addMessage(String text)
	{
		messages.add(0, new Message(text));
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		//g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		
		int chatHistoryHeight = getHeight() - (int) (getHeight() * 0.15);
		int chatHeight = (int) (getHeight() * 0.1);
		int xOffset = scale(8);
		int offsetWidth = getWidth() - xOffset;
		
		Font f = GraphicsUtils.getBoldMDFont(scale(24));
		
		int msgIndex = scroll;
		for (int i = 0 ; i < 12 ; i++)
		{
			if (messages.size() > msgIndex)
			{
				Message m = messages.get(msgIndex);
				if (m.getDisplayTicks() > 0 || chatOpen)
				{
					double opacity = 1;
					if (!chatOpen)
					{
						opacity = Math.min(1, m.getDisplayTicks() * (1 / 90.0));
					}
					int interval = chatHistoryHeight / 12;
					List<String> lines = GraphicsUtils.splitString(m.getText(), g.getFontMetrics(f), offsetWidth);
					for (int l = 1 ; l <= lines.size() ; l++)
					{
						int pos = -(i - 11) - lines.size() + l;
						String line = lines.get(l - 1);
						
						// Draw Transparent Text Area
						g.setColor(new Color(0, 0, 0, (int) (60 * opacity)));
						g.fillRect(xOffset, interval * pos, offsetWidth, interval);
						
						// Draw Text Shadow
						g.setColor(new Color(20, 20, 20, (int) (255 * opacity)));
						TextPainter tp = new TextPainter(line, f, new Rectangle(xOffset + scale(2), interval * pos + scale(2), offsetWidth, interval));
						tp.setVerticalAlignment(Alignment.CENTER);
						tp.paint(g);
						
						// Draw Text
						g.setColor(new Color(255, 255, 255, (int) (255 * opacity)));
						tp = new TextPainter(line, f, new Rectangle(xOffset, interval * pos, offsetWidth, interval));
						tp.setVerticalAlignment(Alignment.CENTER);
						tp.paint(g);
					}
					i += lines.size() - 1;
				}
				msgIndex++;
			}
			else
			{
				break;
			}
		}
		
		if (chatOpen)
		{
			g.setColor(new Color(0, 0, 0, 80));
			g.fillRect(0, getHeight() - chatHeight, getWidth(), chatHeight);
			FontMetrics metrics = g.getFontMetrics(f);
			int typedWidth = metrics.stringWidth(typed.toString());
			int typedOffsetWidth = metrics.stringWidth(typed.substring(0, typed.length() - typeDisplayOffset));
			int posOffsetWidth = metrics.stringWidth(typed.substring(0, typed.length() - typeOffset));
			g.setColor(new Color(0, 0, 0));
			if (typedOffsetWidth - getWidth() > 0)
			{
				g.translate(getWidth() - scale(4) - typedOffsetWidth, 0);
			}
			int y = getHeight();// - (chatHeight / 2) - (metrics.getHeight() / 2);
			//g.setFont(f);
			//g.drawString(typed.toString(), 0, y);
			TextPainter tp = new TextPainter(typed.toString(), f, new Rectangle(0, getHeight() - chatHeight, getWidth() - scale(4), chatHeight), false, false);
			tp.setVerticalAlignment(Alignment.CENTER);
			tp.paint(g);
			int cursorPos = posOffsetWidth;
			if (System.currentTimeMillis() % 1000 >= 500)
			{
				g.fillRect(cursorPos, getHeight() - chatHeight + scale(3), scale(3), chatHeight - scale(6));
			}
			//g.drawString(typed.toString(), x, y);
		}
	}
	
	public class Message
	{
		private String text;
		private int displayTicks = 360;
		
		public Message(String text)
		{
			this.text = text;
		}
		
		public String getText()
		{
			return text;
		}
		
		public int getDisplayTicks()
		{
			return displayTicks;
		}
		
		public boolean tick()
		{
			if (displayTicks > 0)
			{
				displayTicks--;
				return true;
			}
			return false;
		}
	}
}
