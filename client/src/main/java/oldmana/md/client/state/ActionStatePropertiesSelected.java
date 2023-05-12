package oldmana.md.client.state;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.Player;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.gui.component.MDCard;
import oldmana.md.client.gui.component.MDComponent;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.client.gui.component.collection.MDCardCollectionBase;
import oldmana.md.client.gui.component.large.MDPlayer;

public class ActionStatePropertiesSelected extends ActionState
{
	private List<CardProperty> cards;
	
	private List<MDComponent> components;
	
	public ActionStatePropertiesSelected(Player player, Player target, List<CardProperty> cards)
	{
		super(player, target);
		this.cards = cards;
	}

	@Override
	public void setup()
	{
		super.setup();
		
		createSelections();
	}
	
	@Override
	public void updateUI()
	{
		destroySelections();
		createSelections();
	}
	
	private void createSelections()
	{
		Color selectColor = getActionOwner() == getClient().getThePlayer() ? Color.BLUE : Color.RED;
		
		components = new ArrayList<MDComponent>();
		for (Card card : cards)
		{
			MDCard cardView = new MDCard(card);
			MDCardCollectionBase collectionUI = card.getOwningCollection().getUI();
			MDPlayer playerUI = card.getOwner().getUI();
			cardView.setLocation(collectionUI.getLocationOfRelative(card.getOwningCollection().getIndexOf(card), playerUI));
			playerUI.add(cardView, 0);
			MDSelection cardSelection = new MDSelection(selectColor);
			cardSelection.setLocation(cardView.getLocation());
			cardSelection.setSize(cardView.getSize());
			playerUI.add(cardSelection, 0);
			
			components.add(cardView);
			components.add(cardSelection);
		}
	}
	
	private void destroySelections()
	{
		for (MDComponent c : components)
		{
			c.getParent().remove(c);
		}
	}
	
	@Override
	public void cleanup()
	{
		super.cleanup();
		
		destroySelections();
	}
}
