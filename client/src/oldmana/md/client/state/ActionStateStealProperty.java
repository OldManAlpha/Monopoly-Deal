package oldmana.md.client.state;

import java.awt.Color;

import oldmana.md.client.Player;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.gui.component.MDCard;
import oldmana.md.client.gui.component.MDSelection;

public class ActionStateStealProperty extends ActionState
{
	private CardProperty targetCard;
	
	private MDCard cardViewTarget;
	private MDSelection cardSelectionTarget;
	
	public ActionStateStealProperty(Player player, CardProperty targetCard)
	{
		super(player, targetCard.getOwner());
		this.targetCard = targetCard;
	}
	
	@Override
	public void setup()
	{
		super.setup();
		
		Color selectColor = getActionOwner() == getClient().getThePlayer() ? Color.BLUE : Color.RED;
		
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
		
		if (cardViewTarget != null)
		{
			getClient().removeTableComponent(cardViewTarget);
			getClient().removeTableComponent(cardSelectionTarget);
		}
	}
}
