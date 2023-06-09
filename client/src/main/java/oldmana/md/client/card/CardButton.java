package oldmana.md.client.card;

import oldmana.md.common.playerui.ButtonColorScheme;
import oldmana.md.common.playerui.CardButtonBounds;
import oldmana.md.common.playerui.CardButtonType;

public class CardButton
{
	private int id;
	private String text;
	private CardButtonBounds bounds;
	private CardButtonType type;
	private ButtonColorScheme colors;
	
	public CardButton(int id, String text, CardButtonBounds bounds, CardButtonType type, ButtonColorScheme colors)
	{
		this.id = id;
		this.text = text;
		this.bounds = bounds;
		this.type = type;
		this.colors = colors;
	}
	
	public int getID()
	{
		return id;
	}
	
	public String getText()
	{
		return text;
	}
	
	public CardButtonBounds getBounds()
	{
		return bounds;
	}
	
	public CardButtonType getType()
	{
		return type;
	}
	
	public ButtonColorScheme getColors()
	{
		return colors;
	}
}
