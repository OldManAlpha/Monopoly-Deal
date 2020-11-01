package oldmana.general.md.client.gui.action;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import oldmana.general.md.client.card.Card;
import oldmana.general.md.client.card.CardProperty;
import oldmana.general.md.client.card.collection.PropertySet;
import oldmana.general.md.client.gui.component.MDButton;
import oldmana.general.md.client.gui.component.MDCard;
import oldmana.general.md.client.gui.component.MDLabel;

public class ActionScreenSelectProperty extends ActionScreen
{
	private PropertySet set;
	private boolean canTargetNonBase;
	
	private PropertySelectListener listener;
	
	public ActionScreenSelectProperty(PropertySet set, boolean canTargetNonBase)
	{
		this.set = set;
		this.canTargetNonBase = canTargetNonBase;
		
		MDLabel prop = new MDLabel("Select Property");
		prop.setSize(300, 40);
		prop.setLocation(650, 50);
		add(prop);
		
		MDButton cancel = new MDButton("Cancel");
		cancel.setSize(180, 50);
		cancel.setLocation(800 - 90, 900 - 50 - 10);
		cancel.setFontSize(24);
		cancel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				if (listener != null)
				{
					listener.cancel();
				}
			}
		});
		add(cancel);
		
		setup();
	}
	
	public void setListener(PropertySelectListener listener)
	{
		this.listener = listener;
	}
	
	public void setup()
	{
		int cardCount = set.getCardCount();
		int offset = ((cardCount - 1) * (MDCard.CARD_SIZE.width + 25)) + MDCard.CARD_SIZE.width;
		List<Card> cards = set.getCards();
		for (int i = 0 ; i < cardCount ; i++)
		{
			CardProperty prop = (CardProperty) cards.get(i);
			MDCard ui = new MDCard(prop, 2);
			ui.setLocation(800 - offset + (i * (MDCard.CARD_SIZE.width * 2 + 50)), 150);
			add(ui);
			if (canTargetNonBase || prop.isBase())
			{
				ui.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mouseReleased(MouseEvent event)
					{
						if (listener != null)
						{
							listener.propertySelected(prop);
						}
					}
				});
			}
		}
	}
	
	public static interface PropertySelectListener
	{
		public void propertySelected(CardProperty prop);
		
		public void cancel();
	}
}
