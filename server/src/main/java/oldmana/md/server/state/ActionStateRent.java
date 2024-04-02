package oldmana.md.server.state;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.net.packet.server.actionstate.PacketActionStateRent;
import oldmana.md.common.state.TargetState;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.collection.Bank;
import oldmana.md.server.event.state.RentPaymentEvent;

public class ActionStateRent extends ActionState
{
	private Map<Player, Integer> charges = new LinkedHashMap<Player, Integer>();
	
	private String renterName = "Bank"; // Only used if renter is null
	
	public ActionStateRent(Player renter, Player rented, int amount)
	{
		super(renter, rented);
		charges.put(rented, amount);
		checkBrokePlayers();
		createStatus();
	}
	
	public ActionStateRent(Player renter, List<Player> rented, int amount)
	{
		super(renter, rented);
		rented.forEach((player) -> charges.put(player, amount));
		checkBrokePlayers();
		createStatus();
	}
	
	public ActionStateRent(Player renter, Map<Player, Integer> rented)
	{
		super(renter, new ArrayList<Player>(rented.keySet()));
		charges = rented;
		checkBrokePlayers();
		createStatus();
	}
	
	public ActionStateRent(String renterName, Player rented, int amount)
	{
		super(null, rented);
		this.renterName = renterName;
		charges.put(rented, amount);
		checkBrokePlayers();
		createStatus();
	}
	
	public ActionStateRent(String renterName, List<Player> rented, int amount)
	{
		super(null, rented);
		this.renterName = renterName;
		rented.forEach((player) -> charges.put(player, amount));
		checkBrokePlayers();
		createStatus();
	}
	
	public ActionStateRent(String renterName, Map<Player, Integer> rented)
	{
		super(null, new ArrayList<Player>(rented.keySet()));
		this.renterName = renterName;
		charges = rented;
		checkBrokePlayers();
		createStatus();
	}
	
	public void createStatus()
	{
		if (getNumberOfTargets() > 0)
		{
			Map<Integer, List<Player>> sortedCharges = new TreeMap<Integer, List<Player>>();
			charges.forEach((player, charge) ->
			{
				if (!sortedCharges.containsKey(charge))
				{
					sortedCharges.put(charge, new ArrayList<Player>());
				}
				sortedCharges.get(charge).add(player);
			});
			
			String status = null;
			if (sortedCharges.size() == 1)
			{
				status = (getActionOwner() != null ? getActionOwner().getName() : renterName) + " charges " +
						charges.get(getTargetPlayer()) + "M against " +
						oxfordify(getTargetPlayers().stream().map(p -> p.getName()).collect(Collectors.toList()));
			}
			else if (sortedCharges.size() > 1)
			{
				status = (getActionOwner() != null ? getActionOwner().getName() : renterName) + " charges: ";
				List<Entry<Integer, List<Player>>> entries = new ArrayList<Entry<Integer, List<Player>>>(sortedCharges.entrySet());
				for (int i = 0 ; i < entries.size() ; i++)
				{
					int amount = entries.get(i).getKey();
					List<Player> players = entries.get(i).getValue();
					
					status += amount + "M -> ";
					for (int e = 0 ; e < players.size() ; e++)
					{
						status += players.get(e).getName();
						if (e != players.size() - 1)
						{
							status += ", ";
						}
					}
					if (i != entries.size() - 1)
					{
						status += "; ";
					}
				}
			}
			setStatus(status);
		}
	}
	
	private String oxfordify(List<String> elements)
	{
		String str = "";
		for (int i = 0 ; i < elements.size() ; i++)
		{
			str += elements.get(i);
			if (i + 2 == elements.size())
			{
				str += (elements.size() > 2 ? "," : "") + " and ";
			}
			else if (i + 1 < elements.size())
			{
				str += ", ";
			}
		}
		return str;
	}
	
	public void checkBrokePlayers()
	{
		for (Player target : getTargetPlayers())
		{
			if (!target.hasAnyMonetaryAssets())
			{
				setAccepted(target, true);
			}
		}
	}
	
	@Override
	public void setTargetState(Player player, TargetState state)
	{
		if (state == TargetState.NOT_TARGETED)
		{
			charges.remove(player);
		}
		super.setTargetState(player, state);
	}
	
	public int getPlayerRent(Player player)
	{
		return charges.get(player);
	}
	
	public void setPlayerRent(Player player, int charge)
	{
		charges.put(player, charge);
	}
	
	public void playerPaid(Player player, List<Card> cards)
	{
		Player renter = getActionOwner();
		
		RentPaymentEvent event = new RentPaymentEvent(renter, player, cards, getPlayerRent(player));
		getServer().getEventManager().callEvent(event);
		if (event.isCancelled())
		{
			player.resendActionState();
			return;
		}
		cards = event.getPayment();
		
		if (renter != null)
		{
			for (Card card : cards)
			{
				if (card instanceof CardProperty && !(card.getOwningCollection() instanceof Bank))
				{
					CardProperty property = (CardProperty) card;
					renter.safelyGrantProperty(property);
				}
				else
				{
					card.transfer(renter.getBank());
				}
			}
		}
		else
		{
			for (Card card : cards)
			{
				card.transfer(getServer().getDiscardPile());
			}
		}
		setAccepted(player, true);
	}
	
	@Override
	public Packet constructPacket()
	{
		List<Player> targets = new ArrayList<Player>(charges.keySet());
		List<Integer> rents = new ArrayList<Integer>(charges.values());
		int[] rented = new int[targets.size()];
		int[] amounts = new int[targets.size()];
		for (int i = 0 ; i < rented.length ; i++)
		{
			rented[i] = targets.get(i).getID();
		}
		for (int i = 0 ; i < amounts.length ; i++)
		{
			amounts[i] = rents.get(i);
		}
		return new PacketActionStateRent(getActionOwner() != null ? getActionOwner().getID() : -1, rented, amounts);
	}
}
