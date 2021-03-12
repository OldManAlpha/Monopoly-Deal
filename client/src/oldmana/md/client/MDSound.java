package oldmana.md.client;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class MDSound
{
	private static Map<String, byte[]> soundMap = new HashMap<String, byte[]>();
	
	public static void addSound(String name, byte[] data)
	{
		soundMap.put(name, data);
	}
	
	public static void playSound(String name)
	{
		try
		{
			AudioInputStream input = AudioSystem.getAudioInputStream(new ByteArrayInputStream(soundMap.get(name)));
			AudioFormat format = input.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(input);
			clip.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
