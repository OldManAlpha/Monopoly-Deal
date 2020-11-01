package oldmana.md.client.state;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import oldmana.md.client.Player;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.action.ActionScreenSelectProperty;
import oldmana.md.client.gui.action.ActionScreenSelectProperty.PropertySelectListener;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDCard;
import oldmana.md.client.gui.component.MDPropertySet;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.net.packet.client.action.PacketActionSelectSelfPlayerProperty;

public class ActionStateTargetSelfPlayerProperty extends ActionState
{
	private MDCard cardViewSelf;
	private MDSelection cardSelectionSelf;
	
	private MDCard cardViewOther;
	private MDSelection cardSelectionOther;
	
	public ActionStateTargetSelfPlayerProperty(Player player)
	{
		super(player);
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
											getClient().getTableScreen().remove(screen);
											getClient().getTableScreen().repaint();
										}
										
										@Override
										public void cancel()
										{
											getClient().getTableScreen().remove(screen);
											getClient().getTableScreen().repaint();
										}
									});
									getClient().addTableComponent(screen, 110);
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
			for (PropertySet set : player.getPropertySets())
			{
				if (set.hasBase())
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
										propertySelectedSelf(prop);
										getClient().getTableScreen().remove(screen);
										getClient().getTableScreen().repaint();
									}
									
									@Override
									public void cancel()
									{
										getClient().getTableScreen().remove(screen);
										getClient().getTableScreen().repaint();
									}
								});
								getClient().addTableComponent(screen, 110);
							}
							else
							{
								propertySelectedSelf(set.getPropertyCardAt(0));
							}
						}
					});
				}
			}
		}
	}
	
	public void propertySelectedSelf(CardProperty prop)
	{
		if (cardViewSelf != null)
		{
			getClient().removeTableComponent(cardViewSelf);
			getClient().removeTableComponent(cardSelectionSelf);
		}
		
		cardViewSelf = new MDCard(prop);
		cardViewSelf.setLocation(prop.getOwningCollection().getUI().getLocationOf(prop.getOwningCollection().getIndexOf(prop)));
		getClient().addTableComponent(cardViewSelf, 91);
		cardSelectionSelf = new MDSelection(Color.BLUE);
		cardSelectionSelf.setLocation(cardViewSelf.getLocation());
		cardSelectionSelf.setSize(cardViewSelf.getSize());
		getClient().addTableComponent(cardSelectionSelf, 92);
		updateButton();
		getClient().getTableScreen().repaint();
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
			if (cardViewSelf != null && cardViewOther != null)
			{
				button.setEnabled(true);
				button.setListener(new MouseAdapter()
				{
					@Override
					public void mouseReleased(MouseEvent event)
					{
						getClient().sendPacket(new PacketActionSelectSelfPlayerProperty(cardViewSelf.getCard().getID(), cardViewOther.getCard().getID()));
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
		
		if (cardViewSelf != null)
		{
			getClient().removeTableComponent(cardViewSelf);
			getClient().removeTableComponent(cardSelectionSelf);
		}
		
		if (cardViewOther != null)
		{
			getClient().removeTableComponent(cardViewOther);
			getClient().removeTableComponent(cardSelectionOther);
		}
	}
}
