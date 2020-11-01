package oldmana.general.md.client.state;

import java.awt.Color;

import oldmana.general.md.client.card.CardProperty;
import oldmana.general.md.client.gui.component.MDCard;
import oldmana.general.md.client.gui.component.MDSelection;

public class ActionStateTradeProperties extends ActionState
{
	private CardProperty ownerCard;
	private CardProperty targetCard;
	
	private MDCard cardViewOwner;
	private MDSelection cardSelectionOwner;
	
	private MDCard cardViewTarget;
	private MDSelection cardSelectionTarget;
	
	public ActionStateTradeProperties(CardProperty ownerCard, CardProperty targetCard)
	{
		super(ownerCard.getOwner(), targetCard.getOwner());
		this.ownerCard = ownerCard;
		this.targetCard = targetCard;
	}
	
	@Override
	public void setup()
	{
		super.setup();
		Color selectColor = getActionOwner() == getClient().getThePlayer() ? Color.BLUE : Color.RED;
		
		cardViewOwner = new MDCard(ownerCard);
		cardViewOwner.setLocation(ownerCard.getOwningCollection().getUI().getLocationOf(ownerCard.getOwningCollection().getIndexOf(ownerCard)));
		getClient().addTableComponent(cardViewOwner, 91);
		cardSelectionOwner = new MDSelection(selectColor);
		cardSelectionOwner.setLocation(cardViewOwner.getLocation());
		cardSelectionOwner.setSize(cardViewOwner.getSize());
		getClient().addTableComponent(cardSelectionOwner, 92);
		
		cardViewTarget = new MDCard(targetCard);
		cardViewTarget.setLocation(targetCard.getOwningCollection().getUI().getLocationOf(targetCard.getOwningCollection().getIndexOf(targetCard)));
		getClient().addTableComponent(cardViewTarget, 91);
		cardSelectionTarget = new MDSelection(selectColor);
		cardSelectionTarget.setLocation(cardViewTarget.getLocation());
		cardSelectionTarget.setSize(cardViewTarget.getSize());
		getClient().addTableComponent(cardSelectionTarget, 92);
	}
	
	@Override
	public void updateUI()
	{
		
	}
	
	@Override
	public void cleanup()
	{
		super.cleanup();
		if (cardViewOwner != null)
		{
			getClient().removeTableComponent(cardViewOwner);
			getClient().removeTableComponent(cardSelectionOwner);
		}
		
		if (cardViewTarget != null)
		{
			getClient().removeTableComponent(cardViewTarget);
			getClient().removeTableComponent(cardSelectionTarget);
		}
	}
}
