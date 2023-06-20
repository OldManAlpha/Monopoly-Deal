package oldmana.md.server.card;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.card.CardAnimationType;
import oldmana.md.common.net.packet.server.PacketDestroyCard;
import oldmana.md.common.playerui.ButtonColorScheme;
import oldmana.md.common.playerui.CardButtonBounds;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.card.play.PlayArgument;
import oldmana.md.server.card.play.argument.BankArgument;
import oldmana.md.server.card.play.argument.DiscardArgument;
import oldmana.md.server.card.play.argument.IgnoreCanPlayArgument;
import oldmana.md.server.card.play.argument.SilentPlayArgument;
import oldmana.md.server.history.UndoableAction;
import oldmana.md.server.history.BasicUndoableAction;
import oldmana.md.server.card.play.PlayArguments;
import oldmana.md.server.card.collection.CardCollection;
import oldmana.md.server.card.control.CardControls;
import oldmana.md.server.card.control.CardButton;
import oldmana.md.server.event.card.CardDiscardEvent;
import oldmana.md.server.event.card.PostCardPlayEvent;
import oldmana.md.server.event.card.CardPlayEvent;
import oldmana.md.server.rules.DiscardOrderPolicy;

import static oldmana.md.server.card.CardAttributes.*;

public abstract class Card
{
	private int id;
	
	private CardCollection collection;
	
	private CardType<?> type;
	private CardTemplate template;
	
	private int value;
	private String name;
	
	private String[] displayName;
	private int fontSize;
	private int displayOffsetY;
	private CardDescription description;
	
	private Color outerColor;
	private Color innerColor;
	
	private boolean undoable;
	private boolean clearsUndoableCards;
	
	private CardAnimationType playAnimation;
	
	private int moveCost;
	private CardPlayStage consumeMovesStage;
	private CardPlayStage moveStage;
	
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
		value = template.getInt(VALUE);
		name = template.getString(NAME);
		displayName = template.getStringArray(DISPLAY_NAME);
		fontSize = template.getInt(FONT_SIZE);
		displayOffsetY = template.getInt(DISPLAY_OFFSET_Y);
		Object desc = template.getObject(DESCRIPTION);
		description = desc instanceof String ? CardDescription.getDescription((String) desc) :
				CardDescription.getDescription(template.getStringArray(DESCRIPTION));
		outerColor = template.getColor(OUTER_COLOR);
		innerColor = template.has(INNER_COLOR) ?
				template.getColor(INNER_COLOR) : CardValueColor.getByValue(value).getColor();
		undoable = template.getBoolean(UNDOABLE);
		clearsUndoableCards = template.getBoolean(CLEARS_UNDOABLE_ACTIONS);
		playAnimation = CardAnimationType.fromJson(template.getString(PLAY_ANIMATION));
		moveCost = template.getInt(MOVE_COST);
		consumeMovesStage = CardPlayStage.fromJson(template.getString(CONSUME_MOVES_STAGE));
		moveStage = CardPlayStage.fromJson(template.getString(MOVE_STAGE));
	}
	
	public CardTemplate getTemplate()
	{
		return template;
	}
	
	protected CardControls createControls()
	{
		// Basic Card Function Buttons
		CardButton play = new CardButton("Play", CardButtonBounds.TOP);
		play.setCondition((player, card) -> card.canPlayNow());
		play.setListener((player, card, data) -> card.play());
		
		CardButton bank = new CardButton("Bank", CardButtonBounds.BOTTOM);
		bank.setCondition((player, card) -> card.canBank(player) && player.canPlayCards());
		bank.setListener((player, card, data) -> card.play(PlayArguments.BANK));
		
		CardButton discard = new CardButton("Discard", CardButtonBounds.CENTER);
		discard.setColor(ButtonColorScheme.ALERT);
		discard.setCondition((player, card) ->  card.canDiscard(player) && player.isDiscarding());
		discard.setListener((player, card, data) -> card.play(PlayArguments.DISCARD));
		
		return new CardControls(this, play, bank, discard);
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
	
	/**
	 * Should never be called unless you know what you're doing.
	 * @param collection The new owning collection
	 */
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
	
	public Color getOuterColor()
	{
		return outerColor;
	}
	
	protected void setOuterColor(Color outerColor)
	{
		this.outerColor = outerColor;
	}
	
	public Color getInnerColor()
	{
		return innerColor;
	}
	
	protected void setInnerColor(Color innerColor)
	{
		this.innerColor = innerColor;
	}
	
	public boolean isUndoable()
	{
		return undoable;
	}
	
	public boolean shouldClearUndoableCards()
	{
		return clearsUndoableCards;
	}
	
	public CardAnimationType getPlayAnimation()
	{
		return playAnimation;
	}
	
	public int getMoveCost()
	{
		return moveCost;
	}
	
	public CardPlayStage getConsumeMovesStage()
	{
		return consumeMovesStage;
	}
	
	public CardPlayStage getMoveStage()
	{
		return moveStage;
	}
	
	public void transfer(CardCollection to)
	{
		transfer(to, -1);
	}
	
	public void transfer(CardCollection to, int index)
	{
		transfer(to, index, 1);
	}
	
	public void transfer(CardCollection to, CardAnimationType anim)
	{
		transfer(to, -1, anim);
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
	
	public CardType<?> getType()
	{
		return type;
	}
	
	public void setType(CardType<?> type)
	{
		this.type = type;
	}
	
	/**
	 * Plays this card into the owner's bank.
	 * @return True if the moves should be decremented by 1
	 */
	protected boolean doPlayBank(PlayArguments args)
	{
		transfer(getOwner().getBank());
		return true;
	}
	
	/**
	 * Perform the card-specific logic with the given play arguments.
	 * @param player The player playing this card
	 * @param args The arguments for this card play
	 */
	public void doPlay(Player player, PlayArguments args) {}
	
	/**
	 * Make the owner of this card play it. The owner must be in a state that they're able to play this card.
	 */
	public void play()
	{
		play(PlayArguments.EMPTY);
	}
	
	/**
	 * Make the owner of this card play it. The owner must be in a state that they're able to play this card.
	 * @param args The arguments for how this card should be played; conveniently converted into PlayArguments
	 */
	public void play(PlayArgument... args)
	{
		play(PlayArguments.of(args));
	}
	
	/**
	 * Make the owner of this card play it. The owner must be in a state that they're able to play this card.
	 * @param args The arguments for how this card should be played; conveniently converted into PlayArguments
	 */
	public void play(List<PlayArgument> args)
	{
		play(PlayArguments.of(args));
	}
	
	/**
	 * Make the owner of this card play it. The owner must be in a state that they're able to play this card.
	 * @param args The arguments for how this card should be played
	 */
	public void play(PlayArguments args)
	{
		Player player = getOwner(); // Caching the owner since they might not be the owner by the end of this method call.
		
		if (player == null)
		{
			throw new IllegalStateException("Cards without an owner cannot be played");
		}
		
		try
		{
			getServer().getGameState().pushCard(this);
			
			if (args == null)
			{
				args = PlayArguments.EMPTY;
			}
			
			if (args.hasArgument(DiscardArgument.class))
			{
				playStageDiscard(player, args);
				return; // Discarded cards do not continue to play logic
			}
			
			// This card cannot be played and isn't attempted to be banked, so nothing happens
			if (!args.hasArgument(IgnoreCanPlayArgument.class) && !args.hasArgument(BankArgument.class) && !canPlayNow())
			{
				return;
			}
			
			if (playStageCallPrePlayEvent(player, args))
			{
				return; // Event was cancelled, so nothing happens
			}
			
			if (playStageBank(player, args))
			{
				playStageCallPostPlayEvent(player, args);
				// Card was banked, so we're not proceeding to play the card
				return;
			}
			
			playStageAddUndo(player, args);
			
			CardPlayStage consumeMovesStage = getConsumeMovesStage();
			CardPlayStage moveStage = getMoveStage();
			
			if (consumeMovesStage == CardPlayStage.BEFORE_PLAY)
			{
				playStageConsumeMoves(player, args);
			}
			if (moveStage == CardPlayStage.BEFORE_PLAY)
			{
				playStageMoveCard(player, args);
			}
			
			if (consumeMovesStage == CardPlayStage.RIGHT_BEFORE_PLAY)
			{
				playStageConsumeMoves(player, args);
			}
			if (moveStage == CardPlayStage.RIGHT_BEFORE_PLAY)
			{
				playStageMoveCard(player, args);
			}
			
			playStagePlayCard(player, args); // Here's where the card is gonna actually get played
			
			if (consumeMovesStage == CardPlayStage.RIGHT_AFTER_PLAY)
			{
				playStageConsumeMoves(player, args);
			}
			if (moveStage == CardPlayStage.RIGHT_AFTER_PLAY)
			{
				playStageMoveCard(player, args);
			}
			
			if (consumeMovesStage == CardPlayStage.AFTER_PLAY)
			{
				playStageConsumeMoves(player, args);
			}
			if (moveStage == CardPlayStage.AFTER_PLAY)
			{
				playStageMoveCard(player, args);
			}
			
			playStageCallPostPlayEvent(player, args);
			player.clearAwaitingResponse();
		}
		finally
		{
			if (getServer().getGameState().popCard() != this)
			{
				System.out.println("Popped card mismatch!");
			}
			player.checkEmptyHand();
		}
	}
	
	
	/* STAGES OF PLAYING A CARD */
	
	/**
	 * Discards this card and calls CardDiscardEvent.
	 * This is called before any other stages and will not proceed to next stages.
	 */
	protected void playStageDiscard(Player player, PlayArguments args)
	{
		transfer(getServer().getDiscardPile());
		getServer().getGameState().updateTurnState();
		getServer().getEventManager().callEvent(new CardDiscardEvent(player, this));
		logPlay(player, args, player.getName() + " discards " + getName());
	}
	
	/**
	 * Calls the PreCardPlayEvent.
	 * @return True if the event was cancelled (stops the playing process if so)
	 */
	protected boolean playStageCallPrePlayEvent(Player player, PlayArguments args)
	{
		CardPlayEvent event = new CardPlayEvent(player, this, args);
		getServer().getEventManager().callEvent(event);
		return event.isCancelled();
	}
	
	/**
	 * Calls the PostCardPlayEvent.
	 */
	protected void playStageCallPostPlayEvent(Player player, PlayArguments args)
	{
		getServer().getEventManager().callEvent(new PostCardPlayEvent(player, this, args));
	}
	
	/**
	 * Adds an undoable action(if applicable) and clears undoable actions(if applicable)
	 */
	protected void playStageAddUndo(Player player, PlayArguments args)
	{
		if (!player.hasTurn())
		{
			// The player playing this card doesn't have the turn, so they cannot undo cards
			return;
		}
		
		if (shouldClearUndoableCards())
		{
			player.clearUndoableActions();
		}
		if (isUndoable() && getServer().getGameRules().isUndoAllowed())
		{
			player.addUndoableAction(createUndoableAction());
		}
	}
	
	protected UndoableAction createUndoableAction()
	{
		return new BasicUndoableAction(this, getOwner(), getMoveCost());
	}
	
	/**
	 * By default, this method decrements the player's moves by the card's move cost, only if the card is being played
	 * on their turn.
	 */
	protected void playStageConsumeMoves(Player player, PlayArguments args)
	{
		if (player.hasTurn())
		{
			getServer().getGameState().decrementMoves(getMoveCost());
		}
	}
	
	/**
	 * Checks to see if the card should be banked with the given arguments, and does so if it should.
	 * Also adds an undoable action if undoing is allowed by the game rules.
	 * @return True if the card was banked (stops the playing process if so)
	 */
	protected boolean playStageBank(Player player, PlayArguments args)
	{
		if (args.hasArgument(BankArgument.class))
		{
			if (!player.isFocused())
			{
				return true;
			}
			if (getServer().getGameRules().isUndoAllowed())
			{
				player.addUndoableAction(new BasicUndoableAction(this, player, 1));
			}
			if (doPlayBank(args))
			{
				getServer().getGameState().decrementMoves(1);
			}
			logPlay(player, args, player.getName() + " banks " + getName());
			return true;
		}
		return false;
	}
	
	/**
	 * Calls doPlay and logs it.
	 */
	protected void playStagePlayCard(Player player, PlayArguments args)
	{
		doPlay(player, args);
		logPlay(player, args, player.getName() + " plays " + getName());
	}
	
	/**
	 * By default, moves this card into the discard pile, if applicable. Can be overridden to change where this card
	 * should go when played.
	 */
	protected void playStageMoveCard(Player player, PlayArguments args)
	{
		transfer(getServer().getDiscardPile(), getPlayAnimation());
	}
	
	/**
	 * Logs a message to the console in relation to playing a card. By default, this checks if there's a
	 * SilentPlayArgument, and doesn't print if there is one.
	 */
	protected void logPlay(Player player, PlayArguments args, String msg)
	{
		if (!args.hasArgument(SilentPlayArgument.class))
		{
			System.out.println(msg);
		}
	}
	
	/* END OF CARD PLAY STAGES */
	
	/**
	 * Check whether the player is able to play this card, disregarding the current action state.
	 * @return True if this card is playable, disregarding state
	 */
	public boolean canPlay(Player player)
	{
		return true;
	}
	
	/**
	 * Check whether the card owner is able to play this card, all factors considered.
	 * @return True if this card is playable right now
	 */
	public boolean canPlayNow()
	{
		if (getOwner() == null)
		{
			return false;
		}
		Player player = getOwner();
		return canPlay(player) && player.isFocused() && !getServer().getGameState().getTurnState().isDrawing() &&
				getServer().getGameState().getMovesRemaining() >= getMoveCost();
	}
	
	public boolean canBank(Player player)
	{
		return getValue() > 0;
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
		if (card.getOwningCollection() != MDServer.getInstance().getVoidCollection())
		{
			throw new IllegalStateException("Cannot unregister cards that aren't in the void!");
		}
		getCardsMap().remove(card.getID());
		MDServer.getInstance().broadcastPacket(new PacketDestroyCard(card.getID()));
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
