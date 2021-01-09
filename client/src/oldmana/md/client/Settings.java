package oldmana.md.client;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class Settings
{
	private Map<String, String> defaults = new HashMap<String, String>();
	{
		defaults.put("Settings-Version", "1");
		defaults.put("Last-IP", "localhost:27599");
		defaults.put("Last-ID", "1");
	}
	
	private Map<String, String> settings = new HashMap<String, String>();
	
	private File file;
	
	public Settings()
	{
		file = new File("settings.dat");
	}
	
	public void loadSettings()
	{
		try
		{
			if (!file.exists())
			{
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
		
		for (Entry<String, String> defaultSetting : defaults.entrySet())
		{
			if (!settings.containsKey(defaultSetting.getKey()))
			{
				settings.put(defaultSetting.getKey(), defaultSetting.getValue());
			}
		}
	}
	
	public void saveSettings()
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
	
	public String getSetting(String name)
	{
		return settings.get(name);
	}
	
	public void setSetting(String name, String value)
	{
		settings.put(name, value);
	}
}
