package oldmana.md.client;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.collection.Bank;
import oldmana.md.client.card.collection.CardCollection;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.component.MDMovingCard;
import oldmana.md.client.gui.component.collection.MDCardCollection;
import oldmana.md.client.gui.component.collection.MDCardCollectionBase;
import oldmana.md.client.state.ActionState;
import oldmana.md.client.state.ActionStateRent;

public class MDEventQueue
{
	private List<EventTask> queue = new ArrayList<EventTask>();
	private TickingEventTask currentTask;
	
	public MDEventQueue()
	{
		
	}
	
	public void addTask(EventTask task)
	{
		queue.add(task);
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
				EventTask task = queue.remove(0);
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
		public void start();
	}
	
	public interface TickingEventTask extends EventTask
	{
		public boolean tick();
	}
	
	public static class CardMove implements TickingEventTask
	{
		private Card card;
		private CardCollection from;
		private CardCollection to;
		private int toPos;
		private double speed = 1;
		
		private MDMovingCard anim;
		
		public CardMove(Card card, CardCollection from, CardCollection to, int toPos)
		{
			this.card = card;
			this.from = from;
			this.to = to;
			this.toPos = toPos;
		}
		
		public CardMove(Card card, CardCollection from, CardCollection to, int toPos, double speed)
		{
			this(card, from, to, toPos);
			this.speed = speed;
		}
		
		public CardMove(Card card, CardCollection to, int toPos, double speed)
		{
			this.card = card;
			this.to = to;
			this.toPos = toPos;
			this.speed = speed;
		}
		
		public void start()
		{
			Point p1 = null;
			
			if (from == null)
			{
				from = card.getOwningCollection();
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
			Point p2 = null;
			if (to.isUnknown())
			{
				MDCardCollectionBase ui = to.getUI();
				ui.startAddition(/*toPos > -1 ? toPos : */to.getCardCount());
				to.addUnknownCard();
				p2 = ui.getScreenLocationOf(/*toPos > -1 ? toPos : */to.getCardCount() - 1);
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
				p2 = ui.getScreenLocationOf(card);
			}
			anim = new MDMovingCard(from.isUnknown() ? null : card, p1, from.getUI().getCardScale(), to.isUnknown() ? null : card, p2, 
					to.getUI().getCardScale(), speed);
			MDClient.getInstance().addTableComponent(anim, 99);
			
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
			if (anim.tickMove())
			{
				to.getUI().cardArrived();
				to.getUI().modificationFinished();
				from.getUI().modificationFinished();
				anim.getParent().remove(anim);
				return true;
			}
			else
			{
				from.getUI().setCardMoveProgress(anim.progMap[anim.pos]);
				to.getUI().setCardMoveProgress(anim.progMap[anim.pos]);
				from.getUI().repaint();
				to.getUI().repaint();
			}
			return false;
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
			return anim;
		}
	}
}
