package oldmana.md.server;

public class MDSound
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
