package oldmana.general.md.server.net;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import oldmana.general.md.net.packet.client.PacketLogin;
import oldmana.general.md.net.packet.client.action.PacketActionAccept;
import oldmana.general.md.net.packet.client.action.PacketActionChangeSetColor;
import oldmana.general.md.net.packet.client.action.PacketActionDiscard;
import oldmana.general.md.net.packet.client.action.PacketActionDraw;
import oldmana.general.md.net.packet.client.action.PacketActionEndTurn;
import oldmana.general.md.net.packet.client.action.PacketActionMoveProperty;
import oldmana.general.md.net.packet.client.action.PacketActionPay;
import oldmana.general.md.net.packet.client.action.PacketActionPlayCardAction;
import oldmana.general.md.net.packet.client.action.PacketActionPlayCardBank;
import oldmana.general.md.net.packet.client.action.PacketActionPlayCardProperty;
import oldmana.general.md.net.packet.client.action.PacketActionPlayCardSpecial;
import oldmana.general.md.net.packet.client.action.PacketActionPlayMultiCardAction;
import oldmana.general.md.net.packet.client.action.PacketActionSelectPlayer;
import oldmana.general.md.net.packet.client.action.PacketActionSelectPlayerProperty;
import oldmana.general.md.net.packet.client.action.PacketActionSelectPlayerMonopoly;
import oldmana.general.md.net.packet.client.action.PacketActionSelectSelfPlayerProperty;
import oldmana.general.md.net.packet.client.action.PacketActionUndoCard;
import oldmana.general.md.net.packet.server.PacketCardActionRentData;
import oldmana.general.md.net.packet.server.PacketCardCollectionData;
import oldmana.general.md.net.packet.server.PacketCardData;
import oldmana.general.md.net.packet.server.PacketCardPropertyData;
import oldmana.general.md.net.packet.server.PacketDestroyCardCollection;
import oldmana.general.md.net.packet.server.PacketDestroyPlayer;
import oldmana.general.md.net.packet.server.PacketHandshake;
import oldmana.general.md.net.packet.server.PacketKick;
import oldmana.general.md.net.packet.server.PacketMoveCard;
import oldmana.general.md.net.packet.server.PacketMovePropertySet;
import oldmana.general.md.net.packet.server.PacketMoveRevealCard;
import oldmana.general.md.net.packet.server.PacketMoveUnknownCard;
import oldmana.general.md.net.packet.server.PacketPlayerInfo;
import oldmana.general.md.net.packet.server.PacketPlayerStatus;
import oldmana.general.md.net.packet.server.PacketPropertySetColor;
import oldmana.general.md.net.packet.server.PacketPropertySetData;
import oldmana.general.md.net.packet.server.PacketRefresh;
import oldmana.general.md.net.packet.server.PacketStatus;
import oldmana.general.md.net.packet.server.PacketUndoCardStatus;
import oldmana.general.md.net.packet.server.PacketUnknownCardCollectionData;
import oldmana.general.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.general.md.net.packet.server.actionstate.PacketActionStateRent;
import oldmana.general.md.net.packet.server.actionstate.PacketActionStateStealProperty;
import oldmana.general.md.net.packet.server.actionstate.PacketActionStateStealMonopoly;
import oldmana.general.md.net.packet.server.actionstate.PacketActionStateTradeProperties;
import oldmana.general.md.net.packet.server.actionstate.PacketUpdateActionStateAccepted;
import oldmana.general.md.net.packet.server.actionstate.PacketUpdateActionStateRefusal;
import oldmana.general.md.net.packet.server.actionstate.PacketUpdateActionStateTarget;
import oldmana.general.md.server.Client;
import oldmana.general.md.server.MDServer;
import oldmana.general.md.server.Player;
import oldmana.general.md.server.PlayerRegistry;
import oldmana.general.md.server.card.Card;
import oldmana.general.md.server.card.CardAction;
import oldmana.general.md.server.card.CardProperty;
import oldmana.general.md.server.card.CardRegistry;
import oldmana.general.md.server.card.CardProperty.PropertyColor;
import oldmana.general.md.server.card.action.CardActionDoubleTheRent;
import oldmana.general.md.server.card.action.CardActionJustSayNo;
import oldmana.general.md.server.card.action.CardActionRent;
import oldmana.general.md.server.card.collection.CardCollectionRegistry;
import oldmana.general.md.server.card.collection.Hand;
import oldmana.general.md.server.card.collection.PropertySet;
import oldmana.general.md.server.state.ActionState;
import oldmana.general.md.server.state.ActionStateRent;
import oldmana.general.md.server.state.ActionStateStealMonopoly;
import oldmana.general.md.server.state.ActionStateStealProperty;
import oldmana.general.md.server.state.ActionStateTargetPlayer;
import oldmana.general.md.server.state.ActionStateTargetPlayerMonopoly;
import oldmana.general.md.server.state.ActionStateTargetPlayerProperty;
import oldmana.general.md.server.state.ActionStateTargetSelfPlayerProperty;
import oldmana.general.md.server.state.ActionStateTradeProperties;
import oldmana.general.md.server.state.GameState;
import oldmana.general.mjnetworkingapi.packet.Packet;

public class NetServerHandler
{
	public static int PROTOCOL_VERSION = 2;
	public static int PROTOCOL_MINIMUM = 2;
	
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
		Packet.registerPacket(PacketActionSelectPlayerProperty.class);
		Packet.registerPacket(PacketActionSelectSelfPlayerProperty.class);
		Packet.registerPacket(PacketActionSelectPlayerMonopoly.class);
		Packet.registerPacket(PacketActionUndoCard.class);
		
		Packet.registerPacket(PacketActionStateBasic.class);
		Packet.registerPacket(PacketActionStateRent.class);
		Packet.registerPacket(PacketActionStateStealProperty.class);
		Packet.registerPacket(PacketActionStateTradeProperties.class);
		Packet.registerPacket(PacketActionStateStealMonopoly.class);
		Packet.registerPacket(PacketUpdateActionStateAccepted.class);
		Packet.registerPacket(PacketUpdateActionStateRefusal.class);
		Packet.registerPacket(PacketUpdateActionStateTarget.class);
		
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
			System.out.println("Processing: " + packet.getClass().getName());
			if (client instanceof Player)
			{
				Player player = (Player) client;
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
				if (packet instanceof PacketLogin)
				{
					handleLogin(client, (PacketLogin) packet);
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
			if (registry.containsID(uid))
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
					Player player = new Player(server, uid, client.getNet(), registry.getNameOf(packet.getUID()));
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
		Card card = CardRegistry.getCard(packet.id);
		if (checkIntegrity(player, card, true))
		{
			player.getHand().transferCard(card, player.getBank());
			player.addRevokableCard(card);
			
			if (player.getHand().getCardCount() == 0)
			{
				player.clearRevokableCards();
				server.getDeck().drawCards(player, 5, 1.2);
			}
			
			server.getGameState().decrementTurn();
		}
	}
	
	public void handlePlayCardProperty(Player player, PacketActionPlayCardProperty packet)
	{
		Card card = CardRegistry.getCard(packet.id);
		CardProperty property = (CardProperty) card;
		if (checkIntegrity(player, card, true))
		{
			if (packet.setId > -1)
			{
				PropertySet set = player.getPropertySetById(packet.setId);
				player.getHand().transferCard(card, set);
				player.addRevokableCard(card);
			}
			else
			{
				if (property.isSingleColor() && player.hasSolidPropertySet(property.getColor()))
				{
					PropertySet set = player.getSolidPropertySet(property.getColor());
					card.transfer(set);
					set.checkMaxProperties();
					player.addRevokableCard(card);
				}
				else
				{
					PropertySet set = player.createPropertySet();
					player.getHand().transferCard(card, set);
					player.addRevokableCard(card);
				}
			}
			
			if (player.getHand().getCardCount() == 0)
			{
				player.clearRevokableCards();
				server.getDeck().drawCards(player, 5, 1.2);
			}
			
			server.getGameState().decrementTurn();
		}
	}
	
	public void handlePlayCardAction(Player player, PacketActionPlayCardAction packet)
	{
		CardAction card = (CardAction) CardRegistry.getCard(packet.id);
		GameState gs = server.getGameState();
		if (gs.getActivePlayer() == player && gs.getTurnsRemaining() > 0)
		{
			if (card.canPlayCard(player))
			{
				// Play card
				card.transfer(server.getDiscardPile());
				if (card.marksPreviousUnrevocable())
				{
					player.clearRevokableCards();
				}
				if (card.isRevocable())
				{
					player.addRevokableCard(card);
				}
				server.getGameState().decrementTurn();
				card.playCard(player);
				
				if (player.getHand().getCardCount() == 0)
				{
					player.clearRevokableCards();
					server.getDeck().drawCards(player, 5, 1.2);
				}
			}
			else
			{
				server.getGameState().resendActionState(player);
			}
		}
	}
	
	public void handlePlayCardSpecial(Player player, PacketActionPlayCardSpecial packet)
	{
		Card card = CardRegistry.getCard(packet.id);
		ActionState state = server.getGameState().getCurrentActionState();
		if (card instanceof CardActionJustSayNo)
		{
			Player target = server.getPlayerByID(packet.data);
			if (state.getActionOwner() == player && state.isTarget(target) && state.getActionTarget(target).isRefused())
			{
				state.setRefused(target, false);
				card.transfer(server.getDiscardPile());
			}
			else if (state.isTarget(player) && !state.getActionTarget(player).isRefused())
			{
				state.setRefused(player, true);
				card.transfer(server.getDiscardPile());
			}
			if (player.getHand().getCardCount() == 0)
			{
				player.clearRevokableCards();
				server.getDeck().drawCards(player, 5, 1.2);
			}
			if (state.isFinished())
			{
				server.getGameState().nextNaturalActionState();
			}
		}
	}
	
	public void handlePlayMultiCardAction(Player player, PacketActionPlayMultiCardAction packet)
	{
		if (packet.ids.length == 2)
		{
			Card c1 = CardRegistry.getCard(packet.ids[0]);
			Card c2 = CardRegistry.getCard(packet.ids[1]);
			if (c1 instanceof CardActionRent && c2 instanceof CardActionDoubleTheRent)
			{
				CardActionRent rentCard = (CardActionRent) c1;
				c1.transfer(server.getDiscardPile());
				c2.transfer(server.getDiscardPile());
				server.getGameState().decrementTurn();
				server.getGameState().decrementTurn();
				rentCard.playCard(player, 2);
				//server.getGameState().setCurrentActionState(new ActionStateRent(player, server.getPlayersExcluding(player), 
				//		player.getHighestValueRent(rentCard.getRentColors()) * 2));
				player.clearRevokableCards();
				
				if (player.getHand().getCardCount() == 0)
				{
					server.getDeck().drawCards(player, 5, 1.2);
				}
			}
		}
	}
	
	public void handleActionDiscard(Player player, PacketActionDiscard packet)
	{
		Card card = CardRegistry.getCard(packet.card);
		card.transfer(server.getDiscardPile());
		server.getGameState().nextNaturalActionState();
	}
	
	public void handlePay(Player player, PacketActionPay packet)
	{
		if (server.getGameState().getCurrentActionState() instanceof ActionStateRent)
		{
			ActionStateRent rent = (ActionStateRent) server.getGameState().getCurrentActionState();
			rent.playerPaid(player, CardRegistry.getCards(packet.ids));
		}
	}
	
	public void handleActionDraw(Player player, PacketActionDraw packet)
	{
		server.getDeck().drawCards(player, 2);
		server.getGameState().markDrawn();
		server.getGameState().nextNaturalActionState();
	}
	
	public void handleActionEndTurn(Player player, PacketActionEndTurn packet)
	{
		server.getGameState().nextTurn();
		server.getGameState().nextNaturalActionState();
	}
	
	public void handleActionMoveProperty(Player player, PacketActionMoveProperty packet)
	{
		Card card = CardRegistry.getCard(packet.id);
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
		PropertySet set = (PropertySet) CardCollectionRegistry.getCardCollection(packet.setId);
		set.setEffectiveColor(PropertyColor.fromID(packet.color));
		server.getGameState().resendActionState(player);
		server.getGameState().checkWin();
	}
	
	public void handleActionSelectPlayer(Player player, PacketActionSelectPlayer packet)
	{
		ActionState state = server.getGameState().getCurrentActionState();
		if (state instanceof ActionStateTargetPlayer)
		{
			((ActionStateTargetPlayer) state).playerSelected(server.getPlayerByID(packet.player));
		}
		else
		{
			server.getGameState().resendActionState(player);
		}
	}
	
	public void handleActionSelectSelfPlayerProperty(Player player, PacketActionSelectSelfPlayerProperty packet)
	{
		ActionState state = server.getGameState().getCurrentActionState();
		if (state instanceof ActionStateTargetSelfPlayerProperty)
		{
			player.clearRevokableCards();
			server.getGameState().setCurrentActionState(new ActionStateTradeProperties((CardProperty) CardRegistry.getCard(packet.selfCard), 
					(CardProperty) CardRegistry.getCard(packet.otherCard)));
		}
		else
		{
			server.getGameState().resendActionState(player);
		}
	}
	
	public void handleActionSelectPlayerProperty(Player player, PacketActionSelectPlayerProperty packet)
	{
		ActionState state = server.getGameState().getCurrentActionState();
		if (state instanceof ActionStateTargetPlayerProperty)
		{
			player.clearRevokableCards();
			server.getGameState().setCurrentActionState(new ActionStateStealProperty(player, (CardProperty) CardRegistry.getCard(packet.id)));
		}
		else
		{
			server.getGameState().resendActionState(player);
		}
	}
	
	public void handleActionSelectPlayerMonopoly(Player player, PacketActionSelectPlayerMonopoly packet)
	{
		ActionState state = server.getGameState().getCurrentActionState();
		if (state instanceof ActionStateTargetPlayerMonopoly)
		{
			player.clearRevokableCards();
			server.getGameState().setCurrentActionState(new ActionStateStealMonopoly(player, (PropertySet) CardCollectionRegistry.getCardCollection(packet.id)));
		}
		else
		{
			server.getGameState().resendActionState(player);
		}
	}
	
	public void handleActionUndoCard(Player player, PacketActionUndoCard packet)
	{
		server.getGameState().undoCard(player.getLastRevokableCard());
		player.undoCard();
	}
	
	public void handleActionAccept(Player player, PacketActionAccept packet)
	{
		ActionState state = server.getGameState().getCurrentActionState();
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
	
	public boolean checkIntegrity(Player player, Card card, boolean checkHand)
	{
		if (card == null)
		{
			player.sendPacket(new PacketKick("Invalid card ID"));
			server.disconnectPlayer(player);
			return false;
		}
		Hand hand = player.getHand();
		if (!hand.hasCard(card))
		{
			player.sendPacket(new PacketKick("Tried to play a card not in your hand!"));
			server.disconnectPlayer(player);
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
