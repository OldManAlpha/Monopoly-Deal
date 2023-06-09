package oldmana.md.server.card.play.argument;

import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.card.play.PlayArgument;

public class PropertySetArgument implements PlayArgument
{
	private PropertySet targetSet;
	
	public PropertySetArgument(PropertySet targetSet)
	{
		this.targetSet = targetSet;
	}
	
	public PropertySet getTargetSet()
	{
		return targetSet;
	}
}
