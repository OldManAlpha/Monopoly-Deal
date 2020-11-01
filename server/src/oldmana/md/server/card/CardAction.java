package oldmana.md.server.card;

import oldmana.md.server.Player;

public class CardAction extends Card
{
	public CardAction(int value, String name)
	{
		super(value, name);
	}
	
	/*
	public CardAction(int value, String name, boolean playable)
	{
		super(value, name);
		
		this.playable = playable;
	}
	*/
	
	public void playCard(Player player) {}
	
	public boolean canPlayCard(Player player)
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " (" + getValue() + "M)";
	}
}
