package oldmana.md.common.playerui;

public interface Button
{
	String getText();
	
	void setText(String text);
	
	boolean isEnabled();
	
	void setEnabled(boolean enabled);
	
	ButtonColorScheme getColor();
	
	void setColor(ButtonColorScheme color);
}
