package oldmana.md.server;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oldmana.md.common.Message;
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
	
	public MessageBuilder() {}
	
	public MessageBuilder(String str)
	{
		appendSimple(this, str);
	}
	
	public MessageBuilder(ChatColor color)
	{
		setColor(color);
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
			message.put(segment);
			currentText = new StringBuilder();
		}
	}
	
	public Message getMessage()
	{
		finalizeSegment();
		return new Message(message);
	}
	
	public String getUnformattedMessage()
	{
		finalizeSegment();
		return new Message(message).getUnformattedMessage();
	}
	
	public void addString(String str)
	{
		appendSimple(this, str);
	}
	
	private void addStringRaw(String str)
	{
		finalizeSegment();
		currentText.append(str);
	}
	
	public void setColor(ChatColor color)
	{
		finalizeSegment();
		currentColor = color.getColor();
	}
	
	public void setColor(Color color)
	{
		finalizeSegment();
		currentColor = color;
	}
	
	public void setColor(int r, int g, int b)
	{
		finalizeSegment();
		currentColor = new Color(r, g, b);
	}
	
	public ChatLink addLinkedString(String str)
	{
		return addLinkedString(str, -1);
	}
	
	public ChatLink addLinkedString(String str, ChatLinkListener listener)
	{
		ChatLink link = addLinkedString(str, -1);
		link.setListener(listener);
		return link;
	}
	
	public ChatLink addLinkedString(String str, int deleteTimer)
	{
		finalizeSegment();
		ChatLink link = MDServer.getInstance().getChatLinkHandler().createChatLink();
		this.link = link;
		appendSimple(this, str);
		finalizeSegment();
		this.link = null;
		return link;
	}
	
	public void addCommandString(String str, String command)
	{
		finalizeSegment();
		cmd = command;
		appendSimple(this, str);
		finalizeSegment();
		cmd = null;
	}
	
	public void addFillCommandString(String str, String fillCommand)
	{
		finalizeSegment();
		fillCmd = fillCommand;
		appendSimple(this, str);
		finalizeSegment();
		fillCmd = null;
	}
	
	public void addHoverString(String str, List<String> text)
	{
		finalizeSegment();
		currentHoverText = text;
		appendSimple(this, str);
		finalizeSegment();
		currentHoverText = null;
	}
	
	public void addHoverString(String str, String... text)
	{
		addHoverString(str, Arrays.asList(text));
	}
	
	public void startHoverText(String... text)
	{
		finalizeSegment();
		currentHoverText = Arrays.asList(text);
	}
	
	public void startHoverText(List<String> text)
	{
		finalizeSegment();
		currentHoverText = new ArrayList<String>(text);
	}
	
	public void endHoverText()
	{
		finalizeSegment();
		currentHoverText = null;
	}
	
	public void startCommand(String cmd)
	{
		finalizeSegment();
		this.cmd = cmd;
	}
	
	public void endCommand()
	{
		finalizeSegment();
		cmd = null;
	}
	
	public void startFillCommand(String fillCmd)
	{
		finalizeSegment();
		this.fillCmd = fillCmd;
	}
	
	public void endFillCommand()
	{
		finalizeSegment();
		fillCmd = null;
	}
	
	public void endSpecial()
	{
		finalizeSegment();
		link = null;
		cmd = null;
		fillCmd = null;
		currentHoverText = null;
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
	
	private static JSONObject toJSONObject(String text, Color color)
	{
		JSONObject obj = new JSONObject();
		obj.put("txt", text);
		obj.put("color", ChatColor.toHexColor(color));
		return obj;
	}
}
