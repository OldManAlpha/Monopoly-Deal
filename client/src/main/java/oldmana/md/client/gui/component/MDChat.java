package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import oldmana.md.client.MDClient;
import oldmana.md.client.MDScheduler.MDTask;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;
import oldmana.md.net.packet.client.action.PacketActionClickLink;
import oldmana.md.net.packet.universal.PacketChat;

public class MDChat extends MDComponent
{
	private List<Message> messages = new ArrayList<Message>();
	private int scroll;
	private int lineCount;
	
	private StringBuilder typed = new StringBuilder();
	private int typeOffset;
	private int typeDisplayOffset;
	
	private List<String> history = new ArrayList<String>();
	private String startedMsg = "";
	private int historyPos = -1;
	
	private boolean chatOpen = false;
	private boolean blink = false;
	private boolean ignoreNextBlink = false;
	
	private boolean hoveringLink = false;
	
	private MouseAdapter mouseListener;
	private MouseMotionAdapter motionListener;
	private MouseWheelListener wheelListener;
	
	public MDChat()
	{
		getClient().getScheduler().scheduleTask(new MDTask(1, true)
		{
			@Override
			public void run()
			{
				boolean update = false;
				for (int i = 0 ; i < getMaxChatHistoryLines() ; i++)
				{
					if (messages.size() > i)
					{
						Message m = messages.get(i);
						if (m.tick())
						{
							update = true;
						}
					}
				}
				if (update)
				{
					repaint();
				}
			}
		});
		getClient().getScheduler().scheduleTask(new MDTask(30, true)
		{
			@Override
			public void run()
			{
				if (ignoreNextBlink)
				{
					ignoreNextBlink = false;
					return;
				}
				blink = !blink;
				if (chatOpen)
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
				if (getClient().getTableScreen().isVisible())
				{
					if (!chatOpen && event.getID() == KeyEvent.KEY_PRESSED && !getClient().getTableScreen().ingameMenu.isVisible())
					{
						if (event.getKeyCode() == KeyEvent.VK_T)
						{
							setChatOpen(true);
							requestFocus();
							repaint();
							return true;
						}
						else if (event.getKeyCode() == KeyEvent.VK_SLASH)
						{
							setChatOpen(true);
							typed.append("/");
							requestFocus();
							repaint();
							return true;
						}
					}
				}
				return false;
			}
		});
		addKeyListener(new KeyAdapter()
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
						if (history.isEmpty() || !history.get(0).equals(typed.toString()))
						{
							history.add(0, typed.toString());
						}
						typed = new StringBuilder();
					}
					historyPos = -1;
					typeOffset = 0;
					scroll = 0;
					setChatOpen(false);
				}
				else if (event.getKeyCode() == KeyEvent.VK_LEFT)
				{
					typeOffset = Math.min(typeOffset + 1, typed.length());
					blink = true;
					ignoreNextBlink = true;
				}
				else if (event.getKeyCode() == KeyEvent.VK_RIGHT)
				{
					typeOffset = Math.max(typeOffset - 1, 0);
					blink = true;
					ignoreNextBlink = true;
				}
				else if (event.getKeyCode() == KeyEvent.VK_UP)
				{
					if (historyPos < 0)
		        	{
		        		startedMsg = typed.toString();
		        	}
		        	historyPos = Math.min(historyPos + 1, history.size() - 1);
		        	if (historyPos >= 0)
		        	{
		        		typed = new StringBuilder(history.get(historyPos));
		        	}
				}
				else if (event.getKeyCode() == KeyEvent.VK_DOWN)
				{
					historyPos = Math.max(historyPos - 1, -1);
		        	if (historyPos < 0)
		        	{
		        		typed = new StringBuilder(startedMsg);
		        	}
		        	else
		        	{
		        		typed = new StringBuilder(history.get(historyPos));
		        	}
				}
				else if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					setChatOpen(false);
					typed = new StringBuilder();
					historyPos = -1;
					typeOffset = 0;
					scroll = 0;
				}
				else if (event.getKeyChar() != '\uFFFF' && event.getKeyCode() != KeyEvent.VK_DELETE)
				{
					typed.insert(typed.length() - typeOffset, event.getKeyChar());
				}
				repaint();
			}
		});
		
		wheelListener = new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent event)
			{
				int amt = event.getUnitsToScroll();
				if (amt < 0)
				{
					scroll = Math.min(Math.max(lineCount - getMaxChatHistoryLines(), 0), scroll + 1);
				}
				else if (amt > 0)
				{
					scroll = Math.max(0, scroll - 1);
				}
				repaint();
			}
		};
		
		mouseListener = new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				TextSegment seg = getSegmentAt(event.getX(), event.getY());
				if (seg != null && seg.linkID > -1)
				{
					System.out.println("LINK: " + seg.linkID);
					getClient().sendPacket(new PacketActionClickLink(seg.linkID));
				}
			}
		};
		
		motionListener = new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent event)
			{
				TextSegment seg = getSegmentAt(event.getX(), event.getY());
				if (seg != null && seg.linkID > -1 && !hoveringLink)
				{
					Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); 
					setCursor(cursor);
					hoveringLink = true;
				}
				else if ((seg == null || seg.linkID == -1) && hoveringLink)
				{
					setCursor(Cursor.getDefaultCursor());
					hoveringLink = false;
				}
			}
		};
	}
	
	public void addMessage(String text)
	{
		Message m = new Message(text);
		messages.add(0, m);
		lineCount += m.getLineCount();
		repaint();
	}
	
	public void calculateLineCount()
	{
		lineCount = 0;
		for (Message m : messages)
		{
			lineCount += m.getLineCount();
		}
	}
	
	public void setChatOpen(boolean open)
	{
		if (!chatOpen && open)
		{
			addMouseListener(mouseListener);
			addMouseMotionListener(motionListener);
			addMouseWheelListener(wheelListener);
		}
		else if (chatOpen && !open)
		{
			removeMouseListener(mouseListener);
			removeMouseMotionListener(motionListener);
			removeMouseWheelListener(wheelListener);
			
			setCursor(Cursor.getDefaultCursor());
			hoveringLink = false;
		}
		chatOpen = open;
	}
	
	public boolean isChatOpen()
	{
		return chatOpen;
	}
	
	public Map<TextSegment[], Integer> getVisibleLines()
	{
		Map<TextSegment[], Integer> lines = new LinkedHashMap<TextSegment[], Integer>(getMaxChatHistoryLines());
		int linesLeft = getMaxChatHistoryLines();
		int pos = scroll;
		for (int i = 0 ; i < messages.size() ; i++)
		{
			Message m = messages.get(i);
			pos -= m.getLineCount();
			if (pos < 0)
			{
				for (int index = -pos - 1 ; index >= 0 ; index--)
				{
					lines.put(m.getLineAt(index), m.getDisplayTicks());
					if (--linesLeft == 0)
					{
						break;
					}
				}
				pos = 0;
				if (linesLeft == 0)
				{
					break;
				}
			}
		}
		return lines;
	}
	
	public TextSegment getSegmentAt(int x, int y)
	{
		List<TextSegment[]> lines = new ArrayList<TextSegment[]>(getVisibleLines().keySet());
		int interval = getChatHistoryInterval();
		int curY = getChatHistoryHeight();
		for (int i = 0 ; i < lines.size() ; i++)
		{
			if (y < curY && y >= curY - interval)
			{
				int curX = getChatHistoryXOffset();
				for (int e = 0 ; e < lines.get(i).length ; e++)
				{
					int width = lines.get(i)[e].getWidth();
					if (x >= curX && x < curX + width)
					{
						return lines.get(i)[e];
					}
					curX += width;
				}
				break;
			}
			curY -= interval;
		}
		return null;
	}
	
	public int getChatHistoryHeight()
	{
		int height = getHeight() - getChatHeight() - scale(10);
		int maxLines = getMaxChatHistoryLines();
		return (height / maxLines) * maxLines;
	}
	
	public int getChatHistoryWidth()
	{
		return getWidth() - scale(10);
	}
	
	public int getChatHistoryInterval()
	{
		return getChatHistoryHeight() / getMaxChatHistoryLines();
	}
	
	public int getChatHeight()
	{
		return scale(34);
	}
	
	public int getChatHistoryXOffset()
	{
		return scale(8);
	}
	
	public int getMaxChatHistoryLines()
	{
		return 16;
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		int chatHistoryHeight = getChatHistoryHeight();
		int maxLines = getMaxChatHistoryLines();
		int interval = chatHistoryHeight / maxLines;
		int chatHeight = getChatHeight();
		int xOffset = getChatHistoryXOffset();
		int offsetWidth = getWidth() - xOffset;
		
		Font f = getFont();
		
		Map<TextSegment[], Integer> lines = getVisibleLines();
		List<Entry<TextSegment[], Integer>> iter = new ArrayList<>(lines.entrySet());
		
		// Draw visible messages
		for (int i = 0 ; i < iter.size() ; i++)
		{
			Entry<TextSegment[], Integer> line = iter.get(i);
			double opacity = 1;
			if (!chatOpen)
			{
				opacity = Math.min(1, line.getValue() * (1 / 90.0));
			}
			int pos = -(i - maxLines + 1);
			// Draw Transparent Text Area
			g.setColor(new Color(0, 0, 0, (int) (60 * opacity)));
			g.fillRect(xOffset, interval * pos, offsetWidth, interval);
			
			int width = 0;
			for (TextSegment seg : line.getKey())
			{
				Color c = seg.getColor();
				// Draw Text Shadow
				g.setColor(new Color(20, 20, 20, (int) (c.getAlpha() * opacity)));
				TextPainter tp = new TextPainter(seg.getText(), f, new Rectangle(xOffset + width + scale(2) + Math.max(scale(1.5), 1), interval * pos + 
						Math.max(scale(1.5), 1), offsetWidth, interval));
				tp.setVerticalAlignment(Alignment.CENTER);
				tp.paint(g);
				
				// Draw Text
				g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (c.getAlpha() * opacity)));
				tp = new TextPainter(seg.getText(), f, new Rectangle(xOffset + width + scale(2), interval * pos, offsetWidth, interval));
				tp.setVerticalAlignment(Alignment.CENTER);
				tp.paint(g);
				width += seg.getWidth();
			}
		}
		
		if (chatOpen)
		{
			// Draw scroll bar
			if (lineCount > maxLines)
			{
				double barSize = (double) maxLines / lineCount;
				double barPos = (double) (scroll + maxLines) / lineCount;
				int barOffset = (int) (chatHistoryHeight - (chatHistoryHeight * barPos));
				g.setColor(new Color(240, 240, 240));
				g.fillRect(0, barOffset, scale(6), (int) (barSize * chatHistoryHeight));
				g.setColor(Color.DARK_GRAY);
				g.drawRect(0, barOffset, scale(6), (int) (barSize * chatHistoryHeight));
			}
			
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
			
			// Draw Typed Text Shadow
			g.setColor(new Color(20, 20, 20));
			TextPainter tp = new TextPainter(typed.toString(), f, new Rectangle(Math.max(scale(1.5), 1), getHeight() - chatHeight + Math.max(scale(1.5), 1), 
					getWidth() - scale(4), chatHeight), false, false);
			tp.setVerticalAlignment(Alignment.CENTER);
			tp.paint(g);
			
			// Draw Typed Text
			g.setColor(Color.WHITE);
			tp = new TextPainter(typed.toString(), f, new Rectangle(0, getHeight() - chatHeight, getWidth() - scale(4), chatHeight), false, false);
			tp.setVerticalAlignment(Alignment.CENTER);
			tp.paint(g);
			int cursorPos = posOffsetWidth;
			g.setColor(Color.BLACK);
			if (blink)
			{
				g.fillRect(cursorPos, getHeight() - chatHeight + scale(3), scale(3), chatHeight - scale(6));
			}
		}
	}
	
	@Override
	public Font getFont()
	{
		return GraphicsUtils.getBoldMDFont(scale(24));
	}
	
	/**Used for debug purposes only
	 * 
	 * @param msg
	 */
	public static void print(String msg)
	{
		MDClient.getInstance().getTableScreen().getChat().addMessage(msg);
	}
	
	public class Message
	{
		private String text;
		private List<TextSegment[]> lines = new ArrayList<TextSegment[]>();
		private int displayTicks = 360;
		
		public Message(String text)
		{
			this.text = text;
			calculateLines();
		}
		
		public String getText()
		{
			return text;
		}
		
		public int getLineCount()
		{
			return lines.size();
		}
		
		public List<TextSegment[]> getLines()
		{
			return lines;
		}
		
		public TextSegment[] getLineAt(int index)
		{
			return lines.get(index);
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
		
		public List<TextSegment> calculateSegments()
		{
			char[] chars = text.toCharArray();
			
			Color color = Color.WHITE;
			int linkID = -1;
			int linkLen = -1;
			
			List<TextSegment> segments = new ArrayList<TextSegment>();
			
			TextSegment seg = new TextSegment(color, -1);
			
			for (int i = 0 ; i < chars.length ; i++)
			{
				while (chars[i] == '§')
				{
					// 0 = Print next character
					// 1 = Chat color [Color {2 chars}]
					// 2 = Link [ID {2 chars}, Length of text{1 char}]
					i++;
					char cmd = chars[i];
					i++;
					if (cmd == '0')
					{
						
					}
					else if (cmd == '1')
					{
						ByteBuffer buffer = ByteBuffer.allocate(4);
						for (int e = 0 ; e < 2 ; e++)
						{
							buffer.putChar(chars[i]);
							i++;
						}
						buffer.position(0);
						color = new Color(buffer.getInt());
						if (seg.getText().length() > 0)
						{
							segments.add(seg);
						}
						seg = new TextSegment(color, linkID);
						
					}
					else if (cmd == '2')
					{
						ByteBuffer buffer = ByteBuffer.allocate(6);
						for (int e = 0 ; e < 3 ; e++)
						{
							buffer.putChar(chars[i]);
							i++;
						}
						buffer.position(0);
						linkID = buffer.getInt();
						linkLen = buffer.getShort();
						if (seg.getText().length() > 0)
						{
							segments.add(seg);
						}
						seg = new TextSegment(color, linkID);
					}
				}
				
				seg.appendChar(chars[i]);
				if (linkLen > 0)
				{
					linkLen--;
					if (linkLen == 0)
					{
						linkID = -1;
						linkLen = -1;
						
						if (seg.getText().length() > 0)
						{
							segments.add(seg);
						}
						seg = new TextSegment(color, linkID);
					}
				}
			}
			if (!seg.getText().equals(""))
			{
				segments.add(seg);
			}
			return segments;
		}
		
		public void calculateLines()
		{
			Font f = getFont();
			
			int maxWidth = getChatHistoryWidth();
			
			String line = "";
			
			List<TextSegment> segments = calculateSegments();
			List<TextSegment> lineSegments = new ArrayList<TextSegment>();
			for (TextSegment seg : segments)
			{
				String text = seg.getText();
				System.out.println(text);
				int startPos = line.length();
				line += text;
				List<String> split = GraphicsUtils.splitString(line, f, maxWidth);
				if (split.size() == 1)
				{
					lineSegments.add(new TextSegment(text, seg.getColor(), seg.getLinkID()));
				}
				else if (split.size() > 1)
				{
					for (int i = 0 ; i < split.size() ; i++)
					{
						String splStr = split.get(i);
						if (i == 0)
						{
							lineSegments.add(new TextSegment(split.get(0).substring(startPos), seg.getColor(), seg.getLinkID()));
						}
						else
						{
							lineSegments.add(new TextSegment(splStr, seg.getColor(), seg.getLinkID()));
						}
						if (i < split.size() - 1)
						{
							lines.add(lineSegments.toArray(new TextSegment[lineSegments.size()]));
							lineSegments.clear();
						}
					}
					line = split.get(split.size() - 1);
				}
			}
			if (lineSegments.size() > 0)
			{
				lines.add(lineSegments.toArray(new TextSegment[lineSegments.size()]));
			}
		}
	}
	
	public class TextSegment
	{
		private String str;
		private Color color = Color.WHITE;
		private int linkID = -1;
		
		public TextSegment(String str, Color color, int linkID)
		{
			this.str = str;
			this.color = color;
			this.linkID = linkID;
		}
		
		public TextSegment(Color color, int linkID)
		{
			str = "";
			this.color = color;
			this.linkID = linkID;
		}
		
		public String getText()
		{
			return str;
		}
		
		public Color getColor()
		{
			return color;
		}
		
		public int getLinkID()
		{
			return linkID;
		}
		
		public int getWidth()
		{
			return getFontMetrics(getFont()).stringWidth(str);
		}
		
		public void appendChar(char c)
		{
			str += c;
		}
	}
}
