package oldmana.md.server.net;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.NetHandler;
import oldmana.md.net.packet.client.PacketLogin;
import oldmana.md.net.packet.client.PacketQuit;
import oldmana.md.net.packet.client.PacketSoundCache;
import oldmana.md.net.packet.client.action.*;
import oldmana.md.net.packet.server.*;
import oldmana.md.net.packet.universal.PacketChat;
import oldmana.md.net.packet.universal.PacketKeepConnected;
import oldmana.md.server.ChatLinkHandler.ChatLink;
import oldmana.md.server.Client;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.PlayerRegistry;
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
import oldmana.md.server.event.UndoCardEvent;
import oldmana.md.server.state.ActionState;
import oldmana.md.server.state.ActionStateDraw;
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
					System.out.println("Processing: " + packetName + " (Connecting Client)");
				}
				if (packet instanceof PacketLogin)
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
	
	public void handleLogin(Client client, PacketLogin packet)
	{
		int version = packet.getProtocolVersion();
		if (version == NetHandler.PROTOCOL_VERSION)
		{
			PlayerRegistry registry = server.getPlayerRegistry();
			int uid = packet.getUID();
			if (registry.isUIDRegistered(uid))
			{
				server.removeClient(client);
				if (server.isPlayerWithUIDLoggedIn(uid))
				{
					Player player = server.getPlayerByUID(uid);
					if (player.isOnline())
					{
						player.sendPacket(new PacketKick("Logged in from another client!"));
						System.out.println("Previously connected client of " + player.getDescription() + " was kicked");
					}
					player.setOnline(true);
					player.setNet(client.getNet());
					player.sendPacket(new PacketHandshake(player.getID(), player.getName()));
					server.refreshPlayer(player);
					System.out.println("Player " + player.getDescription() + " reconnected");
					server.getEventManager().callEvent(new PlayerReconnectedEvent(player));
				}
				else
				{
					Player player = new Player(server, uid, client.getNet(), registry.getNameOf(packet.getUID()),
							registry.getRegisteredPlayerByUID(uid).op);
					player.sendPacket(new PacketHandshake(player.getID(), player.getName()));
					server.addPlayer(player);
					player.setOnline(true);
					server.refreshPlayer(player);
					System.out.println("Player " + player.getName() + " logged in with ID " + player.getID());
					server.getEventManager().callEvent(new PlayerJoinedEvent(player));
				}
			}
			else
			{
				System.out.println("Client attempted login with invalid user ID");
				client.sendPacket(new PacketKick("User ID does not exist"));
				server.disconnectClient(client);
			}
		}
		else
		{
			System.out.println("Client attempted login with an invalid protocol version (" + version + ")");
			client.sendPacket(new PacketKick("Invalid protocol version"));
			server.disconnectClient(client);
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
		if (state instanceof ActionStateDraw && state.getActionOwner() == player)
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
			building.transfer(set);
			
			player.addRevocableCard(card);
			
			player.checkEmptyHand();
			
			server.getGameState().decrementTurn();
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
		PropertySet set = (PropertySet) CardCollection.getCardCollection(packet.setId);
		PropertyColor color = PropertyColor.fromID(packet.color);
		if (set.getPossibleBaseColors().contains(color))
		{
			set.setEffectiveColor(PropertyColor.fromID(packet.color));
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
			((ActionStateTargetPlayerMonopoly) state).onSetSelected((PropertySet) CardCollection.getCardCollection(packet.id));
		}
		else
		{
			player.resendActionState();
		}
	}
	
	public void handleActionUndoCard(Player player, PacketActionUndoCard packet)
	{
		if (!player.canRevokeCard())
		{
			player.sendUndoStatus();
			return;
		}
		UndoCardEvent event = new UndoCardEvent(player, player.getLastRevocableCard());
		server.getEventManager().callEvent(event);
		if (!event.isCanceled())
		{
			server.getGameState().undoCard(player.getLastRevocableCard());
			player.undoCard();
		}
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
		if (state.isFinished())
		{
			server.getGameState().nextNaturalActionState();
		}
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
	
	public void handleChat(Player player, PacketChat packet)
	{
		String msg = packet.message;
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
