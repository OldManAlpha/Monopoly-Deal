package oldmana.md.client.gui.component;

import oldmana.md.client.card.Card;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.common.playerui.ButtonColorScheme;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class MDCardOption extends MDComponent
{
	private MDCard cardUI;
	private List<MDButton> buttons = new ArrayList<MDButton>();
	
	public MDCardOption(Card card)
	{
		cardUI = new MDCard(card, 2);
		setSize(cardUI.getWidth(), getComponentHeight());
		add(cardUI);
	}
	
	public MDCard getCardUI()
	{
		return cardUI;
	}
	
	public Card getCard()
	{
		return cardUI.getCard();
	}
	
	public void addButton(MDButton button)
	{
		button.setColor(ButtonColorScheme.GRAY);
		button.setFontSize(18);
		button.setSize(getWidth(), scale(30));
		button.setLocation(0, GraphicsUtils.getCardHeight(2) + scale(5) + (buttons.size() * scale(35)));
		buttons.add(button);
		add(button);
	}
	
	public static int getComponentHeight()
	{
		return GraphicsUtils.getCardHeight(2) + GraphicsUtils.scale(70);
	}
}
