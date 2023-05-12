package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.common.util.JSONCompressionUtil;
import org.json.JSONObject;

import java.io.IOException;

public class PacketGameRules extends Packet
{
	public byte[] encodedRules;
	
	public PacketGameRules() {}
	
	public PacketGameRules(JSONObject rules)
	{
		try
		{
			encodedRules = JSONCompressionUtil.compress(rules);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public JSONObject getGameRules()
	{
		try
		{
			return JSONCompressionUtil.decompressObject(encodedRules);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
