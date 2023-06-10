package oldmana.md.server.state;

import oldmana.md.common.net.packet.server.PacketTurnOrder;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.state.primary.ActionStatePlayerTurn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TurnOrder
{
	private Player active;
	private List<Player> order = new ArrayList<Player>();
	
	private Random random = new Random();
	
	public void addPlayer(Player player)
	{
		order.add(player);
		broadcastOrder();
	}
	
	public void addPlayerRandom(Player player)
	{
		order.add(order.isEmpty() ? 0 : random.nextInt(order.size()), player);
		broadcastOrder();
	}
	
	public void removePlayer(Player player)
	{
		order.remove(player);
		broadcastOrder();
	}
	
	public List<Player> getOrder()
	{
		return new ArrayList<Player>(order);
	}
	
	public void shuffle()
	{
		Collections.shuffle(order);
		broadcastOrder();
	}
	
	public Player getActivePlayer()
	{
		return active;
	}
	
	protected ActionStatePlayerTurn nextTurn()
	{
		if (active != null)
		{
			active = order.get((order.indexOf(active) + 1) % order.size());
		}
		else
		{
			active = order.get(random.nextInt(order.size()));
		}
		return new ActionStatePlayerTurn(active);
	}
	
	protected ActionStatePlayerTurn setTurn(Player player)
	{
		active = player;
		return new ActionStatePlayerTurn(active);
	}
	
	public void broadcastOrder()
	{
		MDServer.getInstance().broadcastPacket(new PacketTurnOrder(order.stream().mapToInt(player -> player.getID()).toArray()));
	}
	
	public void sendOrder(Player player)
	{
		player.sendPacket(new PacketTurnOrder(order.stream().mapToInt(p -> p.getID()).toArray()));
	}
}
