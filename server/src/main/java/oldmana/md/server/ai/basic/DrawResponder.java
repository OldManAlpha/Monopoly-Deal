package oldmana.md.server.ai.basic;

import oldmana.md.server.ai.BasicAI.BasicStateResponder;
import oldmana.md.server.state.ActionStateDraw;

public class DrawResponder extends BasicStateResponder<ActionStateDraw>
{
	public DrawResponder() {}
	
	@Override
	public void doAction(ActionStateDraw state)
	{
		getPlayer().draw();
	}
}
