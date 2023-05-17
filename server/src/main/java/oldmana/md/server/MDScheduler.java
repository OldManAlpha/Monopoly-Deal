package oldmana.md.server;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MDScheduler
{
	private final List<MDTask> tasks = new ArrayList<MDTask>();
	
	public void scheduleTask(MDTask task)
	{
		synchronized (tasks)
		{
			tasks.add(task);
		}
	}
	
	public void scheduleTask(Runnable task)
	{
		scheduleTask(1, task);
	}
	
	public void scheduleTask(int delay, Runnable task)
	{
		scheduleTask(new MDTask(delay)
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
		scheduleTask(new MDTask(interval, repeats)
		{
			@Override
			public void run()
			{
				task.accept(this);
			}
		});
	}
	
	private void removeTask(MDTask task)
	{
		synchronized (tasks)
		{
			tasks.remove(task);
		}
	}
	
	public void runTick()
	{
		List<MDTask> tasksToRun;
		synchronized (tasks)
		{
			if (tasks.isEmpty())
			{
				return;
			}
			tasksToRun = new ArrayList<MDTask>(tasks);
		}
		for (MDTask task : tasksToRun)
		{
			if (task.isCancelled())
			{
				removeTask(task);
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
					removeTask(task);
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
