package oldmana.md.server.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.common.state.TargetState;
import oldmana.md.net.packet.server.actionstate.PacketUpdateActionStateTarget;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.history.UndoableAction;

public abstract class ActionState
{
	private Player actionOwner;
	private Map<Player, TargetState> targets;
	
	private String status = "";
	
	public ActionState(Player actionOwner)
	{
		this.actionOwner = actionOwner;
		targets = new HashMap<Player, TargetState>();
	}
	
	public ActionState(Player actionOwner, String status)
	{
		this(actionOwner);
		this.status = status;
	}
	
	public ActionState(Player actionOwner, Player actionTarget)
	{
		this(actionOwner);
		targets.put(actionTarget, TargetState.TARGETED);
	}
	
	public ActionState(Player actionOwner, Player actionTarget, String status)
	{
		this(actionOwner, actionTarget);
		this.status = status;
	}
	
	public ActionState(Player actionOwner, List<Player> actionTargets)
	{
		this(actionOwner);
		for (Player player : actionTargets)
		{
			targets.put(player, TargetState.TARGETED);
		}
	}
	
	public ActionState(Player actionOwner, List<Player> actionTargets, String status)
	{
		this(actionOwner, actionTargets);
		this.status = status;
	}
	
	/**
	 * Removes this action state from the game state with the new state provided.
	 * @param newState The new action state to take this one's place
	 */
	public void replaceState(ActionState newState)
	{
		getGameState().swapActionState(this, newState);
	}
	
	/**
	 * Removes this action state from the game state.
	 */
	public void removeState()
	{
		getGameState().removeActionState(this);
	}
	
	/**
	 * Get the status text of this action state.
	 * @return The state's status text
	 */
	public String getStatus()
	{
		return status;
	}
	
	/**
	 * Set the status text of this action state. Does nothing if getStatus is overridden.
	 * @param status The new status text
	 */
	public void setStatus(String status)
	{
		status = status != null ? status : "";
		if (!status.equals(this.status))
		{
			this.status = status;
			if (getGameState().getActionState() == this)
			{
				getGameState().broadcastStatus();
			}
		}
	}
	
	public void onUndo(UndoableAction action) {}
	
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
		if (getGameState().getActionState() == this)
		{
			getServer().broadcastPacket(new PacketUpdateActionStateTarget(player.getID(), state));
		}
		getGameState().setStateChanged();
		if (state == TargetState.NOT_TARGETED)
		{
			targets.remove(player);
		}
		else
		{
			targets.put(player, state);
		}
		checkFinished();
	}
	
	public void removeActionTarget(Player player)
	{
		setTargetState(player, TargetState.NOT_TARGETED);
	}
	
	/**
	 * Returns the target player. If there's multiple targets, then this will return one of them. If there's no target,
	 * null is returned.
	 * @return The target player
	 */
	public Player getTargetPlayer()
	{
		return targets.isEmpty() ? null : targets.keySet().iterator().next();
	}
	
	public Map<Player, TargetState> getTargets()
	{
		return targets;
	}
	
	public List<Player> getTargetPlayers()
	{
		return new ArrayList<Player>(targets.keySet());
	}
	
	public List<Player> getTargetPlayers(TargetState ofState)
	{
		List<Player> players = new ArrayList<Player>();
		targets.forEach((player, state) ->
		{
			if (state == ofState)
			{
				players.add(player);
			}
		});
		return players;
	}
	
	public void setRefused(Player player, boolean refused)
	{
		setTargetState(player, refused ? TargetState.REFUSED : TargetState.TARGETED);
	}
	
	public boolean isRefused(Player player)
	{
		return targets.get(player) == TargetState.REFUSED;
	}
	
	public List<Player> getRefused()
	{
		return getTargetPlayers(TargetState.REFUSED);
	}
	
	public boolean canRefuse(Player player, Player target)
	{
		return (isTarget(player) && getActionOwner() == target && !isRefused(player)) ||
				(getActionOwner() == player && isTarget(target) && isRefused(target));
	}
	
	public boolean canRefuseAny(Player player)
	{
		return (isTarget(player) && !isRefused(player)) || (getActionOwner() == player && getNumberOfRefused() > 0);
	}
	
	public void refuse(Player player, Player target)
	{
		if (isTarget(player) && getActionOwner() == target && !isRefused(player))
		{
			setRefused(player, true);
		}
		else if (getActionOwner() == player && isTarget(target) && isRefused(target))
		{
			setRefused(target, false);
		}
	}
	
	public void setAccepted(Player player, boolean accepted)
	{
		setTargetState(player, accepted ? TargetState.ACCEPTED : TargetState.TARGETED);
	}
	
	public boolean isAccepted(Player player)
	{
		return targets.get(player) == TargetState.ACCEPTED;
	}
	
	public List<Player> getAccepted()
	{
		return getTargetPlayers(TargetState.ACCEPTED);
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
	
	public int getNumberOfTargets()
	{
		return targets.size();
	}
	
	private void checkFinished()
	{
		if (isFinished())
		{
			removeState();
		}
	}
	
	/**
	 * Check if this state is naturally finished and should be removed from the action state queue.
	 * <br><br>
	 * By default, the state is finished if all targets have accepted.
	 * @return True if this action state is finished
	 */
	public boolean isFinished()
	{
		return getNumberOfAccepted() == getNumberOfTargets();
	}
	
	/**
	 * If a state is not important, it will be removed when any other state is added to the state queue.
	 * @return True if the state is blocking
	 */
	public boolean isImportant()
	{
		return true;
	}
	
	protected MDServer getServer()
	{
		return MDServer.getInstance();
	}
	
	protected GameState getGameState()
	{
		return getServer().getGameState();
	}
	
	public void sendState()
	{
		getServer().broadcastPacket(constructPacket());
		getTargets().forEach((player, ts) ->
		{
			if (ts != TargetState.TARGETED)
			{
				getServer().broadcastPacket(new PacketUpdateActionStateTarget(player.getID(), ts));
			}
		});
	}
	
	public void sendState(Player sendTo)
	{
		sendTo.sendPacket(constructPacket());
		getTargets().forEach((player, ts) ->
		{
			if (ts != TargetState.TARGETED)
			{
				sendTo.sendPacket(new PacketUpdateActionStateTarget(player.getID(), ts));
			}
		});
	}
	
	public abstract Packet constructPacket();
}
