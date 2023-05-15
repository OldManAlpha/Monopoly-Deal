package oldmana.md.server;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MDScheduler
{
	private List<MDTask> tasks = new ArrayList<MDTask>();
	
	public void scheduleTask(MDTask task)
	{
		tasks.add(task);
	}
	
	public void scheduleTask(Runnable task)
	{
		scheduleTask(1, task);
	}
	
	public void scheduleTask(int delay, Runnable task)
	{
		tasks.add(new MDTask(delay)
		{
			@Override
			public void run()
			{
				task.run();
			}
		});
	}
	
	public void scheduleTask(int delay, Consumer<MDTask> task)
	{
		scheduleTask(delay, false, task);
	}
	
	public void scheduleTask(int interval, boolean repeats, Consumer<MDTask> task)
	{
		tasks.add(new MDTask(interval, repeats)
		{
			@Override
			public void run()
			{
				task.accept(this);
			}
		});
	}
	
	public void runTick()
	{
		if (tasks.isEmpty())
		{
			return;
		}
		for (MDTask task : new ArrayList<MDTask>(tasks))
		{
			if (task.isCancelled())
			{
				tasks.remove(task);
				continue;
			}
			task.tick();
			if (task.getNextRun() <= 0)
			{
				if (task.isRepeating())
				{
					task.setNextRun(task.getInterval());
				}
				try
				{
					task.run();
				}
				catch (Exception | Error e)
				{
					System.err.println("Exception occurred in scheduled task");
					e.printStackTrace();
				}
				if (task.getNextRun() <= 0)
				{
					tasks.remove(task);
				}
			}
		}
	}
	
	
	public static abstract class MDTask implements Runnable
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
		
		public int getInterval()
		{
			return interval;
		}
		
		public int getNextRun()
		{
			return next;
		}
		
		public void setNextRun(int next)
		{
			this.next = next;
		}
		
		public boolean isRepeating()
		{
			return repeats;
		}
		
		public boolean isCancelled()
		{
			return cancelled;
		}
		
		public void cancel()
		{
			cancelled = true;
		}
		
		public void tick()
		{
			next--;
		}
	}
}
