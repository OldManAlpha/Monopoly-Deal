package oldmana.md.common.net;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.net.packet.client.*;
import oldmana.md.common.net.packet.client.action.*;
import oldmana.md.common.net.packet.server.*;
import oldmana.md.common.net.packet.server.actionstate.*;
import oldmana.md.common.net.packet.universal.*;

public class NetHandler
{
	public static int PROTOCOL_VERSION = 21;
	
	private static Class<? extends Packet>[] packets = new Class[]
	{
		// Client <-> Server
		PacketPing.class,
		
		// Client -> Server
		PacketInitiateLogin.class,
		PacketLogin.class,
		
		// Server -> Client
		PacketServerInfo.class,
		PacketHandshake.class,
		PacketKick.class,
		
		// Client -> Server
		PacketQuit.class,
		
		// Client <-> Server
		PacketChat.class,
		PacketKeepConnected.class,
		
		// ^ Ideally common protocol among future versions ^
		
		// Server -> Client
		PacketPropertyColors.class,
		PacketCardCollectionData.class,
		PacketCardData.class,
		PacketCardActionRentData.class,
		PacketCardDescription.class,
		PacketCardPropertyData.class,
		PacketCardBuildingData.class,
		PacketDestroyCardCollection.class,
		PacketPropertySetColor.class,
		PacketStatus.class,
		PacketMoveCard.class,
		PacketMovePropertySet.class,
		PacketMoveRevealCard.class,
		PacketMoveUnknownCard.class,
		PacketPlayerInfo.class,
		PacketPropertySetData.class,
		PacketUpdatePlayer.class,
		PacketDestroyPlayer.class,
		PacketRefresh.class,
		PacketUnknownCardCollectionData.class,
		PacketUndoCardStatus.class,
		PacketSoundData.class,
		PacketPlaySound.class,
		PacketPlayerButton.class,
		PacketDestroyButton.class,
		PacketCardButtons.class,
		PacketTurnOrder.class,
		PacketGameRules.class,
		PacketSetChatOpen.class,
		PacketRemoveMessageCategory.class,
		PacketSelectCardCombo.class,
		PacketSetAwaitingResponse.class,
		
		// Client -> Server
		PacketActionAccept.class,
		PacketActionDraw.class,
		PacketActionEndTurn.class,
		PacketActionMoveProperty.class,
		PacketActionChangeSetColor.class,
		PacketActionPay.class,
		PacketActionPlayCardBuilding.class,
		PacketActionDiscard.class,
		PacketActionSelectPlayer.class,
		PacketActionSelectProperties.class,
		PacketActionSelectPlayerMonopoly.class,
		PacketActionUndoCard.class,
		PacketActionClickLink.class,
		PacketActionButtonClick.class,
		PacketActionUseCardButton.class,
		PacketActionRemoveBuilding.class,
		PacketActionSelectCardCombo.class,
		
		PacketSoundCache.class,
		
		// Server -> Client
		PacketActionStatePlayerTurn.class,
		PacketActionStateBasic.class,
		PacketActionStateRent.class,
		PacketActionStatePropertiesSelected.class,
		PacketActionStatePropertySetTargeted.class,
		PacketUpdateActionStateTarget.class
	};
	
	static
	{
		for (Class<? extends Packet> clazz : packets)
		{
			Packet.registerPacket(clazz);
		}
	}
}
