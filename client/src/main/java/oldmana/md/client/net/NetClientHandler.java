package oldmana.md.client.net;

import java.awt.Color;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import oldmana.md.client.gui.component.large.MDPlayer.InfoPlate;
import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.client.MDClient;
import oldmana.md.client.Player;
import oldmana.md.client.EventQueue.EventTask;
import oldmana.md.client.SoundSystem;
import oldmana.md.client.Settings;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.Card.CardDescription;
import oldmana.md.client.card.CardAction;
import oldmana.md.client.card.CardActionRent;
import oldmana.md.client.card.CardBuilding;
import oldmana.md.client.card.CardButton;
import oldmana.md.client.state.client.ActionStateClientSelectCardCombo;
import oldmana.md.common.net.packet.server.*;
import oldmana.md.common.net.packet.server.actionstate.*;
import oldmana.md.common.playerui.CardButtonBounds;
import oldmana.md.common.playerui.CardButtonType;
import oldmana.md.client.card.CardMoney;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.CardProperty.PropertyColor;
import oldmana.md.client.card.collection.Bank;
import oldmana.md.client.card.collection.CardCollection;
import oldmana.md.client.card.collection.Deck;
import oldmana.md.client.card.collection.DiscardPile;
import oldmana.md.client.card.collection.Hand;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.card.collection.VoidCollection;
import oldmana.md.client.gui.component.MDUndoButton;
import oldmana.md.client.gui.component.MDClientButton;
import oldmana.md.client.gui.component.collection.MDHand;
import oldmana.md.client.state.ActionState;
import oldmana.md.client.state.ActionStateDoNothing;
import oldmana.md.client.state.ActionStatePlayerTargeted;
import oldmana.md.client.state.ActionStatePropertiesSelected;
import oldmana.md.client.state.ActionStateRent;
import oldmana.md.client.state.ActionStatePropertySetTargeted;
import oldmana.md.client.state.ActionStateTargetPlayer;
import oldmana.md.client.state.ActionStateTargetPlayerMonopoly;
import oldmana.md.client.state.ActionStateTargetProperties;
import oldmana.md.client.state.ActionStateTargetProperties.TargetMode;
import oldmana.md.client.state.primary.ActionStatePlayerTurn;
import oldmana.md.common.card.CardAnimationType;
import oldmana.md.common.playerui.ButtonColorScheme;
import oldmana.md.common.util.DataUtil;
import oldmana.md.common.net.NetHandler;
import oldmana.md.common.net.packet.client.*;
import oldmana.md.common.net.packet.server.PacketCardCollectionData.CardCollectionType;
import oldmana.md.common.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.common.net.packet.server.actionstate.PacketActionStatePlayerTurn.TurnState;
import oldmana.md.common.net.packet.universal.*;

public class NetClientHandler extends NetHandler
{
	private MDClient client;
	
	private Map<Class<? extends Packet>, Method> packetHandlers = new HashMap<Class<? extends Packet>, Method>();
	
	@SuppressWarnings("unchecked")
	public NetClientHandler(MDClient client)
	{
		this.client = client;
		
		// Find Packet Handlers
		for (Method m : getClass().getDeclaredMethods())
		{
			if (Modifier.isStatic(m.getModifiers()))
			{
				continue;
			}
			Class<?>[] params = m.getParameterTypes();
			if (params.length > 0)
			{
				Class<?> clazz = params[0];
				if (clazz.getSuperclass() == Packet.class)
				{
					packetHandlers.put((Class<? extends Packet>) clazz, m);
				}
			}
		}
	}
	
	public void processPackets(ServerConnection connection)
	{
		for (Packet packet : connection.getInPackets())
		{
			if (!(packet instanceof PacketKeepConnected))
			{
				System.out.println("Processing: " + packet.getClass());
			}
			
			try
			{
				Method handler = packetHandlers.get(packet.getClass());
				if (handler.isAnnotationPresent(Queued.class))
				{
					queueTask(() ->
					{
						try
						{
							handler.invoke(this, packet);
						}
						catch (Exception e)
						{
							System.err.println("Error processing queued packet type " + packet.getClass().getName());
							e.printStackTrace();
						}
					});
				}
				else
				{
					packetHandlers.get(packet.getClass()).invoke(this, packet);
				}
			}
			catch (Exception e)
			{
				System.err.println("Error processing packet type " + packet.getClass().getName());
				e.printStackTrace();
			}
		}
	}
	
	private void queueTask(EventTask task)
	{
		client.getEventQueue().addTask(task);
	}
	
	public void handleServerInfo(PacketServerInfo packet) throws NoSuchAlgorithmException
	{
		Settings settings = client.getSettings();
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(settings.getBigInteger("clientKey").toByteArray());
		digest.update(packet.serverKey);
		if (client.isDevMode() && settings.has("lastSalt"))
		{
			String salt = settings.getString("lastSalt");
			if (!salt.isEmpty())
			{
				digest.update(salt.getBytes());
			}
		}
		client.sendPacket(new PacketLogin(PROTOCOL_VERSION, MDClient.VERSION, digest.digest(), client.getSettings().getString("lastName")));
		client.getTableScreen().getTopbar().setText("Authenticating..");
	}
	
	public void handleHandshake(PacketHandshake packet)
	{
		client.createThePlayer(packet.id, packet.name);
		client.sendPacket(SoundSystem.getSoundsPacket());
	}
	
	public void handleRefresh(PacketRefresh packet)
	{
		client.resetGame();
	}
	
	public void handlePropertyColors(PacketPropertyColors packet)
	{
		int len = packet.name.length;
		for (int i = 0 ; i < len ; i++)
		{
			PropertyColor.create(i, packet.name[i], packet.label[i], new Color(packet.color[i]), packet.buildable[i], packet.rents[i]);
		}
	}
	
	public void handleKick(PacketKick packet)
	{
		client.getServerConnection().closeGracefully();
		client.getTableScreen().getTopbar().setText("Disconnected: " + packet.reason);
	}
	
	public void handleCardDescription(PacketCardDescription packet)
	{
		new CardDescription(packet.id, packet.description);
	}
	
	public void handleCardData(PacketCardData packet)
	{
		Card card = Card.getCard(packet.id);
		if (card == null)
		{
			if (packet.type == 0)
			{
				card = new CardAction(packet.id, packet.value, packet.name);
			}
			else
			{
				card = new CardMoney(packet.id, packet.value, packet.name);
			}
			card.setDisplayName(packet.displayName);
			card.setFontSize(packet.fontSize);
			card.setDisplayOffsetY(packet.displayOffsetY);
			card.setDescription(CardDescription.getDescriptionByID(packet.description));
			card.setOuterColor(new Color(packet.outerColor));
			card.setInnerColor(new Color(packet.innerColor));
			return;
		}
		Card c = card;
		queueTask(() ->
		{
			c.setValue(packet.value);
			c.setName(packet.name);
			c.setDisplayName(packet.displayName);
			c.setFontSize(packet.fontSize);
			c.setDisplayOffsetY(packet.displayOffsetY);
			c.setDescription(CardDescription.getDescriptionByID(packet.description));
			c.setOuterColor(new Color(packet.outerColor));
			c.setInnerColor(new Color(packet.innerColor));
			c.clearGraphicsCache();
		});
	}
	
	public void handleCardActionRentData(PacketCardActionRentData packet)
	{
		CardActionRent card = (CardActionRent) Card.getCard(packet.id);
		if (card == null)
		{
			card = new CardActionRent(packet.id, packet.value, packet.name);
			card.setDisplayName(packet.displayName);
			card.setFontSize(packet.fontSize);
			card.setDisplayOffsetY(packet.displayOffsetY);
			card.setRentColors(PropertyColor.fromIDs(packet.colors).toArray(new PropertyColor[packet.colors.length]));
			card.setDescription(CardDescription.getDescriptionByID(packet.description));
			card.setOuterColor(new Color(packet.outerColor));
			card.setInnerColor(new Color(packet.innerColor));
			return;
		}
		
		CardActionRent c = card;
		queueTask(() ->
		{
			c.setValue(packet.value);
			c.setName(packet.name);
			c.setDisplayName(packet.displayName);
			c.setFontSize(packet.fontSize);
			c.setDisplayOffsetY(packet.displayOffsetY);
			c.setRentColors(PropertyColor.fromIDs(packet.colors).toArray(new PropertyColor[packet.colors.length]));
			c.setDescription(CardDescription.getDescriptionByID(packet.description));
			c.setOuterColor(new Color(packet.outerColor));
			c.setInnerColor(new Color(packet.innerColor));
			c.clearGraphicsCache();
		});
	}
	
	public void handleCardPropertyData(PacketCardPropertyData packet)
	{
		CardProperty card = (CardProperty) Card.getCard(packet.id);
		if (card == null)
		{
			card = new CardProperty(packet.id, PropertyColor.fromIDs(packet.colors), packet.base, packet.stealable, packet.value, packet.name);
			card.setDescription(CardDescription.getDescriptionByID(packet.description));
			card.setOuterColor(new Color(packet.outerColor));
			card.setInnerColor(new Color(packet.innerColor));
			return;
		}
		
		CardProperty c = card;
		queueTask(() ->
		{
			c.setValue(packet.value);
			c.setName(packet.name);
			c.setBase(packet.base);
			c.setStealable(packet.stealable);
			c.setColors(PropertyColor.fromIDs(packet.colors));
			c.setDescription(CardDescription.getDescriptionByID(packet.description));
			c.setOuterColor(new Color(packet.outerColor));
			c.setInnerColor(new Color(packet.innerColor));
			c.clearGraphicsCache();
		});
	}
	
	public void handleCardBuildingData(PacketCardBuildingData packet)
	{
		CardBuilding card = (CardBuilding) Card.getCard(packet.id);
		if (card == null)
		{
			card = new CardBuilding(packet.id, packet.value, packet.name, packet.tier, packet.rentAddition);
			card.setDisplayName(packet.displayName);
			card.setFontSize(packet.fontSize);
			card.setDisplayOffsetY(packet.displayOffsetY);
			card.setDescription(CardDescription.getDescriptionByID(packet.description));
			card.setOuterColor(new Color(packet.outerColor));
			card.setInnerColor(new Color(packet.innerColor));
			return;
		}
		
		CardBuilding c = card;
		queueTask(() ->
		{
			c.setValue(packet.value);
			c.setName(packet.name);
			c.setTier(packet.tier);
			c.setRentAddition(packet.rentAddition);
			c.setDisplayName(packet.displayName);
			c.setFontSize(packet.fontSize);
			c.setDisplayOffsetY(packet.displayOffsetY);
			c.setDescription(CardDescription.getDescriptionByID(packet.description));
			c.setOuterColor(new Color(packet.outerColor));
			c.setInnerColor(new Color(packet.innerColor));
			c.clearGraphicsCache();
		});
	}
	
	public void handlePlayerInfo(PacketPlayerInfo packet)
	{
		if (client.getPlayerByID(packet.id) == null)
		{
			Player player = new Player(client, packet.id, packet.name);
			player.setConnected(packet.connected);
			client.addPlayer(player);
		}
	}
	
	public void handleUpdatePlayer(PacketUpdatePlayer packet)
	{
		Player player = client.getPlayerByID(packet.player);
		player.setName(packet.name);
		player.setConnected(packet.connected);
		player.getUI().updateGraphics();
	}
	
	@Queued
	public void handleDestroyPlayer(PacketDestroyPlayer packet)
	{
		Player player = client.getPlayerByID(packet.player);
		client.destroyPlayer(player);
	}
	
	public void handleCardCollectionData(PacketCardCollectionData packet)
	{
		CardCollectionType type = CardCollectionType.fromID(packet.type);
		
		if (type == CardCollectionType.BANK)
		{
			Player owner = client.getPlayerByID(packet.owner);
			Bank bank = new Bank(packet.id, owner);
			for (int id : packet.cardIds)
			{
				bank.addCard(Card.getCard(id));
			}
			owner.setBank(bank);
		}
		else if (type == CardCollectionType.HAND)
		{
			Player player = client.getPlayerByID(packet.owner);
			Hand hand = new Hand(packet.id, player);
			for (int id : packet.cardIds)
			{
				hand.addCard(Card.getCard(id));
			}
			player.setHand(hand);
		}
		else if (type == CardCollectionType.DISCARD_PILE)
		{
			client.setDiscardPile(new DiscardPile(packet.id, Card.getCards(packet.cardIds)));
		}
	}
	
	public void handleUnknownCardCollectionData(PacketUnknownCardCollectionData packet)
	{
		CardCollectionType type = CardCollectionType.fromID(packet.type);
		
		if (type == CardCollectionType.HAND)
		{
			Player owner = client.getPlayerByID(packet.owner);
			owner.setHand(new Hand(packet.id, owner, packet.cardCount));
		}
		else if (type == CardCollectionType.DECK)
		{
			client.setDeck(new Deck(packet.id, packet.cardCount));
		}
		else if (type == CardCollectionType.VOID)
		{
			client.setVoidCollection(new VoidCollection(packet.id));
		}
	}
	
	@Queued
	public void handlePropertySetData(PacketPropertySetData packet)
	{
		Player owner = client.getPlayerByID(packet.owner);
		PropertySet set = new PropertySet(packet.id, owner, Card.getCards(packet.cardIds), PropertyColor.fromID(packet.activeColor));
		owner.addPropertySet(set);
	}
	
	@Queued
	public void handleStatus(PacketStatus packet)
	{
		client.getTableScreen().getTopbar().setText(packet.text);
	}
	
	@Queued
	public void handleMoveCard(PacketMoveCard packet)
	{
		Card card = Card.getCard(packet.cardId);
		CardCollection collection = CardCollection.getCardCollection(packet.collectionId);
		collection.transferCard(card, packet.index, packet.time, CardAnimationType.fromID(packet.anim));
	}
	
	@Queued
	public void handleMoveRevealCard(PacketMoveRevealCard packet)
	{
		CardCollection from = CardCollection.getCardCollection(packet.from);
		CardCollection to = CardCollection.getCardCollection(packet.to);
		from.transferCardTo(Card.getCard(packet.cardId), to, packet.index, packet.time, CardAnimationType.fromID(packet.anim));
	}
	
	@Queued
	public void handleMoveUnknownCard(PacketMoveUnknownCard packet)
	{
		CardCollection from = CardCollection.getCardCollection(packet.from);
		CardCollection to = CardCollection.getCardCollection(packet.to);
		from.transferCardTo(null, to, -1, packet.time, CardAnimationType.fromID(packet.anim));
	}
	
	@Queued
	public void handleActionStatePlayerTurn(PacketActionStatePlayerTurn packet)
	{
		if (packet.getTurnState() == TurnState.REMOVE_STATE)
		{
			client.getGameState().setPlayerTurn(null);
		}
		else
		{
			client.getGameState().setPlayerTurn(new ActionStatePlayerTurn(client.getPlayerByID(packet.player),
					packet.getTurnState(), packet.moves));
		}
		client.setAwaitingResponse(false);
	}
	
	@Queued
	public void handleActionStateBasic(PacketActionStateBasic packet)
	{
		BasicActionState type = BasicActionState.fromID(packet.type);
		
		System.out.println("BASIC ACTION STATE " + packet.type);
		Player player = client.getPlayerByID(packet.player);
		if (type == BasicActionState.NO_STATE)
		{
			client.getGameState().setActionState(null);
		}
		else if (type == BasicActionState.DO_NOTHING)
		{
			client.getGameState().setActionState(new ActionStateDoNothing());
		}
		else if (type == BasicActionState.TARGET_PLAYER)
		{
			client.getGameState().setActionState(new ActionStateTargetPlayer(player, packet.data == 1));
		}
		else if (type == BasicActionState.TARGET_SELF_PROPERTY)
		{
			boolean[] data = DataUtil.convertByteToBooleans((byte) (packet.data & 0xFF));
			client.getGameState().setActionState(new ActionStateTargetProperties(player, TargetMode.SELF,
					data[0], data[1], data[2]));
		}
		else if (type == BasicActionState.TARGET_PLAYER_PROPERTY)
		{
			boolean[] data = DataUtil.convertByteToBooleans((byte) (packet.data & 0xFF));
			client.getGameState().setActionState(new ActionStateTargetProperties(player, TargetMode.OTHER,
					data[0], data[1], data[2]));
		}
		else if (type == BasicActionState.TARGET_SELF_PLAYER_PROPERTY)
		{
			boolean[] data = DataUtil.convertByteToBooleans((byte) (packet.data & 0xFF));
			client.getGameState().setActionState(new ActionStateTargetProperties(player, TargetMode.SELF_OTHER,
					data[0], data[1], data[2]));
		}
		else if (type == BasicActionState.TARGET_ANY_PROPERTY)
		{
			boolean[] data = DataUtil.convertByteToBooleans((byte) (packet.data & 0xFF));
			client.getGameState().setActionState(new ActionStateTargetProperties(player, TargetMode.ANY,
					data[0], data[1], data[2]));
		}
		else if (type == BasicActionState.TARGET_PLAYER_MONOPOLY)
		{
			client.getGameState().setActionState(new ActionStateTargetPlayerMonopoly(player));
		}
		else if (type == BasicActionState.PLAYER_TARGETED)
		{
			client.getGameState().setActionState(new ActionStatePlayerTargeted(player, client.getPlayerByID(packet.data)));
		}
		client.setAwaitingResponse(false);
	}
	
	@Queued
	public void handleActionStateRent(PacketActionStateRent packet)
	{
		client.setAwaitingResponse(false);
		Map<Player, Integer> charges = new HashMap<Player, Integer>();
		List<Player> players = client.getPlayersByIDs(packet.rented);
		for (int i = 0 ; i < players.size() ; i++)
		{
			charges.put(players.get(i), packet.amounts[i]);
		}
		client.getGameState().setActionState(new ActionStateRent(client.getPlayerByID(packet.renter), charges));
	}
	
	@Queued
	public void handleDestroyCardCollection(PacketDestroyCardCollection packet)
	{
		CardCollection collection = CardCollection.getCardCollection(packet.id);
		if (collection instanceof PropertySet)
		{
			Player player = collection.getOwner();
			player.destroyPropertySet((PropertySet) collection);
			player.getUI().getPropertySets().repaint();
		}
	}
	
	@Queued
	public void handleDestroyCard(PacketDestroyCard packet)
	{
		Card card = Card.getCard(packet.cardID);
		if (!(card.getOwningCollection() instanceof VoidCollection))
		{
			System.out.println("Server tried to delete card that's still in play! " + packet.cardID);
			return;
		}
		Card.getRegisteredCards().remove(packet.cardID);
	}
	
	@Queued
	public void handlePropertySetColor(PacketPropertySetColor packet)
	{
		PropertySet set = (PropertySet) CardCollection.getCardCollection(packet.id);
		set.setEffectiveColor(packet.color > -1 ? PropertyColor.fromID(packet.color) : null);
		set.getUI().updateGraphics();
	}
	
	@Queued
	public void handleActionStatePropertiesSelected(PacketActionStatePropertiesSelected packet)
	{
		client.setAwaitingResponse(false);
		client.getGameState().setActionState(new ActionStatePropertiesSelected(client.getPlayerByID(packet.owner),
				client.getPlayerByID(packet.target), Card.getPropertyCards(packet.cards)));
		client.getTableScreen().repaint();
	}
	
	@Queued
	public void handleActionStatePropertySetTargeted(PacketActionStatePropertySetTargeted packet)
	{
		client.setAwaitingResponse(false);
		client.getGameState().setActionState(new ActionStatePropertySetTargeted(client.getPlayerByID(packet.player),
				(PropertySet) CardCollection.getCardCollection(packet.collection)));
		client.getTableScreen().repaint();
	}
	
	@Queued
	public void handleUpdateActionStateTarget(PacketUpdateActionStateTarget packet)
	{
		ActionState state = client.getGameState().getActionState();
		if (state != null)
		{
			client.setAwaitingResponse(false);
			Player player = client.getPlayerByID(packet.target);
			state.setTargetState(player, packet.getTargetState());
		}
		else
		{
			System.err.println("Server sent illegal action state update: " + packet.getTargetState().name());
		}
	}
	
	public void handleUndoCardStatus(PacketUndoCardStatus packet)
	{
		MDUndoButton undoButton = MDClient.getInstance().getTableScreen().getUndoButton();
		if (packet.cardId > -1)
		{
			undoButton.setUndoCard(Card.getCard(packet.cardId));
		}
		else
		{
			undoButton.removeUndoCard();
		}
	}
	
	public void handleKeepConnected(PacketKeepConnected packet)
	{
		client.sendPacket(packet);
		client.timeSincePing = 0;
	}
	
	public void handleChat(PacketMessage packet)
	{
		client.getTableScreen().getChat().addMessage(packet.getMessage());
	}
	
	public void handleSoundData(PacketSoundData packet)
	{
		SoundSystem.addSound(packet.name, packet.data, packet.hash);
		client.sendPacket(SoundSystem.getSoundsPacket());
	}
	
	public void handlePlaySound(PacketPlaySound packet)
	{
		if (packet.queued)
		{
			queueTask(() -> SoundSystem.playSound(packet.name));
		}
		else
		{
			SoundSystem.playSound(packet.name);
		}
	}
	
	@Queued
	public void handleButton(PacketPlayerButton packet)
	{
		Player player = client.getPlayerByID(packet.playerID);
		MDClientButton b = player.getUI().getAndCreateButton(player, packet.id);
		b.setEnabled(packet.enabled);
		b.setColor(ButtonColorScheme.fromID(packet.color));
		b.setText(packet.name);
		b.setPriority(packet.priority);
		b.setMaxSize(packet.maxSize);
		b.updateGraphics();
		player.getUI().validate();
	}
	
	@Queued
	public void handleDestroyButton(PacketDestroyButton packet)
	{
		for (Player player : client.getAllPlayers())
		{
			if (player.getUI().removeButton(packet.id))
			{
				player.getUI().validate();
				player.getUI().repaint();
				return;
			}
		}
	}
	
	@Queued
	public void handleInfoPlate(PacketInfoPlate packet)
	{
		Player player = client.getPlayerByID(packet.player);
		InfoPlate plate = player.getUI().getAndCreateInfoPlate(packet.id);
		plate.setPriority(packet.priority);
		plate.setText(packet.text);
		plate.setTextColor(packet.textColor);
		plate.setColor(packet.color);
		plate.setBorderColor(packet.borderColor);
		player.getUI().sortInfoPlates();
		player.getUI().updateGraphics();
	}
	
	@Queued
	public void handleDestroyInfoPlate(PacketDestroyInfoPlate packet)
	{
		Player player = client.getPlayerByID(packet.player);
		player.getUI().removeInfoPlate(packet.id);
		player.getUI().updateGraphics();
	}
	
	@Queued
	public void handleCardButtons(PacketCardButtons packet)
	{
		Card card = Card.getCard(packet.cardID);
		card.clearButtons();
		int len = packet.text.length;
		for (int i = 0 ; i < len ; i++)
		{
			card.addButton(new CardButton(packet.id[i], packet.text[i], new CardButtonBounds(packet.x[i], packet.y[i],
					packet.width[i], packet.height[i]), CardButtonType.fromID(packet.type[i]),
					ButtonColorScheme.fromID(packet.color[i])));
		}
		((MDHand) client.getThePlayer().getHand().getUI()).removeOverlay();
	}
	
	@Queued
	public void handleSelectCombo(PacketSelectCardCombo packet)
	{
		List<Card> selected = Arrays.stream(packet.selectedCards).mapToObj(Card::getCard).collect(Collectors.toList());
		List<Card> selectable = Arrays.stream(packet.availableCards).mapToObj(Card::getCard).collect(Collectors.toList());
		client.setAwaitingResponse(false);
		client.getGameState().setClientActionState(new ActionStateClientSelectCardCombo(selected, selectable));
	}
	
	@Queued
	public void handleTurnOrder(PacketTurnOrder packet)
	{
		client.setTurnOrder(client.getPlayersByIDs(packet.order));
	}
	
	@Queued
	public void handleGameRules(PacketGameRules packet)
	{
		client.getRules().applyGameRules(packet.getGameRules());
		client.getTableScreen().getMoves().updateVisibleMaxMoves();
		for (Player p : client.getAllPlayers())
		{
			p.getBank().getUI().updateGraphics();
		}
	}
	
	public void handleOpenChat(PacketSetChatOpen packet)
	{
		client.getTableScreen().getChat().setChatOpen(packet.open);
	}
	
	public void handleRemoveMessageCategory(PacketRemoveMessageCategory packet)
	{
		client.getTableScreen().getChat().removeMessageCategory(packet.category);
	}
	
	@Queued
	public void handleSetAwaitingResponse(PacketSetAwaitingResponse packet)
	{
		client.setAwaitingResponse(packet.awaiting);
	}
	
	/**
	 * Indicates that the packet handler should be ran in the event queue.
	 */
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.METHOD)
	private @interface Queued {}
}
