package oldmana.md.server.card.control;

public class CardButton
{
	private String text;
	
	public CardButton(String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}
	
	public static enum CardButtonType
	{
		NORMAL(0), PROPERTY(1), ACTION_COUNTER(2), BUILDING(3);
		
		int id;
		
		CardButtonType(int id)
		{
			this.id = id;
		}
		
		public byte getID()
		{
			return (byte) id;
		}
	}
}
