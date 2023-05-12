package oldmana.md.client;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.collection.Bank;
import oldmana.md.client.card.collection.CardCollection;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.component.MDMovingCard;
import oldmana.md.client.gui.component.MDMovingCard.CardAnimationType;
import oldmana.md.client.gui.component.collection.MDCardCollection;
import oldmana.md.client.gui.component.collection.MDCardCollectionBase;
import oldmana.md.client.gui.component.large.MDOpponents;
import oldmana.md.client.state.ActionState;
import oldmana.md.client.state.ActionStateRent;

import javax.swing.SwingUtilities;

public class MDEventQueue
{
	private Queue<EventTask> queue = new ArrayDeque<EventTask>();
	private TickingEventTask currentTask;
	
	public void addTask(EventTask task)
	{
		queue.offer(task);
	}
	
	public boolean hasTasks()
	{
		return !queue.isEmpty() || currentTask != null;
	}
	
	public void clearTasks()
	{
		queue.clear();
		currentTask = null;
	}
	
	public TickingEventTask getCurrentTask()
	{
		return currentTask;
	}
	
	public void tick()
	{
		if (currentTask != null)
		{
			if (currentTask.tick())
			{
				currentTask = null;
			}
		}
		if (currentTask == null)
		{
			while (!queue.isEmpty() && currentTask == null)
			{
				EventTask task = queue.poll();
				task.start();
				if (task instanceof TickingEventTask)
				{
					currentTask = (TickingEventTask) task;
				}
			}
		}
	}
	
	public interface EventTask
	{
		void start();
	}
	
	public interface TickingEventTask extends EventTask
	{
		boolean tick();
	}
	
	public static class CardMove implements TickingEventTask
	{
		private Card card;
		private CardCollection from;
		private CardCollection to;
		private int toPos;
		private double time = 1;
		private CardAnimationType anim;
		
		private MDMovingCard moving;
		
		private boolean needsAutoScroll;
		private int opponentScrollStart;
		private int opponentScrollEnd;
		
		public CardMove(Card card, CardCollection from, CardCollection to, int toPos)
		{
			this.card = card;
			this.from = from;
			this.to = to;
			this.toPos = toPos;
		}
		
		public CardMove(Card card, CardCollection from, CardCollection to, int toPos, double time, CardAnimationType anim)
		{
			this(card, from, to, toPos);
			this.time = time;
			this.anim = anim;
		}
		
		public CardMove(Card card, CardCollection to, int toPos, double time)
		{
			this.card = card;
			this.to = to;
			this.toPos = toPos;
			this.time = time;
		}
		
		public void start()
		{
			Point p1;
			
			if (from == null)
			{
				from = card.getOwningCollection();
			}
			
			MDOpponents opponents = MDClient.getInstance().getTableScreen().getOpponents();
			if (SwingUtilities.isDescendingFrom(from.getUI(), opponents))
			{
				opponentScrollStart = opponents.getScrollNeededToView(from.getOwner());
				opponents.setScrollPos(opponentScrollStart);
			}
			
			if (SwingUtilities.isDescendingFrom(to.getUI(), opponents))
			{
				needsAutoScroll = true;
				opponentScrollStart = opponents.getScrollPos();
				opponentScrollEnd = opponents.getScrollNeededToView(to.getOwner());
			}
			
			if (from.isUnknown())
			{
				MDCardCollectionBase ui = from.getUI();
				p1 = ui.getScreenLocationOf(from.getCardCount() - 1);
				ui.startRemoval(from.getCardCount() - 1);
				from.removeUnknownCard();
			}
			else
			{
				MDCardCollection ui = (MDCardCollection) from.getUI();
				p1 = ui.getScreenLocationOf(card);
				ui.startRemoval(card);
				from.removeCard(card);
			}
			Supplier<Point> p2Supplier;
			if (to.isUnknown())
			{
				MDCardCollectionBase ui = to.getUI();
				ui.startAddition(/*toPos > -1 ? toPos : */to.getCardCount());
				to.addUnknownCard();
				p2Supplier = () -> ui.getScreenLocationOf(/*toPos > -1 ? toPos : */to.getCardCount() - 1);
				//ui.cardIncoming();
			}
			else
			{
				MDCardCollection ui = (MDCardCollection) to.getUI();
				ui.startAddition(card, toPos > -1 ? toPos : to.getCardCount());
				if (toPos > -1)
				{
					to.addCardAtIndex(card, toPos);
				}
				else
				{
					to.addCard(card);
				}
				p2Supplier = () -> ui.getScreenLocationOf(card);
			}
			moving = new MDMovingCard(from.isUnknown() ? null : card, p1, from.getUI().getCardScale(), to.isUnknown() ? null : card, p2Supplier,
					to.getUI().getCardScale(), time, anim, from.isUnknown() && to.isUnknown() ? card : null);
			MDClient.getInstance().addTableComponent(moving, 99);
			
			// Update the rent screen if bank/property sets are modified
			Player player = MDClient.getInstance().getThePlayer();
			if (((from instanceof Bank || from instanceof PropertySet) && from.getOwner() == player) || 
					((to instanceof Bank || to instanceof PropertySet) && to.getOwner() == player))
			{
				ActionState state = MDClient.getInstance().getGameState().getActionState();
				if (state instanceof ActionStateRent && state.isTarget(player))
				{
					state.updateUI();
				}
			}
		}
		
		public boolean tick()
		{
			boolean finished = moving.tickMove();
			
			if (needsAutoScroll)
			{
				MDOpponents opponents = MDClient.getInstance().getTableScreen().getOpponents();
				opponents.setScrollPos((int) (opponentScrollStart + ((opponentScrollEnd - opponentScrollStart) * (Math.min(1, moving.getCurrentPosition() * 1.5)))));
			}
			
			if (finished)
			{
				to.getUI().cardArrived();
				to.getUI().modificationFinished();
				from.getUI().modificationFinished();
				MDClient.getInstance().removeTableComponent(moving);
				MDClient.getInstance().getTableScreen().repaint();
			}
			else
			{
				from.getUI().setCardMoveProgress(moving.animMap[moving.pos]);
				to.getUI().setCardMoveProgress(moving.animMap[moving.pos]);
				from.getUI().repaint();
				to.getUI().repaint();
			}
			return finished;
		}
		
		public CardCollection getFrom()
		{
			return from;
		}
		
		public CardCollection getTo()
		{
			return to;
		}
		
		public MDMovingCard getComponent()
		{
			return moving;
		}
	}
}
