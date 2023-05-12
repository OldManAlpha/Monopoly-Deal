package oldmana.md.common;

import org.json.JSONArray;
import org.json.JSONObject;

public class Message
{
	private JSONArray message;
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
	
	public String getCategory()
	{
		return category;
	}
	
	public void setCategory(String category)
	{
		this.category = category;
	}
}
