package oldmana.md.server.card.control;

import oldmana.md.net.packet.server.PacketDestroyCardButton;
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
	
	public CardButton getEnabledButton(int slot)
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
	 * This will return a List with 3 elements. If there is no button enabled corresponding to the index, there will
	 * be a null element.
	 * @return The List of buttons
	 */
	public List<CardButton> getEnabledButtons()
	{
		List<CardButton> enabled = new ArrayList<CardButton>();
		for (int i = 0 ; i < 3 ; i++)
		{
			enabled.add(null);
		}
		for (CardButton button : buttons)
		{
			if (button.isEnabled())
			{
				enabled.set(button.getPosition() - 1, button);
			}
		}
		return enabled;
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
			CardButton button = getEnabledButton(slot);
			if (button != null)
			{
				getEnabledButton(slot).sendPacket();
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
		List<CardButton> buttons = getEnabledButtons();
		for (int i = 0 ; i < buttons.size() ; i++)
		{
			CardButton button = buttons.get(i);
			if (button != null)
			{
				button.sendPacket();
			}
			else
			{
				card.getOwner().sendPacket(new PacketDestroyCardButton(card.getID(), i + 1));
			}
		}
	}
}
