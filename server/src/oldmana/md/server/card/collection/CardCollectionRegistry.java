package oldmana.md.server.card.collection;

import java.util.ArrayList;
import java.util.List;

public class CardCollectionRegistry
{
	public static List<CardCollection> collections = new ArrayList<CardCollection>();
	
	public static CardCollection getCardCollection(int id)
	{
		for (CardCollection collection : collections)
		{
			if (collection.getID() == id)
			{
				return collection;
			}
		}
		return null;
	}
	
	public static void registerCardCollection(CardCollection collection)
	{
		collections.add(collection);
	}
	
	public static List<CardCollection> getRegisteredCardCollections()
	{
		return collections;
	}
}
