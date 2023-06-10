package oldmana.md.server.card;

import oldmana.md.common.net.packet.server.PacketCardDescription;
import oldmana.md.server.MDServer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CardDescription
{
	private static int nextID;
	private static Map<Integer, CardDescription> descriptionMap = new HashMap<Integer, CardDescription>();
	private static Map<Integer, CardDescription> hashMap = new HashMap<Integer, CardDescription>();
	
	private int id;
	private String[] description;
	
	public CardDescription(String... description)
	{
		id = nextID++;
		this.description = description;
		descriptionMap.put(id, this);
		hashMap.put(Arrays.hashCode(description), this);
		MDServer.getInstance().broadcastPacket(new PacketCardDescription(id, description));
	}
	
	public int getID()
	{
		return id;
	}
	
	public String[] getText()
	{
		return description;
	}
	
	public static Collection<CardDescription> getAllDescriptions()
	{
		return descriptionMap.values();
	}
	
	public static CardDescription getDescriptionByID(int id)
	{
		return descriptionMap.get(id);
	}
	
	public static CardDescription getDescriptionByText(String[] text)
	{
		int hash = Arrays.hashCode(text);
		return hashMap.get(hash);
	}
	
	public static CardDescription getDescription(String... text)
	{
		CardDescription desc = getDescriptionByText(text);
		if (desc == null)
		{
			desc = new CardDescription(text);
		}
		return desc;
	}
}
