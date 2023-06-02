package oldmana.md.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil
{
	/**
	 * Reads the InputStream until fully read. Does not close the stream.
	 * @param is The stream to read
	 * @return An array of the bytes read
	 */
	public static byte[] readAllBytes(InputStream is) throws IOException
	{
		int bufferLen = 4096;
		byte[] buffer = new byte[bufferLen];
		
		int readBytes;
		ByteArrayOutputStream os = new ByteArrayOutputStream(is.available());
		while ((readBytes = is.read(buffer, 0, bufferLen)) != -1)
		{
			os.write(buffer, 0, readBytes);
		}
		return os.toByteArray();
	}
}
