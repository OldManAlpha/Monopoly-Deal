package oldmana.md.client.card.collection;

import oldmana.md.client.gui.component.MDVoidCollection;

public class VoidCollection extends CardCollection
{
	public VoidCollection(int id)
	{
		super(id, true);
		setUI(new MDVoidCollection(this));
	}
}
