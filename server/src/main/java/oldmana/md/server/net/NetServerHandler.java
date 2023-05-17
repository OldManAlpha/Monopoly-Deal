package oldmana.md.server.net;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.NetHandler;
import oldmana.md.net.packet.client.PacketInitiateLogin;
import oldmana.md.net.packet.client.PacketLogin;
import oldmana.md.net.packet.client.PacketQuit;
import oldmana.md.net.packet.client.PacketSoundCache;
import oldmana.md.net.packet.client.action.*;
import oldmana.md.net.packet.server.*;
import oldmana.md.net.packet.universal.PacketChat;
import oldmana.md.net.packet.universal.PacketKeepConnected;
import oldmana.md.server.playerui.ChatLinkHandler.ChatLink;
import oldmana.md.server.Client;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.PlayerRegistry;
import oldmana.md.server.PlayerRegistry.RegisteredPlayer;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardBuilding;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.PropertyColor;
import oldmana.md.server.card.collection.CardCollection;
import oldmana.md.server.card.collection.Hand;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.card.control.CardButton;
import oldmana.md.server.event.PlayerJoinedEvent;
import oldmana.md.server.event.PlayerReconnectedEvent;
import oldmana.md.server.state.ActionState;
import oldmana.md.server.state.ActionStateRent;
import oldmana.md.server.state.ActionStateTargetAnyProperty;
import oldmana.md.server.state.ActionStateTargetPlayer;
import oldmana.md.server.state.ActionStateTargetPlayerMonopoly;
import oldmana.md.server.state.ActionStateTargetPlayerProperty;
import oldmana.md.server.state.ActionStateTargetSelfPlayerProperty;

public class NetServerHandler extends NetHandler
{
	private MDServer server;
	
	private Map<Class<? extends Packet>, Method> packetHandlers = new HashMap<Class<? extends Packet>, Method>();
	
	@SuppressWarnings("unchecked")
	public NetServerHandler(MDServer server)
	{
		this.server = server;
		
		// Find Packet Handlers
		for (Method m : getClass().getDeclaredMethods())
		{
			Class<?>[] params = m.getParameterTypes();
			if (params.length > 1)
			{
				Class<?> clazz = params[1];
				if (clazz.getSuperclass() == Packet.class)
				{
					packetHandlers.put((Class<? extends Packet>) clazz, m);
				}
			}
		}
	}
	
	public void processPackets(Client client)
	{
		for (Packet packet : client.getNet().getInPackets())
		{
			String packetName = packet.getClass().getName();
			if (packetName.startsWith("oldmana.md.net.packet"))
			{
				packetName = packetName.substring(22);
			}
			if (client instanceof Player)
			{
				Player player = (Player) client;
				if (server.isVerbose() && !(packet instanceof PacketKeepConnected))
				{
					System.out.println("Processing: " + packetName + " (" + player.getName() + ")");
				}
				try
				{
					packetHandlers.get(packet.getClass()).invoke(this, player, packet);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				if (server.isVerbose())
				{
					System.out.println("Processing: " + packetName + " (" + client.getHostAddress() + ")");
				}
				if (packet instanceof PacketInitiateLogin)
				{
					handleInitiateLogin(client, (PacketInitiateLogin) packet);
				}
				else if (packet instanceof PacketLogin)
				{
					handleLogin(client, (PacketLogin) packet);
				}
				else
				{
					System.out.println("Invalid packet type received from connecting client.");
				}
			}
		}
	}
	
	public void handleInitiateLogin(Client client, PacketInitiateLogin packet)
	{
		if (packet.protocolVersion < 19) // Explicitly checking for versions before this one because the login packet has changed
		{
			System.out.println("Client attempted login with an invalid protocol version (" + packet.protocolVersion + ")");
			System.out.println("We're on version " + NetHandler.PROTOCOL_VERSION);
			server.disconnectClient(client, "Invalid version! Server is on " + MDServer.VERSION);
			return;
		}
		client.sendPacket(new PacketServerInfo(PROTOCOL_VERSION, server.getServerKey()));
	}
	
	public void handleLogin(Client client, PacketLogin packet)
	{
		int version = packet.protocolVersion;
		if (version != NetHandler.PROTOCOL_VERSION)
		{
			System.out.println("Client attempted login with an invalid protocol version (" + packet.clientVersion + " / " + version + ")");
			System.out.println("We're on version " + NetHandler.PROTOCOL_VERSION);
			server.disconnectClient(client, "Invalid version! Server is on " + MDServer.VERSION);
			return;
		}
		
		ByteBuffer buffer = ByteBuffer.wrap(packet.id);
		UUID uuid = new UUID(buffer.getLong(), buffer.getLong());
		PlayerRegistry registry = server.getPlayerRegistry();
		
		server.removeClient(client);
		
		if (server.isPlayerWithUUIDLoggedIn(uuid))
		{
			Player player = server.getPlayerByUUID(uuid);
			if (player.isOnline())
			{
				player.disconnect("Logged in from another client!");
				System.out.println("Previously connected client of " + player.getDescription() + " at " +
						player.getHostAddress() + " was kicked");
			}
			if (!checkName(client, uuid, packet.name).isSuccessful())
			{
				return;
			}
			player.setOnline(true);
			String prevName = player.getName();
			player.setName(packet.name);
			player.setNet(client.getNet());
			player.refresh();
			System.out.println("Player " + player.getDescription() +
					(!prevName.equals(player.getName()) ? " (Previously named " + prevName + ")" : "") +
					" reconnected from " + player.getHostAddress());
			server.getEventManager().callEvent(new PlayerReconnectedEvent(player));
			return;
		}
		
		NameCheckResult result = checkName(client, uuid, packet.name);
		if (!result.isSuccessful())
		{
			return;
		}
		RegisteredPlayer rp = registry.getRegisteredPlayerByUUID(uuid);
		Player player = new Player(server, uuid, client.getNet(), rp.name, rp.op);
		server.addPlayer(player);
		player.setOnline(true);
		player.refresh();
		System.out.println((result == NameCheckResult.SUCCESS_NEW_PLAYER ? "New player " : "Player ") +
				player.getName() + " logged in with ID " + player.getID() + " from " + player.getHostAddress());
		server.getEventManager().callEvent(new PlayerJoinedEvent(player));
	}
	
	private NameCheckResult checkName(Client client, UUID uuid, String name)
	{
		if (name.isEmpty())
		{
			server.disconnectClient(client, "Must provide a name");
			System.out.println("Disconnected client for not providing a name");
			return NameCheckResult.FAILED;
		}
		else if (name.length() > 24)
		{
			server.disconnectClient(client, "Max name length allowed is 24 characters");
			System.out.println("Disconnected client for too long name: " + (name.length() > 200 ?
					name.length() + " characters" : name));
			return NameCheckResult.FAILED;
		}
		PlayerRegistry registry = server.getPlayerRegistry();
		RegisteredPlayer rp = registry.getRegisteredPlayerByUUID(uuid);
		boolean newPlayer = rp == null;
		if (newPlayer)
		{
			rp = registry.registerPlayer(uuid, name);
		}
		if (!rp.staticName && !rp.name.equals(name))
		{
			rp.name = name;
			registry.savePlayers();
		}
		Player sameNamed = server.getPlayerByName(rp.name);
		if (sameNamed != null && !sameNamed.getUUID().equals(uuid))
		{
			server.disconnectClient(client, "A player is already logged in with that name!");
			System.out.println("Disconnected client for using same name as an already logged in player: " + rp.name);
			return NameCheckResult.FAILED;
		}
		return newPlayer ? NameCheckResult.SUCCESS_NEW_PLAYER : NameCheckResult.SUCCESS;
	}
	
	private enum NameCheckResult
	{
		SUCCESS(true), SUCCESS_NEW_PLAYER(true), FAILED(false);
		
		private final boolean successful;
		
		NameCheckResult(boolean successful)
		{
			this.successful = successful;
		}
		
		boolean isSuccessful()
		{
			return successful;
		}
	}
	
	public void handleQuit(Player player, PacketQuit packet)
	{
		player.setOnline(false);
		System.out.println(player.getName() + " left (" + packet.reason + ")");
	}
	
	public void handleSoundCache(Player player, PacketSoundCache packet)
	{
		Map<String, Integer> cachedSounds = new HashMap<String, Integer>();
		for (int i = 0 ; i < packet.cachedSounds.length ; i++)
		{
			cachedSounds.put(packet.cachedSounds[i], packet.soundHashes[i]);
		}
		server.verifySounds(player, cachedSounds);
	}
	
	public void handleDraw(Player player, PacketActionDraw packet)
	{
		ActionState state = server.getGameState().getActionState();
		if (player.canDraw())
		{
			player.draw();
		}
		else
		{
			player.resendActionState();
		}
	}
	
	public void handlePlayCardBuilding(Player player, PacketActionPlayCardBuilding packet)
	{
		Card card = Card.getCard(packet.id);
		CardBuilding building = (CardBuilding) card;
		if (checkIntegrity(player, card, true))
		{
			PropertySet set = player.getPropertySetById(packet.setID);
			player.playCardBuilding(building, set);
		}
	}
	
	public void handleActionDiscard(Player player, PacketActionDiscard packet)
	{
		Card card = Card.getCard(packet.card);
		if (checkIntegrity(player, card, true))
		{
			if (!player.discard(card))
			{
				player.resendActionState();
			}
		}
		else
		{
			player.resendActionState();
		}
	}
	
	public void handlePay(Player player, PacketActionPay packet)
	{
		if (server.getGameState().getActionState() instanceof ActionStateRent)
		{
			ActionStateRent rent = (ActionStateRent) server.getGameState().getActionState();
			List<Card> cards = Card.getCards(packet.ids);
			for (Card card : cards)
			{
				if (card.getOwner() != player)
				{
					System.out.println("Player " + player.getDescription() +
							" attempted to pay rent with a card they do not own! (ID: " + card.getID() + ")");
					player.resendActionState();
					return;
				}
			}
			rent.playerPaid(player, cards);
		}
	}
	
	public void handleActionEndTurn(Player player, PacketActionEndTurn packet)
	{
		if (server.getGameState().getActivePlayer() == player)
		{
			player.endTurn();
		}
		else
		{
			player.resendActionState();
		}
	}
	
	public void handleActionMoveProperty(Player player, PacketActionMoveProperty packet)
	{
		Card card = Card.getCard(packet.id);
		if (card.getOwner() != player || !(card.getOwningCollection() instanceof PropertySet))
		{
			player.resendActionState();
			return;
		}
		CardProperty property = (CardProperty) card;
		PropertySet prevSet = (PropertySet) property.getOwningCollection();
		if (prevSet.isMonopoly() && prevSet.hasBuildings())
		{
			System.out.println(player.getName() + " tried to move a property off of a monopoly with buildings!");
			player.resendActionState();
			return;
		}
		if (packet.setId > -1)
		{
			PropertySet set = player.getPropertySetById(packet.setId);
			card.transfer(set);
		}
		else
		{
			player.safelyGrantProperty(property);
		}
		player.resendActionState();
		server.getGameState().checkWin();
	}
	
	public void handleActionChangeSetColor(Player player, PacketActionChangeSetColor packet)
	{
		PropertySet set = (PropertySet) CardCollection.getByID(packet.setId);
		PropertyColor color = PropertyColor.fromID(packet.color);
		if (set.getPossibleBaseColors().contains(color))
		{
			set.setEffectiveColor(color);
		}
		player.resendActionState();
		server.getGameState().checkWin();
	}
	
	public void handleActionSelectPlayer(Player player, PacketActionSelectPlayer packet)
	{
		ActionState state = server.getGameState().getActionState();
		if (state instanceof ActionStateTargetPlayer)
		{
			((ActionStateTargetPlayer) state).playerSelected(server.getPlayerByID(packet.player));
		}
		else
		{
			player.resendActionState();
		}
	}
	
	public void handleActionSelectProperties(Player player, PacketActionSelectProperties packet)
	{
		ActionState state = server.getGameState().getActionState();
		if (state instanceof ActionStateTargetPlayerProperty)
		{
			((ActionStateTargetPlayerProperty) state).onCardSelected((CardProperty) Card.getCard(packet.ids[0]));
		}
		else if (state instanceof ActionStateTargetSelfPlayerProperty)
		{
			((ActionStateTargetSelfPlayerProperty) state).onCardsSelected((CardProperty) Card.getCard(packet.ids[0]), 
					(CardProperty) Card.getCard(packet.ids[1]));
		}
		else if (state instanceof ActionStateTargetAnyProperty)
		{
			((ActionStateTargetAnyProperty) state).onCardSelected((CardProperty) Card.getCard(packet.ids[0]));
		}
		else
		{
			player.resendActionState();
		}
	}
	
	public void handleActionSelectPlayerMonopoly(Player player, PacketActionSelectPlayerMonopoly packet)
	{
		ActionState state = server.getGameState().getActionState();
		if (state instanceof ActionStateTargetPlayerMonopoly)
		{
			player.clearRevocableCards();
			((ActionStateTargetPlayerMonopoly) state).onSetSelected((PropertySet) CardCollection.getByID(packet.id));
		}
		else
		{
			player.resendActionState();
		}
	}
	
	public void handleActionUndoCard(Player player, PacketActionUndoCard packet)
	{
		player.undoCard();
	}
	
	public void handleActionAccept(Player player, PacketActionAccept packet)
	{
		ActionState state = server.getGameState().getActionState();
		Player target = server.getPlayerByID(packet.playerId);
		if (state.isTarget(player))
		{
			state.setAccepted(player, true);
		}
		else if (state.getActionOwner() == player && state.isRefused(target))
		{
			state.removeActionTarget(target);
		}
		server.getGameState().proceed();
	}
	
	public void handleKeepConnected(Player player, PacketKeepConnected packet)
	{
		player.setLastPing(server.getTickCount());
		player.setSentPing(false);
	}
	
	public void handleActionClickLink(Player player, PacketActionClickLink packet)
	{
		ChatLink link = server.getChatLinkHandler().getChatLinkByID(packet.id);
		if (link != null && link.getListener() != null)
		{
			link.getListener().linkClicked();
		}
	}
	
	public void handleButtonClick(Player player, PacketActionButtonClick packet)
	{
		player.getButton(packet.id).buttonClicked();
	}
	
	public void handleUseCardButton(Player player, PacketActionUseCardButton packet)
	{
		Card card = Card.getCard(packet.cardID);
		CardButton button = card.getControls().getEnabledButton(packet.pos);
		if (card.getOwner() == player && button != null)
		{
			button.click(player, packet.data);
		}
		else
		{
			player.resendActionState();
			player.resendCardButtons();
		}
	}
	
	public void handleRemoveBuilding(Player player, PacketActionRemoveBuilding packet)
	{
		Card card = Card.getCard(packet.card);
		if (card instanceof CardBuilding && card.getOwner() == player && card.getOwningCollection() instanceof PropertySet)
		{
			card.transfer(player.getBank());
		}
	}
	
	public void handleChat(Player player, PacketChat packet)
	{
		String msg = packet.getMessage().getUnformattedMessage();
		if (msg.startsWith("/"))
		{
			player.executeCommand(msg.substring(1));
		}
		else
		{
			player.chat(msg);
		}
	}
	
	public boolean checkIntegrity(Player player, Card card, boolean checkHand)
	{
		if (card == null)
		{
			player.disconnect("Invalid card ID");
			return false;
		}
		Hand hand = player.getHand();
		if (!hand.hasCard(card))
		{
			player.disconnect("Tried to use a card not in your hand!");
			return false;
		}
		return true;
	}
}
