package oldmana.md.server;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oldmana.md.common.Message;
import oldmana.md.common.playerui.ChatAlignment;
import oldmana.md.server.playerui.ChatLinkHandler.ChatLink;
import oldmana.md.server.playerui.ChatLinkHandler.ChatLinkListener;
import org.json.JSONArray;
import org.json.JSONObject;

public class MessageBuilder
{
	private JSONArray message = new JSONArray();
	
	private StringBuilder currentText = new StringBuilder();
	private Color currentColor = Color.WHITE;
	private ChatLink link;
	private String cmd;
	private String fillCmd;
	private List<String> currentHoverText;
	private boolean underline;
	private ChatAlignment alignment = ChatAlignment.LEFT;
	private String category;
	
	public MessageBuilder(MessageBuilder mb)
	{
		message = new JSONArray(mb.message.toList());
		
		currentText = new StringBuilder(mb.currentText);
		currentColor = mb.currentColor;
		link = mb.link;
		cmd = mb.cmd;
		fillCmd = mb.fillCmd;
		currentHoverText = mb.currentHoverText != null ? new ArrayList<String>(mb.currentHoverText) : null;
		underline = mb.underline;
		alignment = mb.alignment;
		category = mb.category;
	}
	
	public MessageBuilder() {}
	
	public MessageBuilder(String str)
	{
		appendSimple(this, str);
	}
	
	public MessageBuilder(ChatColor color)
	{
		setColor(color);
	}
	
	public MessageBuilder(ChatAlignment alignment)
	{
		setAlignment(alignment);
	}
	
	private void finalizeSegment()
	{
		if (currentText.length() > 0)
		{
			JSONObject segment = new JSONObject();
			segment.put("txt", currentText.toString());
			if (!Color.WHITE.equals(currentColor))
			{
				segment.put("color", ChatColor.toHexColor(currentColor));
			}
			if (link != null)
			{
				segment.put("link", link.getID());
			}
			if (cmd != null)
			{
				segment.put("cmd", cmd);
			}
			if (fillCmd != null)
			{
				segment.put("fillCmd", fillCmd);
			}
			if (currentHoverText != null && !currentHoverText.isEmpty())
			{
				segment.put("hover", currentHoverText);
			}
			if (underline)
			{
				segment.put("underline", underline);
			}
			message.put(segment);
			currentText = new StringBuilder();
		}
	}
	
	/**
	 * Build this MessageBuilder into a {@link Message} that can be sent to players.
	 * @return The message
	 */
	public Message build()
	{
		finalizeSegment();
		return new Message(message, alignment, category);
	}
	
	public String getUnformattedMessage()
	{
		finalizeSegment();
		return new Message(message).getUnformattedMessage();
	}
	
	public MessageBuilder add(String str)
	{
		appendSimple(this, str);
		return this;
	}
	
	private void addStringRaw(String str)
	{
		finalizeSegment();
		currentText.append(str);
	}
	
	public MessageBuilder setColor(ChatColor color)
	{
		finalizeSegment();
		currentColor = color.getColor();
		return this;
	}
	
	public MessageBuilder setColor(Color color)
	{
		finalizeSegment();
		currentColor = color;
		return this;
	}
	
	public MessageBuilder setColor(int r, int g, int b)
	{
		finalizeSegment();
		currentColor = new Color(r, g, b);
		return this;
	}
	
	public ChatLink addLinked(String str)
	{
		return addLinked(str, -1);
	}
	
	public ChatLink addLinked(String str, ChatLinkListener listener)
	{
		ChatLink link = addLinked(str, -1);
		link.setListener(listener);
		return link;
	}
	
	public ChatLink addLinked(String str, int deleteTimer)
	{
		finalizeSegment();
		ChatLink link = MDServer.getInstance().getChatLinkHandler().createChatLink();
		this.link = link;
		appendSimple(this, str);
		finalizeSegment();
		this.link = null;
		return link;
	}
	
	public MessageBuilder addCommand(String str, String command)
	{
		finalizeSegment();
		cmd = command;
		appendSimple(this, str);
		finalizeSegment();
		cmd = null;
		return this;
	}
	
	public MessageBuilder addFillCommand(String str, String fillCommand)
	{
		finalizeSegment();
		fillCmd = fillCommand;
		appendSimple(this, str);
		finalizeSegment();
		fillCmd = null;
		return this;
	}
	
	public MessageBuilder addHover(String str, List<String> text)
	{
		finalizeSegment();
		currentHoverText = text;
		appendSimple(this, str);
		finalizeSegment();
		currentHoverText = null;
		return this;
	}
	
	public MessageBuilder addHover(String str, String... text)
	{
		addHover(str, Arrays.asList(text));
		return this;
	}
	
	public MessageBuilder startHoverText(String... text)
	{
		finalizeSegment();
		currentHoverText = Arrays.asList(text);
		return this;
	}
	
	public MessageBuilder startHoverText(List<String> text)
	{
		finalizeSegment();
		currentHoverText = new ArrayList<String>(text);
		return this;
	}
	
	public MessageBuilder endHoverText()
	{
		finalizeSegment();
		currentHoverText = null;
		return this;
	}
	
	public MessageBuilder startUnderline()
	{
		finalizeSegment();
		underline = true;
		return this;
	}
	
	public MessageBuilder endUnderline()
	{
		finalizeSegment();
		underline = false;
		return this;
	}
	
	public MessageBuilder startCommand(String cmd)
	{
		finalizeSegment();
		this.cmd = cmd;
		return this;
	}
	
	public MessageBuilder endCommand()
	{
		finalizeSegment();
		cmd = null;
		return this;
	}
	
	public MessageBuilder startFillCommand(String fillCmd)
	{
		finalizeSegment();
		this.fillCmd = fillCmd;
		return this;
	}
	
	public MessageBuilder endFillCommand()
	{
		finalizeSegment();
		fillCmd = null;
		return this;
	}
	
	public MessageBuilder endSpecial()
	{
		finalizeSegment();
		link = null;
		cmd = null;
		fillCmd = null;
		currentHoverText = null;
		return this;
	}
	
	public MessageBuilder setAlignment(ChatAlignment alignment)
	{
		this.alignment = alignment;
		return this;
	}
	
	public MessageBuilder setCategory(String category)
	{
		this.category = category;
		return this;
	}
	
	public MessageBuilder addMessageBuilder(MessageBuilder mb)
	{
		finalizeSegment();
		message.putAll(mb.message);
		return this;
	}
	
	public JSONArray toJSON()
	{
		return message;
	}
	
	@Override
	public String toString()
	{
		return message.toString();
	}
	
	/**
	 * Transforms the String from the previous chat format into a colorized JSONArray. Other formatting is not supported.
	 * @param str The String to colorize
	 * @return The JSON message
	 */
	public static Message fromSimple(String str)
	{
		JSONArray array = new JSONArray();
		
		StringBuilder text = new StringBuilder();
		Color color = Color.WHITE;
		for (int i = 0 ; i < str.length() ; i++)
		{
			char c = str.charAt(i);
			if (c == '§' && str.charAt(i + 1) == '1')
			{
				if (text.length() > 0)
				{
					array.put(toJSONObject(text.toString(), color));
					text.setLength(0);
				}
				ByteBuffer buffer = ByteBuffer.allocate(4);
				buffer.putChar(str.charAt(i + 2));
				buffer.putChar(str.charAt(i + 3));
				buffer.position(0);
				color = new Color(buffer.getInt());
				i += 3;
				continue;
			}
			text.append(c);
		}
		array.put(toJSONObject(text.toString(), color));
		return new Message(array);
	}
	
	public static void appendSimple(MessageBuilder builder, String str)
	{
		StringBuilder text = new StringBuilder();
		for (int i = 0 ; i < str.length() ; i++)
		{
			char c = str.charAt(i);
			if (c == '§' && str.charAt(i + 1) == '1')
			{
				if (text.length() > 0)
				{
					builder.addStringRaw(text.toString());
					text.setLength(0);
				}
				ByteBuffer buffer = ByteBuffer.allocate(4);
				buffer.putChar(str.charAt(i + 2));
				buffer.putChar(str.charAt(i + 3));
				buffer.position(0);
				Color color = new Color(buffer.getInt());
				builder.setColor(color);
				i += 3;
				continue;
			}
			text.append(c);
		}
		if (text.length() > 0)
		{
			builder.addStringRaw(text.toString());
		}
	}
	
	public static Message ofHover(String message, String... hover)
	{
		return new MessageBuilder().addHover(message, hover).build();
	}
	
	private static JSONObject toJSONObject(String text, Color color)
	{
		JSONObject obj = new JSONObject();
		obj.put("txt", text);
		obj.put("color", ChatColor.toHexColor(color));
		return obj;
	}
}
