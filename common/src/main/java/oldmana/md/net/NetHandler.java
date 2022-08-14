package oldmana.md.net;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.client.PacketLogin;
import oldmana.md.net.packet.client.PacketQuit;
import oldmana.md.net.packet.client.PacketSoundCache;
import oldmana.md.net.packet.client.action.PacketActionAccept;
import oldmana.md.net.packet.client.action.PacketActionButtonClick;
import oldmana.md.net.packet.client.action.PacketActionChangeSetColor;
import oldmana.md.net.packet.client.action.PacketActionClickLink;
import oldmana.md.net.packet.client.action.PacketActionDiscard;
import oldmana.md.net.packet.client.action.PacketActionDraw;
import oldmana.md.net.packet.client.action.PacketActionEndTurn;
import oldmana.md.net.packet.client.action.PacketActionMoveProperty;
import oldmana.md.net.packet.client.action.PacketActionPay;
import oldmana.md.net.packet.client.action.PacketActionPlayCardAction;
import oldmana.md.net.packet.client.action.PacketActionPlayCardBank;
import oldmana.md.net.packet.client.action.PacketActionPlayCardBuilding;
import oldmana.md.net.packet.client.action.PacketActionPlayCardProperty;
import oldmana.md.net.packet.client.action.PacketActionPlayCardSpecial;
import oldmana.md.net.packet.client.action.PacketActionPlayMultiCardAction;
import oldmana.md.net.packet.client.action.PacketActionSelectPlayer;
import oldmana.md.net.packet.client.action.PacketActionSelectPlayerMonopoly;
import oldmana.md.net.packet.client.action.PacketActionSelectProperties;
import oldmana.md.net.packet.client.action.PacketActionUndoCard;
import oldmana.md.net.packet.server.PacketButton;
import oldmana.md.net.packet.server.PacketCardActionRentData;
import oldmana.md.net.packet.server.PacketCardBuildingData;
import oldmana.md.net.packet.server.PacketCardCollectionData;
import oldmana.md.net.packet.server.PacketCardData;
import oldmana.md.net.packet.server.PacketCardDescription;
import oldmana.md.net.packet.server.PacketCardPropertyData;
import oldmana.md.net.packet.server.PacketDestroyButton;
import oldmana.md.net.packet.server.PacketDestroyCardCollection;
import oldmana.md.net.packet.server.PacketDestroyPlayer;
import oldmana.md.net.packet.server.PacketHandshake;
import oldmana.md.net.packet.server.PacketKick;
import oldmana.md.net.packet.server.PacketMoveCard;
import oldmana.md.net.packet.server.PacketMovePropertySet;
import oldmana.md.net.packet.server.PacketMoveRevealCard;
import oldmana.md.net.packet.server.PacketMoveUnknownCard;
import oldmana.md.net.packet.server.PacketPlaySound;
import oldmana.md.net.packet.server.PacketPlayerInfo;
import oldmana.md.net.packet.server.PacketPlayerStatus;
import oldmana.md.net.packet.server.PacketPropertyColors;
import oldmana.md.net.packet.server.PacketPropertySetColor;
import oldmana.md.net.packet.server.PacketPropertySetData;
import oldmana.md.net.packet.server.PacketRefresh;
import oldmana.md.net.packet.server.PacketSoundData;
import oldmana.md.net.packet.server.PacketStatus;
import oldmana.md.net.packet.server.PacketUndoCardStatus;
import oldmana.md.net.packet.server.PacketUnknownCardCollectionData;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.net.packet.server.actionstate.PacketActionStatePropertiesSelected;
import oldmana.md.net.packet.server.actionstate.PacketActionStateRent;
import oldmana.md.net.packet.server.actionstate.PacketActionStateStealMonopoly;
import oldmana.md.net.packet.server.actionstate.PacketUpdateActionStateAccepted;
import oldmana.md.net.packet.server.actionstate.PacketUpdateActionStateRefusal;
import oldmana.md.net.packet.server.actionstate.PacketUpdateActionStateTarget;
import oldmana.md.net.packet.universal.PacketChat;
import oldmana.md.net.packet.universal.PacketKeepConnected;
import oldmana.md.net.packet.universal.PacketPing;

public class NetHandler
{
	public static int PROTOCOL_VERSION = 14;
	
	public NetHandler()
	{
		registerPackets();
	}
	
	public void registerPackets()
	{
		// Client <-> Server
		Packet.registerPacket(PacketPing.class);
		
		// Client -> Server
		Packet.registerPacket(PacketLogin.class);
		
		// Server -> Client
		Packet.registerPacket(PacketHandshake.class);
		Packet.registerPacket(PacketKick.class);
		
		// Client -> Server
		Packet.registerPacket(PacketQuit.class);
		
		// Client <-> Server
		Packet.registerPacket(PacketChat.class);
		Packet.registerPacket(PacketKeepConnected.class);
		
		// ^ Ideally common protocol among future versions ^
		
		// Server -> Client
		Packet.registerPacket(PacketPropertyColors.class);
		Packet.registerPacket(PacketCardCollectionData.class);
		Packet.registerPacket(PacketCardData.class);
		Packet.registerPacket(PacketCardActionRentData.class);
		Packet.registerPacket(PacketCardDescription.class);
		Packet.registerPacket(PacketCardPropertyData.class);
		Packet.registerPacket(PacketCardBuildingData.class);
		Packet.registerPacket(PacketDestroyCardCollection.class);
		Packet.registerPacket(PacketPropertySetColor.class);
		Packet.registerPacket(PacketStatus.class);
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
		Packet.registerPacket(PacketButton.class);
		Packet.registerPacket(PacketDestroyButton.class);
		
		// Client -> Server
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
		Packet.registerPacket(PacketActionPlayCardBuilding.class);
		Packet.registerPacket(PacketActionPlayMultiCardAction.class);
		Packet.registerPacket(PacketActionDiscard.class);
		Packet.registerPacket(PacketActionSelectPlayer.class);
		Packet.registerPacket(PacketActionSelectProperties.class);
		Packet.registerPacket(PacketActionSelectPlayerMonopoly.class);
		Packet.registerPacket(PacketActionUndoCard.class);
		Packet.registerPacket(PacketActionClickLink.class);
		Packet.registerPacket(PacketActionButtonClick.class);
		
		Packet.registerPacket(PacketSoundCache.class);
		
		// Server -> Client
		Packet.registerPacket(PacketActionStateBasic.class);
		Packet.registerPacket(PacketActionStateRent.class);
		Packet.registerPacket(PacketActionStatePropertiesSelected.class);
		Packet.registerPacket(PacketActionStateStealMonopoly.class);
		Packet.registerPacket(PacketUpdateActionStateAccepted.class);
		Packet.registerPacket(PacketUpdateActionStateRefusal.class);
		Packet.registerPacket(PacketUpdateActionStateTarget.class);
	}
}
