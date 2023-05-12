package oldmana.md.client.state.client;

import oldmana.md.client.MDClient;
import oldmana.md.client.card.Card;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.client.gui.component.collection.MDHand;
import oldmana.md.client.gui.util.GraphicsUtils;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class HandCardSelection
{
	private Card card;
	
	private MDSelection select;
	private MDButton cancel;
	private ComponentAdapter handResizeListener = new ComponentAdapter()
	{
		@Override
		public void componentResized(ComponentEvent e)
		{
			MDHand hand = getHand();
			Point propLoc = hand.getLocationOf(card);
			select.setLocation(propLoc);
			cancel.setLocation((int) (select.getWidth() * 0.1) + select.getX(), (int) (select.getHeight() * 0.4) + select.getY());
		}
	};
	
	public HandCardSelection(Card card)
	{
		this.card = card;
	}
	
	public void create(Runnable cancelListener)
	{
		MDHand hand = getHand();
		Point propLoc = hand.getLocationOf(card);
		select = new MDSelection(Color.BLUE);
		select.setLocation(propLoc);
		select.setSize(GraphicsUtils.getCardWidth(2), GraphicsUtils.getCardHeight(2));
		hand.add(select);
		hand.addComponentListener(handResizeListener);
		
		cancel = new MDButton("Cancel");
		cancel.setSize((int) (select.getWidth() * 0.8), (int) (select.getHeight() * 0.2));
		cancel.setLocation((int) (select.getWidth() * 0.1) + select.getX(), (int) (select.getHeight() * 0.4) + select.getY());
		cancel.setListener(cancelListener);
		hand.add(cancel);
	}
	
	public void destroy()
	{
		MDHand hand = getHand();
		hand.removeComponentListener(handResizeListener);
		hand.remove(select);
		hand.remove(cancel);
	}
	
	private MDHand getHand()
	{
		return MDClient.getInstance().getTableScreen().getHand();
	}
}
