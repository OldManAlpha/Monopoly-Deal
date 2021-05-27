package oldmana.md.server.state;

import java.util.List;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.actionstate.PacketActionStateRent;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.event.RentPaymentEvent;

public class ActionStateRent extends ActionState
{
	private int amount;
	
	private String renterName = "Bank"; // Only used if renter is null
	
	public ActionStateRent(Player renter, Player rented, int amount)
	{
		super(renter, rented);
		this.amount = amount;
		checkBrokePlayers();
		broadcastStatus();
	}
	
	public ActionStateRent(Player renter, List<Player> rented, int amount)
	{
		super(renter, rented);
		this.amount = amount;
		checkBrokePlayers();
		broadcastStatus();
	}
	
	public ActionStateRent(String renterName, Player rented, int amount)
	{
		super(null, rented);
		this.renterName = renterName;
		this.amount = amount;
		checkBrokePlayers();
		broadcastStatus();
	}
	
	public ActionStateRent(String renterName, List<Player> rented, int amount)
	{
		super(null, rented);
		this.renterName = renterName;
		this.amount = amount;
		checkBrokePlayers();
		broadcastStatus();
	}
	
	public void broadcastStatus()
	{
		if (getNumberOfTargets() > 0)
		{
			String status = null;
			for (ActionTarget target : getActionTargets())
			{
				if (status == null)
				{
					status = (getActionOwner() != null ? getActionOwner().getName() : renterName) + " charges " + amount + "M against " + 
							target.getPlayer().getName();
				}
				else
				{
					status += ", " + target.getPlayer().getName();
				}
			}
			getServer().getGameState().setStatus(status);
		}
	}
	
	public void checkBrokePlayers()
	{
		for (ActionTarget target : getActionTargets())
		{
			if (!target.getPlayer().hasAnyMonetaryAssets())
			{
				target.setAccepted(true);
			}
		}
	}
	
	public int getRent()
	{
		return amount;
	}
	
	public void playerPaid(Player player, List<Card> cards)
	{
		Player renter = getActionOwner();
		
		if (renter != null)
		{
			RentPaymentEvent event = new RentPaymentEvent(getActionOwner(), player, cards);
			getServer().getEventManager().callEvent(event);
			cards = event.getPayment();
			
			for (Card card : cards)
			{
				if (card instanceof CardProperty)
				{
					CardProperty property = (CardProperty) card;
					if (property.isSingleColor() && renter.hasSolidPropertySet(property.getColor()))
					{
						PropertySet set = renter.getSolidPropertySet(property.getColor());
						card.transfer(set);
						set.checkMaxProperties();
					}
					else
					{
						PropertySet set = renter.createPropertySet();
						card.getOwningCollection().transferCard(card, set);
					}
				}
				else
				{
					player.getBank().transferCard(card, renter.getBank());
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
		if (isFinished())
		{
			getServer().getGameState().nextNaturalActionState();
		}
	}
	
	@Override
	public Packet constructPacket()
	{
		List<ActionTarget> targets = getActionTargets();
		List<Player> refusedPlayers = getRefused();
		List<Player> accepted = getAccepted();
		int[] rented = new int[targets.size()];
		int[] paid = new int[accepted.size()];
		int[] refused = new int[refusedPlayers.size()];
		for (int i = 0 ; i < rented.length ; i ++)
		{
			rented[i] = targets.get(i).getPlayer().getID();
		}
		for (int i = 0 ; i < paid.length ; i++)
		{
			paid[i] = accepted.get(i).getID();
		}
		for (int i = 0 ; i < refused.length ; i++)
		{
			refused[i] = refusedPlayers.get(i).getID();
		}
		return new PacketActionStateRent(getActionOwner() != null ? getActionOwner().getID() : -1, rented, getRent());
	}
}
