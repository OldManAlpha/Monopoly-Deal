package oldmana.md.common.playerui;

import java.awt.Color;

public enum ButtonColorScheme
{
	NORMAL(new Color(212, 212, 212), new Color(112, 112, 112), new Color(0, 0, 0),
			new Color(167, 217, 245), new Color(60, 127, 177), new Color(0, 0, 0),
			new Color(230, 230, 230), new Color(160, 160, 160), new Color(120, 120, 120),
			0.35, 1.2, 0.9, 3),
	ALERT(new Color(240, 100, 100), new Color(150, 80, 80), new Color(0, 0, 0),
			new Color(240, 130, 130), new Color(180, 100, 100), new Color(0, 0, 0),
			new Color(255, 160, 160), new Color(240, 120, 120), new Color(120, 120, 120),
			0.1, 0.3, 0.1, 1),
	GRAY(new Color(190, 190, 190), new Color(100, 100, 100), new Color(0, 0, 0),
			new Color(160, 190, 220), new Color(70, 100, 160), new Color(0, 0, 0),
			new Color(230, 230, 230), new Color(160, 160, 160), new Color(120, 120, 120),
			0.2, 0.5, 0.4, 1.5);
	
	public final Color color;
	public final Color outlineColor;
	public final Color textColor;
	
	public final Color hoveredColor;
	public final Color hoveredOutlineColor;
	public final Color hoveredTextColor;
	
	public final Color disabledColor;
	public final Color disabledOutlineColor;
	public final Color disabledTextColor;
	
	public final double bottomLightGradient;
	public final double topLightFactor;
	public final double topLightGradient;
	public final double innerBorderLightFactor;
	
	ButtonColorScheme(Color color, Color outlineColor, Color textColor, Color hoveredColor, Color hoveredOutlineColor, Color hoveredTextColor,
	                  Color disabledColor, Color disabledOutlineColor, Color disabledTextColor, double bottomLightGradient, double topLightFactor,
	                  double topLightGradient, double innerBorderLightFactor)
	{
		this.color = color;
		this.outlineColor = outlineColor;
		this.textColor = textColor;
		
		this.hoveredColor = hoveredColor;
		this.hoveredOutlineColor = hoveredOutlineColor;
		this.hoveredTextColor = hoveredTextColor;
		
		this.disabledColor = disabledColor;
		this.disabledOutlineColor = disabledOutlineColor;
		this.disabledTextColor = disabledTextColor;
		
		this.bottomLightGradient = bottomLightGradient;
		this.topLightFactor = topLightFactor;
		this.topLightGradient = topLightGradient;
		this.innerBorderLightFactor = innerBorderLightFactor;
	}
	
	public int getID()
	{
		return ordinal();
	}
	
	public static ButtonColorScheme fromID(int id)
	{
		return values()[id];
	}
}
