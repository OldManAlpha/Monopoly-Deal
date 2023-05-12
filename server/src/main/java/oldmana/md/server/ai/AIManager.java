package oldmana.md.server.ai;

import oldmana.md.server.MDServer;
import oldmana.md.server.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class AIManager
{
	private ExecutorService aiExecutor = Executors.newSingleThreadExecutor();
	
	private PlayerAI currentlyRunning;
	
	private CompletableFuture<?> currentTask;
	
	public AIManager()
	{
		if (true)
		{
			return;
		}
		getServer().getScheduler().scheduleTask(1, true, task ->
		{
			if (currentlyRunning != null)
			{
				return;
			}
			
			for (Player player : getServer().getPlayers())
			{
				if (player.isBot())
				{
					currentlyRunning = player.getAI();
					currentlyRunning.doAction();
					break;
				}
			}
		});
	}
	
	public <T> CompletableFuture<T> runAsync(Supplier<T> func)
	{
		return CompletableFuture.supplyAsync(func, aiExecutor);
	}
	
	public ExecutorService getAIExecutor()
	{
		return aiExecutor;
	}
	
	private MDServer getServer()
	{
		return MDServer.getInstance();
	}
}
