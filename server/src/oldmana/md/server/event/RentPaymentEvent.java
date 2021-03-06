package oldmana.md.server.event;

import java.util.ArrayList;
import java.util.List;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;

public class RentPaymentEvent extends Event
{
	private Player renter;
	private Player target;
	
	private List<Card> originalPayment;
	private List<Card> payment;
	
	public RentPaymentEvent(Player renter, Player target, List<Card> payment)
	{
		this.renter = renter;
		this.target = target;
		
		this.originalPayment = new ArrayList<Card>(payment);
		this.payment = new ArrayList<Card>(payment);
	}
	
	public Player getRenter()
	{
		return renter;
	}
	
	public Player getTarget()
	{
		return target;
	}
	
	public List<Card> getOriginalPayment()
	{
		return new ArrayList<Card>(originalPayment);
	}
	
	public List<Card> getPayment()
	{
		return new ArrayList<Card>(payment);
	}
	
	public void setPayment(List<Card> payment)
	{
		this.payment = new ArrayList<Card>(payment);
	}
}
