package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import oldmana.md.client.card.Card;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class MDInfoIcon extends MDComponent
{
	public MDInfoIcon(Card card)
	{
		setSize(scale(22), scale(22));
		
		addMouseListener(new MouseAdapter()
		{
			private MDCardInfo cardInfo;
			
			@Override
			public void mouseEntered(MouseEvent event)
			{
				cardInfo = new MDCardInfo(card);
				Point pos = SwingUtilities.convertPoint(MDInfoIcon.this, new Point(getWidth() / 2, -(cardInfo.getHeight() / 2) - scale(5)), getClient().getTableScreen());
				pos.x = Math.min(pos.x, getClient().getTableScreen().getWidth() - (cardInfo.getWidth() / 2));
				cardInfo.setLocationCentered(pos.x, pos.y);
				getClient().addTableComponent(cardInfo, 110);
			}
			
			@Override
			public void mouseExited(MouseEvent event)
			{
				if (cardInfo != null)
				{
					getClient().removeTableComponent(cardInfo);
					getClient().getTableScreen().repaint();
					cardInfo = null;
				}
			}
		});
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		GradientPaint paint = new GradientPaint(0, 0, new Color(250, 252, 253), 0, getHeight(), new Color(221, 233, 247));
		g.setPaint(paint);
		g.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
		g.setColor(new Color(160, 175, 195));
		g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
		
		g.setColor(Color.DARK_GRAY);
		g.setFont(GraphicsUtils.getBoldMDFont(scale(20)));
		TextPainter tp = new TextPainter("?", g.getFont(), new Rectangle(0, 0, getWidth(), getHeight() + scale(2)));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
	}
}
