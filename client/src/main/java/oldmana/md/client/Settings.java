package oldmana.md.client;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import oldmana.md.client.gui.util.GraphicsUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Scanner;

public class Settings extends JSONObject
{
	private JSONObject defaults = new JSONObject();
	{
		defaults.put("settingsVersion", 2);
		defaults.put("lastIP", "localhost:27599");
		defaults.put("lastID", 1);
		defaults.put("scale", GraphicsUtils.roundScale(Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 1000));
		defaults.put("developerMode", false);
		defaults.put("extraButtons", false);
		defaults.put("framerate", 60);
	}
	
	private Map<String, ConvertType> conversionTypes = new HashMap<String, ConvertType>();
	{
		conversionTypes.put("Last-IP", new ConvertType(String.class, "lastIP"));
		conversionTypes.put("Scale", new ConvertType(double.class, "scale"));
		conversionTypes.put("Developer-Mode", new ConvertType(boolean.class, "developerMode"));
		conversionTypes.put("Extra-Buttons", new ConvertType(boolean.class, "extraButtons"));
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
	
	public Settings()
	{
		applyDefaults();
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
			boolean converted = false;
			try (FileInputStream is = new FileInputStream(file))
			{
				JSONObject object = new JSONObject(new JSONTokener(is));
				setMap(object.getMap());
			}
			catch (JSONException e)
			{
				System.out.println("Failed to load settings. Trying to convert from old format..");
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
					System.err.println("Failed settings conversion!");
					throw new RuntimeException(e2);
				}
			}
			if (converted)
			{
				saveSettings();
				System.out.println("Successfully converted settings to new format");
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
		for (Entry<String, Object> defaultSetting : defaults.getMap().entrySet())
		{
			if (!has(defaultSetting.getKey()))
			{
				put(defaultSetting.getKey(), defaultSetting.getValue());
			}
		}
		if (!has("clientKey"))
		{
			SecureRandom r = new SecureRandom();
			byte[] key = new byte[16];
			r.nextBytes(key);
			put("clientKey", new BigInteger(key));
			saveSettings();
		}
	}
}
