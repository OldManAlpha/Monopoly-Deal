package oldmana.general.md.client.state;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import oldmana.general.md.client.MDClient;
import oldmana.general.md.client.Player;
import oldmana.general.md.client.card.CardActionJustSayNo;
import oldmana.general.md.client.gui.component.MDButton;
import oldmana.general.md.client.gui.component.MDButton.ButtonColorScheme;
import oldmana.general.md.client.gui.component.MDSelection;
import oldmana.general.md.client.gui.component.overlay.MDPlayerAcceptOverlay;
import oldmana.general.md.net.packet.client.action.PacketActionAccept;
import oldmana.general.md.net.packet.client.action.PacketActionPlayCardSpecial;

public class ActionState
{
	private Player actionOwner;
	private List<ActionTarget> targets;
	
	private boolean defaultButton = true;
	
	private CardActionJustSayNo jsn;
	private boolean oneTargetMode = false;
	
	public ActionState(Player actionOwner)
	{
		this.actionOwner = actionOwner;
		targets = new ArrayList<ActionTarget>();
	}
	
	public ActionState(Player actionOwner, Player actionTarget)
	{
		this(actionOwner);
		targets.add(new ActionTarget(actionTarget));
	}
	
	public ActionState(Player actionOwner, Player actionTarget, boolean defaultButton)
	{
		this(actionOwner);
		targets.add(new ActionTarget(actionTarget));
		this.defaultButton = defaultButton;
	}
	
	public ActionState(Player actionOwner, List<Player> actionTargets)
	{
		this(actionOwner);
		for (Player player : actionTargets)
		{
			targets.add(new ActionTarget(player));
		}
	}
	
	public ActionState(Player actionOwner, List<Player> actionTargets, boolean defaultButton)
	{
		this(actionOwner);
		for (Player player : actionTargets)
		{
			targets.add(new ActionTarget(player));
		}
		this.defaultButton = defaultButton;
	}
	
	/**Call superclass method to set up multibutton
	 * 
	 */
	public void setup()
	{
		if (defaultButton && isTarget(getClient().getThePlayer()))
		{
			applyButtonAccept(getActionOwner());
		}
	};
	
	/**Call superclass method to clean up multibutton
	 * 
	 */
	public void cleanup()
	{
		MDButton button = getClient().getTableScreen().getMultiButton();
		button.setColorScheme(ButtonColorScheme.NORMAL);
		button.setText("");
		button.setEnabled(false);
		button.removeListener();
		button.repaint();
	};
	
	public void updateUI() {}
	
	public void onJustSayNo(CardActionJustSayNo card)
	{
		Player player = getClient().getThePlayer();
		if (isTarget(player) && !isAccepted(player) && !isRefused(player))
		{
			getClient().sendPacket(new PacketActionPlayCardSpecial(card.getID(), getActionOwner().getID()));
			disableButton();
			getClient().setAwaitingResponse(true);
		}
		else if (getActionOwner() == player)
		{
			if (getNumberOfRefused() == 1)
			{
				getClient().sendPacket(new PacketActionPlayCardSpecial(card.getID(), getRefused().get(0).getID()));
				getClient().setAwaitingResponse(true);
			}
			else if (getNumberOfRefused() > 1)
			{
				jsn = card;
				applyButtonCancelJustSayNo();
				checkOverlays();
			}
		}
	}
	
	public void onRevokeJustSayNo() // Probably never called
	{
		jsn = null;
		removeButton();
	}
	
	public boolean isUsingJustSayNo()
	{
		return jsn != null;
	}
	
	public void onPlayerAccept(Player player)
	{
		if (getActionOwner() == getClient().getThePlayer()) // UI processing for the action owner
		{
			if (getNumberOfRefused() == 0)
			{
				removeButton();
			}
			else if (getNumberOfRefused() == 1) // Remove overlay and reapply the accept button for the remaining refusal
			{
				for (ActionTarget target : getActionTargets())
				{
					removeOverlay(target.getPlayer());
				}
				applyButtonAccept(getRefused().get(0));
			}
			else if (getNumberOfRefused() > 1)
			{
				removeOverlay(player);
			}
		}
	}
	
	public void onPlayerRefused(Player player)
	{
		if (getActionOwner() == getClient().getThePlayer()) // UI processing for the action owner
		{
			if (getNumberOfRefused() == 1) // Initally apply accept button when it's only one refusal
			{
				applyButtonAccept(player);
			}
			else if (getNumberOfRefused() == 2) // Remove accept button and add overlay to first player instead when it's two refusals
			{
				removeButton();
				for (Player refused : getRefused())
				{
					if (refused != player)
					{
						addOverlay(refused);
						break;
					}
				}
			}
			
			if (getNumberOfRefused() > 1) // Add overlay for any refusals past one
			{
				addOverlay(player);
			}
		}
		else if (player == getClient().getThePlayer()) // UI processing for the refused target
		{
			removeButton();
		}
	}
	
	public void onPlayerUnrefused(Player player)
	{
		if (getActionOwner() == getClient().getThePlayer()) // UI processing for the action owner
		{
			if (getNumberOfRefused() == 0)
			{
				removeButton();
			}
			else if (getNumberOfRefused() == 1) // Remove overlay and reapply the accept button for the remaining refusal
			{
				for (ActionTarget target : getActionTargets())
				{
					removeOverlay(target.getPlayer());
				}
				applyButtonAccept(getRefused().get(0));
			}
			else if (getNumberOfRefused() > 1)
			{
				removeOverlay(player);
			}
		}
		else if (player == getClient().getThePlayer()) // UI processing for the unrefused target
		{
			applyButtonAccept(getActionOwner());
		}
	}
	
	public void onPreTargetRemoved(Player player)
	{
		int refused = getNumberOfRefused() - (isRefused(player) ? 1 : 0);
		if (getActionOwner() == getClient().getThePlayer()) // UI processing for the action owner
		{
			if (refused == 0)
			{
				removeButton();
			}
			else if (refused == 1) // Remove overlay and reapply the accept button for the remaining refusal
			{
				Player remaining = null;
				for (ActionTarget target : getActionTargets())
				{
					removeOverlay(target.getPlayer());
					if (target.getPlayer() != player)
					{
						remaining = target.getPlayer();
					}
				}
				applyButtonAccept(remaining);
			}
			else if (refused > 1)
			{
				removeOverlay(player);
			}
		}
		else if (player == getClient().getThePlayer())
		{
			
		}
	}
	
	public void checkOverlays()
	{
		if (getActionOwner() == getClient().getThePlayer())
		{
			for (Player refused : getRefused())
			{
				ActionTarget target = getActionTarget(refused);
				if ((jsn == null && target.getOverlay() instanceof MDSelection) || (jsn != null && target.getOverlay() instanceof MDPlayerAcceptOverlay))
				{
					removeOverlay(refused);
					addOverlay(refused);
				}
			}
		}
	}
	
	private void addOverlay(Player player)
	{
		if (jsn == null)
		{
			addAcceptOverlay(player);
		}
		else
		{
			addJSNOverlay(player);
		}
	}
	
	private void addAcceptOverlay(Player player)
	{
		MDPlayerAcceptOverlay accept = new MDPlayerAcceptOverlay(player);
		accept.setLocation(player.getUI().getLocation());
		getClient().addTableComponent(accept, 100);
		getActionTarget(player).setOverlay(accept);
		accept.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				getClient().sendPacket(new PacketActionAccept(player.getID()));
				accept.removeMouseListener(this);
				getClient().setAwaitingResponse(true);
			}
		});
	}
	
	private void addJSNOverlay(Player player)
	{
		MDSelection jsnSelect = new MDSelection();
		jsnSelect.setLocation(player.getUI().getLocation());
		jsnSelect.setSize(player.getUI().getSize());
		getClient().addTableComponent(jsnSelect, 100);
		getActionTarget(player).setOverlay(jsnSelect);
		jsnSelect.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				if (!getClient().isAwaitingResponse())
				{
					getClient().sendPacket(new PacketActionPlayCardSpecial(jsn.getID(), player.getID()));
					jsn = null;
					disableButton();
					getClient().setAwaitingResponse(true);
				}
			}
		});
	}
	
	private void removeOverlay(Player player)
	{
		ActionTarget target = getActionTarget(player);
		if (target.getOverlay() != null)
		{
			getClient().removeTableComponent(target.getOverlay());
			target.setOverlay(null);
		}
	}
	
	private void applyButtonAccept(Player player)
	{
		if (defaultButton || getActionOwner() == getClient().getThePlayer())
		{
			MDButton button = getClient().getTableScreen().getMultiButton();
			button.setColorScheme(ButtonColorScheme.ALERT);
			button.setText("Accept");
			button.setEnabled(true);
			button.setListener(new MouseAdapter()
			{
				@Override
				public void mouseReleased(MouseEvent event)
				{
					if (!getClient().isAwaitingResponse())
					{
						getClient().sendPacket(new PacketActionAccept(player.getID()));
						button.removeListener();
						button.setEnabled(false);
						getClient().setAwaitingResponse(true);
					}
				}
			});
			button.repaint();
		}
	}
	
	private void applyButtonCancelJustSayNo()
	{
		if (getActionOwner() == getClient().getThePlayer() && jsn != null)
		{
			MDButton button = getClient().getTableScreen().getMultiButton();
			button.setColorScheme(ButtonColorScheme.ALERT);
			button.setText("Cancel");
			button.setEnabled(true);
			button.setListener(new MouseAdapter()
			{
				@Override
				public void mouseReleased(MouseEvent event)
				{
					jsn = null;
					removeButton();
					checkOverlays();
				}
			});
			button.repaint();
		}
	}
	
	private void removeButton()
	{
		if (defaultButton || getActionOwner() == getClient().getThePlayer())
		{
			MDButton button = getClient().getTableScreen().getMultiButton();
			button.setColorScheme(ButtonColorScheme.NORMAL);
			button.setText("");
			button.setEnabled(false);
			button.removeListener();
			button.repaint();
		}
	}
	
	private void disableButton()
	{
		MDButton button = getClient().getTableScreen().getMultiButton();
		button.setEnabled(false);
	}
	
	public void updateOverlaysAndButton()
	{
		Player thePlayer = getClient().getThePlayer();
		Player owner = getActionOwner();
		List<ActionTarget> targets = new ArrayList<ActionTarget>(getActionTargets());
		if (thePlayer == owner)
		{
			if (getNumberOfRefused() > 1)
			{
				for (ActionTarget target : targets)
				{
					Player player = target.getPlayer();
					if (target.isRefused() && !target.isAccepted())
					{
						if (!target.hasOverlay())
						{
							if (jsn == null)
							{
								addAcceptOverlay(player);
							}
							else
							{
								addJSNOverlay(player);
							}
						}
						else if (jsn != null && target.getOverlay() instanceof MDPlayerAcceptOverlay)
						{
							removeOverlay(player);
							addJSNOverlay(player);
						}
						else if (jsn == null && target.getOverlay() instanceof MDSelection)
						{
							removeOverlay(player);
							addAcceptOverlay(player);
						}
					}
					else
					{
						if (target.hasOverlay())
						{
							removeOverlay(player);
						}
					}
				}
				if (oneTargetMode)
				{
					removeButton();
					oneTargetMode = false;
				}
			}
			else if (getNumberOfRefused() == 1)
			{
				if (!oneTargetMode)
				{
					for (ActionTarget target : targets)
					{
						removeOverlay(target.getPlayer());
					}
					applyButtonAccept(getRefused().get(0));
					oneTargetMode = true;
				}
			}
			else
			{
				if (oneTargetMode)
				{
					removeButton();
					oneTargetMode = false;
				}
			}
			getClient().getTableScreen().repaint();
		}
		else if (isTarget(thePlayer) && !isAccepted(thePlayer))
		{
			MDButton button = getClient().getTableScreen().getMultiButton();
			applyButtonAccept(getActionOwner());
			if (getClient().isInputBlocked() || isRefused(thePlayer))
			{
				button.setEnabled(false);
			}
			else
			{
				button.setEnabled(true);
			}
			/*
			button.setText("Accept");
			button.setColorScheme(ButtonColorScheme.ALERT);
			button.repaint();
			button.setListener(new MouseAdapter()
			{
				@Override
				public void mouseReleased(MouseEvent event)
				{
					if (button.isEnabled())
					{
						getClient().sendPacket(new PacketActionAccept(getActionOwner().getID()));
						getClient().setAwaitingResponse(true);
						button.setEnabled(false);
						button.repaint();
					}
				}
			});
			*/
		}
	}
	
	public void removeState()
	{
		getClient().getGameState().setCurrentActionState(null);
	}
	
	public GameState getGameState()
	{
		return getClient().getGameState();
	}
	
	public Player getActionOwner()
	{
		return actionOwner;
	}
	
	public boolean isTarget(Player player)
	{
		return getActionTarget(player) != null;
	}
	
	public void setTarget(Player player, boolean isTarget)
	{
		if (!isTarget && isTarget(player))
		{
			onPreTargetRemoved(player);
			targets.remove(getActionTarget(player));
		}
		else if (isTarget && !isTarget(player))
		{
			targets.add(new ActionTarget(player));
		}
		getClient().getTableScreen().repaint();
	}
	
	public ActionTarget getActionTarget(Player player)
	{
		for (ActionTarget target : targets)
		{
			if (target.getPlayer() == player)
			{
				return target;
			}
		}
		return null;
	}
	
	public ActionTarget getActionTarget()
	{
		return targets.get(0);
	}
	
	public List<ActionTarget> getActionTargets()
	{
		return targets;
	}
	
	public void setRefused(Player player, boolean refused)
	{
		getActionTarget(player).setRefused(refused);
		if (refused)
		{
			onPlayerRefused(player);
		}
		else
		{
			onPlayerUnrefused(player);
		}
		getClient().getTableScreen().repaint();
	}
	
	public boolean isRefused(Player player)
	{
		return getActionTarget(player).isRefused();
	}
	
	public void setAccepted(Player player, boolean accepted)
	{
		getActionTarget(player).setRefused(false);
		getActionTarget(player).setAccepted(accepted);
		if (accepted)
		{
			onPlayerAccept(player);
		}
		getClient().getTableScreen().repaint();
	}
	
	public boolean isAccepted(Player player)
	{
		return getActionTarget(player).isAccepted();
	}
	
	public List<Player> getRefused()
	{
		List<Player> refused = new ArrayList<Player>();
		for (ActionTarget target : targets)
		{
			if (target.isRefused())
			{
				refused.add(target.getPlayer());
			}
		}
		return refused;
	}
	
	public List<Player> getAccepted()
	{
		List<Player> accepted = new ArrayList<Player>();
		for (ActionTarget target : targets)
		{
			if (target.isAccepted())
			{
				accepted.add(target.getPlayer());
			}
		}
		return accepted;
	}
	
	public int getNumberOfRefused()
	{
		int refused = 0;
		for (ActionTarget target : targets)
		{
			if (target.isRefused())
			{
				refused++;
			}
		}
		return refused;
	}
	
	public int getNumberOfAcceptedRefusals()
	{
		int accepted = 0;
		for (ActionTarget target : targets)
		{
			if (target.isRefusalAccepted())
			{
				accepted++;
			}
		}
		return accepted;
	}
	
	public MDClient getClient()
	{
		return MDClient.getInstance();
	}
}
