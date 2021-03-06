package oldmana.md.server.state;

import java.util.ArrayList;
import java.util.List;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.actionstate.PacketUpdateActionStateAccepted;
import oldmana.md.net.packet.server.actionstate.PacketUpdateActionStateRefusal;
import oldmana.md.net.packet.server.actionstate.PacketUpdateActionStateTarget;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;

public abstract class ActionState
{
	private Player actionOwner;
	private List<ActionTarget> targets;
	
	private ActionStateListener listener;
	
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
	
	public void setListener(ActionStateListener listener)
	{
		this.listener = listener;
	}
	
	public ActionStateListener getListener()
	{
		return listener;
	}
	
	public void onCardUndo(Card card) {}
	
	public Player getActionOwner()
	{
		return actionOwner;
	}
	
	public boolean isTarget(Player player)
	{
		return getActionTarget(player) != null;
	}
	
	public void removeActionTarget(Player player)
	{
		targets.remove(getActionTarget(player));
		getServer().broadcastPacket(new PacketUpdateActionStateTarget(player.getID(), false));
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
	
	public Player getTargetPlayer()
	{
		return targets.get(0).getPlayer();
	}
	
	public List<ActionTarget> getActionTargets()
	{
		return targets;
	}
	
	public List<Player> getTargetPlayers()
	{
		List<Player> players = new ArrayList<Player>(targets.size());
		for (ActionTarget target : targets)
		{
			players.add(target.getPlayer());
		}
		return players;
	}
	
	public void setRefused(Player player, boolean refused)
	{
		getActionTarget(player).setRefused(refused);
		getServer().broadcastPacket(new PacketUpdateActionStateRefusal(player.getID(), refused));
	}
	
	public boolean isRefused(Player player)
	{
		return getActionTarget(player).isRefused();
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
	
	public void setAccepted(Player player, boolean accepted)
	{
		getActionTarget(player).setAccepted(accepted);
		getServer().broadcastPacket(new PacketUpdateActionStateAccepted(player.getID(), accepted));
	}
	
	public boolean isAccepted(Player player)
	{
		return getActionTarget(player).isAccepted();
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
	
	public int getNumberOfTargets()
	{
		return targets.size();
	}
	
	public boolean isFinished()
	{
		return getNumberOfAccepted() == getNumberOfTargets();
	}
	
	public MDServer getServer()
	{
		return MDServer.getInstance();
	}
	
	public abstract Packet constructPacket();
}
