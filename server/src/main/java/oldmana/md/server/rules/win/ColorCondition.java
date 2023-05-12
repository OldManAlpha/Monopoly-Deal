package oldmana.md.server.rules.win;

import java.util.ArrayList;
import java.util.List;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.PropertyColor;
import oldmana.md.server.rules.GameRule;

public class ColorCondition extends WinCondition
{
	private int colorTypes;
	
	public ColorCondition(GameRule rule)
	{
		colorTypes = rule.getInteger();
	}
	
	public ColorCondition(int colorTypes)
	{
		this.colorTypes = colorTypes;
	}
	
	@Override
	public boolean isWinner(Player player)
	{
		List<PropertyColor> colors = new ArrayList<PropertyColor>();
		for (CardProperty prop : player.getAllPropertyCards())
		{
			if (prop.isSingleColor() && !colors.contains(prop.getColor()))
			{
				colors.add(prop.getColor());
			}
		}
		return colors.size() >= colorTypes;
	}
}
