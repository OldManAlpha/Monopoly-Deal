package oldmana.md.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

public class Scheduler
{
	public static int FRAMERATE;
	public static long FRAME_INTERVAL_NANOS;
	public static double FRAME_INTERVAL_MILLIS;
	
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> scheduledTask;
	
	private List<MDTask> tasks = new ArrayList<MDTask>();
	
	public Scheduler()
	{
		setFPS(60);
	}
	
	public void scheduleTask(MDTask task)
	{
		tasks.add(task);
	}
	
	public MDTask scheduleTask(Consumer<MDTask> func, int interval, boolean repeats)
	{
		MDTask task = new MDTask(interval, repeats)
		{
			@Override
			public void run()
			{
				func.accept(this);
			}
		};
		scheduleTask(task);
		return task;
	}
	
	public void scheduleFrameboundTask(Consumer<MDTask> func)
	{
		scheduleTask(new MDTask()
		{
			@Override
			public void run()
			{
				func.accept(this);
			}
		});
	}
	
	public void runTick()
	{
		// TODO: Optimize by holding additions/removals in other lists so that this garbage doesn't have to generated
		//  every tick
		for (MDTask task : new ArrayList<MDTask>(tasks))
		{
			if (task.tick())
			{
				tasks.remove(task);
			}
		}
	}
	
	public void setFPS(int fps)
	{
		FRAMERATE = fps;
		FRAME_INTERVAL_NANOS = (1000 * 1000 * 1000) / fps;
		FRAME_INTERVAL_MILLIS = 1000.0 / fps;
		System.out.println("New Framerate: " + FRAMERATE + " | " + FRAME_INTERVAL_MILLIS + " MS/Frame");
		if (scheduledTask != null)
		{
			scheduledTask.cancel(false);
		}
		scheduledTask = executor.scheduleAtFixedRate(() -> SwingUtilities.invokeLater(() -> runTick()),
				FRAME_INTERVAL_NANOS, FRAME_INTERVAL_NANOS, TimeUnit.NANOSECONDS);
	}
	
	public void setFPS(int fps, boolean save)
	{
		setFPS(fps);
		if (save)
		{
			Settings s = MDClient.getInstance().getSettings();
			s.put("framerate", fps);
			s.saveSettings();
		}
	}
	
	public static int getFPS()
	{
		return FRAMERATE;
	}
	
	/**
	 * Get the frame delay in milliseconds
	 */
	public static double getFrameDelay()
	{
		return FRAME_INTERVAL_MILLIS;
	}
	
	
	public static abstract class MDTask
	{
		private boolean frameBound = false;
		private long interval;
		private long next;
		
		private boolean repeats;
		private boolean cancelled;
		
		/**
		 * Run a repeating task at the client's framerate
		 */
		public MDTask()
		{
			frameBound = true;
			next = FRAME_INTERVAL_NANOS;
			repeats = true;
		}
		
		public MDTask(int interval)
		{
			this.interval = interval * 1000000L;
			this.next = this.interval;
		}
		
		public MDTask(int interval, boolean repeats)
		{
			this.interval = interval * 1000000L;
			this.next = this.interval;
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
			next -= FRAME_INTERVAL_NANOS;
			if (next <= 0)
			{
				run();
				if (repeats)
				{
					next = (frameBound ? FRAME_INTERVAL_NANOS : interval) + next;
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
