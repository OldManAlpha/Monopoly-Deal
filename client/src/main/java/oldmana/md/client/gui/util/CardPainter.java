package oldmana.md.client.gui.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import oldmana.md.client.MDClient;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardAction;
import oldmana.md.client.card.CardActionRent;
import oldmana.md.client.card.CardBuilding;
import oldmana.md.client.card.CardMoney;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.CardProperty.PropertyColor;
import oldmana.md.client.gui.component.MDCard;
import oldmana.md.client.gui.util.TextPainter.Alignment;
import oldmana.md.client.gui.util.TextPainter.Outline;

import javax.imageio.ImageIO;

/**
 * You'd think you're at Olive Garden with how much spaghetti is here.
 */
public class CardPainter
{
	private Card card;
	private double scale;
	
	public static BufferedImage backGraphic;
	
	static
	{
		File cardBackFile = new File(MDClient.getInstance().getDataFolder(), "textures" + File.separator + "cardback.png");
		if (cardBackFile.exists() && !cardBackFile.isDirectory())
		{
			try
			{
				backGraphic = ImageIO.read(cardBackFile);
			}
			catch (IOException e)
			{
				System.out.println("Failed to load card back");
				e.printStackTrace();
			}
		}
	}
	
	public CardPainter(Card card, double scale)
	{
		this.card = card;
		this.scale = scale;
	}
	
	public int scale(double num)
	{
		return (int) Math.round(num * scale);
	}
	
	public int getWidth()
	{
		return scale(MDCard.CARD_SIZE.width);
	}
	
	public int getHeight()
	{
		return scale(MDCard.CARD_SIZE.height);
	}
	
	public Font getFont()
	{
		return new Font("ITCKabelStd-Bold", Font.PLAIN, scale(4));
	}
	
	public void paintMystery(Graphics g)
	{
		// Draw White
		g.setColor(Color.WHITE);
		g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getWidth() / 6, getWidth() / 6);
		
		// Draw Card Outline
		g.setColor(Color.BLACK);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getWidth() / 6, getWidth() / 6);
		
		// Draw Inner Outline
		g.setColor(Color.BLACK);
		for (int i = 0 ; i < 1 + (scale(1) / 4) ; i++)
		{
			g.drawRect(scale(3) + i, scale(3) + i, getWidth() - scale(6) - 1 - (i * 2), getHeight() - scale(6) - 1 - (i * 2));
		}
		
		/*
		// Draw Value
		g.setColor(Color.BLACK);
		g.fillOval(scale(4), scale(4), scale(12), scale(12));
		g.setColor(Color.WHITE);
		g.fillOval(scale(4 + 0.5), scale(4 + 0.5), scale(11), scale(11));
		
		g.setColor(Color.BLACK);
		Font font = new Font(getFont().getFontName(), Font.BOLD, scale(6));
		g.setFont(font);
		TextPainter tp = new TextPainter("??", font, new Rectangle(scale(2.75), scale(5), scale(15), scale(10)));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint((Graphics2D) g);
		
		 */
		
		// Draw ?
		TextPainter tp = new TextPainter("?", GraphicsUtils.getBoldMDFont(scale(60)), new Rectangle(0, scale(4), getWidth(), getHeight()));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint((Graphics2D) g);
	}
	
	
	// TODO: The card scaling is a bit of a failure and needs rethinking or just needs to be removed. This only supports 8x scaled cards.
	public void paint(Graphics gr)
	{
		boolean money = card instanceof CardMoney;
		boolean property = card instanceof CardProperty;
		boolean action = card instanceof CardAction;
		
		boolean specialPropName = property && ((CardProperty) card).getColors().size() > 2;
		if (specialPropName)
		{
			// If the special name doesn't fit in 1 line, do not use special name
			if (GraphicsUtils.splitString(card.getName().toUpperCase(), new Font(getFont().getFontName(), Font.BOLD,
					scale(4.6)), getWidth() - scale(13)).size() > 1)
			{
				specialPropName = false;
			}
		}
		
		CardProperty prop = property ? (CardProperty) card : null;
		
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		if (card == null) // Drawing for unknown cards
		{
			// Draw White
			g.setColor(Color.WHITE);
			g.fillRoundRect(0, 0, getWidth(), getHeight(), (getWidth() / 6) + scale(2), (getWidth() / 6) + scale(2));
			
			// Draw red back
			g.setColor(new Color(239, 15, 20));
			g.fillRect(scale(5), scale(5), getWidth() - (scale(5) * 2), getHeight() - (scale(5) * 2));
			
			// Draw back graphics if present
			if (backGraphic != null)
			{
				Image img = backGraphic.getScaledInstance(getWidth() - (scale(5) * 2), getHeight() - (scale(5) * 2), BufferedImage.SCALE_SMOOTH);
				g.drawImage(img, scale(5), scale(5), null);
			}
			
			// Draw Inner Outline
			g.setColor(Color.BLACK);
			//g.drawRoundRect(scale(4), scale(4), getWidth() - scale(8) - 1, getHeight() - scale(8) - 1, getWidth() / 6, getWidth() / 6);
			return;
		}
		
		// Drawing for known cards
		
		Color outerColor = card.getOuterColor();
		Color innerColor = card.getInnerColor();
		Color textColor = new Color(30, 30, 30);
		
		// Draw White
		g.setColor(outerColor);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), (getWidth() / 6) + scale(2), (getWidth() / 6) + scale(2));
		
		// Draw Value Color
		//if (!property)
		{
			g.setColor(innerColor);
			//g.fillRoundRect(scale(3), scale(3), getWidth() - scale(6) - 1, getHeight() - scale(6) - 1, getWidth() / 6, getWidth() / 6);
			g.fillRect(scale(3), scale(3), getWidth() - scale(6) - 1, getHeight() - scale(6) - 1);
		}
		// Draw Property Color
		if (property)
		{
			List<PropertyColor> colors = prop.getColors();
			int colorHeight = colors.size() > 2 ? scale(18) : scale(16);
			int padding = scale(0.5);
			int doublePadding = padding * 2;
			double interval = (double) (getWidth() - scale(12) - scale(1)) / (double) (colors.size());
			
			g.setColor(Color.BLACK);
			g.fillRect(scale(6), scale(6), getWidth() - scale(12), colorHeight + doublePadding);
			
			for (int i = 0 ; i < colors.size() ; i++)
			{
				g.setColor(colors.get(i).getColor());
				int width = (int) (Math.round(interval * (i + 1)) - Math.round(interval * i));
				g.fillRect(scale(6) + padding + (int) Math.round(interval * i), scale(6) + padding, width, colorHeight);
			}
			/*
			g.setColor(Color.BLACK);
			for (int i = 0 ; i < scale(0.5) ; i++)
			{
				g.drawRect(scale(6) + i, scale(6) + i, getWidth() - scale(12) - (i * 2) - 1, scale(17) - (i * 2) - 1);
			}
			 */
			//g.setColor(prop.getColor().getColor());
			//g.fillRect(scale(6), scale(6), getWidth() - scale(12), scale(17));
			// Old rounded corners color design
			//g.fillRoundRect(scale(3) + 1, scale(3), getWidth() - scale(6) - 2, scale(15), getWidth() / 6, getWidth() / 6);
			//g.fillRect(scale(3), scale(10), getWidth() - scale(6) - 1, scale(8));
		}
		
		// Draw Card Value And Value Border
		boolean hasValue = card.getValue() > 0;
		// Not sure why this was here
		//if (property && hasValue)
		//{
			//g.setColor(innerColor);
			//g.fillOval(scale(4), scale(4), scale(12), scale(12));
		//}
		if (!property)
		{
			Color gray = Color.GRAY;
			Color color = new Color((gray.getRed() + innerColor.getRed()) / 2, (gray.getGreen() + innerColor.getGreen()) / 2,
					(gray.getBlue() + innerColor.getBlue()) / 2);
			g.setColor(color);
			g.fillRect(scale(8), scale(8), getWidth() - scale(16) - 1, getHeight() - scale(16) - 1);
			color = new Color((gray.getRed() + innerColor.getRed() * 5) / 6, (gray.getGreen() + innerColor.getGreen() * 5) / 6,
					(gray.getBlue() + innerColor.getBlue() * 5) / 6);
			g.setColor(innerColor);
			g.fillRect(scale(11), scale(11), getWidth() - scale(22) - 1, getHeight() - scale(22) - 1);
			g.setColor(color);
			g.fillRect(scale(12), scale(12), getWidth() - scale(24) - 1, getHeight() - scale(24) - 1);
		}
		// Draw Inner Outline
		g.setColor(Color.BLACK);
		//g.drawRoundRect(scale(3), scale(3), getWidth() - scale(6) - 1, getHeight() - scale(6) - 1, getWidth() / 6, getWidth() / 6);
		for (int i = 0 ; i < 1 + (scale(1) / 4) ; i++)
		{
			g.drawRect(scale(3) + i, scale(3) + i, getWidth() - scale(6) - 1 - (i * 2), getHeight() - scale(6) - 1 - (i * 2));
		}
		
		// Draw Card Name/Info
		if (money)
		{
			g.setColor(new Color(30, 30, 30));
			g.fillOval(scale(11), scale(26), scale(38), scale(38));
			g.setColor(innerColor);
			g.fillOval(scale(12), scale(27), scale(36), scale(36));
			g.setColor(textColor);
			/*
			for (int i = 0 ; i < scale(1) ; i++)
			{
				g.drawOval(scale(11) + i, scale(26) + i, scale(38) - (i * 2) - 1, scale(38) - (i * 2) - 1);
			}
			*/
			Font font = new Font(getFont().getFontName(), Font.BOLD, scale(16));
			g.setFont(font);
			/*
			TextPainter tp = new TextPainter(card.getValue() + "M", font, new Rectangle(scale(0), scale(30), getWidth(), scale(30)));
			tp.setHorizontalAlignment(Alignment.CENTER);
			tp.setVerticalAlignment(Alignment.CENTER);
			tp.paint(g);
			*/
			TextPainter tp;
			if (card.getDisplayName() != null)
			{
				tp = new TextPainter(Arrays.asList(card.getDisplayName()), font, new Rectangle(scale(12) - 1, scale(30) - 1 + scale(card.getDisplayOffsetY()), scale(36) + 1, scale(30) + 1), false, false);
			}
			else
			{
				tp = new TextPainter(card.getName(), font, new Rectangle(scale(12) - 1, scale(30) - 1 + scale(card.getDisplayOffsetY()), scale(36) + 1, scale(36) + 1), false, false);
			}
			tp.setHorizontalAlignment(Alignment.CENTER);
			tp.setVerticalAlignment(Alignment.CENTER);
			tp.paint(g);
		}
		else
		{
			// Special name drawing for 10-Color Property Wild Cards, disabled for now
			
			List<PropertyColor> colors = prop != null ? prop.getColors() : null;
			if (property && colors.size() > 2)
			{
				TextPainter tp = new TextPainter(Arrays.asList("This card can be used as part", "of " + colors.size() + " different color sets."), GraphicsUtils.getThinMDFont(Font.BOLD, scale(3.8)), new Rectangle(scale(6), scale(75), getWidth() - scale(12), scale(16)), false, false);
				tp.setHorizontalAlignment(Alignment.CENTER);
				tp.setVerticalAlignment(Alignment.TOP);
				tp.paint(g);
				
				double angleInc = Math.min(10, 80.0 / colors.size());
				double angle = (-(colors.size() * angleInc) / 2) + (angleInc / 2);
				
				g.translate((getWidth() / 2) - scale(3), (getHeight() * 0.45) - scale(4.5));
				for (PropertyColor color : colors)
				{
					Graphics2D g2 = (Graphics2D) g.create();
					g2.rotate(Math.toRadians(angle), scale(3), scale(30));
					drawMiniProp(g2, color);
					
					angle += angleInc;
				}
				g.translate(-((getWidth() / 2) - scale(3)), -((getHeight() * 0.45) - scale(4.5)));
				
				tp = new TextPainter(String.valueOf(colors.size()), GraphicsUtils.getBoldMDFont(scale(10)),
						new Rectangle((getWidth() / 2) - scale(10), (int) ((getHeight() * 0.65) - (scale(4.5) / 2) -
								scale(10)), scale(20), scale(20)), false, false);
				tp.setHorizontalAlignment(Alignment.CENTER);
				tp.setVerticalAlignment(Alignment.CENTER);
				tp.paint(g);
			}
			
			if (specialPropName)
			{
				g.setColor(innerColor);
				g.fillRect(scale(6), scale(6 + 5), getWidth() - scale(12), scale(17 - 10 + 2));
				g.setColor(Color.BLACK);
				g.fillRect(scale(6), scale(6 + 5), getWidth() - scale(12), scale(0.5));
				g.fillRect(scale(6), scale(19.5), getWidth() - scale(12), scale(0.5));
				g.fillRect(scale(6), scale(12), getWidth() - scale(12), scale(7));
				g.setColor(innerColor);
				g.fillRect(scale(6.5), scale(12.5), getWidth() - scale(13), scale(6));
				g.setColor(Color.BLACK);
				Font font = new Font(getFont().getFontName(), Font.BOLD, scale(4.6));
				g.setFont(font);
				TextPainter tp = new TextPainter(card.getName().toUpperCase(), font, new Rectangle(scale(6.5), scale(13.5), getWidth() - scale(13), scale(4)));
				tp.setHorizontalAlignment(Alignment.CENTER);
				tp.setVerticalAlignment(Alignment.TOP);
				tp.paint(g);
			}
			else
			{
				Font font = new Font(getFont().getFontName(), Font.BOLD, scale(property ? 4.6 : 5));
				g.setFont(font);
				TextPainter tp = new TextPainter(property ? card.getName().toUpperCase() : "ACTION CARD", font, new Rectangle(scale(6), scale(property ? 24 : 16), getWidth() - scale(12), scale(20)));
				tp.setHorizontalAlignment(Alignment.CENTER);
				tp.setVerticalAlignment(Alignment.TOP);
				if (!property)
				{
					tp.paint(g);
				}
				
				if (property)
				{
					tp = new TextPainter(card.getName().toUpperCase(), font, new Rectangle(hasValue ? scale(15.5) : scale(12), scale(9), getWidth() - scale(24), scale(13)));
					tp.setVerticalAlignment(Alignment.CENTER);
					tp.setHorizontalAlignment(Alignment.CENTER);
					if (colors.size() > 1)
					{
						if (colors.stream().anyMatch(color -> color.isDark()))
						{
							tp.setOutline(Outline.of(Color.WHITE, scale(1.25)));
						}
					}
					else
					{
						if (colors.get(0).isDark())
						{
							g.setColor(Color.WHITE);
						}
					}
					tp.paint(g);
					g.setColor(Color.BLACK);
				}
				
				/*
				double interval = (double) (getWidth() - scale(12)) / (double) (colors.size());
			for (int i = 0 ; i < colors.size() ; i++)
			{
				g.setColor(colors.get(i).getColor());
				g.fillRect(scale(6) + (int) Math.ceil((interval * i)), scale(6), (int) Math.ceil(interval), scale(17));
			}
				 */
			}
			
			if (!property)
			{
				g.setColor(new Color(30, 30, 30));
				g.fillOval(scale(11), scale(26), scale(38), scale(38));
				// Draw Rent Card Colors
				if (card instanceof CardActionRent)
				{
					PropertyColor[] rentColors = ((CardActionRent) card).getRentColors();
					double angleInc = 360.0 / rentColors.length;
					boolean multi = rentColors.length > 2;
					for (int i = 0 ; i < rentColors.length ; i++)
					{
						g.setColor(rentColors[i].getColor());
						g.fillArc(scale(12.5), scale(27.5), scale(35), scale(35), (int) -Math.ceil((angleInc * i) + (multi ? -90 : 180)),
								(int) -Math.ceil(angleInc));
					}
					g.setColor(Color.WHITE);
					g.fillOval(scale(18), scale(33), scale(24), scale(24));
				}
				else
				{
					g.setColor(Color.WHITE);
					g.fillOval(scale(12.5), scale(27.5), scale(35), scale(35));
				}
				g.setColor(textColor);
				Font font = new Font(getFont().getFontName(), Font.BOLD, scale(card.getFontSize()));
				g.setFont(font);
				TextPainter tp;
				if (card.getDisplayName() != null)
				{
					tp = new TextPainter(Arrays.asList(card.getDisplayName()), font, new Rectangle(scale(12) - 1, scale(28) - 1 + scale(card.getDisplayOffsetY()), scale(36) + 1, scale(30) + 1), false, false);
				}
				else
				{
					tp = new TextPainter(card.getName(), font, new Rectangle(scale(12) - 1, scale(28) - 1 + scale(card.getDisplayOffsetY()), scale(36) + 1, scale(30) + 1), false, false);
				}
				tp.setHorizontalAlignment(Alignment.CENTER);
				tp.setVerticalAlignment(Alignment.CENTER);
				tp.paint(g);
			}
			else // Draw Property Rent Info
			{
				if (prop.isSingleColor())
				{
					{
						TextPainter tp = new TextPainter("RENT", GraphicsUtils.getThinMDFont(Font.BOLD, scale(6)),
								new Rectangle(scale(37), scale(31), scale(15), scale(7)), false, false);
						tp.paint(g);
						tp = new TextPainter(Arrays.asList("(No. of properties", "owned in set)"), GraphicsUtils.getThinMDFont(Font.BOLD, scale(3)),
								new Rectangle(scale(10), scale(31), scale(15), scale(7)), false, false);
						tp.setHorizontalAlignment(Alignment.CENTER);
						tp.paint(g);
						for (int i = 0 ; i < prop.getColor().getMaxProperties() ; i++)
						{
							tp = new TextPainter(".................", GraphicsUtils.getThinMDFont(Font.BOLD, scale(3.8)),
									new Rectangle(scale(21), scale(42.5) + (scale(11) * i), scale(20), scale(4)), false, false);
							tp.paint(g);
						}
						g.setColor(innerColor);
						g.fillRect(scale(23), scale(43) + (scale(11) * (prop.getColor().getMaxProperties() - 1)), scale(13), scale(4));
						g.setColor(Color.BLACK);
						tp = new TextPainter("FULL SET", GraphicsUtils.getBoldMDFont(scale(3.5)),
								new Rectangle(scale(20), scale(43) + (scale(11) * (prop.getColor().getMaxProperties() - 1)), scale(20), scale(4)), false, false);
						tp.setHorizontalAlignment(Alignment.CENTER);
						tp.paint(g);
					}
					Graphics2D trans = (Graphics2D) g.create();
					trans.translate(scale(14), scale(40));
					drawMiniProps(trans, prop.getColor());
					trans = (Graphics2D) g.create();
					//trans.translate(scale(39), scale(40));
					trans.translate(scale(37), scale(40));
					drawRentText(trans, prop.getColor());
					if (prop.getColor().getMaxProperties() > 4)
					{
						TextPainter tp = new TextPainter("...  Max " + prop.getColor().getMaxProperties() + "  ...", GraphicsUtils.getBoldMDFont(scale(6)), new Rectangle(0, getHeight() - scale(10), getWidth(), scale(7)), false, false);
						tp.setHorizontalAlignment(Alignment.CENTER);
						tp.setVerticalAlignment(Alignment.CENTER);
						tp.paint(g);
					}
				}
				else if (prop.isBiColor())
				{
					{
						TextPainter tp = new TextPainter("RENT", GraphicsUtils.getThinMDFont(Font.BOLD, scale(6)),
								new Rectangle(getWidth() / 2, scale(31), (getWidth() / 2) - scale(6), scale(7)), false, false);
						tp.setHorizontalAlignment(Alignment.CENTER);
						tp.paint(g);
						tp = new TextPainter("RENT", GraphicsUtils.getThinMDFont(Font.BOLD, scale(6)),
								new Rectangle(scale(6), scale(31), (getWidth() / 2) - scale(6), scale(7)), false, false);
						tp.setHorizontalAlignment(Alignment.CENTER);
						tp.paint(g);
						
						for (int i = 0 ; i < colors.get(0).getMaxProperties() ; i++)
						{
							tp = new TextPainter("..", GraphicsUtils.getThinMDFont(Font.BOLD, scale(3.8)),
									new Rectangle(scale(16.5), scale(42.5) + (scale(11) * i), scale(20), scale(4)), false, false);
							tp.paint(g);
						}
						for (int i = 0 ; i < colors.get(1).getMaxProperties() ; i++)
						{
							tp = new TextPainter("..", GraphicsUtils.getThinMDFont(Font.BOLD, scale(3.8)),
									new Rectangle(scale(40.5), scale(42.5) + (scale(11) * i), scale(20), scale(4)), false, false);
							tp.paint(g);
						}
					}
					Graphics2D trans = (Graphics2D) g.create();
					trans.translate(scale(10), scale(40));
					drawMiniProps(trans, prop.getColors().get(0));
					trans = (Graphics2D) g.create();
					//trans.translate(scale(19), scale(40));
					trans.translate(scale(17), scale(40));
					drawRentText(trans, prop.getColors().get(0));
					if (prop.getColors().get(0).getMaxProperties() > 4)
					{
						TextPainter tp = new TextPainter("Max " + prop.getColors().get(0).getMaxProperties(), GraphicsUtils.getBoldMDFont(scale(6)),
								new Rectangle(0, getHeight() - scale(9), getWidth() / 2, scale(7)), false, false);
						tp.setHorizontalAlignment(Alignment.CENTER);
						tp.setVerticalAlignment(Alignment.CENTER);
						tp.paint(g);
					}
					
					trans = (Graphics2D) g.create();
					trans.translate(scale(34), scale(40));
					drawMiniProps(trans, prop.getColors().get(1));
					trans = (Graphics2D) g.create();
					//trans.translate(scale(43), scale(40));
					trans.translate(scale(41), scale(40));
					drawRentText(trans, prop.getColors().get(1));
					if (prop.getColors().get(1).getMaxProperties() > 4)
					{
						TextPainter tp = new TextPainter("Max " + prop.getColors().get(1).getMaxProperties(), GraphicsUtils.getBoldMDFont(scale(6)),
								new Rectangle(getWidth() / 2, getHeight() - scale(9), getWidth() / 2, scale(7)), false, false);
						tp.setHorizontalAlignment(Alignment.CENTER);
						tp.setVerticalAlignment(Alignment.CENTER);
						tp.paint(g);
					}
				}
			}
		}
		
		if (hasValue)
		{
			if (!property)
			{
				g.setColor(innerColor);
				g.fillOval(scale(4), scale(4), scale(12), scale(12));
				g.fillOval(scale(56 - 12) - 1, scale(86 - 12) - 1, scale(12), scale(12));
			}
			
			if (action || card instanceof CardBuilding)
			{
				g.setColor(new Color(200, 0, 0));
			}
			else
			{
				g.setColor(Color.BLACK);
			}
			if (specialPropName) // We need to move the value up a little bit up so that the special name isn't obstructed
			{
				g.translate(-scale(2.5), -scale(2.5));
			}
			g.fillOval(scale(4), scale(4), scale(12), scale(12));
			//g.setColor(Color.BLACK);
			//g.drawOval(scale(5) - 1, scale(5) - 1, scale(12) + 2, scale(12) + 2);
			if (!property)
			{
				g.fillOval(scale(56 - 12) - 1, scale(86 - 12) - 1, scale(12), scale(12));
				g.setColor(innerColor);
				g.fillOval(scale(56 - 12 + 0.5) - 1, scale(86 - 12 + 0.5) - 1, scale(11), scale(11));
			}
			g.setColor(innerColor);
			
			g.fillOval(scale(4 + 0.5), scale(4 + 0.5), scale(11), scale(11));
			
			g.setColor(Color.BLACK);
			Font font = new Font(getFont().getFontName(), Font.BOLD, card.getValue() < 10 ? scale(6) :
					(card.getValue() < 100 ? scale(5.5) : scale(4.5)));
			g.setFont(font);
			TextPainter tp = new TextPainter(card.getValue() + "M", font, new Rectangle(scale(2.75), scale(5), scale(card.getValue() < 10 ? 15 : 14), scale(10)));
			tp.setHorizontalAlignment(Alignment.CENTER);
			tp.setVerticalAlignment(Alignment.CENTER);
			tp.paint(g);
			if (specialPropName)
			{
				g.translate(scale(2.5), scale(2.5));
			}
			if (!property)
			{
				tp = new TextPainter(card.getValue() + "M", font, new Rectangle(scale(56 - 12 - 1), scale(86 - 12 + 1), scale(14), scale(10)));
				tp.setHorizontalAlignment(Alignment.CENTER);
				tp.setVerticalAlignment(Alignment.CENTER);
				tp.paint(g);
			}
		}
		
		if (MDClient.getInstance().isDebugEnabled())
		{
			g.setColor(Color.GRAY);
			GraphicsUtils.drawDebug(g, "ID: " + card.getID(), scale(10), getWidth(), getHeight());
		}
	}
	
	public void drawMiniProps(Graphics2D g, PropertyColor color)
	{
		for (int i = 0 ; i < Math.min(color.getMaxProperties(), 4) ; i++)
		{
			for (int e = i ; e >= 0 ; e--)
			{
				Graphics2D g2 = (Graphics2D) g.create();
				g2.rotate(Math.toRadians(e * -15), scale(1), scale(8));
				drawMiniProp(g2, color);
			}
			TextPainter tp = new TextPainter(String.valueOf(i + 1), GraphicsUtils.getBoldMDFont(scale(5)), new Rectangle(0, scale(3), scale(6), scale(6)));
			tp.setHorizontalAlignment(Alignment.CENTER);
			tp.setVerticalAlignment(Alignment.CENTER);
			tp.paint(g);
			g.translate(0, scale(11));
		}
		
	}
	
	public void drawMiniProp(Graphics2D g, PropertyColor color)
	{
		g.setColor(Color.BLACK);
		g.fillRoundRect(0, 0, scale(6), scale(9), scale(3), scale(3));
		g.setColor(card.getInnerColor());
		g.fillRoundRect(scale(0.5), scale(0.5), scale(5), scale(8), scale(2), scale(2));
		g.setColor(color.getColor());
		g.fillRoundRect(scale(0.5), scale(0.5), scale(5), scale(2), scale(2), scale(2));
		g.fillRect(scale(0.5), scale(1.5), scale(5), scale(1));
		g.setColor(Color.BLACK);
		g.fillRect(0, scale(2.5), scale(6) - 1, scale(0.5));
	}
	
	public void drawRentText(Graphics2D g, PropertyColor color)
	{
		for (int i = 0 ; i < Math.min(color.getMaxProperties(), 4) ; i++)
		{
			TextPainter tp = new TextPainter(color.getRent(i + 1) + "M", GraphicsUtils.getBoldMDFont(scale(7)), new Rectangle(0, 0, scale(14), scale(9)));
			tp.setHorizontalAlignment(Alignment.CENTER);
			tp.setVerticalAlignment(Alignment.CENTER);
			tp.paint(g);
			g.translate(0, scale(11));
		}
	}
	
	public void drawDots(Graphics2D g, PropertyColor color)
	{
		for (int i = 0 ; i < Math.min(color.getMaxProperties(), 4) ; i++)
		{
			TextPainter tp = new TextPainter(color.getRent(i + 1) + "M", GraphicsUtils.getBoldMDFont(scale(7)), new Rectangle(0, 0, scale(14), scale(9)));
			tp.setHorizontalAlignment(Alignment.CENTER);
			tp.setVerticalAlignment(Alignment.CENTER);
			tp.paint(g);
			g.translate(0, scale(11));
		}
	}
}
