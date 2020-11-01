package oldmana.general.md.client.card;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import oldmana.general.md.client.MDClient;
import oldmana.general.md.client.Player;
import oldmana.general.md.client.card.collection.CardCollection;
import oldmana.general.md.client.gui.component.MDCard;
import oldmana.general.md.client.gui.util.CardPainter;
import oldmana.general.md.client.gui.util.GraphicsUtils;

public class Card
{
	private int id;
	
	private CardCollection collection;
	
	private int value;
	private String name;
	
	private String[] displayName;
	private int fontSize = 8;
	private int displayOffsetY;
	
	private boolean revocable = true;
	private boolean marksPreviousCardsUnrevocable = false;
	
	private BufferedImage graphics;
	private Map<Double, Image> graphicsCache = new HashMap<Double, Image>();
	private static Map<Double, BufferedImage> backGraphicsCache = new HashMap<Double, BufferedImage>();
	
	public Card(int id, int value, String name)
	{
		this.id = id;
		this.value = value;
		this.name = name;
		CardRegistry.registerCard(this);
	}
	
	public Card(int id, int value, String name, String[] displayName, int fontSize, int displayOffsetY)
	{
		this(id, value, name);
		this.displayName = displayName;
		this.fontSize = fontSize;
		this.displayOffsetY = displayOffsetY;
	}
	
	public int getID()
	{
		return id;
	}
	
	public Player getOwner()
	{
		return collection.getOwner();
	}
	
	public boolean hasOwner()
	{
		return collection.getOwner() != null;
	}
	
	public void setOwningCollection(CardCollection collection)
	{
		this.collection = collection;
	}
	
	public CardCollection getOwningCollection()
	{
		return collection;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean isRevocable()
	{
		return revocable;
	}
	
	public boolean marksPreviousUnrevocable()
	{
		return marksPreviousCardsUnrevocable;
	}
	
	public String[] getDisplayName()
	{
		return displayName;
	}
	
	public void setDisplayName(String[] displayName)
	{
		this.displayName = displayName;
	}
	
	public int getFontSize()
	{
		return fontSize;
	}
	
	public void setFontSize(int fontSize)
	{
		this.fontSize = fontSize;
	}
	
	public int getDisplayOffsetY()
	{
		return displayOffsetY;
	}
	
	public void setDisplayOffsetY(int offset)
	{
		displayOffsetY = offset;
	}
	
	public Image getGraphics(double scale)
	{
		int width = (int) (60 * scale);//(int) Math.round(MDCard.CARD_SIZE.width * scale);
		int height = (int) (90 * scale);//(int) Math.round(MDCard.CARD_SIZE.height * scale);
		if (graphics == null)
		{
			graphics = GraphicsUtils.createImage(60 * 8, 90 * 8);
			CardPainter cp = new CardPainter(this, 8);
			cp.paint(graphics.createGraphics());
		}
		if (graphicsCache.containsKey(scale))
		{
			return graphicsCache.get(scale);
		}
		Image img = graphics.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		/*
		 * Image img = GraphicsUtils.createImage(width, height);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.drawImage(scaled, 0, 0, null);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setColor(Color.BLACK);
		g.drawRoundRect(0, 0, width - 1, height - 1, width / 6, width / 6);
		 */
		graphicsCache.put(scale, img);
		return getGraphics(scale);
	}
	
	public void clearGraphicsCache()
	{
		graphics = null;
		graphicsCache.clear();
	}
	
	public static BufferedImage getBackGraphics(double scale)
	{
		int width = (int) Math.round(MDCard.CARD_SIZE.width * scale);
		int height = (int) Math.round(MDCard.CARD_SIZE.height * scale);
		if (backGraphicsCache.containsKey(scale))
		{
			BufferedImage img = GraphicsUtils.createImage(width, height);
			Graphics2D g = img.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(backGraphicsCache.get(scale), 0, 0, null);
			return img;
		}
		BufferedImage img = GraphicsUtils.createImage(width, height);
		CardPainter cp = new CardPainter(null, scale);
		cp.paint(img.createGraphics());
		backGraphicsCache.put(scale, img);
		return getBackGraphics(scale);
	}
	
	public Color getValueColor()
	{
		return CardValueColor.getByValue(value).getColor();
	}
	
	public MDClient getClient()
	{
		return MDClient.getInstance();
	}
	
	public static enum CardType
	{
		MONEY(0), PROPERTY(1), ACTION(2), JUST_SAY_NO(3), DOUBLE_THE_RENT(4), SPECIAL(5);
		
		private int id;
		
		CardType(int id)
		{
			this.id = id;
		}
		
		public int getID()
		{
			return id;
		}
	}
	
	public static enum CardValueColor
	{
		ZERO(Color.LIGHT_GRAY), ONE(new Color(243, 237, 159)), TWO(new Color(237, 209, 178)), THREE(new Color(230, 242, 203)), FOUR(new Color(194, 224, 233)), 
		FIVE(new Color(193, 161, 203)), TEN(new Color(247, 210, 82)), GREATER_THAN_TEN(new Color(132, 240, 255)), OTHER(new Color(0, 200, 0));
		
		private Color color;
		
		CardValueColor(Color color)
		{
			this.color = color;
		}
		
		public Color getColor()
		{
			return color;
		}
		
		public static CardValueColor getByValue(int value)
		{
			if (value == 0)
			{
				return ZERO;
			}
			else if (value == 1)
			{
				return ONE;
			}
			else if (value == 2)
			{
				return TWO;
			}
			else if (value == 3)
			{
				return THREE;
			}
			else if (value == 4)
			{
				return FOUR;
			}
			else if (value == 5)
			{
				return FIVE;
			}
			else if (value == 10)
			{
				return TEN;
			}
			else if (value > 10)
			{
				return GREATER_THAN_TEN;
			}
			return OTHER;
			/*
			switch (value)
			{
				case 0:
					return ZERO;
				case 1:
					return ONE;
				case 2:
					return TWO;
				case 3:
					return THREE;
				case 4:
					return FOUR;
				case 5:
					return FIVE;
				case 10:
					return TEN;
				default:
					return OTHER;
			}
			*/
		}
	}
}
