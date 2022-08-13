package oldmana.md.server;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

public class ServerConfig
{
	private Map<String, String> defaults = new HashMap<String, String>();
	{
		defaults.put("Server-Port", "27599");
		defaults.put("Verbose", "false");
	}
	
	private Map<String, String> settings = new HashMap<String, String>();
	
	private File file;
	
	public ServerConfig()
	{
		file = new File("config.txt");
	}
	
	public void loadConfig()
	{
		try
		{
			if (!file.exists())
			{
				System.out.println("Server config doesn't exist, generating file.");
				file.createNewFile();
			}
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine())
			{
				String[] setting = scanner.nextLine().split("=");
				settings.put(setting[0], setting[1]);
			}
			scanner.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		boolean shouldSave = false;
		for (Entry<String, String> defaultSetting : defaults.entrySet())
		{
			if (!settings.containsKey(defaultSetting.getKey()))
			{
				settings.put(defaultSetting.getKey(), defaultSetting.getValue());
				shouldSave = true;
			}
		}
		
		if (shouldSave)
		{
			saveConfig();
		}
	}
	
	public void saveConfig()
	{
		try
		{
			PrintWriter pw = new PrintWriter(new FileWriter(file, false));
			for (Entry<String, String> setting : settings.entrySet())
			{
				pw.println(setting.getKey() + "=" + setting.getValue());
			}
			pw.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String getDefault(String name)
	{
		return defaults.get(name);
	}
	
	public String getSetting(String name)
	{
		return settings.get(name);
	}
	
	public void setSetting(String name, String value)
	{
		settings.put(name, value);
	}
}
