package oldmana.md.server;

import java.util.HashMap;
import java.util.Map;

public class ChatLinkHandler
{
	private int nextID;
	
	private Map<Integer, ChatLink> links = new HashMap<Integer, ChatLink>();
	
	public ChatLinkHandler()
	{
		
	}
	
	public ChatLink createChatLink()
	{
		ChatLink link = new ChatLink(nextID++);
		links.put(link.getID(), link);
		return link;
	}
	
	public void deleteChatLink(ChatLink link)
	{
		links.remove(link.getID());
	}
	
	public ChatLink getChatLinkByID(int id)
	{
		return links.get(id);
	}
	
	public class ChatLink
	{
		private int id;
		
		private ChatLinkListener listener;
		
		private int deleteTimer;
		
		public ChatLink(int id)
		{
			this.id = id;
		}
		
		public ChatLink(int id, int deleteTimer)
		{
			this(id);
			this.deleteTimer = deleteTimer;
		}
		
		public int getID()
		{
			return id;
		}
		
		public ChatLinkListener getListener()
		{
			return listener;
		}
		
		public void setListener(ChatLinkListener listener)
		{
			this.listener = listener;
		}
	}
	
	public static interface ChatLinkListener
	{
		public void linkClicked();
	}
}
