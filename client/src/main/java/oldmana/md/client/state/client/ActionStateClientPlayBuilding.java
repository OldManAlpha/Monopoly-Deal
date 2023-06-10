package oldmana.md.client.state.client;

import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.card.CardBuilding;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.component.collection.MDPropertySet;
import oldmana.md.common.net.packet.client.action.PacketActionPlayCardBuilding;

public class ActionStateClientPlayBuilding extends ActionStateClient
{
	private CardBuilding building;
	
	private HandCardSelection cardSelection;
	private List<PropertySet> setSelects = new ArrayList<PropertySet>();
	
	public ActionStateClientPlayBuilding(CardBuilding building)
	{
		this.building = building;
	}
	
	@Override
	public void updateUI()
	{
		cleanup();
		setup();
	}
	
	@Override
	public void setup()
	{
		cardSelection = new HandCardSelection(building);
		cardSelection.create(() -> removeState());
		
		for (PropertySet set : getClient().getThePlayer().getPropertySetsCompatibleWithBuildingTier(building.getTier()))
		{
			MDPropertySet setUI = (MDPropertySet) set.getUI();
			setUI.enableSelection(() ->
			{
				getClient().sendPacket(new PacketActionPlayCardBuilding(building.getID(), set.getID()));
				getClient().setAwaitingResponse(true);
				
				removeState();
			});
			setSelects.add(set);
		}
	}
	
	@Override
	public void cleanup()
	{
		cardSelection.destroy();
		for (PropertySet set : setSelects)
		{
			((MDPropertySet) set.getUI()).disableSelection();
		}
		getClient().getTableScreen().repaint();
	}
}
