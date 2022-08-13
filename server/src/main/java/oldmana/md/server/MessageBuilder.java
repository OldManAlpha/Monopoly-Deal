package oldmana.md.server;

import java.awt.Color;
import java.nio.ByteBuffer;

import oldmana.md.server.ChatLinkHandler.ChatLink;
import oldmana.md.server.ChatLinkHandler.ChatLinkListener;

public class MessageBuilder
{
	private StringBuilder message;
	
	private String[] currentHoverText;
	private int hoverTextPos;
	
	public MessageBuilder()
	{
		message = new StringBuilder();
	}
	
	public MessageBuilder(String str)
	{
		message = new StringBuilder(str);
	}
	
	public String getMessage()
	{
		return message.toString();
	}
	
	public String getStrippedMessage()
	{
		return ChatColor.stripColors(message.toString());
	}
	
	public void addString(String str)
	{
		message.append(str);
	}
	
	public void setColor(Color color)
	{
		message.append(ChatColor.toChatColor(color));
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
		ChatLink link = MDServer.getInstance().getChatLinkHandler().createChatLink();
		ByteBuffer buffer = ByteBuffer.allocate(6);
		buffer.putInt(link.getID());
		buffer.putShort((short) str.length());
		buffer.position(0);
		message.append("§2" + buffer.getChar() + buffer.getChar() + buffer.getChar() + str);
		return link;
	}
	
	public void startHoverText(String[] text)
	{
		currentHoverText = text;
		hoverTextPos = message.length();
	}
	
	public void endHoverText()
	{
		String hoverText = "";
		for (int i = 0 ; i < currentHoverText.length ; i++)
		{
			
		}
		message.insert(hoverTextPos, "§3");
	}
	
	@Override
	public String toString()
	{
		return message.toString();
	}
}
