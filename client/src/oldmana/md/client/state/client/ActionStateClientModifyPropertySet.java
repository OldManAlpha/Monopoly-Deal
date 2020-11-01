package oldmana.md.client.state.client;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.CardProperty.PropertyColor;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.action.ActionScreenModifyPropertySet;
import oldmana.md.client.gui.component.MDCard;
import oldmana.md.client.gui.component.MDCreateSet;
import oldmana.md.client.gui.component.MDPropertySet;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.net.packet.client.action.PacketActionChangeSetColor;
import oldmana.md.net.packet.client.action.PacketActionMoveProperty;

public class ActionStateClientModifyPropertySet extends ActionStateClient
{
	private PropertySet set;
	
	private ActionScreenModifyPropertySet screen;
	
	private MDCard cardView;
	private MDSelection cardSelection;
	
	private List<PropertySet> setSelects = new ArrayList<PropertySet>();
	private MDCreateSet createSet;
	
	public ActionStateClientModifyPropertySet(PropertySet set)
	{
		this.set = set;
	}
	
	public void propertySelected(Card card)
	{
		getClient().removeTableComponent(screen);
		getClient().getTableScreen().repaint();
		CardProperty property = (CardProperty) card;
		
		cardView = new MDCard(card);
		cardView.setLocation(card.getOwningCollection().getUI().getLocationOf(card.getOwningCollection().getIndexOf(card)));
		getClient().addTableComponent(cardView, 90);
		cardSelection = new MDSelection(Color.BLUE);
		cardSelection.setLocation(cardView.getLocation());
		cardSelection.setSize(cardView.getSize());
		getClient().addTableComponent(cardSelection, 91);
		
		for (PropertySet set : getClient().getThePlayer().getPropertySets(true))
		{
			if (set == this.set)
			{
				continue;
			}
			if (!set.isMonopoly() && set.isCompatibleWith(property))
			{
				MDPropertySet setUI = (MDPropertySet) set.getUI();
				setUI.enableSelection(new Runnable()
				{
					@Override
					public void run()
					{
						getClient().sendPacket(new PacketActionMoveProperty(property.getID(), set.getID()));
						getClient().setAwaitingResponse(true);
						
						removeState();
					}
				});
				setSelects.add(set);
			}
		}
		
		createSet = new MDCreateSet(getClient().getThePlayer());
		getClient().addTableComponent(createSet, 90);
		createSet.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				getClient().sendPacket(new PacketActionMoveProperty(property.getID(), -1));
				getClient().setAwaitingResponse(true);
				
				removeState();
			}
		});
	}
	
	public void colorSelected(PropertyColor color)
	{
		getClient().sendPacket(new PacketActionChangeSetColor(set.getID(), color.getID()));
		getClient().setAwaitingResponse(true);
		getClient().removeTableComponent(screen);
		getClient().getTableScreen().repaint();
		removeState();
	}
	
	public void cancel()
	{
		getClient().removeTableComponent(screen);
		getClient().getTableScreen().repaint();
		removeState();
	}
	
	@Override
	public void setup()
	{
		screen = new ActionScreenModifyPropertySet(this, set);
		getClient().addTableComponent(screen, 200);
	}
	
	@Override
	public void cleanup()
	{
		if (createSet != null)
		{
			getClient().getTableScreen().remove(createSet);
		}
		for (PropertySet set : setSelects)
		{
			((MDPropertySet) set.getUI()).disableSelection();
		}
		if (cardView != null)
		{
			getClient().removeTableComponent(cardView);
			getClient().removeTableComponent(cardSelection);
		}
	}
}
