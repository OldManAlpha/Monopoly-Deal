package oldmana.md.client.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import oldmana.md.client.MDClient;
import oldmana.md.client.Player;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardButton;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDButton.ButtonColorScheme;
import oldmana.md.client.state.client.ActionStateClientCounterPlayer;
import oldmana.md.net.packet.client.action.PacketActionAccept;
import oldmana.md.net.packet.client.action.PacketActionUseCardButton;

public class ActionState
{
	private Player actionOwner;
	private List<ActionTarget> targets;
	
	private Card jsn;
	
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
			applyButtonAccept();
		}
	}
	
	/**Call superclass method to clean up multibutton
	 * 
	 */
	public void cleanup()
	{
		removeButton();
	};
	
	private void applyButtonAccept()
	{
		MDButton button = getClient().getTableScreen().getMultiButton();
		button.setColorScheme(ButtonColorScheme.ALERT);
		button.setText("Accept" + ((getRefusableTargets().size() > 1 ? " All" : "")));
		button.setEnabled(true);
		button.setListener(() ->
		{
			if (!getClient().isAwaitingResponse())
			{
				for (Player target : getRefusableTargets())
				{
					getClient().sendPacket(new PacketActionAccept(target.getID()));
				}
				button.removeListener();
				button.setEnabled(false);
				getClient().setAwaitingResponse(true);
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
	
	public void onActionCounter(Card card, CardButton button)
	{
		Player player = getClient().getThePlayer();
		if (isTarget(player) && !isAccepted(player) && !isRefused(player))
		{
			getClient().sendPacket(new PacketActionUseCardButton(card.getID(),
					button.getPosition().getID(), getActionOwner().getID()));
			disableButton();
			getClient().setAwaitingResponse(true);
		}
		else if (getActionOwner() == player)
		{
			if (getNumberOfRefused() == 1)
			{
				getClient().sendPacket(new PacketActionUseCardButton(card.getID(),
						button.getPosition().getID(), getRefused().get(0).getID()));
				getClient().setAwaitingResponse(true);
			}
			else if (getNumberOfRefused() > 1)
			{
				jsn = card;
				getGameState().setClientActionState(new ActionStateClientCounterPlayer(jsn, button));
			}
		}
	}
	
	public void removeActionCounter()
	{
		jsn = null;
		evaluateAcceptButton();
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
	
	public void onPreTargetRemoved(Player player) {}
	
	public void onTargetRemoved(Player player)
	{
		evaluateAcceptButton();
	}
	
	private void evaluateAcceptButton()
	{
		if (getActionOwner() == getClient().getThePlayer() && !isUsingActionCounter())
		{
			if (getNumberOfRefused() > 0)
			{
				applyButtonAccept();
			}
			else
			{
				removeButton();
			}
		}
	}
	
	public Card getActionCounterCard()
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
			onTargetRemoved(player);
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
	
	public List<Player> getRefusableTargets()
	{
		if (getActionOwner() == getClient().getThePlayer())
		{
			return getRefused();
		}
		else if (!isRefused(getClient().getThePlayer()))
		{
			return Collections.singletonList(getActionOwner());
		}
		return Collections.emptyList();
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
