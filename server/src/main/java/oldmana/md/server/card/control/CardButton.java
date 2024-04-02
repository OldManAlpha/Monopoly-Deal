package oldmana.md.server.card.control;

import oldmana.md.common.playerui.CardButtonBounds;
import oldmana.md.common.playerui.CardButtonType;
import oldmana.md.common.playerui.ButtonColorScheme;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;

public class CardButton
{
	private Card card;
	
	private int id;
	
	private String text;
	private CardButtonType type = CardButtonType.NORMAL;
	private CardButtonBounds bounds;
	private ButtonColorScheme color = ButtonColorScheme.NORMAL;
	
	private CardButtonCondition condition;
	
	private CardButtonClickListener listener;
	
	private boolean lastEval = false;
	
	public CardButton(String text, CardButtonBounds bounds)
	{
		this.text = text;
		this.bounds = bounds;
	}
	
	public CardButton(String text, CardButtonBounds bounds, CardButtonType type)
	{
		this.text = text;
		this.bounds = bounds;
		this.type = type;
	}
	
	public CardButton(String text, CardButtonBounds bounds, CardButtonCondition condition)
	{
		this(text, bounds);
		this.condition = condition;
	}
	
	public CardButton(String text, CardButtonBounds bounds, CardButtonCondition condition, CardButtonClickListener listener)
	{
		this(text, bounds, condition);
		this.listener = listener;
	}
	
	protected void setAssociatedCard(Card card)
	{
		this.card = card;
	}
	
	public int getID()
	{
		return id;
	}
	
	protected void setID(int id)
	{
		this.id = id;
	}
	
	public String getText()
	{
		return text;
	}
	
	public CardButtonType getType()
	{
		return type;
	}
	
	public void setType(CardButtonType type)
	{
		this.type = type;
	}
	
	public CardButtonBounds getBounds()
	{
		return bounds;
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
	
	public void setCondition(CardButtonCondition condition)
	{
		this.condition = condition;
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
		return card.getOwner() != null && evaluate(card.getOwner());
	}
	
	/**
	 * Evaluates the condition as if the given player is the owner.
	 * @return If the condition is found to be true at this moment
	 */
	public boolean evaluate(Player player)
	{
		return condition.evaluate(player, card);
	}
	
	public void click(Player player, int data)
	{
		listener.onClick(player, card, data);
	}
	
	@FunctionalInterface
	public interface CardButtonClickListener
	{
		void onClick(Player player, Card card, int data);
	}
}
