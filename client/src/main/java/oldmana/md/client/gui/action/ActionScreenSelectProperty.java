package oldmana.md.client.gui.action;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.LayoutAdapter;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDCard;
import oldmana.md.client.gui.component.MDLabel;
import oldmana.md.client.gui.util.GraphicsUtils;

public class ActionScreenSelectProperty extends ActionScreen
{
	private PropertySet set;
	private boolean canTargetNonBase;
	
	private PropertySelectListener listener;
	
	private MDLabel propLabel;
	
	private List<MDCard> cards = new ArrayList<MDCard>();
	
	private MDButton cancel;
	
	public ActionScreenSelectProperty(PropertySet set, boolean canTargetNonBase)
	{
		this.set = set;
		this.canTargetNonBase = canTargetNonBase;
		
		propLabel = new MDLabel("Select Property");
		add(propLabel);
		
		cancel = new MDButton("Cancel");
		cancel.setFontSize(24);
		cancel.addClickListener(() ->
		{
			if (listener != null)
			{
				listener.cancel();
			}
		});
		add(cancel);
		
		setup();
		setLayout(new SelectPropertyLayout());
	}
	
	public void setListener(PropertySelectListener listener)
	{
		this.listener = listener;
	}
	
	public void setup()
	{
		List<CardProperty> cards = set.getPropertyCards();
		for (CardProperty prop : cards)
		{
			MDCard ui = new MDCard(prop, 2);
			add(ui);
			this.cards.add(ui);
			if (canTargetNonBase || prop.isBase())
			{
				ui.addClickListener(() ->
				{
					if (listener != null)
					{
						listener.propertySelected(prop);
					}
				});
			}
			else
			{
				ui.setBanner("Immune");
			}
		}
	}
	
	public interface PropertySelectListener
	{
		void propertySelected(CardProperty prop);
		
		void cancel();
	}
	
	public class SelectPropertyLayout extends LayoutAdapter
	{
		@Override
		public void layoutContainer(Container arg0)
		{
			propLabel.setSize(scale(40));
			propLabel.setLocationCentered(getWidth() * 0.5, getHeight() * 0.1);
			
			int offset = ((cards.size() - 1) * (GraphicsUtils.getCardWidth() + scale(25))) + GraphicsUtils.getCardWidth();
			for (int i = 0 ; i < cards.size() ; i++)
			{
				MDCard ui = cards.get(i);
				ui.setLocation((int) (getWidth() * 0.5) - offset + (i * (GraphicsUtils.getCardWidth() * 2 + 50)), propLabel.getMaxY() + scale(20));
			}
			
			cancel.setSize(scale(180), scale(50));
			cancel.setLocationCentered(getWidth() / 2, getHeight() - scale(40));
			
			//colorLabel.setSize(scale(40));
			//colorLabel.setLocationCentered(getWidth() * 0.5, getHeight() * 0.4);
			
//			int offset = ((cards.size() - 1) * (GraphicsUtils.getCardWidth() + scale(25))) + GraphicsUtils.getCardWidth();
//			for (int i = 0 ; i < cards.size() ; i++)
//			{
//				MDCard ui = cards.get(i);
//				ui.setLocation((int) (getWidth() * 0.5) - offset + (i * (GraphicsUtils.getCardWidth() * 2 + 50)), propLabel.getMaxY() + scale(10));
//			}
		}
		
		@Override
		public void invalidateLayout(Container target)
		{
			layoutContainer(target);
		}
	}
}
