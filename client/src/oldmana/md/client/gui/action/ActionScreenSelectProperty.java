package oldmana.md.client.gui.action;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.collection.PropertySet;
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
		setLayout(new SelectPropertyLayout());
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
			this.cards.add(ui);
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
	
	public class SelectPropertyLayout implements LayoutManager2
	{
		@Override
		public void addLayoutComponent(String arg0, Component arg1) {}

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
		public Dimension minimumLayoutSize(Container arg0) {return null;}

		@Override
		public Dimension preferredLayoutSize(Container arg0) {return null;}

		@Override
		public void removeLayoutComponent(Component arg0) {}

		@Override
		public void addLayoutComponent(Component comp, Object constraints) {}

		@Override
		public float getLayoutAlignmentX(Container target) {return 0;}

		@Override
		public float getLayoutAlignmentY(Container target) {return 0;}

		@Override
		public void invalidateLayout(Container target)
		{
			layoutContainer(target);
		}

		@Override
		public Dimension maximumLayoutSize(Container target) {return null;}
	}
}
