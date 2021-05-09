package oldmana.md.server;

import java.awt.Color;
import java.nio.ByteBuffer;

import oldmana.md.server.ChatLinkHandler.ChatLink;

public class MessageBuilder
{
	private String message;
	
	public MessageBuilder()
	{
		message = "";
	}
	
	public MessageBuilder(String str)
	{
		message = str;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public String getStrippedMessage()
	{
		return ChatColor.stripColors(message);
	}
	
	public void addString(String str)
	{
		message += str;
	}
	
	public void setColor(Color color)
	{
		message += ChatColor.toChatColor(color);
	}
	
	public ChatLink addLinkedString(String str)
	{
		return addLinkedString(str, -1);
	}
	
	public ChatLink addLinkedString(String str, int deleteTimer)
	{
		ChatLink link = MDServer.getInstance().getChatLinkHandler().createChatLink();
		ByteBuffer buffer = ByteBuffer.allocate(6);
		buffer.putInt(link.getID());
		buffer.putShort((short) str.length());
		buffer.position(0);
		message += "§2" + buffer.getChar() + buffer.getChar() + buffer.getChar() + str;
		return link;
	}
	
	@Override
	public String toString()
	{
		return message;
	}
}
