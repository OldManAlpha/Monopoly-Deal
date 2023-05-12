package oldmana.md.server.playerui;

import oldmana.md.server.MDServer;

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
			if (deleteTimer > 0)
			{
				MDServer.getInstance().getScheduler().scheduleTask(deleteTimer, task -> links.remove(id));
			}
			this.deleteTimer = deleteTimer;
		}
		
		public int getID()
		{
			return id;
		}
		
		public void invalidate()
		{
			deleteChatLink(this);
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
	
	@FunctionalInterface
	public interface ChatLinkListener
	{
		void linkClicked();
	}
}
