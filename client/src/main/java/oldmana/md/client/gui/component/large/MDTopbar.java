package oldmana.md.client.gui.component.large;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import oldmana.md.client.SoundSystem;
import oldmana.md.client.gui.component.MDComponent;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;
import oldmana.md.client.gui.util.TextPainter.Outline;

public class MDTopbar extends MDComponent
{
	private String text;
	private int alertTicks;
	
	public MDTopbar()
	{
		super();
		setLocation(0, 0);
		getClient().getScheduler().scheduleTask(task ->
		{
			if (alertTicks > 0)
			{
				alertTicks--;
				updateGraphics();
			}
		}, 200, true);
	}
	
	public void setText(String text)
	{
		if (!text.equals(this.text))
		{
			this.text = text;
			updateGraphics();
		}
	}
	
	public void triggerAlert()
	{
		alertTicks = 6;
		SoundSystem.playSound("Alert");
		updateGraphics();
	}
	
	@Override
	public void doPaint(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		if (getClient().getThePlayer() != null)
		{
			Color color = getClient().getThePlayer().getUI().getInnerColor();
			GradientPaint paint = new GradientPaint(0, 0, GraphicsUtils.getLighterColor(color, 0.5), 0, getHeight() * 0.45F, color);
			g.setPaint(paint);
			//g.setColor(new Color(230, 245, 255));
			g.fillRect(0, 0, getWidth(), (int) (getHeight() * 0.45));
			paint = new GradientPaint(0, getHeight() * 0.45F, GraphicsUtils.getDarkerColor(color, 0.3), 0, getHeight(), GraphicsUtils.getDarkerColor(color, 1));
			g.setPaint(paint);
			//g.setColor(new Color(220, 235, 255));
			g.fillRect(0, (int) (getHeight() * 0.45), getWidth(), (int) (getHeight() * 0.55));
			color = getClient().getThePlayer().getUI().getBorderColor();
			g.setColor(color);
		}
		g.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
		
		g.setColor(Color.DARK_GRAY);
		Font font = GraphicsUtils.getBoldMDFont(scale(26));
		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		TextPainter tp = new TextPainter(text, font, new Rectangle(0, 2, getWidth(), getHeight()));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		if (alertTicks % 2 != 0)
		{
			g.setColor(Color.WHITE);
			tp.setOutline(Outline.of(Color.DARK_GRAY, scale(4)));
		}
		tp.paint(g);
	}
}
