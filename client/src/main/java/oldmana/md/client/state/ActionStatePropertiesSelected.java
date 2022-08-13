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
		
		Color selectColor = getActionOwner() == getClient().getThePlayer() ? Color.BLUE : Color.RED;
		
		components = new ArrayList<MDComponent>();
		for (Card card : cards)
		{
			MDCard cardView = new MDCard(card);
			cardView.setLocation(card.getOwningCollection().getUI().getScreenLocationOf(card.getOwningCollection().getIndexOf(card)));
			getClient().addTableComponent(cardView, 91);
			MDSelection cardSelection = new MDSelection(selectColor);
			cardSelection.setLocation(cardView.getLocation());
			cardSelection.setSize(cardView.getSize());
			getClient().addTableComponent(cardSelection, 92);
			
			components.add(cardView);
			components.add(cardSelection);
		}
	}
	
	@Override
	public void updateUI()
	{
		
	}
	
	@Override
	public void cleanup()
	{
		super.cleanup();
		
		for (MDComponent c : components)
		{
			getClient().removeTableComponent(c);
		}
	}
}
