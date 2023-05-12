package oldmana.md.net.packet.universal;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.common.Message;
import oldmana.md.common.util.JSONCompressionUtil;
import org.json.JSONArray;
import org.json.JSONObject;

public class PacketChat extends Packet
{
	public byte[] encodedJsonMsg;
	
	public PacketChat() {}
	
	public PacketChat(String json)
	{
		try
		{
			encodedJsonMsg = JSONCompressionUtil.compress(json);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public PacketChat(JSONArray json)
	{
		this(json.toString());
	}
	
	public PacketChat(Message message)
	{
		this(message.getMessage());
	}
	
	public Message getMessage()
	{
		return new Message(getDecodedJSON());
	}
	
	public JSONArray getDecodedJSON()
	{
		try
		{
			return JSONCompressionUtil.decompressArray(encodedJsonMsg);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return new JSONArray("[{\"txt\": \"Message Decode Error\"}]");
		}
	}
	
	public static PacketChat ofSimpleString(String msg)
	{
		JSONObject obj = new JSONObject();
		obj.put("txt", msg);
		JSONArray array = new JSONArray();
		array.put(obj);
		return new PacketChat(array);
	}
}
