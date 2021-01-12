package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.List;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardActionRent;
import oldmana.md.client.card.CardMoney;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class MDCardInfo extends MDComponent
{
	private Card card;
	
	public MDCardInfo(Card card)
	{
		setLocation(20, 20);
		this.card = card;
		
		FontMetrics metrics = getFontMetrics(GraphicsUtils.getThinMDFont(scale(20)));
		List<String> desc = GraphicsUtils.splitStrings(Arrays.asList(card.getDescription()), metrics, scale(342), true);
		setSize(scale(350), scale(60) + (metrics.getHeight() * desc.size()));
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Draw base
		g.setFont(GraphicsUtils.getBoldMDFont(scale(24)));
		Color cc = card instanceof CardProperty ? new Color(240, 240, 240) : card.getValueColor();
		g.setColor(cc);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), scale(20), scale(20));
		
		// Draw divider
		g.setColor(Color.BLACK);
		g.fillRect(0, scale(28), getWidth(), scale(2));
		
		// Draw card name
		String name = card.getName();
		if (card instanceof CardMoney)
		{
			name = card.getValue() + "M";
		}
		else if (card instanceof CardActionRent)
		{
			name = ((CardActionRent) card).getRentColors().length + "-Color Rent";
		}
		TextPainter tp = new TextPainter(name, g.getFont(), 
				new Rectangle(0, scale(2), getWidth(), scale(28)));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
		
		// Draw card description
		g.setFont(GraphicsUtils.getThinMDFont(scale(20)));
		tp = new TextPainter(Arrays.asList(card.getDescription()), g.getFont(), new Rectangle(scale(4), scale(32), getWidth() - scale(8), getHeight() - scale(32)), 
				true, true);
		tp.paint(g);
		
		// Draw card type
		Color brighter = new Color((255 + cc.getRed()) / 2, (255 + cc.getGreen()) / 2, (255 + cc.getBlue()) / 2);
		g.setColor(brighter);
		g.fillRoundRect(getWidth() / 2 - scale(65), getHeight() - scale(24), scale(130), scale(24), scale(10), scale(10));
		g.setColor(Color.BLACK);
		g.drawRoundRect(getWidth() / 2 - scale(65), getHeight() - scale(24), scale(130), scale(24), scale(10), scale(10));
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
		tp = new TextPainter(type + " Card", g.getFont(), new Rectangle(0, getHeight() - scale(28), getWidth(), scale(28)));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.BOTTOM);
		tp.paint(g);
		
		// Draw border
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, scale(20), scale(20));
	}
}
