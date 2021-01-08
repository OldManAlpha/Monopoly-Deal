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
import oldmana.md.client.gui.component.MDPropertySet;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.net.packet.client.action.PacketActionSelectProperties;

public class ActionStateTargetAnyProperty extends ActionState
{
	private Card targetCard;
	
	private MDCard cardView;
	private MDSelection cardSelection;
	
	public ActionStateTargetAnyProperty(Player player)
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
			for (Player other : getClient().getAllPlayers())
			{
				for (PropertySet set : other.getPropertySets())
				{
					MDPropertySet setUi = (MDPropertySet) set.getUI();
					setUi.enableSelection(new Runnable()
					{
						@Override
						public void run()
						{
							if (set.getCardCount() > 1)
							{
								ActionScreenSelectProperty screen = new ActionScreenSelectProperty(set, true);
								screen.setListener(new PropertySelectListener()
								{
									@Override
									public void propertySelected(CardProperty prop)
									{
										propertyTargeted(prop);
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
								propertyTargeted(set.getPropertyCardAt(0));
							}
						}
					});
				}
			}
		}
	}
	
	public void propertyTargeted(CardProperty prop)
	{
		if (cardView != null)
		{
			getClient().removeTableComponent(cardView);
			getClient().removeTableComponent(cardSelection);
		}
		
		cardView = new MDCard(prop);
		cardView.setLocation(prop.getOwningCollection().getUI().getLocationOf(prop.getOwningCollection().getIndexOf(prop)));
		getClient().addTableComponent(cardView, 91);
		cardSelection = new MDSelection(Color.BLUE);
		cardSelection.setLocation(cardView.getLocation());
		cardSelection.setSize(cardView.getSize());
		getClient().addTableComponent(cardSelection, 92);
		updateButton();
		getClient().getTableScreen().repaint();
	}
	
	public void updateButton()
	{
		if (getActionOwner() == getClient().getThePlayer())
		{
			MDButton button = getClient().getTableScreen().getMultiButton();
			button.setText("Confirm");
			if (cardView != null)
			{
				button.setEnabled(true);
				button.setListener(new MouseAdapter()
				{
					@Override
					public void mouseReleased(MouseEvent event)
					{
						getClient().sendPacket(new PacketActionSelectProperties(cardView.getCard().getID()));
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
		for (Player player : getClient().getAllPlayers())
		{
			for (PropertySet set : player.getPropertySets())
			{
				((MDPropertySet) set.getUI()).disableSelection();
			}
		}
		
		if (cardView != null)
		{
			getClient().removeTableComponent(cardView);
			getClient().removeTableComponent(cardSelection);
		}
	}
}
