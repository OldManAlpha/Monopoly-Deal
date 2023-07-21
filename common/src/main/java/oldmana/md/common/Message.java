package oldmana.md.common;

import oldmana.md.common.playerui.ChatAlignment;
import org.json.JSONArray;
import org.json.JSONObject;

public class Message
{
	private JSONArray message;
	private ChatAlignment alignment = ChatAlignment.LEFT;
	private String category;
	
	public Message(JSONArray message)
	{
		this.message = message;
	}
	
	public Message(JSONArray message, String category)
	{
		this.message = message;
		this.category = category;
	}
	
	public Message(JSONArray message, ChatAlignment alignment, String category)
	{
		this.message = message;
		this.alignment = alignment;
		this.category = category;
	}
	
	public JSONArray getMessage()
	{
		return message;
	}
	
	public String getUnformattedMessage()
	{
		StringBuilder sb = new StringBuilder();
		for (Object o : message)
		{
			sb.append(((JSONObject) o).getString("txt"));
		}
		return sb.toString();
	}
	
	public ChatAlignment getAlignment()
	{
		return alignment;
	}
	
	public void setAlignment(ChatAlignment alignment)
	{
		this.alignment = alignment;
	}
	
	public String getCategory()
	{
		return category;
	}
	
	public void setCategory(String category)
	{
		this.category = category;
	}
}
