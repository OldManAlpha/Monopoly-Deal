package oldmana.md.client.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oldmana.md.client.MDClient;
import oldmana.md.client.Player;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardButton;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.state.client.ActionStateClientCounterPlayer;
import oldmana.md.common.playerui.ButtonColorScheme;
import oldmana.md.common.state.TargetState;
import oldmana.md.net.packet.client.action.PacketActionAccept;
import oldmana.md.net.packet.client.action.PacketActionUseCardButton;

public class ActionState
{
	private Player actionOwner;
	private Map<Player, TargetState> targets;
	
	private Card jsn;
	
	public ActionState(Player actionOwner)
	{
		this.actionOwner = actionOwner;
		targets = new HashMap<Player, TargetState>();
	}
	
	public ActionState(Player actionOwner, Player actionTarget)
	{
		this(actionOwner);
		targets.put(actionTarget, TargetState.TARGETED);
	}
	
	public ActionState(Player actionOwner, List<Player> actionTargets)
	{
		this(actionOwner);
		for (Player player : actionTargets)
		{
			targets.put(player, TargetState.TARGETED);
		}
	}
	
	/**Call superclass method to setup multibutton
	 * 
	 */
	public void setup()
	{
		if (isTarget(getClient().getThePlayer()) && targets.size() == 1)
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
	
	public boolean isTurnState()
	{
		return false;
	}
	
	private void applyButtonAccept()
	{
		MDButton button = getClient().getTableScreen().getMultiButton();
		button.setColor(ButtonColorScheme.ALERT);
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
		button.setColor(ButtonColorScheme.NORMAL);
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
			getClient().sendPacket(new PacketActionUseCardButton(card.getID(), button.getID(), getActionOwner().getID()));
			disableButton();
			getClient().setAwaitingResponse(true);
		}
		else if (getActionOwner() == player)
		{
			if (getNumberOfRefused() == 1)
			{
				getClient().sendPacket(new PacketActionUseCardButton(card.getID(), button.getID(), getRefused().get(0).getID()));
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
		return targets.containsKey(player);
	}
	
	public void setTargetState(Player player, TargetState state)
	{
		if (state == TargetState.NOT_TARGETED)
		{
			if (targets.containsKey(player))
			{
				onPreTargetRemoved(player);
				targets.remove(player);
				onTargetRemoved(player);
				getClient().getTableScreen().repaint();
			}
			return;
		}
		targets.put(player, state);
		evaluateAcceptButton();
		getClient().getWindow().setAlert(true);
		if (player == getClient().getThePlayer() && state == TargetState.TARGETED)
		{
			getClient().getTableScreen().getTopbar().triggerAlert();
		}
		getClient().getTableScreen().repaint();
	}
	
	public Player getTarget()
	{
		return targets.keySet().iterator().next();
	}
	
	public Map<Player, TargetState> getTargets()
	{
		return targets;
	}
	
	public boolean isRefused(Player player)
	{
		return targets.get(player) == TargetState.REFUSED;
	}
	
	public boolean isAccepted(Player player)
	{
		return targets.get(player) == TargetState.ACCEPTED;
	}
	
	public List<Player> getRefused()
	{
		List<Player> refused = new ArrayList<Player>();
		targets.forEach((player, state) ->
		{
			if (state == TargetState.REFUSED)
			{
				refused.add(player);
			}
		});
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
		targets.forEach((player, state) ->
		{
			if (state == TargetState.ACCEPTED)
			{
				accepted.add(player);
			}
		});
		return accepted;
	}
	
	public int getNumberOfRefused()
	{
		int refused = 0;
		for (TargetState state : targets.values())
		{
			if (state == TargetState.REFUSED)
			{
				refused++;
			}
		}
		return refused;
	}
	
	public int getNumberOfAccepted()
	{
		int accepted = 0;
		for (TargetState state : targets.values())
		{
			if (state == TargetState.ACCEPTED)
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
