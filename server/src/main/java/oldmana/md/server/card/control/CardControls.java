package oldmana.md.server.card.control;

import oldmana.md.server.card.Card;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CardControls
{
	private Card card;
	
	private List<CardButton> buttons = new ArrayList<CardButton>();
	
	public CardControls(Card card)
	{
		this.card = card;
	}
	
	public CardControls(Card card, CardButton... buttons)
	{
		this.card = card;
		for (CardButton button : buttons)
		{
			addButton(button);
		}
	}
	
	public Card getCard()
	{
		return card;
	}
	
	public List<CardButton> getButtons()
	{
		return buttons;
	}
	
	public void addButton(CardButton button)
	{
		button.setAssociatedCard(card);
		buttons.add(button);
	}
	
	public CardButton getButton(int slot)
	{
		for (CardButton button : buttons)
		{
			if (button.getPosition() == slot && button.isEnabled())
			{
				return button;
			}
		}
		return null;
	}
	
	/**
	 * Reevaluate all the buttons on the card and send packets to the owner
	 */
	public void updateButtons()
	{
		Set<Integer> updatedSlots = new HashSet<Integer>();
		for (CardButton button : buttons)
		{
			if (button.update())
			{
				if (button.isEnabled())
				{
					updatedSlots.add(button.getPosition());
				}
				else
				{
					button.sendDestroy();
				}
			}
		}
		for (int slot : updatedSlots)
		{
			CardButton button = getButton(slot);
			if (button != null)
			{
				getButton(slot).sendPacket();
			}
		}
	}
	
	/**
	 * Resets the current evaluations on the buttons
	 */
	public void resetButtons()
	{
		for (CardButton button : buttons)
		{
			button.reset();
		}
	}
	
	public void resendButtons()
	{
		for (CardButton button : buttons)
		{
			if (button.isEnabled())
			{
				button.sendPacket();
			}
			else
			{
				button.sendDestroy();
			}
		}
	}
}
