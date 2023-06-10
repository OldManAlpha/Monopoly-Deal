package oldmana.md.client.state.client;

import oldmana.md.client.card.Card;
import oldmana.md.common.net.packet.client.action.PacketActionSelectCardCombo;

import java.util.ArrayList;
import java.util.List;

public class ActionStateClientSelectCardCombo extends ActionStateClient
{
	private List<Card> selectedCards;
	private List<Card> selectableCards;
	
	private List<HandCardSelection> selected = new ArrayList<HandCardSelection>();
	private HandCardSelectables selectables;
	
	public ActionStateClientSelectCardCombo(List<Card> selectedCards, List<Card> selectableCards)
	{
		this.selectedCards = selectedCards;
		this.selectableCards = selectableCards;
	}
	
	@Override
	public void setup()
	{
		for (int i = 0 ; i < selectedCards.size() ; i++)
		{
			HandCardSelection selection = new HandCardSelection(selectedCards.get(i));
			selection.create(i == 0 ? () -> removeState() : null);
			selected.add(selection);
		}
		selectables = new HandCardSelectables(selectableCards);
		selectables.create(card ->
		{
			getClient().sendPacket(new PacketActionSelectCardCombo(card.getID(),
					selectedCards.stream().mapToInt(Card::getID).toArray()));
			removeState();
			getClient().setAwaitingResponse(true);
		});
	}
	
	@Override
	public void cleanup()
	{
		for (HandCardSelection selection : selected)
		{
			selection.destroy();
		}
		selectables.destroy();
		getClient().getTableScreen().repaint();
	}
	
	@Override
	public void updateUI()
	{
		cleanup();
		setup();
	}
}
