package oldmana.md.client.state.client;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.card.CardBuilding;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.CardProperty.PropertyColor;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.action.ActionScreenModifyPropertySet;
import oldmana.md.client.gui.component.MDCard;
import oldmana.md.client.gui.component.MDPlayerPropertySets;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.client.gui.component.collection.MDPropertySet;
import oldmana.md.common.net.packet.client.action.PacketActionChangeSetColor;
import oldmana.md.common.net.packet.client.action.PacketActionMoveProperty;
import oldmana.md.common.net.packet.client.action.PacketActionRemoveBuilding;

public class ActionStateClientModifyPropertySet extends ActionStateClient
{
	private PropertySet set;
	
	private ActionScreenModifyPropertySet screen;
	
	private CardProperty card;
	private MDCard cardView;
	private MDSelection selection;
	
	private List<PropertySet> setSelects = new ArrayList<PropertySet>();
	
	public ActionStateClientModifyPropertySet(PropertySet set)
	{
		this.set = set;
	}
	
	public void moveProperty(CardProperty card)
	{
		getClient().getTableScreen().removeActionScreen();
		getClient().getTableScreen().repaint();
		
		this.card = card;
		
		createMoveUI();
	}
	
	public void moveBuilding(CardBuilding card)
	{
		getClient().getTableScreen().removeActionScreen();
		getClient().getTableScreen().repaint();
		
		getClient().sendPacket(new PacketActionRemoveBuilding(card.getID()));
		
		removeState();
	}
	
	public void colorSelected(PropertyColor color)
	{
		getClient().sendPacket(new PacketActionChangeSetColor(set.getID(), color.getID()));
		getClient().setAwaitingResponse(true);
		getClient().getTableScreen().removeActionScreen();
		getClient().getTableScreen().repaint();
		removeState();
	}
	
	public void cancel()
	{
		getClient().getTableScreen().removeActionScreen();
		getClient().getTableScreen().repaint();
		removeState();
	}
	
	private void createMoveUI()
	{
		MDPropertySet setUI = (MDPropertySet) card.getOwningCollection().getUI();
		
		cardView = new MDCard(card);
		cardView.setLocation(setUI.getLocationOf(card));
		selection = new MDSelection(Color.BLUE);
		selection.setLocation(cardView.getLocation());
		selection.setSize(cardView.getSize());
		selection.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				removeState();
				getClient().getTableScreen().repaint();
			}
		});
		setUI.add(selection);
		setUI.add(cardView);
		
		for (PropertySet set : getClient().getThePlayer().getPropertySets(true))
		{
			if (set == this.set)
			{
				continue;
			}
			if (!set.isMonopoly() && set.isCompatibleWith(card))
			{
				((MDPropertySet) set.getUI()).enableSelection(() ->
				{
					getClient().sendPacket(new PacketActionMoveProperty(card.getID(), set.getID()));
					getClient().setAwaitingResponse(true);
					
					removeState();
				});
				setSelects.add(set);
			}
		}
		
		MDPlayerPropertySets setsUI = getClient().getThePlayer().getUI().getPropertySets();
		setsUI.addCreateSet(() ->
		{
			getClient().sendPacket(new PacketActionMoveProperty(cardView.getCard().getID(), -1));
			getClient().setAwaitingResponse(true);
			
			removeState();
		});
	}
	
	@Override
	public void setup()
	{
		screen = new ActionScreenModifyPropertySet(this, set);
		getClient().getTableScreen().setActionScreen(screen);
	}
	
	@Override
	public void cleanup()
	{
		getClient().getTableScreen().removeActionScreen();
		getClient().getThePlayer().getUI().getPropertySets().removeCreateSet();
		for (PropertySet set : setSelects)
		{
			((MDPropertySet) set.getUI()).disableSelection();
		}
		if (cardView != null)
		{
			cardView.getParent().remove(cardView);
		}
		if (selection != null)
		{
			selection.getParent().remove(selection);
		}
	}
	
	@Override
	public void updateUI()
	{
		if (card != null)
		{
			cleanup();
			createMoveUI();
		}
	}
}
