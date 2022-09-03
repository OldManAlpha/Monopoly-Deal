package oldmana.md.server.card.control;

import oldmana.md.net.packet.server.PacketCardButton;
import oldmana.md.net.packet.server.PacketDestroyCardButton;
import oldmana.md.server.ButtonColorScheme;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.control.condition.AbstractButtonCondition;

public class CardButton
{
	public static final int TOP = 1;
	public static final int CENTER = 2;
	public static final int BOTTOM = 3;
	
	private Card card;
	
	private String text;
	private CardButtonType type = CardButtonType.NORMAL;
	private int pos;
	private ButtonColorScheme color = ButtonColorScheme.NORMAL;
	
	private AbstractButtonCondition condition;
	private ButtonCondition newCondition;
	
	private CardButtonClickListener listener;
	
	private boolean lastEval = false;
	
	public CardButton(String text, int pos)
	{
		this.text = text;
		this.pos = pos;
	}
	
	public CardButton(String text, int pos, CardButtonType type)
	{
		this.text = text;
		this.pos = pos;
		this.type = type;
	}
	
	public CardButton(String text, int pos, ButtonCondition condition)
	{
		this(text, pos);
		this.newCondition = condition;
	}
	
	public CardButton(String text, int pos, ButtonCondition condition, CardButtonClickListener listener)
	{
		this(text, pos, condition);
		this.listener = listener;
	}
	
	protected void setAssociatedCard(Card card)
	{
		this.card = card;
	}
	
	public String getText()
	{
		return text;
	}
	
	public CardButtonType getType()
	{
		return type;
	}
	
	public int getPosition()
	{
		return pos;
	}
	
	public ButtonColorScheme getColor()
	{
		return color;
	}
	
	public void setColor(ButtonColorScheme color)
	{
		this.color = color;
	}
	
	public boolean isEnabled()
	{
		return lastEval;
	}
	
	public void setCondition(AbstractButtonCondition condition)
	{
		this.condition = condition;
	}
	
	public void setCondition(ButtonCondition condition)
	{
		this.newCondition = condition;
	}
	
	public void setListener(CardButtonClickListener listener)
	{
		this.listener = listener;
	}
	
	/**
	 * Evaluates the condition and updates the enabled status.
	 * @return Whether the enabled status has changed
	 */
	public boolean update()
	{
		boolean result = evaluate();
		boolean changed = result != lastEval;
		lastEval = result;
		return changed;
	}
	
	public void reset()
	{
		lastEval = false;
	}
	
	/**
	 * Evaluates the condition using the card's owner.
	 * @return If the condition is found to be true at this moment
	 */
	public boolean evaluate()
	{
		return evaluate(card.getOwner());
	}
	
	/**
	 * Evaluates the condition as if the given player is the owner.
	 * @return If the condition is found to be true at this moment
	 */
	public boolean evaluate(Player player)
	{
		return newCondition.evaluate(player, card);
	}
	
	public void click(Player player, int data)
	{
		listener.onClick(player, card, data);
	}
	
	public void sendPacket()
	{
		card.getOwner().sendPacket(new PacketCardButton(card.getID(), text, pos, type.getID(), color.getID()));
	}
	
	public void sendDestroy()
	{
		card.getOwner().sendPacket(new PacketDestroyCardButton(card.getID(), pos));
	}
	
	public enum CardButtonType
	{
		NORMAL(0), PROPERTY(1), ACTION_COUNTER(2), BUILDING(3);
		
		private final int id;
		
		CardButtonType(int id)
		{
			this.id = id;
		}
		
		public byte getID()
		{
			return (byte) id;
		}
		
		public CardButtonType fromID(int id)
		{
			for (CardButtonType type : values())
			{
				if (type.getID() == id)
				{
					return type;
				}
			}
			return null;
		}
	}
	
	@FunctionalInterface
	public interface CardButtonClickListener
	{
		void onClick(Player player, Card card, int data);
	}
}
