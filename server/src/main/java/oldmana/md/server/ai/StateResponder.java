package oldmana.md.server.ai;

import oldmana.md.server.state.ActionState;

import java.util.concurrent.CompletableFuture;

public interface StateResponder<AS extends ActionState>
{
	/**
	 * Handles the ActionState
	 * @param state The ActionState
	 * @return The CompletableFuture that will complete when the state has been properly responded to,
	 * or null if the state has already been responded to
	 */
	CompletableFuture<Void> handle(AS state);
}
