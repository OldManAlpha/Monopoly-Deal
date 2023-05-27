package oldmana.md.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent.Type;

public class MDSoundSystem
{
	private static Map<String, MDSound> sounds = new HashMap<String, MDSound>();
	
	private static Map<String, List<MDSound>> defaultSounds = new HashMap<String, List<MDSound>>();
	
	private static List<String> defaultSoundNames = Arrays.asList("CardMove", "CardFlip", "ImportantCardMove", "CardPlace", "DeckShuffle",
			"Alert", "DrawAlert");
	
	static
	{
		try
		{
			loadDefaultSounds();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static void loadDefaultSounds() throws Exception
	{
		for (String name : defaultSoundNames)
		{
			defaultSounds.put(name, new ArrayList<MDSound>());
		}
		
		FileSystem fs = null;
		try
		{
			URI uri = MDClient.class.getResource("/oldmana/md/client/sounds").toURI();
			Path path;
			if (uri.getScheme().equals("jar"))
			{
				fs = FileSystems.newFileSystem(uri, new HashMap<String, Object>());
				path = fs.getPath("/oldmana/md/client/sounds");
			}
			else
			{
				path = Paths.get(uri);
			}
			try (Stream<Path> pathWalker = Files.walk(path, 1))
			{
				pathWalker.forEach(p ->
				{
					String name = p.getFileName().toString();
					if (!name.endsWith(".wav"))
					{
						return;
					}
					name = name.substring(0, name.length() - 4);
					try
					{
						MDSound sound = new MDSound(name, Files.readAllBytes(p), 0);
						for (String defName : defaultSoundNames)
						{
							if (name.startsWith(defName))
							{
								defaultSounds.get(defName).add(sound);
							}
						}
					}
					catch (Exception e)
					{
						System.err.println("Failed to load internal sound: " + name);
						e.printStackTrace();
					}
				});
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (fs != null)
			{
				fs.close();
			}
		}
	}
	
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
			MDSound sound = loadSound(new FileInputStream(file), name);
			sounds.put(name, sound);
			System.out.println("Loaded sound file: " + file.getName());
		}
		catch (Exception e)
		{
			System.out.println("Error loading sound file: " + file.getName());
			e.printStackTrace();
		}
	}
	
	public static MDSound loadSound(InputStream is, String name) throws Exception
	{
		DigestInputStream dis = new DigestInputStream(is, MessageDigest.getInstance("MD5"));
		byte[] data = new byte[dis.available()];
		dis.read(data);
		dis.close();
		int hash = Arrays.hashCode(dis.getMessageDigest().digest());
		return new MDSound(name, data, hash);
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
			byte[] data = defaultSounds.containsKey(name) ?
					defaultSounds.get(name).get(ThreadLocalRandom.current().nextInt(defaultSounds.get(name).size())).getData() :
					sounds.get(name).getData();
			AudioInputStream input = AudioSystem.getAudioInputStream(new ByteArrayInputStream(data));
			AudioFormat format = input.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(input);
			clip.start();
			clip.addLineListener(event ->
			{
				if (event.getType() == Type.STOP)
				{
					event.getLine().close();
				}
			});
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
