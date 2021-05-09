package oldmana.md.client;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.collection.Bank;
import oldmana.md.client.card.collection.CardCollection;
import oldmana.md.client.gui.component.MDCardCollection;
import oldmana.md.client.gui.component.MDMovingCard;
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
			if (!queue.isEmpty())
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
				p1 = from.getUI().getLocationOf(0);
				from.removeUnknownCard();
			}
			else
			{
				p1 = ((MDCardCollection) from.getUI()).getLocationOf(card);
				from.removeCard(card);
			}
			Point p2 = null;
			if (to.isUnknown())
			{
				to.addUnknownCard();
				to.getUI().cardIncoming();
				p2 = to.getUI().getLocationOf(0);
			}
			else
			{
				if (toPos > -1)
				{
					to.addCardAtIndex(card, toPos);
				}
				else
				{
					to.addCard(card);
				}
				((MDCardCollection) to.getUI()).setIncomingCard(card);
				p2 = ((MDCardCollection) to.getUI()).getLocationOf(card);
			}
			anim = new MDMovingCard(from.isUnknown() ? null : card, p1, from.getUI().getComponentScale(), to.isUnknown() ? null : card, p2, 
					to.getUI().getComponentScale(), speed);
			MDClient.getInstance().addTableComponent(anim, 99);
			
			// Update the rent screen if money is added/removed from their bank
			// TODO: Probably should do that for properties too...
			Player player = MDClient.getInstance().getThePlayer();
			if ((from != null && from instanceof Bank && from.getOwner() == player) || (to != null && to instanceof Bank && to.getOwner() == player))
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
				anim.getParent().remove(anim);
				return true;
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
	}
}
