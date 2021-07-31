package oldmana.md.client.state;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.MDClient;
import oldmana.md.client.Player;
import oldmana.md.client.card.CardActionActionCounter;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.client.gui.component.MDButton.ButtonColorScheme;
import oldmana.md.client.gui.component.collection.MDHand;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.net.packet.client.action.PacketActionAccept;
import oldmana.md.net.packet.client.action.PacketActionPlayCardSpecial;

public class ActionState
{
	private Player actionOwner;
	private List<ActionTarget> targets;
	
	private CardActionActionCounter jsn;
	
	private MDSelection counterSelect;
	private MDButton counterCancel;
	
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
	
	public ActionState(Player actionOwner, List<Player> actionTargets)
	{
		this(actionOwner);
		for (Player player : actionTargets)
		{
			targets.add(new ActionTarget(player));
		}
	}
	
	/**Call superclass method to setup multibutton
	 * 
	 */
	public void setup()
	{
		if (isTarget(getClient().getThePlayer()) && getActionTargets().size() == 1)
		{
			applyButtonAccept(getActionOwner());
		}
	}
	
	/**Call superclass method to clean up multibutton
	 * 
	 */
	public void cleanup()
	{
		removeButton();
	};
	
	private void applyButtonAccept(Player player)
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
	
	private void removeButton()
	{
		MDButton button = getClient().getTableScreen().getMultiButton();
		button.setColorScheme(ButtonColorScheme.NORMAL);
		button.setText("");
		button.setEnabled(false);
		button.removeListener();
		button.repaint();
	}
	
	public void updateUI() {}
	
	public void onActionCounter(CardActionActionCounter card)
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
				setupActionCounterOverlay();
				for (Player p : getClient().getOtherPlayers())
				{
					p.getUI().getButtons()[0].updateRefusableText();
				}
			}
		}
	}
	
	public void setupActionCounterOverlay()
	{
		Point propLoc = ((MDHand) getClient().getThePlayer().getHand().getUI()).getScreenLocationOf(jsn);
		counterSelect = new MDSelection(Color.BLUE);
		counterSelect.setLocation(propLoc);
		counterSelect.setSize(GraphicsUtils.getCardWidth(2), GraphicsUtils.getCardHeight(2));
		getClient().addTableComponent(counterSelect, 90);
		
		counterCancel = new MDButton("Cancel");
		counterCancel.setSize((int) (counterSelect.getWidth() * 0.8), (int) (counterSelect.getHeight() * 0.2));
		counterCancel.setLocation((int) (counterSelect.getWidth() * 0.1) + counterSelect.getX(), (int) (counterSelect.getHeight() * 0.4) + counterSelect.getY());
		counterCancel.setListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				removeActionCounter();
			}
		});
		getClient().addTableComponent(counterCancel, 91);
	}
	
	public void cleanupActionCounterOverlay()
	{
		getClient().removeTableComponent(counterSelect);
		getClient().removeTableComponent(counterCancel);
		getClient().getTableScreen().getHand().repaint();
	}
	
	public void removeActionCounter()
	{
		jsn = null;
		cleanupActionCounterOverlay();
		for (Player p : getClient().getOtherPlayers())
		{
			p.getUI().getButtons()[0].updateRefusableText();
		}
	}
	
	public boolean isUsingActionCounter()
	{
		return jsn != null;
	}
	
	public void onPlayerAccept(Player player)
	{
		evaluateAcceptButton();
	}
	
	public void onPlayerRefused(Player player)
	{
		evaluateAcceptButton();
	}
	
	public void onPlayerUnrefused(Player player)
	{
		evaluateAcceptButton();
	}
	
	public void onPreTargetRemoved(Player player)
	{
		evaluateAcceptButton();
	}
	
	private void evaluateAcceptButton()
	{
		if (getActionOwner() == getClient().getThePlayer())
		{
			if (getNumberOfRefused() == 1) // Apply the accept button for the one refusal
			{
				applyButtonAccept(getRefused().get(0));
			}
			else
			{
				removeButton();
			}
		}
	}
	
	public CardActionActionCounter getActionCounterCard()
	{
		return jsn;
	}
	
	private void disableButton()
	{
		MDButton button = getClient().getTableScreen().getMultiButton();
		button.setEnabled(false);
	}
	
	public void removeState()
	{
		getClient().getGameState().setActionState(null);
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
	
	public int getNumberOfAccepted()
	{
		int accepted = 0;
		for (ActionTarget target : targets)
		{
			if (target.isAccepted())
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
