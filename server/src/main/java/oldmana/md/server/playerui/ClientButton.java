package oldmana.md.server.playerui;

import oldmana.md.common.playerui.Button;
import oldmana.md.common.playerui.ButtonColorScheme;
import oldmana.md.common.playerui.ClientButtonType;

public class ClientButton implements Button
{
	private String text;
	private boolean enabled;
	private ButtonColorScheme color;
	private ClientButtonType type;
	
	@Override
	public String getText()
	{
		return text;
	}
	
	@Override
	public void setText(String text)
	{
		this.text = text;
	}
	
	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	@Override
	public ButtonColorScheme getColor()
	{
		return color;
	}
	
	@Override
	public void setColor(ButtonColorScheme color)
	{
		this.color = color;
	}
}
