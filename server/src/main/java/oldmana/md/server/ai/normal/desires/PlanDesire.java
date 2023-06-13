package oldmana.md.server.ai.normal.desires;

import oldmana.md.server.Player;
import oldmana.md.server.ai.normal.plan.Plan;

public interface PlanDesire<T extends Plan>
{
	/**
	 * Desire range: 0 to 100
	 * 0 - 25: Extremely low desire, will not execute unless a move must be made
	 * 25 - 50: Enough desire to execute the plan if there's nothing better to do
	 * 50 - 75: High desire to execute the plan
	 * 75 - 100: Plan is extremely lucrative and takes high priority in execution
	 * 0: Plan is illegal to perform
	 */
	double getDesire(Player player);
}
