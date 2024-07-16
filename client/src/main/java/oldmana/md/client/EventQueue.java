package oldmana.md.client;

import java.awt.Container;
import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.collection.Bank;
import oldmana.md.client.card.collection.CardCollection;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.AutoScrollable;
import oldmana.md.client.gui.component.MDComponent;
import oldmana.md.client.gui.component.MDMovingCard;
import oldmana.md.client.gui.component.collection.MDCardCollection;
import oldmana.md.client.gui.component.collection.MDCardCollectionBase;
import oldmana.md.client.state.ActionState;
import oldmana.md.client.state.ActionStateRent;
import oldmana.md.common.card.CardAnimationType;

public class EventQueue
{
	private ArrayDeque<EventTask> queue = new ArrayDeque<EventTask>();
	private TickingEventTask currentTask;
	
	public void addTask(EventTask task)
	{
		queue.offer(task);
	}
	
	public void addPriorityTask(EventTask task)
	{
		queue.offerFirst(task);
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
		
		private List<AutoScroll> scrolls = new ArrayList<AutoScroll>();
		
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
		
		public void start()
		{
			Point p1;
			
			if (from == null)
			{
				from = card.getOwningCollection();
			}
			
			// Check containers that need to be snapped to the starting position
			Container prevFromContainer = from.getUI();
			Container fromContainer = prevFromContainer.getParent();
			while (fromContainer != null)
			{
				if (fromContainer instanceof AutoScrollable)
				{
					AutoScrollable scrollable = (AutoScrollable) fromContainer;
					int scrollNeeded = scrollable.getScrollNeededToView((MDComponent) prevFromContainer);
					scrollable.setScrollPos(scrollNeeded);
				}
				prevFromContainer = fromContainer;
				fromContainer = fromContainer.getParent();
			}
			
			// Check containers that need to be scrolled to the end position
			Container prevToContainer = to.getUI();
			Container toContainer = prevToContainer.getParent();
			while (toContainer != null)
			{
				if (toContainer instanceof AutoScrollable)
				{
					AutoScrollable scrollable = (AutoScrollable) toContainer;
					int scrollStart = scrollable.getScrollPos();
					int scrollEnd = scrollable.getScrollNeededToView((MDComponent) prevToContainer);
					if (scrollStart != scrollEnd)
					{
						AutoScroll scroll = new AutoScroll(scrollable, scrollStart, scrollEnd);
						scrolls.add(scroll);
					}
				}
				prevToContainer = toContainer;
				toContainer = toContainer.getParent();
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
			
			SoundSystem.playSound(anim == CardAnimationType.IMPORTANT ? "ImportantCardMove" :
					(from.isUnknown() == to.isUnknown() ? "CardMove" : "CardFlip"));
			
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
			
			double scrollPos = Math.min(1, moving.getCurrentPosition() * 1.5);
			for (AutoScroll scroll : scrolls)
			{
				scroll.component.setScrollPos((int) (scroll.scrollStart + ((scroll.scrollEnd - scroll.scrollStart) * scrollPos)));
			}
			
			if (finished)
			{
				to.getUI().cardArrived();
				to.getUI().modificationFinished();
				from.getUI().modificationFinished();
				MDClient.getInstance().removeTableComponent(moving);
				MDClient.getInstance().getTableScreen().repaint();
				if (to.makesPlaceSound())
				{
					SoundSystem.playSound("CardPlace");
				}
				if (to.isUnknown() && card != null) // Must set owning collection after the card has moved
				{
					card.setOwningCollection(to);
				}
			}
			else
			{
				from.getUI().setCardMoveProgress(moving.animMap[moving.pos]);
				to.getUI().setCardMoveProgress(moving.animMap[moving.pos]);
				if (from.getUI().shouldAnimateModification())
				{
					from.getUI().updateGraphics();
				}
				if (to.getUI().shouldAnimateModification())
				{
					to.getUI().updateGraphics();
				}
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
		
		private static class AutoScroll
		{
			public AutoScrollable component;
			public int scrollStart;
			public int scrollEnd;
			
			public AutoScroll(AutoScrollable component, int scrollStart, int scrollEnd)
			{
				this.component = component;
				this.scrollStart = scrollStart;
				this.scrollEnd = scrollEnd;
			}
		}
	}
}
