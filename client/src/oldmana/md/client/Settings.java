package oldmana.md.client;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import oldmana.md.client.gui.util.GraphicsUtils;

import java.util.Scanner;

public class Settings
{
	private Map<String, String> defaults = new HashMap<String, String>();
	{
		defaults.put("Settings-Version", "1");
		defaults.put("Last-IP", "localhost:27599");
		defaults.put("Last-ID", "1");
		defaults.put("Scale", String.valueOf(GraphicsUtils.roundScale(Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 1000)));
		defaults.put("Developer-Mode", "false");
		defaults.put("Extra-Buttons", "false");
	}
	
	private Map<String, String> settings = new HashMap<String, String>();
	
	private File file;
	
	public Settings()
	{
		for (Entry<String, String> defaultSetting : defaults.entrySet())
		{
			if (!settings.containsKey(defaultSetting.getKey()))
			{
				settings.put(defaultSetting.getKey(), defaultSetting.getValue());
			}
		}
	}
	
	public void setLocation(File folder)
	{
		file = new File(folder, "settings.dat");
		saveSettings();
	}
	
	public void loadSettings(File folder)
	{
		this.file = new File(folder, "settings.dat");
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
		if (file != null)
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
	}
	
	public String getSetting(String name)
	{
		return settings.get(name);
	}
	
	public int getIntSetting(String name)
	{
		int i = Integer.parseInt(defaults.get(name));
		try
		{
			i = Integer.parseInt(settings.get(name));
		}
		catch (Exception e)
		{
			System.err.println("Failed to parse setting: " + name);
		}
		return i;
	}
	
	public double getDoubleSetting(String name)
	{
		double d = Double.parseDouble(defaults.get(name));
		try
		{
			d = Double.parseDouble(settings.get(name));
		}
		catch (Exception e)
		{
			System.err.println("Failed to parse setting: " + name);
		}
		return d;
	}
	
	public boolean getBooleanSetting(String name)
	{
		boolean b = Boolean.parseBoolean(defaults.get(name));
		try
		{
			b = Boolean.parseBoolean(settings.get(name));
		}
		catch (Exception e)
		{
			System.err.println("Failed to parse setting: " + name);
		}
		return b;
	}
	
	public void setSetting(String name, String value)
	{
		settings.put(name, value);
	}
	
	public void setSetting(String name, int value)
	{
		settings.put(name, String.valueOf(value));
	}
	
	public void setSetting(String name, double value)
	{
		settings.put(name, String.valueOf(value));
	}
	
	public void setSetting(String name, boolean value)
	{
		settings.put(name, String.valueOf(value));
	}
}
