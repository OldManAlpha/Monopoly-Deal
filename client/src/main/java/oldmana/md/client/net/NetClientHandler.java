package oldmana.md.client.net;

import java.awt.Color;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.client.MDClient;
import oldmana.md.client.Player;
import oldmana.md.client.MDEventQueue.EventTask;
import oldmana.md.client.MDSoundSystem;
import oldmana.md.client.MDSoundSystem.MDSound;
import oldmana.md.client.Settings;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.Card.CardDescription;
import oldmana.md.client.card.CardAction;
import oldmana.md.client.card.CardActionRent;
import oldmana.md.client.card.CardBuilding;
import oldmana.md.client.card.CardButton;
import oldmana.md.client.card.CardButton.CardButtonPosition;
import oldmana.md.client.card.CardButton.CardButtonType;
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
import oldmana.md.client.gui.component.MDMovingCard.CardAnimationType;
import oldmana.md.client.gui.component.MDUndoButton;
import oldmana.md.client.gui.component.MDButton.ButtonColorScheme;
import oldmana.md.client.gui.component.MDClientButton;
import oldmana.md.client.gui.component.collection.MDHand;
import oldmana.md.client.state.ActionState;
import oldmana.md.client.state.ActionStateDiscard;
import oldmana.md.client.state.ActionStateDoNothing;
import oldmana.md.client.state.ActionStateDraw;
import oldmana.md.client.state.ActionStateFinishTurn;
import oldmana.md.client.state.ActionStatePlay;
import oldmana.md.client.state.ActionStatePlayerTargeted;
import oldmana.md.client.state.ActionStatePropertiesSelected;
import oldmana.md.client.state.ActionStateRent;
import oldmana.md.client.state.ActionStateStealMonopoly;
import oldmana.md.client.state.ActionStateTargetAnyProperty;
import oldmana.md.client.state.ActionStateTargetPlayer;
import oldmana.md.client.state.ActionStateTargetPlayerMonopoly;
import oldmana.md.client.state.ActionStateTargetPlayerProperty;
import oldmana.md.client.state.ActionStateTargetSelfPlayerProperty;
import oldmana.md.net.NetHandler;
import oldmana.md.net.packet.client.PacketLogin;
import oldmana.md.net.packet.client.PacketSoundCache;
import oldmana.md.net.packet.server.*;
import oldmana.md.net.packet.server.PacketCardCollectionData.CardCollectionType;
import oldmana.md.net.packet.server.actionstate.*;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.net.packet.universal.PacketChat;
import oldmana.md.net.packet.universal.PacketKeepConnected;

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
	
	public void processPackets(ConnectionThread connection)
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
		client.sendPacket(new PacketLogin(PROTOCOL_VERSION, digest.digest(), client.getSettings().getString("lastName")));
		client.getTableScreen().getTopbar().setText("Authenticating..");
		client.getTableScreen().getTopbar().repaint();
	}
	
	public void handleHandshake(PacketHandshake packet)
	{
		client.createThePlayer(packet.id, packet.name);
		List<MDSound> sounds = new ArrayList<MDSound>(MDSoundSystem.getSounds().values());
		String[] soundNames = new String[sounds.size()];
		int[] soundHashes = new int[sounds.size()];
		for (int i = 0 ; i < sounds.size() ; i++)
		{
			MDSound sound = sounds.get(i);
			soundNames[i] = sound.getName();
			soundHashes[i] = sound.getHash();
		}
		client.sendPacket(new PacketSoundCache(soundNames, soundHashes));
	}
	
	public void handlePropertyColors(PacketPropertyColors packet)
	{
		int len = packet.name.length;
		System.out.println(packet.name.length);
		System.out.println(packet.label.length);
		System.out.println(packet.color.length);
		System.out.println(packet.rents.length);
		System.out.println(packet.buildable.length);
		System.out.println(packet.rents.length);
		for (int i = 0 ; i < len ; i++)
		{
			PropertyColor.create(i, packet.name[i], packet.label[i], new Color(packet.color[i]), packet.buildable[i], packet.rents[i]);
		}
	}
	
	public void handleKick(PacketKick packet)
	{
		client.getTableScreen().getTopbar().setText("Disconnected: " + packet.reason);
		client.getTableScreen().getTopbar().repaint();
	}
	
	public void handleCardDescription(PacketCardDescription packet)
	{
		new CardDescription(packet.id, packet.description);
	}
	
	public void handleCardData(PacketCardData packet)
	{
		Card card;
		if ((card = Card.getCard(packet.id)) != null)
		{
			card.setValue(packet.value);
			card.setName(packet.name);
		}
		else
		{
			if (packet.type == 0)
			{
				card = new CardAction(packet.id, packet.value, packet.name);
			}
			else
			{
				card = new CardMoney(packet.id, packet.value);
			}
		}
		card.setDisplayName(packet.displayName);
		card.setFontSize(packet.fontSize);
		card.setDisplayOffsetY(packet.displayOffsetY);
		card.setDescription(CardDescription.getDescriptionByID(packet.description));
	}
	
	public void handleCardActionRentData(PacketCardActionRentData packet)
	{
		new CardActionRent(packet.id, packet.value, packet.name, PropertyColor.fromIDs(packet.colors).toArray(new PropertyColor[packet.colors.length]), 
				CardDescription.getDescriptionByID(packet.description));
	}
	
	public void handleCardPropertyData(PacketCardPropertyData packet)
	{
		new CardProperty(packet.id, PropertyColor.fromIDs(packet.colors), packet.base, packet.value, packet.name, 
				CardDescription.getDescriptionByID(packet.description));
	}
	
	public void handleCardBuildingData(PacketCardBuildingData packet)
	{
		CardBuilding card = new CardBuilding(packet.id, packet.value, packet.name, packet.tier, packet.rentAddition);
		card.setDisplayName(packet.displayName);
		card.setFontSize(packet.fontSize);
		card.setDisplayOffsetY(packet.displayOffsetY);
		card.setDescription(CardDescription.getDescriptionByID(packet.description));
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
	
	public void handlePlayerStatus(PacketPlayerStatus packet)
	{
		Player player = client.getPlayerByID(packet.player);
		player.setConnected(packet.connected);
		player.getUI().repaint();
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
		client.getTableScreen().getTopbar().repaint();
	}
	
	public void handleMoveCard(PacketMoveCard packet)
	{
		Card card = Card.getCard(packet.cardId);
		CardCollection collection = CardCollection.getCardCollection(packet.collectionId);
		collection.transferCard(card, packet.index, packet.time, CardAnimationType.fromID(packet.anim));
	}
	
	public void handleMoveRevealCard(PacketMoveRevealCard packet)
	{
		CardCollection from = CardCollection.getCardCollection(packet.from);
		CardCollection to = CardCollection.getCardCollection(packet.to);
		from.transferCardTo(Card.getCard(packet.cardId), to, packet.index, packet.time, CardAnimationType.fromID(packet.anim));
	}
	
	public void handleMoveUnknownCard(PacketMoveUnknownCard packet)
	{
		CardCollection from = CardCollection.getCardCollection(packet.from);
		CardCollection to = CardCollection.getCardCollection(packet.to);
		from.transferCardTo(null, to, -1, packet.time, CardAnimationType.fromID(packet.anim));
	}
	
	@Queued
	public void handleActionStateBasic(PacketActionStateBasic packet)
	{
		BasicActionState type = BasicActionState.fromID(packet.type);
		
		System.out.println("BASIC ACTION STATE " + packet.type);
		Player player = client.getPlayerByID(packet.player);
		if (type == BasicActionState.DO_NOTHING)
		{
			client.getGameState().setActionState(new ActionStateDoNothing());
		}
		else if (type == BasicActionState.DRAW)
		{
			client.getGameState().setActionState(new ActionStateDraw(player));
		}
		else if (type == BasicActionState.PLAY)
		{
			client.getGameState().setActionState(new ActionStatePlay(player, packet.data));
		}
		else if (type == BasicActionState.DISCARD)
		{
			client.getGameState().setActionState(new ActionStateDiscard(player));
		}
		else if (type == BasicActionState.FINISH_TURN)
		{
			client.getGameState().setActionState(new ActionStateFinishTurn(player));
		}
		else if (type == BasicActionState.TARGET_PLAYER)
		{
			client.getGameState().setActionState(new ActionStateTargetPlayer(player));
		}
		else if (type == BasicActionState.TARGET_PLAYER_PROPERTY)
		{
			client.getGameState().setActionState(new ActionStateTargetPlayerProperty(player));
		}
		else if (type == BasicActionState.TARGET_SELF_PLAYER_PROPERTY)
		{
			client.getGameState().setActionState(new ActionStateTargetSelfPlayerProperty(player));
		}
		else if (type == BasicActionState.TARGET_ANY_PROPERTY)
		{
			client.getGameState().setActionState(new ActionStateTargetAnyProperty(player));
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
		client.getTableScreen().repaint();
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
		client.getTableScreen().repaint();
	}
	
	public void handleDestroyCardCollection(PacketDestroyCardCollection packet)
	{
		CardCollection collection = CardCollection.getCardCollection(packet.id);
		if (collection instanceof PropertySet)
		{
			queueTask(() ->
			{
				Player player = collection.getOwner();
				player.destroyPropertySet((PropertySet) collection);
				player.getUI().getPropertySets().repaint();
			});
		}
	}
	
	@Queued
	public void handlePropertySetColor(PacketPropertySetColor packet)
	{
		PropertySet set = (PropertySet) CardCollection.getCardCollection(packet.id);
		set.setEffectiveColor(packet.color > -1 ? PropertyColor.fromID(packet.color) : null);
		set.getUI().repaint();
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
	public void handleActionStateStealMonopoly(PacketActionStateStealMonopoly packet)
	{
		client.setAwaitingResponse(false);
		client.getGameState().setActionState(new ActionStateStealMonopoly(client.getPlayerByID(packet.thief),
				(PropertySet) CardCollection.getCardCollection(packet.collection)));
		client.getTableScreen().repaint();
	}
	
	@Queued
	public void handleUpdateActionStateAccepted(PacketUpdateActionStateAccepted packet)
	{
		ActionState state = client.getGameState().getActionState();
		Player player = client.getPlayerByID(packet.target);
		if (state != null && state.isTarget(player))
		{
			client.setAwaitingResponse(false);
			state.setAccepted(player, packet.accepted);
			player.getUI().repaint();
		}
		else
		{
			System.err.println("Server sent illegal action state update (acceptance)");
		}
	}
	
	@Queued
	public void handleUpdateActionStateRefusal(PacketUpdateActionStateRefusal packet)
	{
		ActionState state = client.getGameState().getActionState();
		Player player = client.getPlayerByID(packet.target);
		if (state != null && state.isTarget(player))
		{
			client.setAwaitingResponse(false);
			state.setRefused(player, packet.refused);
			player.getUI().repaint();
			if ((packet.refused && state.getActionOwner() == client.getThePlayer()) || (!packet.refused && player == client.getThePlayer()))
			{
				client.getWindow().setAlert(true);
			}
		}
		else
		{
			System.err.println("Server sent illegal action state update (refusal)");
		}
	}
	
	@Queued
	public void handleUpdateActionStateTarget(PacketUpdateActionStateTarget packet)
	{
		ActionState state = client.getGameState().getActionState();
		if (state != null)
		{
			client.setAwaitingResponse(false);
			Player player = client.getPlayerByID(packet.target);
			if (!packet.isTarget)
			{
				state.setTarget(player, false);
			}
		}
		else
		{
			System.err.println("Server sent illegal action state update (target)");
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
	
	public void handleChat(PacketChat packet)
	{
		client.getTableScreen().getChat().addMessage(packet.message);
	}
	
	public void handleSoundData(PacketSoundData packet)
	{
		MDSoundSystem.addSound(packet.name, packet.data, packet.hash);
	}
	
	public void handlePlaySound(PacketPlaySound packet)
	{
		MDSoundSystem.playSound(packet.name);
	}
	
	@Queued
	public void handleButton(PacketButton packet)
	{
		Player player = client.getPlayerByID(packet.playerID);
		MDClientButton b = player.getUI().getAndCreateButton(player, packet.id);
		b.setEnabled(packet.enabled);
		b.setColorScheme(ButtonColorScheme.fromID(packet.color));
		b.setText(packet.name);
		b.setPriority(packet.priority);
		b.setMaxSize(packet.maxSize);
		b.repaint();
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
	
	public void handleCardButton(PacketCardButton packet)
	{
		Card card = Card.getCard(packet.cardID);
		CardButtonPosition pos = CardButtonPosition.fromID(packet.pos);
		card.setButton(pos, new CardButton(packet.text, pos, CardButtonType.fromID(packet.type),
				ButtonColorScheme.fromID(packet.color)));
		queueTask(() -> ((MDHand) client.getThePlayer().getHand().getUI()).removeOverlay());
	}
	
	public void handleDestroyCardButton(PacketDestroyCardButton packet)
	{
		Card card = Card.getCard(packet.cardID);
		card.removeButton(CardButtonPosition.fromID(packet.pos));
		queueTask(() -> ((MDHand) client.getThePlayer().getHand().getUI()).removeOverlay());
	}
	
	/**
	 * Indicates that the packet handler should be ran in the event queue.
	 */
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.METHOD)
	private @interface Queued {}
}
