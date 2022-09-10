package oldmana.md.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

public class MDScheduler
{
	public static int FRAMERATE;
	public static long FRAME_INTERVAL;
	
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> scheduledTask;
	
	private List<MDTask> tasks = new ArrayList<MDTask>();
	
	public MDScheduler()
	{
		setFPS(60);
	}
	
	public void scheduleTask(MDTask task)
	{
		tasks.add(task);
	}
	
	public void scheduleTask(Consumer<MDTask> func, int interval, boolean repeats)
	{
		scheduleTask(new MDTask(interval, repeats)
		{
			@Override
			public void run()
			{
				func.accept(this);
			}
		});
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
		FRAME_INTERVAL = (1000 * 1000 * 1000) / fps;
		System.out.println("New Framerate: " + FRAMERATE);
		if (scheduledTask != null)
		{
			scheduledTask.cancel(false);
		}
		scheduledTask = executor.scheduleAtFixedRate(() -> SwingUtilities.invokeLater(() -> runTick()),
				FRAME_INTERVAL, FRAME_INTERVAL, TimeUnit.NANOSECONDS);
	}
	
	public void setFPS(int fps, boolean save)
	{
		setFPS(fps);
		if (save)
		{
			Settings s = MDClient.getInstance().getSettings();
			s.setSetting("Framerate", fps);
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
		return 1000.0 / FRAMERATE;
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
			next = FRAME_INTERVAL;
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
			next -= FRAME_INTERVAL;
			if (next <= 0)
			{
				run();
				if (repeats)
				{
					next = (frameBound ? FRAME_INTERVAL : interval) + next;
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
