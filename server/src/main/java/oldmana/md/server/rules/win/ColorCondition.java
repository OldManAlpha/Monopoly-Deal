package oldmana.md.server.rules.win;

import java.util.ArrayList;
import java.util.List;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.PropertyColor;

public class ColorCondition extends WinCondition
{
	private int colorTypes;
	
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
