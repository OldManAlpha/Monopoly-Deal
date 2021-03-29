package oldmana.md.server.net;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.client.PacketLogin;
import oldmana.md.net.packet.client.action.*;
import oldmana.md.net.packet.server.*;
import oldmana.md.net.packet.server.actionstate.*;
import oldmana.md.net.packet.universal.PacketChat;
import oldmana.md.server.Client;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.PlayerRegistry;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.CardProperty.PropertyColor;
import oldmana.md.server.card.CardSpecial;
import oldmana.md.server.card.action.CardActionDoubleTheRent;
import oldmana.md.server.card.action.CardActionRent;
import oldmana.md.server.card.collection.CardCollection;
import oldmana.md.server.card.collection.Hand;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.event.CardDiscardEvent;
import oldmana.md.server.event.DeckDrawEvent;
import oldmana.md.server.event.PostCardBankedEvent;
import oldmana.md.server.event.PreCardBankedEvent;
import oldmana.md.server.event.UndoCardEvent;
import oldmana.md.server.state.ActionState;
import oldmana.md.server.state.ActionStateRent;
import oldmana.md.server.state.ActionStateStealMonopoly;
import oldmana.md.server.state.ActionStateTargetAnyProperty;
import oldmana.md.server.state.ActionStateTargetPlayer;
import oldmana.md.server.state.ActionStateTargetPlayerMonopoly;
import oldmana.md.server.state.ActionStateTargetPlayerProperty;
import oldmana.md.server.state.ActionStateTargetSelfPlayerProperty;
import oldmana.md.server.state.GameState;

public class NetServerHandler
{
	public static int PROTOCOL_VERSION = 4;
	public static int PROTOCOL_MINIMUM = 4;
	
	private MDServer server;
	
	private Map<Class<? extends Packet>, Method> packetHandlers = new HashMap<Class<? extends Packet>, Method>();
	
	public NetServerHandler(MDServer server)
	{
		this.server = server;
	}
	
	@SuppressWarnings("unchecked")
	public void registerPackets()
	{
		Packet.registerPacket(PacketLogin.class);
		
		Packet.registerPacket(PacketHandshake.class);
		Packet.registerPacket(PacketCardCollectionData.class);
		Packet.registerPacket(PacketCardData.class);
		Packet.registerPacket(PacketCardActionRentData.class);
		Packet.registerPacket(PacketCardDescription.class);
		Packet.registerPacket(PacketCardPropertyData.class);
		Packet.registerPacket(PacketDestroyCardCollection.class);
		Packet.registerPacket(PacketPropertySetColor.class);
		Packet.registerPacket(PacketStatus.class);
		Packet.registerPacket(PacketKick.class);
		Packet.registerPacket(PacketMoveCard.class);
		Packet.registerPacket(PacketMovePropertySet.class);
		Packet.registerPacket(PacketMoveRevealCard.class);
		Packet.registerPacket(PacketMoveUnknownCard.class);
		Packet.registerPacket(PacketPlayerInfo.class);
		Packet.registerPacket(PacketPropertySetData.class);
		Packet.registerPacket(PacketPlayerStatus.class);
		Packet.registerPacket(PacketDestroyPlayer.class);
		Packet.registerPacket(PacketRefresh.class);
		Packet.registerPacket(PacketUnknownCardCollectionData.class);
		Packet.registerPacket(PacketUndoCardStatus.class);
		Packet.registerPacket(PacketSoundData.class);
		Packet.registerPacket(PacketPlaySound.class);
		
		Packet.registerPacket(PacketActionAccept.class);
		Packet.registerPacket(PacketActionDraw.class);
		Packet.registerPacket(PacketActionEndTurn.class);
		Packet.registerPacket(PacketActionMoveProperty.class);
		Packet.registerPacket(PacketActionChangeSetColor.class);
		Packet.registerPacket(PacketActionPay.class);
		Packet.registerPacket(PacketActionPlayCardAction.class);
		Packet.registerPacket(PacketActionPlayCardBank.class);
		Packet.registerPacket(PacketActionPlayCardProperty.class);
		Packet.registerPacket(PacketActionPlayCardSpecial.class);
		Packet.registerPacket(PacketActionPlayMultiCardAction.class);
		Packet.registerPacket(PacketActionDiscard.class);
		Packet.registerPacket(PacketActionSelectPlayer.class);
		Packet.registerPacket(PacketActionSelectProperties.class);
		Packet.registerPacket(PacketActionSelectPlayerMonopoly.class);
		Packet.registerPacket(PacketActionUndoCard.class);
		
		Packet.registerPacket(PacketActionStateBasic.class);
		Packet.registerPacket(PacketActionStateRent.class);
		Packet.registerPacket(PacketActionStatePropertiesSelected.class);
		Packet.registerPacket(PacketActionStateStealMonopoly.class);
		Packet.registerPacket(PacketUpdateActionStateAccepted.class);
		Packet.registerPacket(PacketUpdateActionStateRefusal.class);
		Packet.registerPacket(PacketUpdateActionStateTarget.class);
		
		// Client <-> Server
		Packet.registerPacket(PacketChat.class);
		
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
			if (client instanceof Player)
			{
				Player player = (Player) client;
				System.out.println("Processing: " + packet.getClass().getName() + " (" + player.getName() + ")");
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
				System.out.println("Processing: " + packet.getClass().getName() + " (Connecting Client)");
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
		if (version >= PROTOCOL_MINIMUM && version <= PROTOCOL_VERSION)
		{
			PlayerRegistry registry = server.getPlayerRegistry();
			int uid = packet.getUID();
			if (registry.isUIDRegistered(uid))
			{
				server.removeClient(client);
				if (server.isPlayerWithUIDLoggedIn(uid))
				{
					Player player = server.getPlayerByUID(uid);
					player.setNet(client.getNet());
					player.sendPacket(new PacketHandshake(player.getID(), player.getName()));
					player.setLoggedIn(true);
					server.refreshPlayer(player);
					server.broadcastPacket(new PacketPlayerStatus(player.getID(), true), player);
					System.out.println("Player " + player.getName() + " reconnected (ID: " + player.getID() + ")");
				}
				else
				{
					Player player = new Player(server, uid, client.getNet(), registry.getNameOf(packet.getUID()), registry.getRegisteredPlayerByUID(uid).op);
					player.sendPacket(new PacketHandshake(player.getID(), player.getName()));
					server.addPlayer(player);
					//server.broadcastPacket(new PacketPlayerInfo(player.getID(), player.getName()), player);
					player.setLoggedIn(true);
					server.refreshPlayer(player);
					System.out.println("Player " + player.getName() + " logged in with ID " + player.getID());
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
	
	public void handleDraw(Player player, PacketActionDraw packet)
	{
		server.getDeck().drawCards(player, 2);
		server.getGameState().markDrawn();
		server.getGameState().nextNaturalActionState();
	}
	
	public void handlePlayCardBank(Player player, PacketActionPlayCardBank packet)
	{
		Card card = Card.getCard(packet.id);
		if (checkIntegrity(player, card, true))
		{
			PreCardBankedEvent preEvent = new PreCardBankedEvent(player, card);
			server.getEventManager().callEvent(preEvent);
			if (!preEvent.isCanceled())
			{
				player.getHand().transferCard(card, player.getBank());
				player.addRevocableCard(card);
				PostCardBankedEvent postEvent = new PostCardBankedEvent(player, card);
				server.getEventManager().callEvent(postEvent);
				player.checkEmptyHand();
				
				server.getGameState().decrementTurn();
			}
			else
			{
				server.getGameState().resendActionState(player);
			}
		}
	}
	
	public void handlePlayCardProperty(Player player, PacketActionPlayCardProperty packet)
	{
		Card card = Card.getCard(packet.id);
		CardProperty property = (CardProperty) card;
		if (checkIntegrity(player, card, true))
		{
			if (packet.setId > -1)
			{
				PropertySet set = player.getPropertySetById(packet.setId);
				player.getHand().transferCard(card, set);
			}
			else
			{
				if (property.isSingleColor() && player.hasSolidPropertySet(property.getColor()))
				{
					PropertySet set = player.getSolidPropertySet(property.getColor());
					card.transfer(set);
					set.checkMaxProperties();
				}
				else
				{
					PropertySet set = player.createPropertySet();
					player.getHand().transferCard(card, set);
				}
			}
			player.addRevocableCard(card);
			
			player.checkEmptyHand();
			
			server.getGameState().decrementTurn();
		}
	}
	
	public void handlePlayCardAction(Player player, PacketActionPlayCardAction packet)
	{
		CardAction card = (CardAction) Card.getCard(packet.id);
		GameState gs = server.getGameState();
		if (gs.getActivePlayer() == player && gs.getTurnsRemaining() > 0)
		{
			if (card.canPlayCard(player))
			{
				card.transfer(server.getDiscardPile());
				if (card.clearsRevocableCards())
				{
					player.clearRevocableCards();
				}
				if (card.isRevocable())
				{
					player.addRevocableCard(card);
				}
				server.getGameState().decrementTurn();
				card.playCard(player);
				
				player.checkEmptyHand();
			}
			else
			{
				server.getGameState().resendActionState(player);
			}
		}
	}
	
	public void handlePlayCardSpecial(Player player, PacketActionPlayCardSpecial packet)
	{
		Card card = Card.getCard(packet.id);
		if (card instanceof CardSpecial)
		{
			((CardSpecial) card).playCard(player, packet.data);
		}
	}
	
	public void handlePlayMultiCardAction(Player player, PacketActionPlayMultiCardAction packet)
	{
		if (packet.ids.length == 2)
		{
			Card c1 = Card.getCard(packet.ids[0]);
			Card c2 = Card.getCard(packet.ids[1]);
			if (c1 instanceof CardActionRent && c2 instanceof CardActionDoubleTheRent)
			{
				CardActionRent rentCard = (CardActionRent) c1;
				if (rentCard.canPlayCard(player))
				{
					c1.transfer(server.getDiscardPile());
					c2.transfer(server.getDiscardPile());
					server.getGameState().decrementTurn();
					server.getGameState().decrementTurn();
					rentCard.playCard(player, 2);
					player.clearRevocableCards();
					
					player.checkEmptyHand();
				}
				else
				{
					server.getGameState().resendActionState(player);
				}
			}
		}
	}
	
	public void handleActionDiscard(Player player, PacketActionDiscard packet)
	{
		Card card = Card.getCard(packet.card);
		card.transfer(server.getDiscardPile());
		server.getGameState().nextNaturalActionState();
		server.getEventManager().callEvent(new CardDiscardEvent(player, card));
	}
	
	public void handlePay(Player player, PacketActionPay packet)
	{
		if (server.getGameState().getActionState() instanceof ActionStateRent)
		{
			ActionStateRent rent = (ActionStateRent) server.getGameState().getActionState();
			rent.playerPaid(player, Card.getCards(packet.ids));
		}
	}
	
	public void handleActionDraw(Player player, PacketActionDraw packet)
	{
		server.getDeck().drawCards(player, 2);
		server.getGameState().markDrawn();
		server.getGameState().nextNaturalActionState();
		server.getEventManager().callEvent(new DeckDrawEvent(player));
	}
	
	public void handleActionEndTurn(Player player, PacketActionEndTurn packet)
	{
		server.getGameState().nextTurn();
	}
	
	public void handleActionMoveProperty(Player player, PacketActionMoveProperty packet)
	{
		Card card = Card.getCard(packet.id);
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
		server.getGameState().resendActionState(player);
		server.getGameState().checkWin();
	}
	
	public void handleActionChangeSetColor(Player player, PacketActionChangeSetColor packet)
	{
		PropertySet set = (PropertySet) CardCollection.getCardCollection(packet.setId);
		set.setEffectiveColor(PropertyColor.fromID(packet.color));
		server.getGameState().resendActionState(player);
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
			server.getGameState().resendActionState(player);
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
			server.getGameState().resendActionState(player);
		}
	}
	
	public void handleActionSelectPlayerMonopoly(Player player, PacketActionSelectPlayerMonopoly packet)
	{
		ActionState state = server.getGameState().getActionState();
		if (state instanceof ActionStateTargetPlayerMonopoly)
		{
			player.clearRevocableCards();
			server.getGameState().setActionState(new ActionStateStealMonopoly(player, (PropertySet) CardCollection.getCardCollection(packet.id)));
		}
		else
		{
			server.getGameState().resendActionState(player);
		}
	}
	
	public void handleActionUndoCard(Player player, PacketActionUndoCard packet)
	{
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
	
	public void handleChat(Player player, PacketChat packet)
	{
		String msg = packet.message;
		if (msg.startsWith("/"))
		{
			server.getCommandHandler().executeCommand(player, msg.substring(1));
		}
		else
		{
			System.out.println(player.getName() + ": " + msg);
			server.broadcastMessage(player.getName() + ": " + msg);
		}
	}
	
	public boolean checkIntegrity(Player player, Card card, boolean checkHand)
	{
		if (card == null)
		{
			player.sendPacket(new PacketKick("Invalid card ID"));
			server.kickPlayer(player);
			return false;
		}
		Hand hand = player.getHand();
		if (!hand.hasCard(card))
		{
			player.sendPacket(new PacketKick("Tried to play a card not in your hand!"));
			server.kickPlayer(player);
			return false;
		}
		return true;
	}
	
	/*
	public void handlePlayCard(Player player, PacketPlayCard packet)
	{
		Card card = CardRegistry.getCard(packet.getCardID());
		if (card == null)
		{
			player.sendPacket(new PacketKick("Invalid card ID played"));
			server.disconnectPlayer(player);
			return;
		}
		Hand hand = player.getHand();
		if (!hand.hasCard(card))
		{
			player.sendPacket(new PacketKick("Tried to play a card not in your hand!"));
			server.disconnectPlayer(player);
			return;
		}
		
	}
	*/
}
