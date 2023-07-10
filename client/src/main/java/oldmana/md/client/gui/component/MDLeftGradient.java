package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;

public class MDLeftGradient extends MDComponent
{
	private final Color[] sideColors = new Color[] {new Color(227, 230, 232), new Color(232, 235, 237),
			new Color(227, 230, 232), new Color(160, 180, 200)};
	private final float[] sideColorsPositions = new float[] {0, 0.5F, 0.985F, 1};
	
	public MDLeftGradient()
	{
		setOpaque(true);
	}
	
	@Override
	public void doPaint(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		
		g.setColor(new Color(240, 240, 240));
		g.fillRect(0, 0, getWidth(), getHeight());
		
		LinearGradientPaint paint = new LinearGradientPaint(0, 0, getWidth(), 0, sideColorsPositions, sideColors);
		g.setPaint(paint);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}
