package oldmana.md.client.gui.component;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import oldmana.md.client.MDScheduler;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardMoney;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;
import oldmana.md.client.gui.util.TextPainter.Outline;

public class MDCardInfo extends MDComponent
{
	private BufferedImage cache;
	
	private Card card;
	
	private int existTime;
	
	private int cardPos;
	
	public MDCardInfo(Card card)
	{
		setLocation(20, 20);
		this.card = card;
		
		int width = 400;
		try
		{
			Font f = GraphicsUtils.getThinMDFont(scale(20));
			List<String> desc = GraphicsUtils.splitStrings(Arrays.asList(card.getDescription().getText()), GraphicsUtils.getThinMDFont(scale(20)), 
					scale(width - 8), true);
			setSize(scale(width), scale(60) + scale(15) + (f.getSize() * desc.size()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		getClient().getScheduler().scheduleFrameboundTask(task ->
		{
			if (!isDisplayable())
			{
				task.cancel();
				return;
			}
			existTime = (int) Math.min(existTime + MDScheduler.getFrameDelay(), 200);
			repaint();
		});
	}
	
	public void setCardPos(int cardPos)
	{
		this.cardPos = cardPos;
	}
	
	private void render(Graphics gr)
	{
		int width = getWidth();
		int height = getHeight() - scale(15);
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Draw base
		g.setFont(GraphicsUtils.getBoldMDFont(scale(24)));
		Color background = card instanceof CardProperty ? new Color(240, 240, 240) : card.getValueColor();
		Color foreground = new Color(40, 40, 40);
		g.setColor(background);
		LinearGradientPaint propPaint = null;
		if (card instanceof CardProperty)
		{
			CardProperty prop = (CardProperty) card;
			int colorCount = prop.getColors().size();
			float[] fractions = new float[colorCount * 2];
			Color[] colors = new Color[colorCount * 2];
			for (int i = 0 ; i < colorCount * 2 ; i++)
			{
				fractions[i] = (i + (i % 2 == 0 ? 0.01F : 0.99F)) / (float) (colorCount * 2);
				colors[i] = /*GraphicsUtils.getLighterColor(*/prop.getColors().get(i / 2).getColor();//, 1);
			}
			propPaint = new LinearGradientPaint(0, 0, width, 0,
					fractions, colors);
		}
		g.setColor(foreground);
		g.fillRoundRect(0, 0, width, height, scale(20), scale(20));
		g.setColor(background);
		g.fillRoundRect(scale(2), scale(2), width - (scale(2) * 2), height - (scale(2) * 2), scale(18), scale(18));
		if (card instanceof CardProperty)
		{
			g.setPaint(propPaint);
			g.fillRoundRect(scale(2), scale(2), width - (scale(2) * 2), scale(24), scale(18), scale(18));
			g.fillRect(scale(2), scale(18), width - (scale(2) * 2), scale(10));
			g.setPaint(null);
			
			LinearGradientPaint paint = new LinearGradientPaint(0, scale(2), 0, scale(18), new float[] {0, 1}, new Color[] {new Color(255, 255, 255, 80), new Color(255, 255, 255, 0)});
			g.setPaint(paint);
			g.fillRoundRect(scale(2), scale(2), width - (scale(2) * 2), scale(24), scale(18), scale(18));
		}
		
		// Draw dividers
		g.setColor(foreground);
		g.fillRect(0, scale(28), width, scale(2));
		g.fillRect(0, height - scale(26), width, scale(2));
		
		// Draw card name
		g.setColor(foreground);
		String name = card.getName();
		if (card instanceof CardMoney)
		{
			name = card.getValue() + "M";
		}
		g.setColor(card instanceof CardProperty ? Color.WHITE : Color.BLACK);
		TextPainter tp = new TextPainter(name, g.getFont(),
				new Rectangle(0, scale(2), width, scale(28)));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		if (card instanceof CardProperty)
		{
			tp.setOutline(Outline.of(Color.BLACK, scale(3)));
		}
		tp.paint(g);
		g.setColor(Color.BLACK);
		
		// Draw card description
		g.setFont(GraphicsUtils.getThinMDFont(scale(20)));
		tp = new TextPainter(Arrays.asList(card.getDescription().getText()), g.getFont(), new Rectangle(scale(4), scale(32), width - scale(8),
				height - scale(32)), true, true);
		tp.paint(g);
		
		// Draw card type + value
		Color brighter = GraphicsUtils.getLighterColor(background, 1);
		GradientPaint paint = new GradientPaint(0, height - scale(24), brighter, 0, height - (scale(2) * 2), background);
		g.setPaint(paint);
		
		g.fillRoundRect(scale(2), height - scale(24), width - (scale(2) * 2), scale(24) - (scale(2) * 2), scale(18), scale(18));
		g.fillRect(scale(2), height - scale(24), width - (scale(2) * 2), scale(10));
		
		g.setColor(foreground);
		g.setFont(GraphicsUtils.getBoldMDFont(scale(20)));
		String type = "Action";
		if (card instanceof CardMoney)
		{
			type = "Money";
		}
		else if (card instanceof CardProperty)
		{
			type = "Property";
		}
		tp = new TextPainter(type + " Card", g.getFont(), new Rectangle(0, height - scale(24), width, scale(24)));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
		
		tp = new TextPainter(card.getValue() + "M", g.getFont(), new Rectangle(width - scale(46), height - scale(24), scale(40), scale(24)));
		tp.setHorizontalAlignment(Alignment.RIGHT);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
		
		// Draw arrow
		Polygon arrow = new Polygon();
		arrow.addPoint(cardPos - scale(12), height - scale(2));
		arrow.addPoint(cardPos + scale(12), height - scale(2));
		arrow.addPoint(cardPos, getHeight());
		g.setColor(Color.BLACK);
		g.fillPolygon(arrow);
		g.setColor(background);
		arrow = new Polygon();
		arrow.addPoint(cardPos - scale(10), height - scale(2));
		arrow.addPoint(cardPos + scale(10), height - scale(2));
		arrow.addPoint(cardPos, getHeight() - scale(2));
		g.fillPolygon(arrow);
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		if (cache == null)
		{
			cache = GraphicsUtils.createImage(getWidth(), getHeight());
			render(cache.createGraphics());
		}
		Graphics2D g = (Graphics2D) gr;
		if (existTime < 200)
		{
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, existTime / 200F));
		}
		g.drawImage(cache, 0, 0, null);
	}
}
