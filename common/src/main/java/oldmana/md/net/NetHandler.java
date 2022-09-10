package oldmana.md.net;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.client.*;
import oldmana.md.net.packet.client.action.*;
import oldmana.md.net.packet.server.*;
import oldmana.md.net.packet.server.actionstate.*;
import oldmana.md.net.packet.universal.*;

public class NetHandler
{
	public static int PROTOCOL_VERSION = 17;
	
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
		Packet.registerPacket(PacketCardButton.class);
		Packet.registerPacket(PacketDestroyCardButton.class);
		
		// Client -> Server
		Packet.registerPacket(PacketActionAccept.class);
		Packet.registerPacket(PacketActionDraw.class);
		Packet.registerPacket(PacketActionEndTurn.class);
		Packet.registerPacket(PacketActionMoveProperty.class);
		Packet.registerPacket(PacketActionChangeSetColor.class);
		Packet.registerPacket(PacketActionPay.class);
		Packet.registerPacket(PacketActionPlayCardBuilding.class);
		Packet.registerPacket(PacketActionDiscard.class);
		Packet.registerPacket(PacketActionSelectPlayer.class);
		Packet.registerPacket(PacketActionSelectProperties.class);
		Packet.registerPacket(PacketActionSelectPlayerMonopoly.class);
		Packet.registerPacket(PacketActionUndoCard.class);
		Packet.registerPacket(PacketActionClickLink.class);
		Packet.registerPacket(PacketActionButtonClick.class);
		Packet.registerPacket(PacketActionUseCardButton.class);
		
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
