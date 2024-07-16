package oldmana.md.client.state.client;

import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.card.CardButton;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.component.MDPlayerPropertySets;
import oldmana.md.client.gui.component.collection.MDPropertySet;
import oldmana.md.common.net.packet.client.action.PacketActionUseCardButton;

public class ActionStateClientPlayProperty extends ActionStateClient
{
	private CardProperty property;
	private CardButton button;
	
	private HandCardSelection cardSelection;
	private List<PropertySet> setSelects = new ArrayList<PropertySet>();
	
	public ActionStateClientPlayProperty(CardProperty property, CardButton button)
	{
		this.property = property;
		this.button = button;
	}
	
	public void createSelections()
	{
		cardSelection = new HandCardSelection(property);
		cardSelection.create(() -> removeState());
		
		for (PropertySet set : getClient().getThePlayer().getPropertySets(true))
		{
			if (!set.isMonopoly() && set.isCompatibleWith(property))
			{
				MDPropertySet setUI = (MDPropertySet) set.getUI();
				setUI.enableSelection(() ->
				{
					getClient().sendPacket(new PacketActionUseCardButton(property.getID(), button.getID(), set.getID()));
					getClient().setAwaitingResponse(true);
					
					removeState();
				});
				setSelects.add(set);
			}
		}
		
		MDPlayerPropertySets setsUI = getClient().getThePlayer().getUI().getPropertySets();
		setsUI.addCreateSet(() ->
		{
			getClient().sendPacket(new PacketActionUseCardButton(property.getID(), button.getID(), -1));
			getClient().setAwaitingResponse(true);
			
			removeState();
		});
		getClient().getTableScreen().repaint();
	}
	
	public void destroySelections()
	{
		cardSelection.destroy();
		getClient().getThePlayer().getUI().getPropertySets().removeCreateSet();
		for (PropertySet set : setSelects)
		{
			((MDPropertySet) set.getUI()).disableSelection();
		}
		getClient().getTableScreen().repaint();
	}
	
	@Override
	public void updateUI()
	{
		destroySelections();
		createSelections();
	}
	
	@Override
	public void setup()
	{
		createSelections();
	}
	
	@Override
	public void cleanup()
	{
		destroySelections();
	}
}
