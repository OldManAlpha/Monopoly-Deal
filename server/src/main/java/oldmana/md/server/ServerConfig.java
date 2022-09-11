package oldmana.md.server;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

public class ServerConfig extends JSONObject
{
	private JSONObject defaults = new JSONObject();
	{
		defaults.put("configVersion", 2);
		defaults.put("port", 27599);
		defaults.put("verbose", false);
	}
	
	private Map<String, ConvertType> conversionTypes = new HashMap<String, ConvertType>();
	{
		conversionTypes.put("Server-Port", new ConvertType(int.class, "port"));
		conversionTypes.put("Verbose", new ConvertType(boolean.class, "verbose"));
	}
	
	private static class ConvertType
	{
		Class<?> type;
		String newName;
		
		ConvertType(Class<?> type, String newName)
		{
			this.type = type;
			this.newName = newName;
		}
	}
	
	private File file;
	
	public ServerConfig()
	{
		file = new File(MDServer.getInstance().getDataFolder(), "config.txt");
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
			boolean converted = false;
			try (FileInputStream is = new FileInputStream(file))
			{
				JSONObject object = new JSONObject(new JSONTokener(is));
				setMap(object.getMap());
			}
			catch (JSONException e)
			{
				System.out.println("Failed to load config. Trying to convert from old format..");
				try (Scanner scanner = new Scanner(file))
				{
					while (scanner.hasNextLine())
					{
						String[] setting = scanner.nextLine().split("=");
						ConvertType convertType = conversionTypes.get(setting[0]);
						if (convertType != null)
						{
							Object value = null;
							if (convertType.type == int.class)
							{
								value = Integer.parseInt(setting[1]);
							}
							else if (convertType.type == double.class)
							{
								value = Double.parseDouble(setting[1]);
							}
							else if (convertType.type == boolean.class)
							{
								value = Boolean.parseBoolean(setting[1]);
							}
							else if (convertType.type == String.class)
							{
								value = setting[1];
							}
							put(convertType.newName, value);
						}
					}
					converted = true;
				}
				catch (Exception e2)
				{
					System.err.println("Failed config conversion!");
					throw new RuntimeException(e2);
				}
			}
			if (converted)
			{
				saveSettings();
				System.out.println("Successfully converted config to new format");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		applyDefaults();
	}
	
	public void saveSettings()
	{
		if (file != null)
		{
			try (FileWriter fw = new FileWriter(file))
			{
				write(fw, 2, 0);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void applyDefaults()
	{
		boolean shouldSave = false;
		for (Entry<String, Object> defaultSetting : defaults.getMap().entrySet())
		{
			if (!has(defaultSetting.getKey()))
			{
				put(defaultSetting.getKey(), defaultSetting.getValue());
				shouldSave = true;
			}
		}
		if (shouldSave)
		{
			saveSettings();
		}
	}
}
