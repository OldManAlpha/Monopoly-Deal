package oldmana.md.common.state;

import java.util.HashMap;
import java.util.Map;

public enum TargetState
{
	NOT_TARGETED(false),
	TARGETED(true),
	REFUSED(true),
	ACCEPTED(false);
	
	private static final Map<Integer, TargetState> idMap = new HashMap<Integer, TargetState>();
	static
	{
		for (TargetState state : values())
		{
			idMap.put(state.ordinal(), state);
		}
	}
	
	private final boolean blocking;
	
	TargetState(boolean blocking)
	{
		this.blocking = blocking;
	}
	
	public boolean isBlocking()
	{
		return blocking;
	}
	
	public static TargetState fromID(int id)
	{
		return idMap.get(id);
	}
}
