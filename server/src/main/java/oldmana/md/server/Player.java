package oldmana.md.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.Message;
import oldmana.md.common.net.packet.server.PacketCardDescription;
import oldmana.md.common.net.packet.server.PacketDestroyButton;
import oldmana.md.common.net.packet.server.PacketDestroyCardCollection;
import oldmana.md.common.net.packet.server.PacketHandshake;
import oldmana.md.common.net.packet.server.PacketKick;
import oldmana.md.common.net.packet.server.PacketPlaySound;
import oldmana.md.common.net.packet.server.PacketPlayerInfo;
import oldmana.md.common.net.packet.server.PacketRemoveMessageCategory;
import oldmana.md.common.net.packet.server.PacketSelectCardCombo;
import oldmana.md.common.net.packet.server.PacketSetAwaitingResponse;
import oldmana.md.common.net.packet.server.PacketSetChatOpen;
import oldmana.md.common.net.packet.server.PacketUpdatePlayer;
import oldmana.md.common.net.packet.server.PacketRefresh;
import oldmana.md.common.net.packet.server.PacketStatus;
import oldmana.md.common.net.packet.server.PacketUndoCardStatus;
import oldmana.md.common.net.packet.server.actionstate.PacketActionStatePlayerTurn.TurnState;
import oldmana.md.common.net.packet.server.PacketMessage;
import oldmana.md.common.playerui.ChatAlignment;
import oldmana.md.server.event.EventListener;
import oldmana.md.server.event.player.PlayerPreDrawEvent;
import oldmana.md.server.history.UndoableAction;
import oldmana.md.server.event.player.PlayerUndoActionEvent;
import oldmana.md.server.net.Client;
import oldmana.md.server.net.NetClient.GracefulDisconnect;
import oldmana.md.server.playerui.ClientButton;
import oldmana.md.server.playerui.PlayerButton;
import oldmana.md.server.playerui.PlayerButton.ButtonTag;
import oldmana.md.server.ai.BasicAI;
import oldmana.md.server.ai.PlayerAI;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardDescription;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.PropertyColor;
import oldmana.md.server.card.collection.Bank;
import oldmana.md.server.card.collection.CardCollection;
import oldmana.md.server.card.collection.Hand;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.event.player.PlayerDrawEvent;
import oldmana.md.server.playerui.clientbutton.MultiButton;
import oldmana.md.server.playerui.clientbutton.UndoButton;
import oldmana.md.server.rules.DrawExtraCardsPolicy;
import oldmana.md.server.state.GameState;
import oldmana.md.server.state.primary.ActionStatePlayerTurn;

public class Player implements CommandSender
{
	private static int nextID;
	
	private Client client;
	
	private UUID uuid;
	
	private int id;
	
	private boolean op;
	
	private String name;
	
	private Hand hand;
	private Bank bank;
	private List<PropertySet> propertySets;
	
	// Declared as LinkedList so we have access to its methods
	private LinkedList<UndoableAction> undoableActions = new LinkedList<UndoableAction>();
	
	private List<StatusEffect> statusEffects = new ArrayList<StatusEffect>();
	
	private List<ClientButton> clientButtons = new ArrayList<ClientButton>();
	private Map<Player, List<PlayerButton>> playerButtons = new HashMap<Player, List<PlayerButton>>();
	
	private boolean online = false;
	private int lastPing;
	private boolean sentPing = false;
	
	private boolean bot;
	private PlayerAI ai;
	
	public Player(Client client, UUID uuid, String name, boolean op)
	{
		setClient(client);
		this.uuid = uuid;
		this.name = name;
		this.op = op;
		id = nextID++;
		
		getServer().broadcastPacket(new PacketPlayerInfo(getID(), getName(), true), this);
		hand = new Hand(this);
		bank = new Bank(this);
		propertySets = new ArrayList<PropertySet>();
		
		lastPing = getServer().getTickCount();
		
		clientButtons.add(new MultiButton());
		clientButtons.add(new UndoButton());
		
		setAI(new BasicAI(this));
	}
	
	/**
	 * Bot constructor
	 */
	public Player(String name)
	{
		this(null, UUID.randomUUID(), name, false);
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
	
	public boolean canUndoAction()
	{
		return !undoableActions.isEmpty();
	}
	
	public UndoableAction getLastUndoableAction()
	{
		return undoableActions.getLast();
	}
	
	public void addUndoableAction(UndoableAction action)
	{
		undoableActions.add(action);
		sendPacket(new PacketUndoCardStatus(action.getFace().getID()));
	}
	
	public UndoableAction popUndoableAction()
	{
		if (!undoableActions.isEmpty())
		{
			UndoableAction action = undoableActions.removeLast();
			sendUndoStatus();
			return action;
		}
		return null;
	}
	
	public void sendUndoStatus()
	{
		sendPacket(new PacketUndoCardStatus(canUndoAction() ? getLastUndoableAction().getFace().getID() : -1));
	}
	
	/**
	 * Undo the last undoable action the player played. Does nothing if there are no undoable actions.
	 * @return The action that was undone, or null if no action was undone
	 */
	public UndoableAction undoLastAction()
	{
		if (!canUndoAction())
		{
			sendUndoStatus();
			return null;
		}
		if (getGameState().isUndoing())
		{
			throw new IllegalStateException("Cannot undo concurrently");
		}
		UndoableAction action = getLastUndoableAction();
		PlayerUndoActionEvent event = new PlayerUndoActionEvent(this, action);
		getServer().getEventManager().callEvent(event);
		if (event.isCancelled())
		{
			sendUndoStatus();
			return null;
		}
		popUndoableAction();
		getGameState().onUndo(action);
		System.out.println(getName() + " undos " + action.getFace().getName());
		action.performUndo();
		clearAwaitingResponse();
		return action;
	}
	
	public void clearUndoableActions()
	{
		undoableActions.clear();
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
			closeConnection();
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
	
	public PropertySet getPropertySet(int id)
	{
		CardCollection collection = CardCollection.getByID(id);
		if (collection instanceof PropertySet && collection.getOwner() == this)
		{
			return (PropertySet) collection;
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
	
	public PropertySet createPropertySet()
	{
		PropertySet set = new PropertySet(this);
		addPropertySet(set);
		return set;
	}
	
	public void destroyPropertySet(PropertySet set)
	{
		propertySets.remove(set);
		CardCollection.unregister(set);
		getServer().broadcastPacket(new PacketDestroyCardCollection(set.getID()));
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
	
	/**
	 * Get the solid property set the player has for the given property color.
	 * @param color The property color
	 * @return The solid set associated with the color, or null if there isn't one
	 */
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
	
	/**
	 * Check if the player can rent on the given property color.
	 * @param color The color to check
	 * @return True if the player can rent on the property color
	 */
	public boolean hasRentableProperties(PropertyColor color)
	{
		for (PropertySet set : propertySets)
		{
			if (set.hasBuildings())
			{
				if (set.getEffectiveColor() == color)
				{
					return true;
				}
			}
			else
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
		}
		return false;
	}
	
	/**
	 * Check if the player can rent on any of the given property colors.
	 * @param colors The colors to check
	 * @return True if the player can rent on at least one of the property colors
	 */
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
	
	/**
	 * Get the highest rent value the player can charge from the provided property colors.
	 * @param colors The colors to check
	 * @return The highest value rent
	 */
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
	
	/**
	 * Check if the player has anything on their table that has value. The hand is not part of their table.
	 * @return True if the player has anything of value
	 */
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
	
	/**
	 * Get the total value of everything on the player's table. The hand is not part of their table.
	 * @return The total value
	 */
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
	
	/**
	 * Get the total value of the properties on the player's table.
	 * @return The total value
	 */
	public int getTotalPropertyValue()
	{
		int propValue = 0;
		for (CardProperty prop : getAllPropertyCards())
		{
			propValue += prop.getValue();
		}
		return propValue;
	}
	
	/**
	 * Get the number of completed property sets the player has.
	 * @return The number of non-unique-colored property sets
	 */
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
	
	/**
	 * Get the number of unique completed property sets the player has.
	 * @return The number of unique-colored property sets
	 */
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
	 */
	public void checkEmptyHand()
	{
		if (getHand().getCardCount() == 0)
		{
			DrawExtraCardsPolicy policy = getServer().getGameRules().getDrawExtraCardsPolicy();
			if (policy == DrawExtraCardsPolicy.IMMEDIATELY || (policy == DrawExtraCardsPolicy.IMMEDIATELY_AFTER_ACTION
					&& getGameState().getActionState() instanceof ActionStatePlayerTurn && !getGameState().isProcessingCards()))
			{
				drawExtraCards();
			}
		}
	}
	
	private void drawExtraCards()
	{
		int cardsToDraw = getServer().getGameRules().getExtraCardsDrawn();
		PlayerPreDrawEvent event = new PlayerPreDrawEvent(this, cardsToDraw, true);
		getServer().getEventManager().callEvent(event);
		if (event.isCancelled())
		{
			return;
		}
		clearUndoableActions();
		List<Card> cards = getServer().getDeck().drawCards(this, cardsToDraw, 0.8);
		getServer().getEventManager().callEvent(new PlayerDrawEvent(this, cards, true));
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
		effect.setPlayer(this);
		getServer().getEventManager().registerEvents(effect);
		statusEffects.add(effect);
		effect.onApply();
	}
	
	/**Removes the status effect instance from the player
	 * 
	 * @param effect - The effect to remove
	 */
	public void removeStatusEffect(StatusEffect effect)
	{
		getServer().getEventManager().unregisterEvents(effect);
		statusEffects.remove(effect);
		effect.onRemove();
	}
	
	/**Removes all instances of a status effect type from the player
	 * 
	 * @param clazz - The type to remove
	 */
	public void removeStatusEffect(Class<? extends StatusEffect> clazz)
	{
		for (StatusEffect effect : new ArrayList<StatusEffect>(statusEffects))
		{
			if (effect.getClass() == clazz)
			{
				removeStatusEffect(effect);
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
		for (StatusEffect effect : new ArrayList<StatusEffect>(statusEffects))
		{
			removeStatusEffect(effect);
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
	
	public void sendCardButtons()
	{
		getHand().sendCardButtons();
	}
	
	/**
	 * Prompts the player to select a card in their hand to combo with other cards. The player has the option to cancel
	 * and will <b>not</b> inform the server if they do so.
	 * @param currentCombo Cards already selected in this combo
	 * @param targets Cards that can be selected
	 */
	public void promptCardCombo(List<? extends Card> currentCombo, List<? extends Card> targets)
	{
		sendPacket(new PacketSelectCardCombo(currentCombo.stream().mapToInt(Card::getID).toArray(),
				targets.stream().mapToInt(Card::getID).toArray()));
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
	 * Check if it is currently this player's turn, regardless of current state.
	 * @return Whether it is this player's turn
	 */
	public boolean hasTurn()
	{
		GameState gs = getServer().getGameState();
		return gs.hasTurnState() && gs.getTurnState().getActionOwner() == this;
	}
	
	public int getMoves()
	{
		return hasTurn() ? getServer().getGameState().getMovesRemaining() : 0;
	}
	
	/**
	 * Check if it's the player's turn and the current state allows playing cards.
	 * @return True if the player can play cards right now
	 */
	public boolean canPlayCards()
	{
		return isFocused() && getServer().getGameState().getTurnState().canPlayCards();
	}
	
	/**
	 * Check if the player can draw from the deck right now.
	 * @return True if the player can draw
	 */
	public boolean canDraw()
	{
		return isFocused() && getServer().getGameState().getTurnState().isDrawing();
	}
	
	/**
	 * Check if the player can discard cards from their hand right now.
	 * @return True if the player is discarding
	 */
	public boolean isDiscarding()
	{
		return isFocused() && getServer().getGameState().getTurnState().getTurnState() == TurnState.DISCARD;
	}
	
	/**
	 * Make the player draw from the deck. Can only be used at the start of their turn.
	 */
	public void draw()
	{
		if (!canDraw())
		{
			System.out.println(getName() + " tried to draw without being able to!");
			return;
		}
		boolean extra = getServer().getGameRules().getDrawExtraCardsPolicy() == DrawExtraCardsPolicy.NEXT_DRAW &&
				getHand().getCardCount() == 0;
		int cardsToDraw = extra ? getServer().getGameRules().getExtraCardsDrawn() :
				getServer().getGameRules().getCardsDrawnPerTurn();
		PlayerPreDrawEvent event = new PlayerPreDrawEvent(this, cardsToDraw, extra);
		getServer().getEventManager().callEvent(event);
		if (event.isCancelled())
		{
			return;
		}
		List<Card> cards = drawCards(cardsToDraw);
		getGameState().setDrawn();
		getServer().getEventManager().callEvent(new PlayerDrawEvent(this, cards, extra));
	}
	
	/**
	 * Draws an amount of cards from the top of the deck into the player's hand.
	 * @param amount The amount of cards to draw
	 * @return The cards that the player just drew
	 */
	public List<Card> drawCards(int amount)
	{
		return getServer().getDeck().drawCards(this, amount);
	}
	
	/**
	 * End the player's turn. This fails silently if the player does not meet the conditions to end their turn.
	 */
	public void endTurn()
	{
		endTurn(false);
	}
	
	/**
	 * End the player's turn.
	 * @param ignoreConditions If true, the player will end their turn now even if they shouldn't be able to
	 */
	public void endTurn(boolean ignoreConditions)
	{
		if (!ignoreConditions)
		{
			if (getHand().hasTooManyCards())
			{
				if (getServer().getGameRules().canDiscardEarly())
				{
					getServer().getGameState().setMoves(0);
				}
				else
				{
					resendActionState();
				}
				return;
			}
		}
		getGameState().nextTurn();
	}
	
	/**
	 * Make the player run a command from a raw string.
	 * @param raw The command to run; it should not include a slash
	 */
	public void executeCommand(String raw)
	{
		getServer().getCommandHandler().executeCommand(this, raw);
	}
	
	/**
	 * Make the player send a message in chat.
	 * @param msg The message the player should send
	 */
	public void chat(String msg)
	{
		getServer().broadcastMessage(getName() + ": " + msg, true);
	}
	
	/**
	 * Forces the player's chat window to be opened or closed.
	 * @param chatOpen Whether the chat should be opened or closed
	 */
	public void setChatOpen(boolean chatOpen)
	{
		sendPacket(new PacketSetChatOpen(chatOpen));
	}
	
	public void resendActionState()
	{
		getGameState().resendActionState(this);
	}
	
	public void resendTurnState()
	{
		getGameState().resendTurnState(this);
	}
	
	/**
	 * Tells the client to stop awaiting a response from the server, regardless of giving any new information.
	 */
	public void clearAwaitingResponse()
	{
		sendPacket(new PacketSetAwaitingResponse(false));
	}
	
	private Packet[] getPropertySetPackets()
	{
		Packet[] packets = new Packet[propertySets.size()];
		for (int i = 0 ; i < packets.length ; i++)
		{
			packets[i] = propertySets.get(i).getCollectionDataPacket();
		}
		return packets;
	}
	
	private Packet getInfoPacket()
	{
		return new PacketPlayerInfo(getID(), getName(), isConnected() || isBot());
	}
	
	@Override
	public void sendMessage(String message)
	{
		sendPacket(new PacketMessage(MessageBuilder.fromSimple(message)));
	}
	
	@Override
	public void sendMessage(String message, String category)
	{
		Message simpleMessage = MessageBuilder.fromSimple(message);
		simpleMessage.setCategory(category);
		sendPacket(new PacketMessage(simpleMessage));
	}
	
	@Override
	public void sendMessage(String message, ChatAlignment alignment)
	{
		Message simpleMessage = MessageBuilder.fromSimple(message);
		simpleMessage.setAlignment(alignment);
		sendPacket(new PacketMessage(simpleMessage));
	}
	
	@Override
	public void sendMessage(String message, ChatAlignment alignment, String category)
	{
		Message simpleMessage = MessageBuilder.fromSimple(message);
		simpleMessage.setAlignment(alignment);
		simpleMessage.setCategory(category);
		sendPacket(new PacketMessage(simpleMessage));
	}
	
	@Override
	public void sendMessage(Message message)
	{
		sendPacket(new PacketMessage(message));
	}
	
	@Override
	public void clearMessages(String category)
	{
		sendPacket(new PacketRemoveMessageCategory(category));
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
	
	public void playSound(String sound)
	{
		playSound(sound, false);
	}
	
	public void playSound(String sound, boolean queued)
	{
		sendPacket(new PacketPlaySound(sound, queued));
	}
	
	/**
	 * Resend all data to the client.
	 */
	public void refresh()
	{
		sendPacket(new PacketRefresh());
		sendPacket(new PacketHandshake(getID(), getName()));
		sendPacket(new PacketStatus("Loading.."));
		for (Player other : getServer().getPlayersExcluding(this))
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
		sendCardButtons();
		sendButtonPackets();
		sendUndoStatus();
		getServer().getGameState().sendStatus(this);
	}
	
	protected MDServer getServer()
	{
		return MDServer.getInstance();
	}
	
	protected GameState getGameState()
	{
		return getServer().getGameState();
	}
	
	public Client getClient()
	{
		return client;
	}
	
	public void setClient(Client client)
	{
		this.client = client;
		if (client != null)
		{
			client.setCloseHandler(e ->
			{
				getServer().getScheduler().scheduleTask(() ->
				{
					if (this.client == client)
					{
						if (!(e instanceof GracefulDisconnect))
						{
							System.out.println(getName() + " lost connection");
						}
						setOnline(false);
					}
				});
			});
		}
	}
	
	public void closeConnection()
	{
		if (client != null)
		{
			client.closeConnection();
			client = null;
		}
	}
	
	public String getHostAddress()
	{
		return client != null ? client.getHostAddress() : "No Address";
	}
	
	public boolean isConnected()
	{
		return client != null && client.isConnected();
	}
	
	public void sendPacket(Packet packet)
	{
		if (client != null && packet != null)
		{
			client.addOutPacket(packet);
		}
	}
	
	/**
	 * Status effects can be applied to players to perform special effects on the player over time. StatusEffect instances
	 * are automatically registered for events when applied to a player and automatically unregistered when removed. Status
	 * effects are also cleared from all players when a game ends.
	 */
	public static class StatusEffect implements EventListener
	{
		private Player player;
		
		public Player getPlayer()
		{
			return player;
		}
		
		private void setPlayer(Player player)
		{
			this.player = player;
		}
		
		/**
		 * Shortcut to add this effect to the player.
		 * @param player The player to apply this effect to.
		 */
		public void applyEffect(Player player)
		{
			player.addStatusEffect(this);
		}
		
		/**
		 * Removes this effect from the player.
		 */
		public void removeEffect()
		{
			player.removeStatusEffect(this);
		}
		
		/**
		 * Called when this effect is applied to a player.
		 */
		public void onApply() {}
		
		/**
		 * Called when this effect is removed. Can be overridden to ensure that everything is properly cleaned up when an
		 * effect is removed.
		 */
		public void onRemove() {}
		
		protected MDServer getServer()
		{
			return MDServer.getInstance();
		}
	}
}
