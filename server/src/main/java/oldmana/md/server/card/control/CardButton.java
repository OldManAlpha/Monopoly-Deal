package oldmana.md.server.card.control;

import oldmana.md.server.card.control.condition.ButtonCondition;

import java.util.List;

public class CardButton
{
	private String text;
	
	private List<ButtonCondition> conditions;
	
	public CardButton(String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void addCondition(ButtonCondition condition)
	{
		conditions.add(condition);
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
