package oldmana.md.client.state.client;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardBuilding;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.component.MDCard;
import oldmana.md.client.gui.component.MDCreateSet;
import oldmana.md.client.gui.component.MDPlayerPropertySets;
import oldmana.md.client.gui.component.collection.MDBank;
import oldmana.md.client.gui.component.collection.MDPropertySet;
import oldmana.md.common.net.packet.client.action.PacketActionMoveProperty;
import oldmana.md.common.net.packet.client.action.PacketActionRemoveBuilding;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

public class ActionStateClientDragSetCard extends ActionStateClient
{
	private PropertySet set;
	
	private Card card;
	private MDCard cardView;
	private Runnable cancelCallback;
	
	private List<PropertySet> setSelects = new ArrayList<PropertySet>();
	
	public ActionStateClientDragSetCard(PropertySet set, MDCard cardView, Runnable cancelCallback)
	{
		this.set = set;
		card = cardView.getCard();
		this.cardView = cardView;
		this.cancelCallback = cancelCallback;
	}
	
	private void createMoveUI()
	{
		if (card instanceof CardProperty)
		{
			for (PropertySet set : getClient().getThePlayer().getPropertySets(true))
			{
				CardProperty prop = (CardProperty) card;
				if (set == this.set)
				{
					continue;
				}
				if (!set.isMonopoly() && set.isCompatibleWith(prop))
				{
					((MDPropertySet) set.getUI()).enableSelection(() ->
					{
					
					});
					setSelects.add(set);
				}
			}
			
			MDPlayerPropertySets setsUI = getClient().getThePlayer().getUI().getPropertySets();
			setsUI.addCreateSet(() ->
			{
			
			});
		}
		else if (card instanceof CardBuilding)
		{
			// Maybe visually show bank as an option
		}
	}
	
	public void cardDropped(Component component)
	{
		if (component instanceof MDPropertySet)
		{
			PropertySet set = (PropertySet) ((MDPropertySet) component).getCollection();
			if (set != card.getOwningCollection())
			{
				getClient().sendPacket(new PacketActionMoveProperty(card.getID(), set.getID()));
				getClient().setAwaitingResponse(true);
			}
		}
		else if (component instanceof MDCreateSet)
		{
			getClient().sendPacket(new PacketActionMoveProperty(card.getID(), -1));
			getClient().setAwaitingResponse(true);
		}
		else if (component instanceof MDBank)
		{
			if (card instanceof CardBuilding)
			{
				getClient().sendPacket(new PacketActionRemoveBuilding(card.getID()));
				getClient().setAwaitingResponse(true);
			}
		}
		removeState();
	}
	
	@Override
	public void setup()
	{
		createMoveUI();
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
		cardView.getParent().remove(cardView);
		cancelCallback.run();
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
