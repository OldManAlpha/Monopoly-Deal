package oldmana.md.server.state;

import java.util.List;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketStatus;
import oldmana.md.net.packet.server.actionstate.PacketActionStateRent;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.CardRegistry;
import oldmana.md.server.card.collection.PropertySet;

public class ActionStateRent extends ActionState
{
	private int amount;
	
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
	
	public void broadcastStatus()
	{
		if (getNumberOfTargets() > 0)
		{
			String status = null;
			for (ActionTarget target : getActionTargets())
			{
				if (status == null)
				{
					status = getActionOwner().getName() + " charges " + amount + "M against " + target.getTarget().getName();
				}
				else
				{
					status += ", " + target.getTarget().getName();
				}
			}
			getServer().broadcastPacket(new PacketStatus(status));
		}
	}
	
	public void checkBrokePlayers()
	{
		for (ActionTarget target : getActionTargets())
		{
			if (!target.getTarget().hasAnyMonetaryAssets())
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
			rented[i] = targets.get(i).getTarget().getID();
		}
		for (int i = 0 ; i < paid.length ; i++)
		{
			paid[i] = accepted.get(i).getID();
		}
		for (int i = 0 ; i < refused.length ; i++)
		{
			refused[i] = refusedPlayers.get(i).getID();
		}
		return new PacketActionStateRent(getActionOwner().getID(), rented, getRent());
	}
}
