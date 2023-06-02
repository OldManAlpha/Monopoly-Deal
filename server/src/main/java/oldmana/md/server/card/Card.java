package oldmana.md.server.card;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.common.card.CardAnimationType;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.card.collection.CardCollection;
import oldmana.md.server.card.control.CardControls;
import oldmana.md.server.card.control.CardButton;
import oldmana.md.server.rules.DiscardOrderPolicy;

public abstract class Card
{
	private int id;
	
	private CardCollection collection;
	
	private CardType<?> type;
	private CardTemplate template;
	
	private int value;
	private String name;
	
	private String[] displayName;
	private int fontSize = 8;
	private int displayOffsetY = 0;
	private CardDescription description;
	
	private boolean revocable = true;
	private boolean clearsRevocableCards = false;
	
	private CardControls controls;
	
	public Card()
	{
		id = getServer().nextCardID();
		register(this);
		controls = createControls();
	}
	
	public void applyTemplate(CardTemplate template)
	{
		this.template = template.clone();
		value = template.getInt("value");
		name = template.getString("name");
		displayName = template.getStringArray("displayName");
		fontSize = template.getInt("fontSize");
		displayOffsetY = template.getInt("displayOffsetY");
		description = CardDescription.getDescription(template.getStringArray("description"));
		revocable = template.getBoolean("revocable");
		clearsRevocableCards = template.getBoolean("clearsRevocableCards");
	}
	
	public CardTemplate getTemplate()
	{
		return template;
	}
	
	public CardControls createControls()
	{
		CardButton discard = new CardButton("Discard", CardButton.CENTER);
		discard.setCondition((player, card) -> player.isDiscarding() && card.canDiscard(player));
		discard.setListener((player, card, data) -> player.discard(card));
		return new CardControls(this, discard);
	}
	
	public CardControls getControls()
	{
		return controls;
	}
	
	public void updateButtons()
	{
		controls.updateButtons();
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
	
	public void setValue(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public void setName(String name)
	{
		if (displayName == null)
		{
			displayName = new String[] {name.toUpperCase()};
		}
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setRevocable(boolean revocable)
	{
		this.revocable = revocable;
	}
	
	public boolean isRevocable()
	{
		return revocable;
	}
	
	public void setClearsRevocableCards(boolean clears)
	{
		clearsRevocableCards = clears;
	}
	
	public boolean clearsRevocableCards()
	{
		return clearsRevocableCards;
	}
	
	public void setDisplayName(String... displayName)
	{
		this.displayName = displayName;
	}
	
	public String[] getDisplayName()
	{
		return displayName;
	}
	
	public void setFontSize(int fontSize)
	{
		this.fontSize = fontSize;
	}
	
	public int getFontSize()
	{
		return fontSize;
	}
	
	public void setDisplayOffsetY(int offset)
	{
		this.displayOffsetY = offset;
	}
	
	public int getDisplayOffsetY()
	{
		return displayOffsetY;
	}
	
	public void setDescription(String... description)
	{
		CardDescription desc = CardDescription.getDescriptionByText(description);
		if (desc == null)
		{
			desc = new CardDescription(description);
		}
		this.description = desc;
	}
	
	public void setDescription(CardDescription description)
	{
		this.description = description;
	}
	
	public CardDescription getDescription()
	{
		return description;
	}
	
	public void transfer(CardCollection to)
	{
		transfer(to, -1);
	}
	
	public void transfer(CardCollection to, int index)
	{
		transfer(to, index, 1);
	}
	
	public void transfer(CardCollection to, int index, double time)
	{
		transfer(to, index, time, false);
	}
	
	public void transfer(CardCollection to, int index, CardAnimationType anim)
	{
		if (collection != null)
		{
			collection.transferCard(this, to, index, anim);
		}
	}
	
	public void transfer(CardCollection to, int index, CardAnimationType anim, boolean flash)
	{
		if (collection != null)
		{
			collection.transferCard(this, to, index, anim, flash);
		}
	}
	
	public void transfer(CardCollection to, int index, double time, CardAnimationType anim)
	{
		if (collection != null)
		{
			collection.transferCard(this, to, index, time, anim);
		}
	}
	
	public void transfer(CardCollection to, int index, double time, boolean flash)
	{
		if (collection != null)
		{
			collection.transferCard(this, to, index, time, flash);
		}
	}
	
	public void transfer(CardCollection to, int index, double time, CardAnimationType anim, boolean flash)
	{
		if (collection != null)
		{
			collection.transferCard(this, to, index, time, anim, flash);
		}
	}
	
	/**
	 * Reveals the card to all players using the IMPORTANT animation.
	 */
	public void flash()
	{
		flash(2.5);
	}
	
	/**
	 * Reveals the card to all players using the IMPORTANT animation.
	 * @param time The time at which to animate the card
	 */
	public void flash(double time)
	{
		flash(time, CardAnimationType.IMPORTANT);
	}
	
	/**
	 * Reveals the card to all players using the provided animation.
	 * @param time The time at which to animate the card
	 * @param anim The animation type for the flash
	 */
	public void flash(double time, CardAnimationType anim)
	{
		if (collection != null)
		{
			collection.flashCard(this, time, anim);
		}
	}
	
	
	public CardAnimationType getPlayAnimation()
	{
		return CardAnimationType.NORMAL;
	}
	
	/**
	 * Check to see if this card can be discarded by the player. By default, this checks other cards in the hand to
	 * see if an exception should be made.
	 * @param player The player to potentially discard this card
	 * @return True if the player should be able to discard this card
	 */
	public boolean canDiscard(Player player)
	{
		DiscardOrderPolicy policy = getServer().getGameRules().getDiscardOrderPolicy();
		return policy.canDiscard(this) || policy.canIgnorePolicy(player.getHand().getCards());
	}
	
	/**
	 * Called right before a player discards this card.
	 * 
	 * @param player The player discarding this card
	 * @return Whether this card should be discarded
	 */
	public boolean onDiscard(Player player)
	{
		return true;
	}
	
	public CardType<?> getType()
	{
		return type;
	}
	
	public void setType(CardType<?> type)
	{
		this.type = type;
	}
	
	protected MDServer getServer()
	{
		return MDServer.getInstance();
	}
	
	public abstract Packet getCardDataPacket();
	
	@Override
	public String toString()
	{
		return getName() + " (" + getValue() + "M)";
	}
	
	
	private static CardType<Card> createType()
	{
		CardType<Card> type = new CardType<Card>(Card.class, "Card");
		type.setDefaultTemplate(new CardTemplate());
		return type;
	}
	
	
	public static Map<Integer, Card> getRegisteredCards()
	{
		return getCardsMap();
	}
	
	public static void register(Card card)
	{
		getCardsMap().put(card.getID(), card);
	}
	
	public static void unregister(Card card)
	{
		getCardsMap().remove(card.getID());
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
		return getCardsMap().get(id);
	}
	
	private static Map<Integer, Card> getCardsMap()
	{
		return MDServer.getInstance().getCards();
	}
}
