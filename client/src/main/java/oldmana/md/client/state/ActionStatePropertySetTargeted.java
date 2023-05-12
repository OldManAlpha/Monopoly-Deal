package oldmana.md.client.state;

import java.awt.Color;

import oldmana.md.client.Player;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.component.collection.MDPropertySet;

public class ActionStatePropertySetTargeted extends ActionState
{
	private PropertySet targetMonopoly;
	
	public ActionStatePropertySetTargeted(Player player, PropertySet targetMonopoly)
	{
		super(player, targetMonopoly.getOwner());
		this.targetMonopoly = targetMonopoly;
	}
	
	@Override
	public void updateUI()
	{
		MDPropertySet ui = (MDPropertySet) targetMonopoly.getUI();
		ui.disableSelection();
		ui.enableSelection(getActionOwner() == getClient().getThePlayer() ? Color.BLUE : Color.RED);
	}
	
	@Override
	public void setup()
	{
		super.setup();
		((MDPropertySet) targetMonopoly.getUI()).enableSelection(getActionOwner() == getClient().getThePlayer() ? Color.BLUE : Color.RED);
	}
	
	@Override
	public void cleanup()
	{
		super.cleanup();
		
		if (targetMonopoly != null)
		{
			((MDPropertySet) targetMonopoly.getUI()).disableSelection();
		}
	}
}
