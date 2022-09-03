package oldmana.md.server;

import java.io.File;

public class Main
{
	public static void main(String[] args)
	{
		File dataFolder = new File(System.getProperty("user.dir"));
		if (args.length > 0)
		{
			if (args[0].equals("dir=appdata"))
			{
				dataFolder = getLocalFolder();
			}
			else if (args[0].startsWith("dir="))
			{
				dataFolder = new File(args[0].substring(4));
			}
		}
		if (!dataFolder.exists())
		{
			dataFolder.mkdirs();
		}
		File finalDataFolder = dataFolder;
		new Thread(() -> new MDServer(finalDataFolder).startServer(), "Server Thread").start();
	}
	
	public static File getLocalFolder()
	{
		String dirPath;
		
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win"))
		{
			dirPath = System.getenv("AppData");
		}
		else
		{
			// lol who knows if this actually will work properly on linux/mac
			dirPath = System.getProperty("user.home");
			if (os.contains("mac"))
			{
				dirPath += "/Library/Application Support";
			}
		}
		return new File(dirPath, "Monopoly Deal" + File.separator + "server");
	}
}
