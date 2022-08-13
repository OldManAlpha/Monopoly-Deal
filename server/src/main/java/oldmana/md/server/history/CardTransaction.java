package oldmana.md.server.history;

import java.util.ArrayList;
import java.util.List;

public class CardTransaction
{
	private List<CardTransfer> transfers;
	
	public CardTransaction()
	{
		transfers = new ArrayList<CardTransfer>();
	}
	
	public CardTransaction(List<CardTransfer> transfers)
	{
		this.transfers = transfers;
	}
	
	public void addCardTransfer(CardTransfer transfer)
	{
		transfers.add(transfer);
	}
	
	public void removeCardTransfer(CardTransfer transfer)
	{
		transfers.remove(transfer);
	}
	
	public List<CardTransfer> getCardTransfers()
	{
		return transfers;
	}
	
	public void undoTransaction()
	{
		for (CardTransfer transfer : transfers)
		{
			
		}
	}
}
