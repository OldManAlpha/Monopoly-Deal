package oldmana.md.client.state.client;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardBuilding;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.CardProperty.PropertyColor;
import oldmana.md.client.card.collection.Bank;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.action.ActionScreenModifyPropertySet;
import oldmana.md.client.gui.component.MDCard;
import oldmana.md.client.gui.component.MDCreateSet;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.client.gui.component.collection.MDPropertySet;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.net.packet.client.action.PacketActionChangeSetColor;
import oldmana.md.net.packet.client.action.PacketActionMoveProperty;

public class ActionStateClientModifyPropertySet extends ActionStateClient
{
	private PropertySet set;
	
	private ActionScreenModifyPropertySet screen;
	
	private MDCard cardView;
	private MDSelection selection;
	
	private List<PropertySet> setSelects = new ArrayList<PropertySet>();
	private MDCreateSet createSet;
	
	public ActionStateClientModifyPropertySet(PropertySet set)
	{
		this.set = set;
	}
	
	public void propertySelected(Card card)
	{
		getClient().getTableScreen().removeActionScreen();
		getClient().getTableScreen().repaint();
		CardProperty property = (CardProperty) card;
		
		cardView = new MDCard(card);
		cardView.setLocation(card.getOwningCollection().getUI().getScreenLocationOf(card.getOwningCollection().getIndexOf(card)));
		getClient().addTableComponent(cardView, 90);
		selection = new MDSelection(Color.BLUE);
		selection.setLocation(cardView.getLocation());
		selection.setSize(cardView.getSize());
		getClient().addTableComponent(selection, 91);
		
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
		createSet.setSize(GraphicsUtils.getCardWidth(), GraphicsUtils.getCardHeight());
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
	
	public void buildingSelected(Card card)
	{
		CardBuilding building = (CardBuilding) card;
		Bank bank = card.getOwner().getBank();
		selection = new MDSelection(Color.BLUE);
		selection.setLocation(bank.getUI().getLocation());
		selection.setSize(bank.getUI().getSize());
		getClient().addTableComponent(selection, 91);
		selection.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				// TODO: Move building to bank packet
				getClient().setAwaitingResponse(true);
				
				removeState();
			}
		});
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
	
	@Override
	public void setup()
	{
		screen = new ActionScreenModifyPropertySet(this, set);
		getClient().getTableScreen().setActionScreen(screen);
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
		}
		if (selection != null)
		{
			getClient().removeTableComponent(selection);
		}
	}
	
	@Override
	public void updateUI()
	{
		if (createSet != null)
		{
			createSet.setSize(GraphicsUtils.getCardWidth(), GraphicsUtils.getCardHeight());
		}
	}
}
