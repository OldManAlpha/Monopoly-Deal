package oldmana.md.server.state;

import oldmana.md.net.packet.server.PacketStatus;
import oldmana.md.server.Player;
import oldmana.md.server.card.collection.Deck;

public class ActionStateTargetMutualAgreement extends ActionStateTargetPlayer
{
	public ActionStateTargetMutualAgreement(Player player)
	{
		super(player);
		getServer().broadcastPacket(new PacketStatus(player.getName() + " is making a mutual agreement"));
	}
	
	@Override
	public void playerSelected(Player player)
	{
		getActionOwner().clearRevokableCards();
		Deck deck = getServer().getDeck();
		deck.drawCard(getActionOwner());
		deck.drawCard(player);
		getServer().getGameState().nextNaturalActionState();
	}
}
