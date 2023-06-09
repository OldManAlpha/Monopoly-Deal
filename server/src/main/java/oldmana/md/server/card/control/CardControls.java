package oldmana.md.server.card.control;

import oldmana.md.common.playerui.CardButtonBounds;
import oldmana.md.net.packet.server.PacketCardButtons;
import oldmana.md.server.card.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardControls
{
	private Card card;
	
	private Map<Integer, CardButton> buttons = new HashMap<Integer, CardButton>();
	private List<CardButton> enabled = new ArrayList<CardButton>();
	private int nextID;
	
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
	
	public Map<Integer, CardButton> getButtons()
	{
		return buttons;
	}
	
	public CardButton getButton(int id)
	{
		return buttons.get(id);
	}
	
	public CardButton getButtonByText(String text)
	{
		for (CardButton button : buttons.values())
		{
			if (button.getText().equals(text))
			{
				return button;
			}
		}
		return null;
	}
	
	public void addButton(CardButton button)
	{
		button.setAssociatedCard(card);
		button.setID(nextID++);
		buttons.put(button.getID(), button);
	}
	
	public void addButtons(CardButton... buttons)
	{
		for (CardButton button : buttons)
		{
			addButton(button);
		}
	}
	
	public void removeButton(CardButton button)
	{
		buttons.remove(button.getID());
		compressIDs();
		forceUpdateButtons();
	}
	
	public void replaceButtonByText(String text, CardButton newButton)
	{
		removeButton(getButtonByText(text));
		addButton(newButton);
	}
	
	private void compressIDs()
	{
		List<CardButton> buttonList = new ArrayList<CardButton>(buttons.values());
		buttons.clear();
		for (int i = 0 ; i < buttonList.size() ; i++)
		{
			CardButton button = buttonList.get(i);
			button.setID(i);
			buttons.put(i, button);
		}
		nextID = buttonList.size();
	}
	
	/**
	 * Removes all buttons from the controls
	 */
	public void clearButtons()
	{
		buttons.clear();
		enabled.clear();
		nextID = 0;
		sendButtons();
	}
	
	public List<CardButton> getEnabledButtons()
	{
		return enabled;
	}
	
	/**
	 * Reevaluate all the buttons on the card and send packets to the owner
	 */
	public void updateButtons()
	{
		enabled.clear();
		boolean changed = false;
		for (CardButton button : buttons.values())
		{
			if (button.update())
			{
				changed = true;
			}
			if (button.isEnabled())
			{
				enabled.add(button);
			}
		}
		if (changed)
		{
			sendButtons();
		}
	}
	
	private void forceUpdateButtons()
	{
		enabled.clear();
		for (CardButton button : buttons.values())
		{
			button.update();
			if (button.isEnabled())
			{
				enabled.add(button);
			}
		}
		sendButtons();
	}
	
	/**
	 * Resets the current evaluations on the buttons.
	 */
	public void resetButtons()
	{
		for (CardButton button : buttons.values())
		{
			button.reset();
		}
	}
	
	public void sendButtons()
	{
		List<CardButton> buttons = getEnabledButtons();
		
		String[] text = new String[buttons.size()];
		byte[] id = new byte[buttons.size()];
		byte[] type = new byte[buttons.size()];
		byte[] color = new byte[buttons.size()];
		short[] x = new short[buttons.size()];
		short[] y = new short[buttons.size()];
		short[] width = new short[buttons.size()];
		short[] height = new short[buttons.size()];
		for (int i = 0 ; i < buttons.size() ; i++)
		{
			CardButton button = buttons.get(i);
			CardButtonBounds bounds = button.getBounds();
			text[i] = button.getText();
			id[i] = (byte) button.getID();
			type[i] = button.getType().getID();
			color[i] = (byte) button.getColor().getID();
			x[i] = bounds.getEncodedX();
			y[i] = bounds.getEncodedY();
			width[i] = bounds.getEncodedWidth();
			height[i] = bounds.getEncodedHeight();
		}
		card.getOwner().sendPacket(new PacketCardButtons(card.getID(), text, id, type, color, x, y, width, height));
	}
}
