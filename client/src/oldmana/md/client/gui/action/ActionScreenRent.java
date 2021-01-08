package oldmana.md.client.gui.action;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager2;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import oldmana.md.client.Player;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.collection.Bank;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDCardSelection;
import oldmana.md.client.gui.component.MDCardSelection.CardSelectListener;
import oldmana.md.client.gui.component.MDLabel;
import oldmana.md.client.state.ActionStateRent;

public class ActionScreenRent extends ActionScreen
{
	private Player player;
	private int rent;
	
	private JScrollPane scrollPane;
	private CardSelectUI cardSelect;
	
	private MDButton viewTable;
	
	private MDButton pay;
	
	private MDLabel rentLabel;
	private MDLabel selectedLabel;
	
	public ActionScreenRent(Player player, ActionStateRent state)
	{
		super();
		this.player = player;
		this.rent = state.getRent();
		//cardSelect.setPreferredSize(new Dimension(1350, 2000));
		scrollPane = new JScrollPane();
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		add(scrollPane);
		cardSelect = new CardSelectUI();
		scrollPane.setViewportView(cardSelect);
		viewTable = new MDButton("View Table");
		viewTable.setLocation(10, 840);
		viewTable.setSize(180, 50);
		viewTable.setFontSize(24);
		viewTable.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				setVisible(false);
			}
		});
		add(viewTable);
		
		rentLabel = new MDLabel("Rent: " + rent + "M");
		add(rentLabel);
		
		selectedLabel = new MDLabel("");
		add(selectedLabel);
		
		pay = new MDButton("Pay");
		pay.setLocation(710, 825);
		pay.setSize(180, 50);
		pay.setFontSize(24);
		add(pay);
		pay.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				if (isRequiredAmountSelected())
				{
					List<Card> selectedCards = new ArrayList<Card>();
					for (MDCardSelection select : cardSelect.getSelects())
					{
						if (select.isSelected())
						{
							selectedCards.add(select.getCard());
						}
					}
					state.payRent(selectedCards);
				}
			}
		});
		
		setLayout(new RentLayout());
		
		checkSelected();
	}
	
	public void checkSelected()
	{
		int selectedValue = getAmountSelected();
		selectedLabel.setText("Selected: " + selectedValue + "M");
		selectedLabel.repaint();
		if (isRequiredAmountSelected())
		{
			pay.setEnabled(true);
		}
		else
		{
			pay.setEnabled(false);
		}
	}
	
	public int getAmountSelected()
	{
		int selectedValue = 0;
		for (MDCardSelection select : cardSelect.getSelects())
		{
			if (select.isSelected())
			{
				selectedValue += select.getCard().getValue();
			}
		}
		return selectedValue;
	}
	
	public boolean isRequiredAmountSelected()
	{
		return getAmountSelected() >= rent || player.getTotalMonetaryAssets() <= rent;
	}
	
	public class CardSelectUI extends JComponent
	{
		private MDLabel bankLabel;
		private MDLabel propLabel;
		
		private List<MDCardSelection> bankSelects = new ArrayList<MDCardSelection>();
		private List<MDCardSelection> propSelects = new ArrayList<MDCardSelection>();
		
		public CardSelectUI()
		{
			super();
			constructComponents();
			setLayout(new CardSelectLayout());
		}
		
		public void constructComponents()
		{
			boolean mustPayEverything = player.getTotalMonetaryAssets() <= rent;
			
			bankLabel = new MDLabel("Bank");
			propLabel = new MDLabel("Properties");
			
			add(bankLabel);
			add(propLabel);
			
			// Bank
			Bank bank = player.getBank();
			List<Card> cards = bank.getCards();
			for (int i = 0 ; i < cards.size() ; i++)
			{
				Card card = cards.get(i);
				MDCardSelection cs = constructSelect(card, mustPayEverything, mustPayEverything && card.getValue() > 0);
				bankSelects.add(cs);
				add(cs);
			}
			
			// Properties
			List<PropertySet> propSets = player.getPropertySets();
			for (int i = 0 ; i < propSets.size() ; i++)
			{
				PropertySet set = propSets.get(i);
				for (int e = 0 ; e < set.getCardCount() ; e++)
				{
					Card card = set.getCardAt(e);
					MDCardSelection cs = constructSelect(card, mustPayEverything, mustPayEverything && card.getValue() > 0);
					propSelects.add(cs);
					add(cs);
				}
			}
		}
		
		public MDCardSelection constructSelect(Card card, boolean disabled, boolean selected)
		{
			MDCardSelection cs = new MDCardSelection(card, selected);
			cs.setDisabled(disabled);
			cs.setListener(new CardSelectListener()
			{
				@Override
				public boolean preSelect()
				{
					return !isRequiredAmountSelected() && card.getValue() != 0;
				}
				
				@Override
				public void postSelectToggle()
				{
					checkSelected();
				}
			});
			return cs;
		}
		
		public void layoutComponents()
		{
			int width = scrollPane.getViewport().getWidth();
			int y = scale(10);
			
			bankLabel.setLocation(scale(10), y);
			bankLabel.setSize(scale(80), scale(40));
			
			y += scale(50);
			
			y = layoutSelects(bankSelects, width, y);
			
			y += scale(190 + 10);
			
			propLabel.setLocation(scale(10), y);
			propLabel.setSize(scale(160), scale(40));
			
			y += scale(50);
			
			y = layoutSelects(propSelects, width, y);
			
			y += scale(180);
			
			System.out.println("VIEWPORT WIDTH: " + width);
			setPreferredSize(new Dimension(width, y));
		}
		
		public int layoutSelects(List<MDCardSelection> selects, int width, int y)
		{
			int x = scale(5);
			for (MDCardSelection select : selects)
			{
				if (x + scale(130) > width)
				{
					x = scale(5);
					y += scale(190);
				}
				select.setLocation(x, y);
				select.updateSize();
				x += scale(130);
			}
			return y;
		}
		
		public List<MDCardSelection> getSelects()
		{
			List<MDCardSelection> selects = new ArrayList<MDCardSelection>(bankSelects);
			selects.addAll(propSelects);
			return selects;
		}
		
		@Override
		public void paintComponent(Graphics gr)
		{
			super.paintComponent(gr);
		}
		
		public class CardSelectLayout implements LayoutManager2
		{

			@Override
			public void addLayoutComponent(String name, Component comp) {}

			@Override
			public void layoutContainer(Container parent)
			{
				layoutComponents();
			}

			@Override
			public Dimension minimumLayoutSize(Container parent) {return null;}

			@Override
			public Dimension preferredLayoutSize(Container parent) {return null;}

			@Override
			public void removeLayoutComponent(Component comp) {}

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
	
	public class RentLayout implements LayoutManager2
	{

		@Override
		public void addLayoutComponent(String arg0, Component arg1) {}

		@Override
		public void layoutContainer(Container arg0)
		{
			rentLabel.setSize(scale(40));
			rentLabel.setLocationCentered((int) (getWidth() * 0.4), scale(50));
			
			selectedLabel.setSize(scale(40));
			selectedLabel.setLocationCentered((int) (getWidth() * 0.6), scale(50));
			
			pay.setSize(scale(180), scale(50));
			pay.setLocationCentered(getWidth() / 2, getHeight() - scale(35));
			
			viewTable.setSize(scale(180), scale(50));
			viewTable.setLocationCentered(scale(100), getHeight() - scale(35));
			
			scrollPane.setSize((int) (getWidth() * 0.9), getHeight() - rentLabel.getMaxY() - pay.getHeight() - scale(30));
			scrollPane.setLocation((int) (getWidth() * 0.05), rentLabel.getMaxY() + scale(10));
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
