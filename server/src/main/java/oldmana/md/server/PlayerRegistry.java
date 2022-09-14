package oldmana.md.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerRegistry
{
	private Map<UUID, RegisteredPlayer> players = new HashMap<UUID, RegisteredPlayer>();
	
	public void loadPlayers() throws IOException
	{
		File file = new File(MDServer.getInstance().getDataFolder(), "players.dat");
		if (!file.exists())
		{
			System.out.println("Server config doesn't exist, generating file.");
			file.createNewFile();
		}
		if (file.length() == 0)
		{
			return;
		}
		try (FileInputStream is = new FileInputStream(file))
		{
			JSONArray array = new JSONArray(new JSONTokener(is));
			for (Object o : array)
			{
				JSONObject obj = (JSONObject) o;
				UUID uuid = UUID.fromString(obj.getString("uuid"));
				RegisteredPlayer rp = new RegisteredPlayer(uuid,
						obj.has("name") ? obj.getString("name") : "Unknown",
						obj.has("staticName") && obj.getBoolean("staticName"),
						obj.has("op") && obj.getBoolean("op"));
				players.put(uuid, rp);
			}
		}
		catch (JSONException e)
		{
			System.err.println("Failed to parse players.dat(old format?). The file will be overwritten if players join.");
			e.printStackTrace();
		}
	}
	
	public void savePlayers()
	{
		try
		{
			JSONArray array = new JSONArray();
			for (RegisteredPlayer rp : players.values())
			{
				array.put(rp.toJSON());
			}
			
			File file = new File(MDServer.getInstance().getDataFolder(), "players.dat");
			try (FileWriter w = new FileWriter(file))
			{
				array.write(w, 2, 0);
			}
		}
		catch (Exception e)
		{
			System.err.println("Error while saving players!");
			e.printStackTrace();
		}
	}
	
	public RegisteredPlayer registerPlayer(UUID uuid, String name)
	{
		if (isUUIDRegistered(uuid))
		{
			return getRegisteredPlayerByUUID(uuid);
		}
		RegisteredPlayer rp = new RegisteredPlayer(uuid, name, false, false);
		players.put(uuid, rp);
		savePlayers();
		return rp;
	}
	
	public void unregisterPlayer(RegisteredPlayer player)
	{
		players.remove(player.uuid);
	}
	
	public RegisteredPlayer getRegisteredPlayerByUUID(UUID uuid)
	{
		return players.get(uuid);
	}
	
	public boolean isUUIDRegistered(UUID uuid)
	{
		return players.get(uuid) != null;
	}
	
	public List<RegisteredPlayer> getRegisteredPlayers()
	{
		return new ArrayList<RegisteredPlayer>(players.values());
	}
	
	public static class RegisteredPlayer
	{
		public UUID uuid;
		public String name;
		public boolean staticName;
		public boolean op;
		
		public RegisteredPlayer(UUID uuid, String name, boolean staticName, boolean op)
		{
			this.uuid = uuid;
			this.name = name;
			this.staticName = staticName;
			this.op = op;
		}
		
		public JSONObject toJSON()
		{
			JSONObject obj = new JSONObject();
			obj.put("uuid", uuid.toString());
			obj.put("name", name);
			if (staticName)
			{
				obj.put("staticName", staticName);
			}
			obj.put("op", op);
			return obj;
		}
	}
}
