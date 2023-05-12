package oldmana.md.server.ai.normal;

import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.ai.BasicAI;
import oldmana.md.server.ai.StateResponder;
import oldmana.md.server.state.ActionState;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BasicStateResponder<AS extends ActionState> implements StateResponder<AS>
{
	private BasicAI ai;
	
	private volatile boolean interrupted;
	
	protected Player getPlayer()
	{
		return ai.getPlayer();
	}
	
	protected MDServer getServer()
	{
		return MDServer.getInstance();
	}
	
	public BasicAI getAI()
	{
		return ai;
	}
	
	public void setAI(BasicAI ai)
	{
		this.ai = ai;
	}
	
	protected CompletableFuture<Void> runAsync(Runnable task)
	{
		return CompletableFuture.runAsync(task, getServer().getAIManager().getAIExecutor());
	}
	
	protected <T> CompletableFuture<T> runAsync(Supplier<T> task)
	{
		return CompletableFuture.supplyAsync(task, getServer().getAIManager().getAIExecutor());
	}
	
	protected <T, V> CompletableFuture<V> compute(Iterator<T> iterator, Function<T, V> func)
	{
		CompletableFuture<V> future = new CompletableFuture<V>();
		while (iterator.hasNext())
		{
			T t = iterator.next();
			V v = func.apply(t);
			if (v != null)
			{
				future.complete(v);
			}
		}
		return future;
	}
	
	public boolean isInterrupted()
	{
		return interrupted;
	}
	
	public void interrupt()
	{
		interrupted = true;
	}
}
