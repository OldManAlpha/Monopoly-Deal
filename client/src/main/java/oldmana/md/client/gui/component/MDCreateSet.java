package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Polygon;
import java.awt.RenderingHints;

import oldmana.md.client.Player;
import oldmana.md.client.gui.util.HoverHelper;

public class MDCreateSet extends MDComponent
{
	private HoverHelper hover;
	
	public MDCreateSet(Player player)
	{
		setOpaque(true);
		setToolTipText("Create New Set");
		hover = new HoverHelper(this, 250);
	}
	
	private int getHighlight(int max)
	{
		return (int) (hover.getHighlight() * max);
	}
	
	@Override
	public void doPaint(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		int paintHighlight = getHighlight(20);
		LinearGradientPaint grayPaint = new LinearGradientPaint(0, 0, getWidth(), getHeight(),
				new float[] {0, 1}, new Color[] {new Color(200 + paintHighlight, 200 + paintHighlight, 200 + paintHighlight),
				new Color(170 + paintHighlight, 170 + paintHighlight, 170 + paintHighlight)});
		g.setColor(Color.LIGHT_GRAY);
		g.setPaint(grayPaint);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(new Color(128, 128, 128 + getHighlight(50)));
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		
		int halfWidth = getWidth() / 2;
		int halfHeight = getHeight() / 2;
		int halfThickness = scale(4);
		int halfLength = scale(13);
		
		Polygon plus = new Polygon();
		plus.addPoint(halfWidth - halfThickness, halfHeight - halfLength);
		plus.addPoint(halfWidth + halfThickness, halfHeight - halfLength);
		plus.addPoint(halfWidth + halfThickness, halfHeight - halfThickness);
		plus.addPoint(halfWidth + halfLength, halfHeight - halfThickness);
		plus.addPoint(halfWidth + halfLength, halfHeight + halfThickness);
		plus.addPoint(halfWidth + halfThickness, halfHeight + halfThickness);
		plus.addPoint(halfWidth + halfThickness, halfHeight + halfLength);
		plus.addPoint(halfWidth - halfThickness, halfHeight + halfLength);
		plus.addPoint(halfWidth - halfThickness, halfHeight + halfThickness);
		plus.addPoint(halfWidth - halfLength, halfHeight + halfThickness);
		plus.addPoint(halfWidth - halfLength, halfHeight - halfThickness);
		plus.addPoint(halfWidth - halfThickness, halfHeight - halfThickness);
		
		g.setColor(new Color(getHighlight(50), 255, getHighlight(50)));
		g.fillPolygon(plus);
		g.setColor(new Color(100, 160, 100));
		g.drawPolygon(plus);
	}
}
