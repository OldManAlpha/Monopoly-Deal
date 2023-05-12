package oldmana.md.common.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class JSONCompressionUtil
{
	public static byte[] compress(JSONObject obj) throws IOException
	{
		return compress(obj.toString());
	}
	
	public static byte[] compress(JSONArray array) throws IOException
	{
		return compress(array.toString());
	}
	
	public static byte[] compress(String json) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream os = new GZIPOutputStream(bos);
		os.write(json.getBytes(StandardCharsets.UTF_8));
		os.close();
		return bos.toByteArray();
	}
	
	public static JSONArray decompressArray(byte[] compressed) throws IOException
	{
		return new JSONArray(new JSONTokener(new GZIPInputStream(new ByteArrayInputStream(compressed))));
	}
	
	public static JSONObject decompressObject(byte[] compressed) throws IOException
	{
		return new JSONObject(new JSONTokener(new GZIPInputStream(new ByteArrayInputStream(compressed))));
	}
}
