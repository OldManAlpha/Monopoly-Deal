package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.Message;
import oldmana.md.common.playerui.ChatAlignment;
import oldmana.md.common.util.JSONCompressionUtil;
import org.json.JSONArray;
import org.json.JSONObject;

public class PacketMessage extends Packet
{
	public byte[] encodedJsonMsg;
	public byte alignment;
	public String category = "";
	
	public PacketMessage() {}
	
	public PacketMessage(String json)
	{
		try
		{
			encodedJsonMsg = JSONCompressionUtil.compress(json);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		alignment = (byte) ChatAlignment.LEFT.ordinal();
	}
	
	public PacketMessage(JSONArray json)
	{
		this(json.toString());
	}
	
	public PacketMessage(Message message)
	{
		this(message.getMessage());
		alignment = (byte) message.getAlignment().ordinal();
		if (message.getCategory() != null)
		{
			category = message.getCategory();
		}
	}
	
	public Message getMessage()
	{
		return new Message(getDecodedJSON(), getAlignment(), "".equals(category) ? null : category);
	}
	
	public ChatAlignment getAlignment()
	{
		return ChatAlignment.values()[alignment];
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
	
	public static PacketMessage ofSimpleString(String msg)
	{
		JSONObject obj = new JSONObject();
		obj.put("txt", msg);
		JSONArray array = new JSONArray();
		array.put(obj);
		return new PacketMessage(array);
	}
}
