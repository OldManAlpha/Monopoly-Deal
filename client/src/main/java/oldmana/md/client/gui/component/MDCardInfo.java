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
import java.util.Arrays;
import java.util.List;

import oldmana.md.client.MDScheduler;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardMoney;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class MDCardInfo extends MDComponent
{
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
	
	@Override
	public void paintComponent(Graphics gr)
	{
		int width = getWidth();
		int height = getHeight() - scale(15);
		Graphics2D g = (Graphics2D) gr;
		if (existTime < 200)
		{
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, existTime / 200F));
		}
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Draw base
		g.setFont(GraphicsUtils.getBoldMDFont(scale(24)));
		Color cc = card instanceof CardProperty ? new Color(240, 240, 240) : card.getValueColor();
		g.setColor(cc);
		LinearGradientPaint propPaint = null;
		if (card instanceof CardProperty)
		{
			CardProperty prop = (CardProperty) card;
			int colorCount = prop.getColors().size();
			float[] fractions = new float[colorCount * 2];
			Color[] colors = new Color[colorCount * 2];
			for (int i = 0 ; i < colorCount * 2 ; i++)
			{
				fractions[i] = (i + (i % 2 == 0 ? 0.2F : 0.8F)) / (float) (colorCount * 2);
				colors[i] = GraphicsUtils.getLighterColor(prop.getColors().get(i / 2).getColor(), 1);
			}
			propPaint = new LinearGradientPaint(0, 0, width, 0,
					fractions, colors);
		}
		g.fillRoundRect(0, 0, width, height, scale(20), scale(20));
		if (card instanceof CardProperty)
		{
			g.setPaint(propPaint);
			g.fillRoundRect(0, 0, width, scale(28), scale(20), scale(20));
			g.fillRect(0, scale(18), width, scale(10));
			g.setPaint(null);
		}
		
		// Draw divider
		g.setColor(Color.BLACK);
		g.fillRect(0, scale(28), width, scale(2));
		
		// Draw card name
		String name = card.getName();
		if (card instanceof CardMoney)
		{
			name = card.getValue() + "M";
		}
		TextPainter tp = new TextPainter(name, g.getFont(), 
				new Rectangle(0, scale(2), width, scale(28)));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
		
		// Draw card description
		g.setFont(GraphicsUtils.getThinMDFont(scale(20)));
		tp = new TextPainter(Arrays.asList(card.getDescription().getText()), g.getFont(), new Rectangle(scale(4), scale(58), width - scale(8),
				height - scale(58)), true, true);
		tp.paint(g);
		
		// Draw card type
		Color brighter = GraphicsUtils.getLighterColor(cc, 1);
		GradientPaint paint = new GradientPaint(0, scale(29), brighter, 0, scale(29) + scale(24), cc);
		g.setPaint(paint);
		g.fillRoundRect(0, scale(29), scale(130), scale(24), scale(10), scale(10));
		g.setColor(Color.BLACK);
		g.drawRoundRect(0, scale(29), scale(130) - 1, scale(24) - 1, scale(10), scale(10));
		g.setPaint(paint);
		g.fillRect(0, scale(29) + 1, scale(6), scale(6));
		g.setColor(Color.BLACK);
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
		tp = new TextPainter(type + " Card", g.getFont(), new Rectangle(0, scale(28), scale(130), scale(28)));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
		
		// Draw Value
		g.setPaint(paint);
		g.fillRoundRect(width - scale(130), scale(29), scale(130), scale(24), scale(10), scale(10));
		g.setColor(Color.BLACK);
		g.drawRoundRect(width - scale(130), scale(29), scale(130) - 1, scale(24) - 1, scale(10), scale(10));
		g.setPaint(paint);
		g.fillRect(width - scale(6), scale(29) + 1, scale(6), scale(4));
		g.setColor(Color.BLACK);
		g.setFont(GraphicsUtils.getBoldMDFont(scale(20)));
		
		tp = new TextPainter("Value: " + card.getValue() + "M", g.getFont(), new Rectangle(width - scale(130), scale(28), scale(130), scale(28)));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
		
		// Draw border
		g.drawRoundRect(0, 0, width - 1, height - 1, scale(20), scale(20));
		
		Polygon arrow = new Polygon();
		arrow.addPoint(cardPos - scale(10), height - 1);
		arrow.addPoint(cardPos + scale(10), height - 1);
		arrow.addPoint(cardPos, getHeight());
		g.setColor(cc);
		g.fillPolygon(arrow);
		g.setColor(Color.BLACK);
		g.drawLine(cardPos - scale(10), height, cardPos, getHeight());
		g.drawLine(cardPos + scale(10), height, cardPos, getHeight());
	}
}
