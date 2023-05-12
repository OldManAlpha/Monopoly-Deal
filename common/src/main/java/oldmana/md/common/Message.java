package oldmana.md.common;

import org.json.JSONArray;
import org.json.JSONObject;

public class Message
{
	private JSONArray message;
	
	public Message(JSONArray message)
	{
		this.message = message;
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
}
