package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import oldmana.md.client.MDClient;
import oldmana.md.client.Scheduler;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;
import oldmana.md.common.Message;
import oldmana.md.common.net.packet.client.PacketChat;
import oldmana.md.common.playerui.ChatAlignment;
import oldmana.md.common.util.ColorUtil;
import oldmana.md.common.net.packet.client.action.PacketActionClickLink;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.SwingUtilities;

public class MDChat extends MDComponent
{
	private List<ChatMessage> messages = new ArrayList<ChatMessage>();
	private int scroll;
	private int lineCount;
	
	private StringBuilder typed = new StringBuilder();
	private int typeOffset;
	private int typeDisplayOffset;
	
	private List<String> history = new ArrayList<String>();
	private String startedMsg = "";
	private int historyPos = -1;
	
	private boolean tickMessages = false;
	
	private boolean chatOpen = false;
	private boolean blink = false;
	private boolean ignoreNextBlink = false;
	
	private boolean hoveringLink = false;
	
	private MouseAdapter mouseListener;
	private MouseMotionAdapter motionListener;
	private MouseWheelListener wheelListener;
	
	private MDChatHover hoverText;
	
	public MDChat()
	{
		getClient().getScheduler().scheduleFrameboundTask(task ->
		{
			if (!tickMessages)
			{
				return;
			}
			boolean update = false;
			int lines = Math.min(getMaxChatHistoryLines(), messages.size());
			for (int i = 0 ; i < lines ; i++)
			{
				ChatMessage m = messages.get(i);
				if (m.tick())
				{
					update = true;
				}
			}
			if (update)
			{
				if (!chatOpen)
				{
					updateGraphics();
				}
			}
			else
			{
				tickMessages = false;
			}
		});
		getClient().getScheduler().scheduleTask(task ->
		{
			if (ignoreNextBlink)
			{
				ignoreNextBlink = false;
				return;
			}
			blink = !blink;
			if (chatOpen)
			{
				updateGraphics();
			}
		}, 500, true);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(event ->
		{
			if (getClient().getTableScreen().isVisible())
			{
				if (!chatOpen && event.getID() == KeyEvent.KEY_PRESSED && !getClient().getTableScreen().ingameMenu.isVisible())
				{
					if (event.getKeyCode() == KeyEvent.VK_T)
					{
						setChatOpen(true);
						requestFocus();
						updateGraphics();
						return true;
					}
					else if (event.getKeyCode() == KeyEvent.VK_SLASH)
					{
						setChatOpen(true);
						typed.append("/");
						requestFocus();
						updateGraphics();
						return true;
					}
				}
			}
			return false;
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
						typeOffset = 0;
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
					typeOffset = 0;
				}
				else if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					setChatOpen(false);
				}
				else if (event.getKeyChar() != '\uFFFF' && event.getKeyCode() != KeyEvent.VK_DELETE)
				{
					typed.insert(typed.length() - typeOffset, event.getKeyChar());
				}
				updateGraphics();
			}
		});
		
		wheelListener = event ->
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
			updateGraphics();
		};
		
		mouseListener = new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				TextSegment seg = getSegmentAt(event.getX(), event.getY());
				if (seg != null)
				{
					if (seg.linkID > -1)
					{
						getClient().sendPacket(new PacketActionClickLink(seg.linkID));
					}
					if (seg.cmd != null)
					{
						getClient().sendPacket(new PacketChat("/" + seg.cmd));
					}
					if (seg.fillCmd != null)
					{
						typed = new StringBuilder("/" + seg.fillCmd);
						typeOffset = 0;
						historyPos = -1;
						updateGraphics();
					}
				}
			}
		};
		
		motionListener = new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent event)
			{
				TextSegment seg = getSegmentAt(event.getX(), event.getY());
				
				if (seg != null)
				{
					if (seg.isClickable() && !hoveringLink)
					{
						setHovering(true);
					}
					else if (!seg.isClickable() && hoveringLink)
					{
						setHovering(false);
					}
					if (seg.hasHoverText())
					{
						if (hoverText == null || hoverText.getText() != seg.getHoverText())
						{
							if (hoverText != null)
							{
								getClient().getTableScreen().remove(hoverText);
								getClient().getTableScreen().repaint();
							}
							hoverText = new MDChatHover(seg.getHoverText());
							getClient().addTableComponent(hoverText, 151);
						}
						hoverText.setLocation(SwingUtilities.convertPoint(MDChat.this, event.getX(), event.getY() - hoverText.getHeight() - scale(10), getClient().getTableScreen()));
					}
					else if (hoverText != null)
					{
						getClient().getTableScreen().remove(hoverText);
						getClient().getTableScreen().repaint();
						hoverText = null;
					}
				}
				else
				{
					if (hoveringLink)
					{
						setHovering(false);
					}
					if (hoverText != null)
					{
						getClient().getTableScreen().remove(hoverText);
						getClient().getTableScreen().repaint();
						hoverText = null;
					}
				}
			}
		};
	}
	
	public void addMessage(Message message)
	{
		ChatMessage m = new ChatMessage(message);
		messages.add(0, m);
		lineCount += m.getLineCount();
		if (scroll > 0)
		{
			scroll += m.getLineCount();
		}
		final int MAX_MESSAGES = 5000;
		if (messages.size() > MAX_MESSAGES)
		{
			messages.remove(messages.size() - 1);
		}
		updateGraphics();
		tickMessages = true;
	}
	
	public void addMessage(String text)
	{
		JSONArray array = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("txt", text);
		array.put(obj);
		
		addMessage(new Message(array));
	}
	
	public void setHovering(boolean hovering)
	{
		setCursor(hovering ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
		hoveringLink = hovering;
	}
	
	public void calculateLineCount()
	{
		lineCount = 0;
		for (ChatMessage m : messages)
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
			historyPos = -1;
			typeOffset = 0;
			scroll = 0;
			typed = new StringBuilder();
			if (hoverText != null)
			{
				getClient().getTableScreen().remove(hoverText);
				getClient().getTableScreen().repaint();
			}
		}
		chatOpen = open;
	}
	
	public boolean isChatOpen()
	{
		return chatOpen;
	}
	
	// TODO: Optimize this by caching lines
	public List<ChatLine> getVisibleLines()
	{
		List<ChatLine> lines = new ArrayList<ChatLine>(getMaxChatHistoryLines());
		int linesLeft = getMaxChatHistoryLines();
		int pos = scroll;
		for (int i = 0 ; i < messages.size() ; i++)
		{
			ChatMessage m = messages.get(i);
			pos -= m.getLineCount();
			if (pos < 0)
			{
				for (int index = -pos - 1 ; index >= 0 ; index--)
				{
					lines.add(new ChatLine(m.getLineAt(index), m.getAlignment(), m.getDisplayTime()));
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
		List<ChatLine> lines = getVisibleLines();
		int interval = getChatHistoryInterval();
		int curY = getChatHistoryHeight();
		for (int i = 0 ; i < lines.size() ; i++)
		{
			if (y < curY && y >= curY - interval)
			{
				return lines.get(i).getSegmentAt(x);
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
		return 20;
	}
	
	@Override
	public void doPaint(Graphics gr)
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
		
		List<ChatLine> lines = getVisibleLines();
		
		// Draw visible messages
		for (int i = 0 ; i < lines.size() ; i++)
		{
			ChatLine line = lines.get(i);
			if (!chatOpen && line.getDisplayTime() == 0) // Don't draw line if it's not visible
			{
				continue;
			}
			double opacity = 1;
			if (!chatOpen)
			{
				opacity = Math.min(1, line.getDisplayTime() * (1 / 1500.0));
			}
			int pos = -(i - maxLines + 1);
			// Draw Transparent Text Area
			g.setColor(new Color(0, 0, 0, (int) ((chatOpen ? 60 : 40) * opacity)));
			g.fillRect(xOffset, interval * pos, offsetWidth, interval);
			
			int drawPos = line.getStartOffset();
			for (TextSegment seg : line.getSegments())
			{
				Color c = seg.getColor();
				Color shadow = new Color(20, 20, 20, (int) (c.getAlpha() * opacity));
				Color textColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (c.getAlpha() * opacity));
				int shadowOffset = Math.max(scale(1.5), 1);
				
				// Draw underline
				if (seg.isUnderline())
				{
					g.setColor(shadow);
					g.fillRect(drawPos + scale(2) + shadowOffset, (interval * (pos + 1)) - scale(5) + shadowOffset,
							seg.getWidth(), scale(3));
					g.setColor(textColor);
					g.fillRect(drawPos + scale(2), (interval * (pos + 1)) - scale(5), seg.getWidth(), scale(3));
				}
				
				// Draw Text Shadow
				g.setColor(shadow);
				TextPainter tp = new TextPainter(seg.getText(), f, new Rectangle(drawPos + scale(2) + shadowOffset, interval * pos +
						shadowOffset, offsetWidth, interval));
				tp.setVerticalAlignment(Alignment.CENTER);
				tp.paint(g);
				
				// Draw Text
				g.setColor(textColor);
				tp = new TextPainter(seg.getText(), f, new Rectangle(drawPos + scale(2), interval * pos, offsetWidth, interval));
				tp.setVerticalAlignment(Alignment.CENTER);
				tp.paint(g);
				drawPos += seg.getWidth();
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
	
	public void removeMessageCategory(String category)
	{
		int pos = 0;
		Iterator<ChatMessage> it = messages.iterator();
		while (it.hasNext())
		{
			ChatMessage message = it.next();
			if (category.equals(message.getCategory()))
			{
				it.remove();
				if (scroll > pos)
				{
					scroll = Math.max(scroll - message.getLineCount(), 0);
				}
				lineCount -= message.getLineCount();
				continue;
			}
			pos += message.getLineCount();
		}
		updateGraphics();
	}
	
	public void recalculateLines()
	{
		for (ChatMessage message : messages)
		{
			message.calculateLines();
		}
	}
	
	/**Used for debug purposes only
	 * 
	 * @param msg
	 */
	public static void print(String msg)
	{
		if (MDClient.getInstance().getWindow() != null)
		{
			MDClient.getInstance().getTableScreen().getChat().addMessage(msg);
		}
	}
	
	public class ChatMessage
	{
		private Message message;
		
		private String text;
		private List<List<TextSegment>> lines = new ArrayList<List<TextSegment>>();
		private int displayTime = 6000;
		
		private ChatAlignment alignment;
		
		private String category;
		
		public ChatMessage(Message message)
		{
			this.message = message;
			this.alignment = message.getAlignment();
			this.category = message.getCategory();
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
		
		public List<List<TextSegment>> getLines()
		{
			return lines;
		}
		
		public List<TextSegment> getLineAt(int index)
		{
			return lines.get(index);
		}
		
		public int getDisplayTime()
		{
			return displayTime;
		}
		
		public ChatAlignment getAlignment()
		{
			return alignment;
		}
		
		public boolean hasCategory()
		{
			return category != null;
		}
		
		public String getCategory()
		{
			return category;
		}
		
		public boolean tick()
		{
			if (displayTime > 0)
			{
				displayTime = (int) Math.max(displayTime - Scheduler.getFrameDelay(), 0);
				return true;
			}
			return false;
		}
		
		public List<TextSegment> calculateSegments()
		{
			List<TextSegment> segments = new ArrayList<TextSegment>(message.getMessage().length());
			for (Object o : message.getMessage())
			{
				JSONObject json = (JSONObject) o;
				TextSegment segment = new TextSegment(json.getString("txt"),
						json.has("color") ? ColorUtil.fromRGBHex(json.getString("color")) : Color.WHITE);
				if (json.has("link"))
				{
					segment.setLinkID(json.getInt("link"));
				}
				if (json.has("cmd"))
				{
					segment.setCommand(json.getString("cmd"));
				}
				if (json.has("fillCmd"))
				{
					segment.setFillCommand(json.getString("fillCmd"));
				}
				if (json.has("hover"))
				{
					JSONArray array = json.getJSONArray("hover");
					List<String> hoverText = new ArrayList<String>(array.length());
					for (Object str : array)
					{
						hoverText.add((String) str);
					}
					segment.setHoverText(hoverText);
				}
				if (json.has("underline"))
				{
					segment.setUnderline(json.getBoolean("underline"));
				}
				segments.add(segment);
			}
			return segments;
		}
		
		public void calculateLines()
		{
			lines.clear();
			
			List<TextSegment> segments = calculateSegments();
			
			String wholeText = "";
			for (TextSegment seg : segments)
			{
				wholeText += seg.getText();
			}
			List<String> wholeSplit = GraphicsUtils.splitString(wholeText, getFont(), getChatHistoryWidth());
			//System.out.println(wholeSplit);
			ListIterator<TextSegment> it = segments.listIterator();
			for (String str : wholeSplit)
			{
				int strLen = str.length();
				List<TextSegment> lineSegments = new ArrayList<TextSegment>();
				int curPos = 0;
				while (it.hasNext())
				{
					TextSegment segment = it.next();
					// Remove first space if this is the first segment of the line
					if (curPos == 0)
					{
						String text = segment.getText();
						if (text.length() > 0)
						{
							if (text.charAt(0) == ' ')
							{
								segment.setText(text.substring(1));
							}
						}
					}
					
					int len = segment.getText().length();
					if (curPos + len > strLen)
					{
						List<TextSegment> segSplit = segment.splitSegmentAt(strLen - curPos);
						lineSegments.add(segSplit.get(0));
						lines.add(lineSegments);
						lineSegments = null;
						it.set(segSplit.get(1)); // Carry over remainder to next line
						it.previous();
						break;
					}
					else
					{
						lineSegments.add(segment);
						curPos += len;
					}
				}
				if (lineSegments != null && !lineSegments.isEmpty())
				{
					lines.add(lineSegments);
				}
			}
			if (lines.isEmpty()) // Zero-character message, need to manually add a line
			{
				lines.add(new ArrayList<TextSegment>());
			}
		}
	}
	
	public class TextSegment
	{
		private String text;
		private Color color = Color.WHITE;
		private int linkID = -1;
		private String cmd;
		private String fillCmd;
		private List<String> hoverText;
		private boolean underline;
		
		public TextSegment(String text, TextSegment context)
		{
			this.text = text;
			this.color = context.color;
			this.linkID = context.linkID;
			this.cmd = context.cmd;
			this.fillCmd = context.fillCmd;
			this.hoverText = context.hoverText;
			this.underline = context.underline;
		}
		
		public TextSegment(String text, Color color)
		{
			this.text = text;
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
		
		public Color getColor()
		{
			return color;
		}
		
		public int getLinkID()
		{
			return linkID;
		}
		
		public void setLinkID(int linkID)
		{
			this.linkID = linkID;
		}
		
		public String getCommand()
		{
			return cmd;
		}
		
		public void setCommand(String cmd)
		{
			this.cmd = cmd;
		}
		
		public String getFillCommand()
		{
			return fillCmd;
		}
		
		public void setFillCommand(String fillCmd)
		{
			this.fillCmd = fillCmd;
		}
		
		public boolean hasHoverText()
		{
			return hoverText != null;
		}
		
		public List<String> getHoverText()
		{
			return hoverText;
		}
		
		public void setHoverText(List<String> hoverText)
		{
			this.hoverText = hoverText;
		}
		
		public boolean isUnderline()
		{
			return underline;
		}
		
		public void setUnderline(boolean underline)
		{
			this.underline = underline;
		}
		
		public boolean isClickable()
		{
			return linkID > -1 || cmd != null || fillCmd != null;
		}
		
		public int getWidth()
		{
			return getFontMetrics(getFont()).stringWidth(text);
		}
		
		public List<TextSegment> splitSegmentAt(int index)
		{
			return Arrays.asList(new TextSegment(text.substring(0, index), this),
					new TextSegment(text.substring(index), this));
		}
	}
	
	public class ChatLine
	{
		private List<TextSegment> segments;
		private ChatAlignment alignment;
		private int displayTime;
		
		public ChatLine(List<TextSegment> segments, ChatAlignment alignment, int displayTime)
		{
			this.segments = segments;
			this.alignment = alignment;
			this.displayTime = displayTime;
		}
		
		public TextSegment getSegmentAt(int x)
		{
			int curX = getStartOffset();
			for (int e = 0 ; e < segments.size() ; e++)
			{
				int width = segments.get(e).getWidth();
				if (x >= curX && x < curX + width)
				{
					return segments.get(e);
				}
				curX += width;
			}
			return null;
		}
		
		public int getStartOffset()
		{
			switch (alignment)
			{
				case LEFT: return getChatHistoryXOffset();
				case CENTER: return (getChatHistoryWidth() - getWidth()) / 2 + (scale(10) / 2);
				case RIGHT: return getChatHistoryWidth() - getWidth();
			}
			return 0;
		}
		
		public List<TextSegment> getSegments()
		{
			return segments;
		}
		
		public int getWidth()
		{
			int width = 0;
			for (TextSegment segment : segments)
			{
				width += segment.getWidth();
			}
			return width;
		}
		
		public int getDisplayTime()
		{
			return displayTime;
		}
	}
}
