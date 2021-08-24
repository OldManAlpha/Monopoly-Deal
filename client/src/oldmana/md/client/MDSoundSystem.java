package oldmana.md.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class MDSoundSystem
{
	private static Map<String, MDSound> sounds = new HashMap<String, MDSound>();
	
	public static void loadCache()
	{
		try
		{
			File soundsFolder = new File(MDClient.getInstance().getDataFolder(), "cache" + File.separator + "sounds");
			if (!soundsFolder.exists())
			{
				soundsFolder.mkdirs();
			}
			for (File f : soundsFolder.listFiles())
			{
				if (!f.isDirectory() && f.getName().endsWith(".wav"))
				{
					loadSound(f, f.getName().substring(0, f.getName().length() - 4));
				}
			}
		}
		catch (Exception e)
		{
			System.err.println("Failed to load sound cache!");
			e.printStackTrace();
		}
	}
	
	public static void saveSound(String name)
	{
		try
		{
			File folder = new File(MDClient.getInstance().getDataFolder(), "cache" + File.separator + "sounds");
			File file = new File(folder, name + ".wav");
			FileOutputStream os = new FileOutputStream(file);
			os.write(sounds.get(name).getData());
			os.close();
			System.out.println("Saved sound: " + name);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void loadSound(File file, String name)
	{
		try
		{
			DigestInputStream is = new DigestInputStream(new FileInputStream(file), MessageDigest.getInstance("MD5"));
			byte[] data = new byte[is.available()];
			is.read(data);
			is.close();
			int hash = Arrays.hashCode(is.getMessageDigest().digest());
			sounds.put(name, new MDSound(name, data, hash));
			System.out.println("Loaded sound file: " + file.getName());
		}
		catch (Exception e)
		{
			System.out.println("Error loading sound file: " + file.getName());
			e.printStackTrace();
		}
	}
	
	public static void addSound(String name, byte[] data, int hash)
	{
		sounds.put(name, new MDSound(name, data, hash));
		saveSound(name);
	}
	
	public static void playSound(String name)
	{
		try
		{
			AudioInputStream input = AudioSystem.getAudioInputStream(new ByteArrayInputStream(sounds.get(name).getData()));
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
	
	public static Map<String, MDSound> getSounds()
	{
		return sounds;
	}
	
	public static class MDSound
	{
		private String name;
		private byte[] data;
		private int hash;
		
		public MDSound(String name, byte[] data, int hash)
		{
			this.name = name;
			this.data = data;
			this.hash = hash;
		}
		
		public String getName()
		{
			return name;
		}
		
		public byte[] getData()
		{
			return data;
		}
		
		public int getHash()
		{
			return hash;
		}
	}
}
