package oldmana.md.client.card.collection;

public class VoidCollection extends CardCollection
{
	public VoidCollection(int id)
	{
		super(id, true);
	}
	
	@Override
	public void addUnknownCards(int amount) {}
	
	@Override
	public void removeUnknownCards(int amount) {}
}
