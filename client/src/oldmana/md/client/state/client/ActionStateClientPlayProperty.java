package oldmana.md.client.state.client;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.component.MDCard;
import oldmana.md.client.gui.component.MDCreateSet;
import oldmana.md.client.gui.component.MDPropertySet;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.client.gui.component.large.MDHand;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.net.packet.client.action.PacketActionPlayCardProperty;

public class ActionStateClientPlayProperty extends ActionStateClient
{
	private CardProperty property;
	
	private MDSelection propSelect;
	private List<PropertySet> setSelects = new ArrayList<PropertySet>();
	private MDCreateSet createSet;
	
	public ActionStateClientPlayProperty(CardProperty property)
	{
		this.property = property;
	}
	
	@Override
	public void setup()
	{
		Point propLoc = ((MDHand) getClient().getThePlayer().getHand().getUI()).getLocationOf(property);
		propSelect = new MDSelection(Color.BLUE);
		propSelect.setLocation(propLoc);
		propSelect.setSize(MDCard.CARD_SIZE.width * 2, MDCard.CARD_SIZE.height * 2);
		getClient().getTableScreen().add(propSelect, new Integer(90));
		
		for (PropertySet set : getClient().getThePlayer().getPropertySets(true))
		{
			if (!set.isMonopoly() && set.isCompatibleWith(property))
			{
				MDPropertySet setUI = (MDPropertySet) set.getUI();
				setUI.enableSelection(new Runnable()
				{
					@Override
					public void run()
					{
						getClient().sendPacket(new PacketActionPlayCardProperty(property.getID(), set.getID()));
						getClient().setAwaitingResponse(true);
						
						removeState();
					}
				});
				setSelects.add(set);
			}
		}
		
		createSet = new MDCreateSet(getClient().getThePlayer());
		createSet.setSize(GraphicsUtils.getCardWidth(), GraphicsUtils.getCardHeight());
		getClient().getTableScreen().add(createSet, new Integer(90));
		createSet.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				getClient().sendPacket(new PacketActionPlayCardProperty(property.getID(), -1));
				getClient().setAwaitingResponse(true);
				
				removeState();
			}
		});
		
		/*
		MDButton button = getClient().getTableScreen().getMultiButton();
		button.setEnabled(true);
		button.setText("Cancel");
		button.setListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				removeState();
			}
		});
		*/
	}
	
	@Override
	public void cleanup()
	{
		getClient().getTableScreen().remove(propSelect);
		if (createSet != null)
		{
			getClient().getTableScreen().remove(createSet);
		}
		for (PropertySet set : setSelects)
		{
			((MDPropertySet) set.getUI()).disableSelection();
		}
		
		/*
		MDButton button = getClient().getTableScreen().getMultiButton();
		button.setEnabled(false);
		button.removeListener();
		*/
	}
}
