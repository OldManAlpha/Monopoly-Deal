package oldmana.md.client.state;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import oldmana.md.client.Player;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.action.ActionScreenSelectProperty;
import oldmana.md.client.gui.action.ActionScreenSelectProperty.PropertySelectListener;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDCard;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.client.gui.component.collection.MDPropertySet;
import oldmana.md.net.packet.client.action.PacketActionSelectProperties;

public class ActionStateTargetPlayerProperty extends ActionState
{
	private Card targetCard;
	
	private MDCard cardViewOther;
	private MDSelection cardSelectionOther;
	
	public ActionStateTargetPlayerProperty(Player player)
	{
		super(player);
	}
	
	public void setTargetCard(Card card)
	{
		targetCard = card;
	}
	
	public Card getTargetCard()
	{
		return targetCard;
	}
	
	@Override
	public void setup()
	{
		Player player = getActionOwner();
		if (player == getClient().getThePlayer())
		{
			for (Player other : getClient().getOtherPlayers())
			{
				for (PropertySet set : other.getPropertySets())
				{
					if (set.hasBase() && !set.isMonopoly())
					{
						MDPropertySet setUi = (MDPropertySet) set.getUI();
						setUi.enableSelection(new Runnable()
						{
							@Override
							public void run()
							{
								if (set.getCardCount() > 1)
								{
									ActionScreenSelectProperty screen = new ActionScreenSelectProperty(set, false);
									screen.setListener(new PropertySelectListener()
									{
										@Override
										public void propertySelected(CardProperty prop)
										{
											propertySelectedOther(prop);
											getClient().getTableScreen().removeActionScreen();
										}
										
										@Override
										public void cancel()
										{
											getClient().getTableScreen().removeActionScreen();
										}
									});
									getClient().getTableScreen().setActionScreen(screen);
								}
								else
								{
									propertySelectedOther(set.getPropertyCardAt(0));
								}
							}
						});
					}
				}
			}
		}
	}
	
	public void propertySelectedOther(CardProperty prop)
	{
		if (cardViewOther != null)
		{
			getClient().removeTableComponent(cardViewOther);
			getClient().removeTableComponent(cardSelectionOther);
		}
		
		cardViewOther = new MDCard(prop);
		cardViewOther.setLocation(prop.getOwningCollection().getUI().getLocationOf(prop.getOwningCollection().getIndexOf(prop)));
		getClient().addTableComponent(cardViewOther, 91);
		cardSelectionOther = new MDSelection(Color.BLUE);
		cardSelectionOther.setLocation(cardViewOther.getLocation());
		cardSelectionOther.setSize(cardViewOther.getSize());
		getClient().addTableComponent(cardSelectionOther, 92);
		updateButton();
		getClient().getTableScreen().repaint();
	}
	
	public void updateButton()
	{
		if (getActionOwner() == getClient().getThePlayer())
		{
			MDButton button = getClient().getTableScreen().getMultiButton();
			button.setText("Confirm");
			if (cardViewOther != null)
			{
				button.setEnabled(true);
				button.setListener(new MouseAdapter()
				{
					@Override
					public void mouseReleased(MouseEvent event)
					{
						getClient().sendPacket(new PacketActionSelectProperties(cardViewOther.getCard().getID()));
						cleanup();
						getClient().setAwaitingResponse(true);
						button.setEnabled(false);
						button.removeListener();
					}
				});
			}
			else
			{
				button.setEnabled(false);
				button.removeListener();
			}
		}
	}
	
	@Override
	public void cleanup()
	{
		for (Player player : getClient().getOtherPlayers())
		{
			for (PropertySet set : player.getPropertySets())
			{
				((MDPropertySet) set.getUI()).disableSelection();
			}
		}
		
		if (cardViewOther != null)
		{
			getClient().removeTableComponent(cardViewOther);
			getClient().removeTableComponent(cardSelectionOther);
		}
	}
}
