package oldmana.md.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MDScheduler
{
	private List<MDTask> tasks = new ArrayList<MDTask>();
	
	public MDScheduler()
	{
		
	}
	
	public void scheduleTask(MDTask task)
	{
		tasks.add(task);
	}
	
	public void runTick()
	{
		Iterator<MDTask> it = new ArrayList<MDTask>(tasks).iterator();
		while (it.hasNext())
		{
			MDTask task = it.next();
			if (task.tick())
			{
				tasks.remove(task);
			}
		}
	}
	
	
	public static abstract class MDTask
	{
		private int interval;
		private int next;
		
		private boolean repeats;
		private boolean cancelled;
		
		public MDTask(int interval)
		{
			this.interval = interval;
			this.next = interval;
		}
		
		public MDTask(int interval, boolean repeats)
		{
			this.interval = interval;
			this.next = interval;
			this.repeats = repeats;
		}
		
		public boolean isCancelled()
		{
			return cancelled;
		}
		
		public void cancel()
		{
			cancelled = true;
		}
		
		public boolean tick()
		{
			if (cancelled)
			{
				return true;
			}
			next--;
			if (next == 0)
			{
				run();
				if (repeats)
				{
					next = interval;
				}
				else
				{
					return true;
				}
			}
			return false;
		}
		
		public abstract void run();
	}
}
