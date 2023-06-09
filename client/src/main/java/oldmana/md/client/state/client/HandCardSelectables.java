package oldmana.md.client.state.client;

import oldmana.md.client.MDClient;
import oldmana.md.client.card.Card;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.client.gui.component.collection.MDHand;
import oldmana.md.client.gui.util.GraphicsUtils;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class HandCardSelectables
{
	private Map<Card, MDSelection> selections = new HashMap<Card, MDSelection>();
	
	private ComponentAdapter handResizeListener = new ComponentAdapter()
	{
		@Override
		public void componentResized(ComponentEvent e)
		{
			selections.forEach((card, select) -> select.setLocation(getHand().getLocationOf(card)));
		}
	};
	
	public HandCardSelectables(List<Card> cards)
	{
		for (Card card : cards)
		{
			selections.put(card, null);
		}
	}
	
	public void create(Consumer<Card> listener)
	{
		MDHand hand = getHand();
		for (Card card : selections.keySet())
		{
			Point loc = hand.getLocationOf(card);
			MDSelection select = new MDSelection(Color.YELLOW);
			select.setLocation(loc);
			select.setSize(GraphicsUtils.getCardWidth(2), GraphicsUtils.getCardHeight(2));
			select.addClickListener(() -> listener.accept(card));
			hand.add(select);
			selections.put(card, select);
			hand.addComponentListener(handResizeListener);
		}
	}
	
	public void destroy()
	{
		MDHand hand = getHand();
		hand.removeComponentListener(handResizeListener);
		for (MDSelection select : selections.values())
		{
			hand.remove(select);
		}
	}
	
	private MDHand getHand()
	{
		return MDClient.getInstance().getTableScreen().getHand();
	}
}
