package oldmana.md.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.common.Message;
import oldmana.md.net.packet.server.PacketCardDescription;
import oldmana.md.net.packet.server.PacketDestroyButton;
import oldmana.md.net.packet.server.PacketDestroyCardCollection;
import oldmana.md.net.packet.server.PacketHandshake;
import oldmana.md.net.packet.server.PacketKick;
import oldmana.md.net.packet.server.PacketPlayerInfo;
import oldmana.md.net.packet.server.PacketUpdatePlayer;
import oldmana.md.net.packet.server.PacketRefresh;
import oldmana.md.net.packet.server.PacketStatus;
import oldmana.md.net.packet.server.PacketUndoCardStatus;
import oldmana.md.net.packet.server.actionstate.PacketActionStatePlayerTurn.TurnState;
import oldmana.md.net.packet.universal.PacketChat;
import oldmana.md.server.card.CardBuilding;
import oldmana.md.server.event.UndoCardEvent;
import oldmana.md.server.playerui.ClientButton;
import oldmana.md.server.playerui.PlayerButton;
import oldmana.md.server.playerui.PlayerButton.ButtonTag;
import oldmana.md.server.ai.BasicAI;
import oldmana.md.server.ai.PlayerAI;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.Card.CardDescription;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.PropertyColor;
import oldmana.md.server.card.collection.Bank;
import oldmana.md.server.card.collection.CardCollection;
import oldmana.md.server.card.collection.Hand;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.event.CardDiscardEvent;
import oldmana.md.server.event.DeckDrawEvent;
import oldmana.md.server.event.PostActionCardPlayedEvent;
import oldmana.md.server.event.PostCardBankedEvent;
import oldmana.md.server.event.PreActionCardPlayedEvent;
import oldmana.md.server.event.PreCardBankedEvent;
import oldmana.md.server.net.ConnectionThread;
import oldmana.md.server.playerui.clientbutton.MultiButton;
import oldmana.md.server.playerui.clientbutton.UndoButton;
import oldmana.md.server.rules.DrawExtraCardsPolicy;
import oldmana.md.server.state.GameState;
import oldmana.md.server.state.primary.ActionStatePlayerTurn;
import oldmana.md.server.status.StatusEffect;

public class Player extends Client implements CommandSender
{
	private static int nextID;
	
	private MDServer server;
	
	private UUID uuid;
	
	private int id;
	
	private boolean op;
	
	private String name;
	
	private Hand hand;
	private Bank bank;
	private List<PropertySet> propertySets;
	
	private List<Card> revocableCards = new ArrayList<Card>();
	
	private List<StatusEffect> statusEffects = new ArrayList<StatusEffect>();
	
	private List<ClientButton> clientButtons = new ArrayList<ClientButton>();
	private Map<Player, List<PlayerButton>> playerButtons = new HashMap<Player, List<PlayerButton>>();
	
	private boolean online = false;
	private int lastPing;
	private boolean sentPing = false;
	
	private boolean bot;
	private PlayerAI ai;
	
	public Player(MDServer server, UUID uuid, ConnectionThread net, String name, boolean op)
	{
		super(net);
		this.server = server;
		this.uuid = uuid;
		this.name = name;
		this.op = op;
		id = nextID++;
		
		server.broadcastPacket(new PacketPlayerInfo(getID(), getName(), true), this);
		hand = new Hand(this);
		bank = new Bank(this);
		propertySets = new ArrayList<PropertySet>();
		
		lastPing = server.getTickCount();
		
		clientButtons.add(new MultiButton());
		clientButtons.add(new UndoButton());
		
		setAI(new BasicAI(this));
	}
	
	/**
	 * Bot constructor
	 */
	public Player(MDServer server, String name)
	{
		this(server, UUID.randomUUID(), null, name, false);
		bot = true;
	}
	
	public UUID getUUID()
	{
		return uuid;
	}
	
	public int getID()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
		updatePlayer();
	}
	
	/**
	 * Returns a description of the player formatted like: "{Name} (ID: {Id})"
	 * @return The description of the player
	 */
	public String getDescription()
	{
		return name + " (ID: " + id + ")";
	}
	
	public boolean canRevokeCard()
	{
		return !revocableCards.isEmpty();
	}
	
	public Card getLastRevocableCard()
	{
		return revocableCards.get(revocableCards.size() - 1);
	}
	
	public void addRevocableCard(Card card)
	{
		revocableCards.add(card);
		sendPacket(new PacketUndoCardStatus(card.getID()));
	}
	
	public Card popRevocableCard()
	{
		if (!revocableCards.isEmpty())
		{
			Card card = getLastRevocableCard();
			revocableCards.remove(card);
			sendUndoStatus();
			return card;
		}
		return null;
	}
	
	public void sendUndoStatus()
	{
		if (canRevokeCard())
		{
			sendPacket(new PacketUndoCardStatus(getLastRevocableCard().getID()));
		}
		else
		{
			sendPacket(new PacketUndoCardStatus(-1));
		}
	}
	
	/**
	 * Undo the last revocable card the player played. Does nothing if there are no revocable cards.
	 * @return The card that was undone, or null if no card was undone
	 */
	public Card undoCard()
	{
		if (!canRevokeCard())
		{
			sendUndoStatus();
			return null;
		}
		Card card = getLastRevocableCard();
		UndoCardEvent event = new UndoCardEvent(this, card);
		server.getEventManager().callEvent(event);
		if (event.isCancelled())
		{
			sendUndoStatus();
			return null;
		}
		popRevocableCard();
		card.transfer(getHand());
		server.getGameState().undoCard(card);
		System.out.println(getName() + " undos " + card.getName());
		return card;
	}
	
	public void clearRevocableCards()
	{
		revocableCards.clear();
		sendPacket(new PacketUndoCardStatus(-1));
	}
	
	public boolean isOnline()
	{
		return online;
	}
	
	public void setOnline(boolean online)
	{
		this.online = online;
		setSentPing(false);
		if (online)
		{
			setLastPing(getServer().getTickCount());
		}
		else
		{
			if (getNet() != null)
			{
				getNet().close();
				setNet(null);
			}
		}
		updatePlayer();
	}
	
	public int getLastPing()
	{
		return lastPing;
	}
	
	public void setLastPing(int lastPing)
	{
		this.lastPing = lastPing;
	}
	
	public boolean hasSentPing()
	{
		return sentPing;
	}
	
	public void setSentPing(boolean sent)
	{
		sentPing = sent;
	}
	
	public Hand getHand()
	{
		return hand;
	}
	
	public Bank getBank()
	{
		return bank;
	}
	
	public List<PropertySet> getPropertySets()
	{
		return propertySets;
	}
	
	public List<PropertySet> getPropertySets(boolean copy)
	{
		if (!copy)
		{
			return getPropertySets();
		}
		return new ArrayList<PropertySet>(getPropertySets());
	}
	
	public PropertySet getPropertySetById(int id)
	{
		for (PropertySet set : propertySets)
		{
			if (set.getID() == id)
			{
				return set;
			}
		}
		return null;
	}
	
	public List<CardProperty> getAllPropertyCards()
	{
		List<CardProperty> cards = new ArrayList<CardProperty>();
		getPropertySets().forEach((set) -> cards.addAll(set.getPropertyCards()));
		return cards;
	}
	
	public List<Card> getAllTableCards()
	{
		List<Card> cards = new ArrayList<Card>(getBank().getCards());
		getPropertySets().forEach((set) -> cards.addAll(set.getCards()));
		return cards;
	}
	
	public List<Card> getAllCards()
	{
		List<Card> cards = new ArrayList<Card>();
		cards.addAll(getHand().getCards());
		cards.addAll(getBank().getCards());
		getPropertySets().forEach((set) -> cards.addAll(set.getCards()));
		return cards;
	}
	
	public void safelyGrantProperty(CardProperty property)
	{
		safelyGrantProperty(property, 1);
	}
	
	public void safelyGrantProperty(CardProperty property, double time)
	{
		if (property.isSingleColor() && hasSolidPropertySet(property.getColor()))
		{
			PropertySet set = getSolidPropertySet(property.getColor());
			property.transfer(set, -1, time);
			set.checkLegality();
		}
		else
		{
			PropertySet set = createPropertySet();
			property.transfer(set, -1, time);
		}
	}
	
	public boolean transferPropertyCard(CardProperty card, PropertySet set)
	{
		List<PropertyColor> compatible = set.getPossibleColors();
		compatible.retainAll(card.getColors());
		if (!compatible.isEmpty())
		{
			PropertySet prevSet = (PropertySet) card.getOwningCollection();
			prevSet.transferCard(card, set);
			if (prevSet.getCardCount() == 0)
			{
				destroyPropertySet(prevSet);
			}
			return true;
		}
		return false;
	}
	
	public PropertySet transferPropertyCardNewSet(CardProperty card)
	{
		PropertySet prevSet = (PropertySet) card.getOwningCollection();
		PropertySet set = createPropertySet();
		prevSet.transferCard(card, set);
		if (prevSet.getCardCount() == 0)
		{
			destroyPropertySet(prevSet);
		}
		return set;
	}
	
	public PropertySet createPropertySet()
	{
		PropertySet set = new PropertySet(this);
		addPropertySet(set);
		return set;
	}
	
	public void destroyPropertySet(PropertySet set)
	{
		propertySets.remove(set);
		CardCollection.unregisterCardCollection(set);
		server.broadcastPacket(new PacketDestroyCardCollection(set.getID()));
	}
	
	private void addPropertySet(PropertySet set)
	{
		propertySets.add(set);
	}
	
	public void transferPropertySet(PropertySet set, Player player)
	{
		propertySets.remove(set);
		player.addPropertySet(set);
		set.setOwner(this);
	}
	
	public boolean hasSolidPropertySet(PropertyColor color)
	{
		return getSolidPropertySet(color) != null;
	}
	
	public PropertySet getSolidPropertySet(PropertyColor color)
	{
		for (PropertySet set : getPropertySets(true))
		{
			if (set.getEffectiveColor() == color && set.hasSingleColorProperty())
			{
				return set;
			}
		}
		return null;
	}
	
	public boolean hasRentableProperties(PropertyColor color)
	{
		for (PropertySet set : propertySets)
		{
			for (CardProperty card : set.getPropertyCards())
			{
				for (PropertyColor cardColor : card.getColors())
				{
					if (cardColor == color && card.isBase())
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean hasRentableProperties(List<PropertyColor> colors)
	{
		for (PropertyColor color : colors)
		{
			if (hasRentableProperties(color))
			{
				return true;
			}
		}
		return false;
	}
	
	public int getHighestValueRent(List<PropertyColor> colors)
	{
		Map<PropertyColor, Integer> colorCounts = new HashMap<PropertyColor, Integer>();
		List<PropertyColor> hasBaseColor = new ArrayList<PropertyColor>();
		for (PropertyColor color : colors)
		{
			colorCounts.put(color, 0);
		}
		for (PropertySet set : propertySets)
		{
			if (!set.hasBuildings())
			{
				for (CardProperty card : set.getPropertyCards())
				{
					for (PropertyColor color : card.getColors())
					{
						if (colorCounts.containsKey(color))
						{
							colorCounts.put(color, colorCounts.get(color) + 1);
							if (card.isBase() && !hasBaseColor.contains(color))
							{
								hasBaseColor.add(color);
							}
						}
					}
				}
			}
		}
		int highestValue = -1;
		for (Entry<PropertyColor, Integer> entry : colorCounts.entrySet())
		{
			if (hasBaseColor.contains(entry.getKey()))
			{
				highestValue = Math.max(highestValue, entry.getKey().getRent(Math.min(entry.getValue(), entry.getKey().getMaxProperties())));
			}
		}
		for (PropertySet set : propertySets)
		{
			PropertyColor color = set.getEffectiveColor();
			if (colorCounts.containsKey(color) && set.isMonopoly() && set.hasBuildings())
			{
				highestValue = Math.max(highestValue, color.getRent(color.getMaxProperties()) + set.getBuildingRentAddition());
			}
		}
		return highestValue;
	}
	
	public boolean hasAnyMonetaryAssets()
	{
		if (!getBank().isEmpty())
		{
			return true;
		}
		for (PropertySet set : getPropertySets())
		{
			for (CardProperty card : set.getPropertyCards())
			{
				if (card.getValue() > 0)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public int getTotalMonetaryAssets()
	{
		int wealth = 0;
		wealth += getBank().getTotalValue();
		for (PropertySet set : getPropertySets())
		{
			for (Card card : set.getCards())
			{
				wealth += card.getValue();
			}
		}
		return wealth;
	}
	
	public int getTotalPropertyValue()
	{
		int propValue = 0;
		for (CardProperty prop : getAllPropertyCards())
		{
			propValue += prop.getValue();
		}
		return propValue;
	}
	
	public int getMonopolyCount()
	{
		int monopolyCount = 0;
		for (PropertySet set : propertySets)
		{
			if (set.isMonopoly())
			{
				monopolyCount++;
			}
		}
		return monopolyCount;
	}
	
	public int getUniqueMonopolyCount()
	{
		List<PropertyColor> monopolyColors = new ArrayList<PropertyColor>();
		for (PropertySet set : propertySets)
		{
			if (set.isMonopoly() && !monopolyColors.contains(set.getEffectiveColor()))
			{
				monopolyColors.add(set.getEffectiveColor());
			}
		}
		return monopolyColors.size();
	}
	
	/**
	 * Draws extra cards if the hand is empty and if the draw policy permits extra cards to be drawn.
	 * This should be called after an action state is plausibly changed.
	 * @return True if extra cards were drawn
	 */
	public boolean checkEmptyHand()
	{
		if (getHand().getCardCount() == 0)
		{
			DrawExtraCardsPolicy policy = getServer().getGameRules().getDrawExtraCardsPolicy();
			if (policy == DrawExtraCardsPolicy.IMMEDIATELY || (policy == DrawExtraCardsPolicy.IMMEDIATELY_AFTER_ACTION
					&& getServer().getGameState().getActionState() instanceof ActionStatePlayerTurn))
			{
				clearRevocableCards();
				server.getDeck().drawCards(this, getServer().getGameRules().getExtraCardsDrawn(), 0.8);
				return true;
			}
		}
		return false;
	}
	
	/**Get all status effects currently applied to the player
	 * 
	 * @return A copy of the list of status effects currently on the player
	 */
	public List<StatusEffect> getStatusEffects()
	{
		return new ArrayList<StatusEffect>(statusEffects);
	}
	
	/**Adds a status effect to the player
	 * 
	 * @param effect - The effect to add
	 */
	public void addStatusEffect(StatusEffect effect)
	{
		server.getEventManager().registerEvents(effect);
		statusEffects.add(effect);
	}
	
	/**Removes the status effect instance from the player
	 * 
	 * @param effect - The effect to remove
	 */
	public void removeStatusEffect(StatusEffect effect)
	{
		server.getEventManager().unregisterEvents(effect);
		statusEffects.remove(effect);
	}
	
	/**Removes all instances of a status effect type from the player
	 * 
	 * @param clazz - The type to remove
	 */
	public void removeStatusEffect(Class<? extends StatusEffect> clazz)
	{
		Iterator<StatusEffect> it = statusEffects.iterator();
		while (it.hasNext())
		{
			StatusEffect effect = it.next();
			if (effect.getClass() == clazz)
			{
				server.getEventManager().unregisterEvents(effect);
				it.remove();
			}
		}
	}
	
	public void removeStatusEffect(int index)
	{
		removeStatusEffect(statusEffects.get(index));
	}
	
	/**Gets a status effect by the type. There's no guarantee that there aren't multiple instances of a type.
	 * 
	 * @param clazz - The type to get
	 * @return The status effect
	 */
	@SuppressWarnings("unchecked")
	public <T extends StatusEffect> T getStatusEffect(Class<T> clazz)
	{
		for (StatusEffect effect : statusEffects)
		{
			if (effect.getClass() == clazz)
			{
				return (T) effect;
			}
		}
		return null;
	}
	
	public StatusEffect getStatusEffect(int index)
	{
		return statusEffects.get(index);
	}
	
	/**Check if the player has at least one instance of a given type.
	 * 
	 * @param clazz - The type to check
	 * @return Whether or not the player has one instance of the type
	 */
	public boolean hasStatusEffect(Class<? extends StatusEffect> clazz)
	{
		return getStatusEffect(clazz) != null;
	}
	
	public void clearStatusEffects()
	{
		Iterator<StatusEffect> it = statusEffects.iterator();
		while (it.hasNext())
		{
			StatusEffect effect = it.next();
			server.getEventManager().unregisterEvents(effect);
			it.remove();
		}
	}
	
	/**
	 * Register a button that this player will see on the provided Player.
	 * @param button The button to register
	 * @param view The player the button will be shown on
	 */
	public void registerButton(PlayerButton button, Player view)
	{
		getButtonsFor(view).add(button);
		button.setOwner(this);
		button.setView(view);
		button.sendUpdate();
	}
	
	public void removeButton(PlayerButton button)
	{
		getButtonsFor(button.getView()).remove(button);
		sendPacket(new PacketDestroyButton(button.getID()));
	}
	
	public List<PlayerButton> getButtonsFor(Player view)
	{
		return playerButtons.computeIfAbsent(view, key -> new ArrayList<PlayerButton>());
	}
	
	public PlayerButton getButton(int id)
	{
		for (List<PlayerButton> view : playerButtons.values())
		{
			for (PlayerButton button : view)
			{
				if (button.getID() == id)
				{
					return button;
				}
			}
		}
		return null;
	}
	
	public PlayerButton getButton(Player view, ButtonTag tag)
	{
		if (tag == null)
		{
			throw new IllegalArgumentException("Tag cannot be null.");
		}
		for (PlayerButton button : getButtonsFor(view))
		{
			if (button.getTag() == tag)
			{
				return button;
			}
		}
		return null;
	}
	
	/**
	 * Button tags do not have guaranteed unique names, so this method of getting buttons is discouraged.
	 */
	public PlayerButton getButton(Player view, String tagName)
	{
		for (PlayerButton button : getButtonsFor(view))
		{
			if (button.getTag() != null && button.getTag().getName().equals(tagName))
			{
				return button;
			}
		}
		return null;
	}
	
	public boolean hasButton(Player view, ButtonTag tag)
	{
		return getButton(view, tag) != null;
	}
	
	protected void removeButtons(Player player)
	{
		if (playerButtons.containsKey(player))
		{
			for (PlayerButton button : playerButtons.get(player))
			{
				sendPacket(new PacketDestroyButton(button.getID()));
			}
			playerButtons.remove(player);
		}
	}
	
	public void clearAllButtons()
	{
		for (PlayerButton button : getAllButtons())
		{
			button.remove();
		}
	}
	
	public void sendButtonPackets()
	{
		for (PlayerButton button : getAllButtons())
		{
			button.sendUpdate();
		}
	}
	
	public List<PlayerButton> getAllButtons()
	{
		List<PlayerButton> all = new ArrayList<PlayerButton>();
		for (List<PlayerButton> view : playerButtons.values())
		{
			all.addAll(view);
		}
		return all;
	}
	
	public void resendCardButtons()
	{
		getHand().resendCardButtons();
	}
	
	/**
	 * This returns true when it's the player's turn and there's no pending action states.
	 * @return Whether the player is being focused on
	 */
	public boolean isFocused()
	{
		GameState gs = getServer().getGameState();
		return gs.hasTurnState() && gs.getActionState() == gs.getTurnState() && gs.getTurnState().getActionOwner() == this;
	}
	
	/**
	 * This returns true when it's the player's turn and the current state allows playing cards.
	 * @return Whether the player can play cards
	 */
	public boolean canPlayCards()
	{
		return isFocused() && getServer().getGameState().getTurnState().canPlayCards();
	}
	
	public boolean canDraw()
	{
		return isFocused() && getServer().getGameState().getTurnState().isDrawing();
	}
	
	public boolean isDiscarding()
	{
		return isFocused() && getServer().getGameState().getTurnState().getTurnState() == TurnState.DISCARD;
	}
	
	public void draw()
	{
		if (!canDraw())
		{
			System.out.println(getName() + " tried to draw without being able to!");
			return;
		}
		int cardsToDraw = server.getGameRules().getCardsDrawnPerTurn();
		if (getServer().getGameRules().getDrawExtraCardsPolicy() == DrawExtraCardsPolicy.NEXT_DRAW &&
				getHand().getCardCount() == 0)
		{
			cardsToDraw = server.getGameRules().getExtraCardsDrawn();
		}
		server.getDeck().drawCards(this, cardsToDraw);
		server.getGameState().setDrawn();
		server.getEventManager().callEvent(new DeckDrawEvent(this));
	}
	
	public void playCardBank(Card card)
	{
		PreCardBankedEvent preEvent = new PreCardBankedEvent(this, card);
		server.getEventManager().callEvent(preEvent);
		if (!preEvent.isCancelled())
		{
			getHand().transferCard(card, getBank());
			if (server.getGameRules().isUndoAllowed())
			{
				addRevocableCard(card);
			}
			PostCardBankedEvent postEvent = new PostCardBankedEvent(this, card);
			server.getEventManager().callEvent(postEvent);
			System.out.println(getName() + " banks " + card.getName());
			
			server.getGameState().decrementMoves();
			checkEmptyHand();
		}
		else
		{
			resendActionState();
		}
	}
	
	public void playCardProperty(CardProperty property, int setID)
	{
		if (setID > -1)
		{
			PropertySet set = getPropertySetById(setID);
			getHand().transferCard(property, set);
		}
		else
		{
			if (property.isSingleColor() && hasSolidPropertySet(property.getColor()))
			{
				PropertySet set = getSolidPropertySet(property.getColor());
				property.transfer(set);
				set.checkLegality();
			}
			else
			{
				PropertySet set = createPropertySet();
				getHand().transferCard(property, set);
			}
		}
		if (server.getGameRules().isUndoAllowed())
		{
			addRevocableCard(property);
		}
		
		server.getGameState().decrementMoves();
		
		System.out.println(getName() + " plays property " + property.getName());
		if (server.getGameState().checkWin())
		{
			return;
		}
		checkEmptyHand();
	}
	
	public void playCardProperty(CardProperty property, PropertySet set)
	{
		playCardProperty(property, set == null ? -1 : set.getID());
	}
	
	public void playCardAction(CardAction card)
	{
		playCardAction(card, false);
	}
	
	/**
	 * A sneaky play will bypass turn checks and not consume a turn.
	 */
	public void playCardAction(CardAction card, boolean sneaky)
	{
		if (!sneaky && !canPlayCards())
		{
			System.out.println("Warning: " + getName() + " tried to play " + card.getName() +
					" (ID: " + card.getID() + ") without being able to!");
			resendActionState();
			return;
		}
		
		PreActionCardPlayedEvent event = new PreActionCardPlayedEvent(this, card);
		server.getEventManager().callEvent(event);
		if (event.isCancelled())
		{
			return;
		}
		card.transfer(server.getDiscardPile(), -1, card.getPlayAnimation());
		if (!sneaky)
		{
			if (card.clearsRevocableCards())
			{
				clearRevocableCards();
			}
			if (card.isRevocable() && server.getGameRules().isUndoAllowed())
			{
				addRevocableCard(card);
			}
			server.getGameState().decrementMoves();
		}
		card.playCard(this);
		
		System.out.println(getName() + " plays action card " + card.getName());
		
		server.getEventManager().callEvent(new PostActionCardPlayedEvent(this, card));
		
		checkEmptyHand();
	}
	
	public void playCardBuilding(CardBuilding building, PropertySet set)
	{
		building.transfer(set);
		
		if (server.getGameRules().isUndoAllowed())
		{
			addRevocableCard(building);
		}
		
		server.getGameState().decrementMoves();
		
		checkEmptyHand();
	}
	
	public boolean discard(Card card)
	{
		if (getHand().hasCard(card) && card.onDiscard(this))
		{
			card.transfer(server.getDiscardPile());
			server.getGameState().updateTurnState();
			server.getEventManager().callEvent(new CardDiscardEvent(this, card));
			return true;
		}
		return false;
	}
	
	public void endTurn()
	{
		server.getGameState().nextTurn();
	}
	
	public void executeCommand(String raw)
	{
		server.getCommandHandler().executeCommand(this, raw);
	}
	
	public void chat(String msg)
	{
		server.broadcastMessage(getName() + ": " + msg, true);
	}
	
	public void resendActionState()
	{
		server.getGameState().resendActionState(this);
	}
	
	public void resendTurnState()
	{
		server.getGameState().resendTurnState(this);
	}
	
	public Packet[] getPropertySetPackets()
	{
		Packet[] packets = new Packet[propertySets.size()];
		for (int i = 0 ; i < packets.length ; i++)
		{
			packets[i] = propertySets.get(i).getCollectionDataPacket();
		}
		return packets;
	}
	
	public Packet getInfoPacket()
	{
		return new PacketPlayerInfo(getID(), getName(), isConnected() || isBot());
	}
	
	@Override
	public void sendMessage(String message)
	{
		sendPacket(new PacketChat(MessageBuilder.fromSimple(message)));
	}
	
	@Override
	public void sendMessage(Message message)
	{
		sendPacket(new PacketChat(message));
	}
	
	@Override
	public boolean isOp()
	{
		return op;
	}
	
	public void setOp(boolean op)
	{
		this.op = op;
	}
	
	/**
	 * Closes the connection with the player. This does not remove them from the game.
	 */
	public void disconnect()
	{
		disconnect("Disconnected by server");
	}
	
	/**
	 * Closes the connection with the player for a given reason. This does not remove them from the game.
	 * 
	 * @param reason - The message displayed on the client when disconnected
	 */
	public void disconnect(String reason)
	{
		sendPacket(new PacketKick(reason));
		setOnline(false);
		System.out.println("Player " + getName() + " (ID: " + getID() + ") was disconnected for '" + reason + "'");
	}
	
	public boolean isBot()
	{
		return bot;
	}
	
	public void setBot(boolean bot)
	{
		this.bot = bot;
		updatePlayer();
	}
	
	public void setAI(PlayerAI ai)
	{
		this.ai = ai;
	}
	
	public PlayerAI getAI()
	{
		return ai;
	}
	
	public void doAIAction()
	{
		ai.doAction();
	}
	
	private void updatePlayer()
	{
		getServer().broadcastPacket(new PacketUpdatePlayer(getID(), getName(), isOnline() || isBot()));
	}
	
	/**
	 * Resend all data to the client.
	 */
	public void refresh()
	{
		sendPacket(new PacketRefresh());
		sendPacket(new PacketHandshake(getID(), getName()));
		sendPacket(new PacketStatus("Loading.."));
		for (Player other : server.getPlayersExcluding(this))
		{
			sendPacket(other.getInfoPacket());
		}
		
		// Send card descriptions
		for (CardDescription desc : CardDescription.getAllDescriptions())
		{
			sendPacket(new PacketCardDescription(desc.getID(), desc.getText()));
		}
		
		// Send card colors
		sendPacket(PropertyColor.getColorsPacket());
		
		// Send all card data
		for (Card card : Card.getRegisteredCards().values())
		{
			sendPacket(card.getCardDataPacket());
		}
		// Send void
		sendPacket(getServer().getVoidCollection().getCollectionDataPacket());
		// Send deck
		sendPacket(getServer().getDeck().getCollectionDataPacket());
		// Send discard pile
		sendPacket(getServer().getDiscardPile().getCollectionDataPacket());
		// Send player data (including own)
		for (Player other : getServer().getPlayers())
		{
			for (Packet packet : other.getPropertySetPackets())
			{
				sendPacket(packet);
			}
			sendPacket(other.getBank().getCollectionDataPacket());
			sendPacket(this == other ? other.getHand().getOwnerHandDataPacket() : other.getHand().getCollectionDataPacket());
		}
		sendPacket(getServer().getGameRules().constructPacket());
		getServer().getGameState().getTurnOrder().sendOrder(this);
		resendActionState();
		resendTurnState();
		resendCardButtons();
		sendButtonPackets();
		sendUndoStatus();
		server.getGameState().sendStatus(this);
	}
	
	protected MDServer getServer()
	{
		return server;
	}
}
