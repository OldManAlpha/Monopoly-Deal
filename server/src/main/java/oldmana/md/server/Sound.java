package oldmana.md.server;

public class Sound
{
	private String name;
	private byte[] data;
	private int hash;
	
	public Sound(String name, byte[] data, int hash)
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
