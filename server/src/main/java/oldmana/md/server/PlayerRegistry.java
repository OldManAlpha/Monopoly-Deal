package oldmana.md.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlayerRegistry
{
	private List<RegisteredPlayer> players = new ArrayList<RegisteredPlayer>();
	
	public void loadPlayers()
	{
		try
		{
			File f = new File("players.dat");
			if (!f.exists())
			{
				System.out.println("Player data doesn't exist, generating file.");
				f.createNewFile();
			}
			Scanner s = new Scanner(new FileInputStream(f));
			while (s.hasNextLine())
			{
				String line = s.nextLine();
				String[] data = line.split(",");
				registerPlayer(new RegisteredPlayer(Integer.parseInt(data[0]), data[1], Boolean.parseBoolean(data[2])));
			}
			s.close();
		}
		catch (Exception e)
		{
			System.out.println("Error while loading players!");
			e.printStackTrace();
		}
	}
	
	public void savePlayers()
	{
		try
		{
			File f = new File("players.dat");
			PrintWriter pw = new PrintWriter(new FileOutputStream(f));
			for (RegisteredPlayer player : players)
			{
				pw.println(player.uid + "," + player.name + "," + player.op);
			}
			pw.close();
		}
		catch (Exception e)
		{
			System.out.println("Error while saving players!");
			e.printStackTrace();
		}
	}
	
	public void registerPlayer(RegisteredPlayer player)
	{
		players.add(player);
	}
	
	public void unregisterPlayer(RegisteredPlayer player)
	{
		players.remove(player);
	}
	
	public RegisteredPlayer getRegisteredPlayerByUID(int uid)
	{
		for (RegisteredPlayer rp : players)
		{
			if (rp.uid == uid)
			{
				return rp;
			}
		}
		return null;
	}
	
	public boolean isUIDRegistered(int uid)
	{
		return getRegisteredPlayerByUID(uid) != null;
	}
	
	public String getNameOf(int uid)
	{
		return getRegisteredPlayerByUID(uid).name;
	}
	
	public List<RegisteredPlayer> getRegisteredPlayers()
	{
		return players;
	}
	
	public static class RegisteredPlayer
	{
		public int uid;
		public String name;
		public boolean op;
		
		public RegisteredPlayer(int uid, String name, boolean op)
		{
			this.uid = uid;
			this.name = name;
			this.op = op;
		}
	}
}
