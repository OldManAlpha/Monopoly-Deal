package oldmana.md.client.state.client;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.card.CardBuilding;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDCard;
import oldmana.md.client.gui.component.MDPropertySet;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.client.gui.component.large.MDHand;
import oldmana.md.net.packet.client.action.PacketActionPlayCardBuilding;

public class ActionStateClientPlayBuilding extends ActionStateClient
{
	private CardBuilding building;
	
	private MDSelection buildingSelect;
	private MDButton cancel;
	private List<PropertySet> setSelects = new ArrayList<PropertySet>();
	
	public ActionStateClientPlayBuilding(CardBuilding building)
	{
		this.building = building;
	}
	
	@Override
	public void setup()
	{
		Point propLoc = ((MDHand) getClient().getThePlayer().getHand().getUI()).getLocationOf(building);
		buildingSelect = new MDSelection(Color.BLUE);
		buildingSelect.setLocation(propLoc);
		buildingSelect.setSize(MDCard.CARD_SIZE.width * 2, MDCard.CARD_SIZE.height * 2);
		getClient().addTableComponent(buildingSelect, 90);
		
		cancel = new MDButton("Cancel");
		cancel.setSize((int) (buildingSelect.getWidth() * 0.8), (int) (buildingSelect.getHeight() * 0.2));
		cancel.setLocation((int) (buildingSelect.getWidth() * 0.1) + buildingSelect.getX(), (int) (buildingSelect.getHeight() * 0.4) + buildingSelect.getY());
		cancel.setListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				removeState();
			}
		});
		getClient().addTableComponent(cancel, 91);
		
		for (PropertySet set : getClient().getThePlayer().getPropertySetsCompatibleWithBuildingTier(building.getTier()))
		{
			MDPropertySet setUI = (MDPropertySet) set.getUI();
			setUI.enableSelection(new Runnable()
			{
				@Override
				public void run()
				{
					getClient().sendPacket(new PacketActionPlayCardBuilding(building.getID(), set.getID()));
					getClient().setAwaitingResponse(true);
					
					removeState();
				}
			});
			setSelects.add(set);
		}
	}
	
	@Override
	public void cleanup()
	{
		getClient().removeTableComponent(buildingSelect);
		getClient().removeTableComponent(cancel);
		for (PropertySet set : setSelects)
		{
			((MDPropertySet) set.getUI()).disableSelection();
		}
		getClient().getTableScreen().repaint();
	}
}
