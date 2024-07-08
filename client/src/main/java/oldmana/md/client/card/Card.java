package oldmana.md.client.card;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oldmana.md.client.MDClient;
import oldmana.md.client.Player;
import oldmana.md.client.card.collection.CardCollection;
import oldmana.md.client.gui.component.MDCard;
import oldmana.md.client.gui.component.collection.MDHand;
import oldmana.md.client.gui.util.CardPainter;
import oldmana.md.client.gui.util.GraphicsUtils;

public class Card
{
	private static Map<Integer, Card> cards = new HashMap<Integer, Card>();
	
	
	private int id;
	
	private CardCollection collection;
	
	private int value;
	private String name;
	
	private String[] displayName;
	private int fontSize = 8;
	private int displayOffsetY;
	private CardDescription description;
	
	private Color outerColor;
	private Color innerColor;
	
	private List<CardButton> buttons = new ArrayList<CardButton>();
	
	private BufferedImage graphics;
	private Map<Double, BufferedImage> graphicsCache = new HashMap<Double, BufferedImage>();
	
	private static Map<Double, BufferedImage> backGraphicsCache = new HashMap<Double, BufferedImage>();
	
	public Card(int id, int value, String name)
	{
		this.id = id;
		this.value = value;
		this.name = name;
		registerCard(this);
	}
	
	public Card(int id, int value, String name, String[] displayName, int fontSize, int displayOffsetY, CardDescription description)
	{
		this(id, value, name);
		this.displayName = displayName;
		this.fontSize = fontSize;
		this.displayOffsetY = displayOffsetY;
		this.description = description;
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
		return collection != null && collection.getOwner() != null;
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
	
	public void setValue(int value)
	{
		this.value = value;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
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
	
	public void setDescription(CardDescription description)
	{
		this.description = description;
	}
	
	public CardDescription getDescription()
	{
		return description;
	}
	
	public void setOuterColor(Color outerColor)
	{
		this.outerColor = outerColor;
	}
	
	public void setInnerColor(Color innerColor)
	{
		this.innerColor = innerColor;
	}
	
	public List<CardButton> getButtons()
	{
		return buttons;
	}
	
	public void addButton(CardButton button)
	{
		buttons.add(button);
	}
	
	public void clearButtons()
	{
		buttons.clear();
		((MDHand) getClient().getThePlayer().getHand().getUI()).removeOverlay();
	}
	
	public BufferedImage getGraphics(double scale)
	{
		if (graphicsCache.containsKey(scale))
		{
			return graphicsCache.get(scale);
		}
		int width = (int) (60 * scale);//(int) Math.round(MDCard.CARD_SIZE.width * scale);
		int height = (int) (90 * scale);//(int) Math.round(MDCard.CARD_SIZE.height * scale);
		if (graphics == null)
		{
			graphics = GraphicsUtils.createImage(60 * 8, 90 * 8);
			CardPainter cp = new CardPainter(this, 8);
			Graphics g = graphics.createGraphics();
			cp.paint(g);
			g.dispose();
		}
		Image img = graphics.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage buffer = GraphicsUtils.createImage(width, height);
		Graphics g = buffer.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		graphicsCache.put(scale, buffer);
		return getGraphics(scale);
	}
	
	public void clearGraphicsCache()
	{
		if (graphics != null)
		{
			graphics.flush();
			graphics = null;
		}
		graphicsCache.values().forEach(Image::flush);
		graphicsCache.clear();
		
		if (getOwningCollection() != null)
		{
			getOwningCollection().getUI().updateGraphics();
		}
	}
	
	public static BufferedImage getBackGraphics(double scale)
	{
		int width = (int) Math.round(MDCard.CARD_SIZE.width * scale);
		int height = (int) Math.round(MDCard.CARD_SIZE.height * scale);
		if (backGraphicsCache.containsKey(scale))
		{
			return backGraphicsCache.get(scale);
		}
		BufferedImage img = GraphicsUtils.createImage(width, height);
		CardPainter cp = new CardPainter(null, scale);
		Graphics2D g = img.createGraphics();
		cp.paint(g);
		g.dispose();
		backGraphicsCache.put(scale, img);
		return getBackGraphics(scale);
	}
	
	public static BufferedImage getMysteryGraphics(double scale)
	{
		int width = (int) Math.round(MDCard.CARD_SIZE.width * scale);
		int height = (int) Math.round(MDCard.CARD_SIZE.height * scale);
		BufferedImage img = GraphicsUtils.createImage(width, height);
		CardPainter cp = new CardPainter(null, scale);
		cp.paintMystery(img.createGraphics());
		return img;
	}
	
	public Color getOuterColor()
	{
		return outerColor;
	}
	
	public Color getInnerColor()
	{
		return innerColor;
	}
	
	protected MDClient getClient()
	{
		return MDClient.getInstance();
	}
	
	
	public static Map<Integer, Card> getRegisteredCards()
	{
		return cards;
	}
	
	public static void registerCard(Card card)
	{
		cards.put(card.getID(), card);
	}
	
	public static List<CardProperty> getPropertyCards(int[] ids)
	{
		List<CardProperty> props = new ArrayList<CardProperty>();
		List<Card> cards = getCards(ids);
		for (Card card : cards)
		{
			props.add((CardProperty) card);
		}
		return props;
	}
	
	public static List<Card> getCards(int[] ids)
	{
		List<Card> cards = new ArrayList<Card>();
		for (int id : ids)
		{
			cards.add(getCard(id));
		}
		return cards;
	}
	
	public static Card getCard(int id)
	{
		return cards.get(id);
	}
	
	
	public static class CardDescription
	{
		private static Map<Integer, CardDescription> descriptionMap = new HashMap<Integer, CardDescription>();
		
		private int id;
		private String[] description;
		
		public CardDescription(int id, String... description)
		{
			this.id = id;
			this.description = description;
			descriptionMap.put(id, this);
		}
		
		public int getID()
		{
			return id;
		}
		
		public String[] getText()
		{
			return description;
		}
		
		public static CardDescription getDescriptionByID(int id)
		{
			return descriptionMap.get(id);
		}
	}
}
