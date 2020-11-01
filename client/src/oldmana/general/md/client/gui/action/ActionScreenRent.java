package oldmana.general.md.client.gui.action;

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

import oldmana.general.md.client.Player;
import oldmana.general.md.client.card.Card;
import oldmana.general.md.client.card.collection.Bank;
import oldmana.general.md.client.card.collection.PropertySet;
import oldmana.general.md.client.gui.component.MDCardSelection;
import oldmana.general.md.client.gui.component.MDCardSelection.CardSelectListener;
import oldmana.general.md.client.gui.component.MDLabel;
import oldmana.general.md.client.gui.component.MDButton;
import oldmana.general.md.client.state.ActionStateRent;

public class ActionScreenRent extends ActionScreen
{
	private Player player;
	private int rent;
	
	private ActionStateRent state;
	
	private JScrollPane scrollPane;
	private CardSelectUI cardSelect;
	
	private MDButton viewTable;
	
	private MDButton pay;
	private MDButton jsn;
	
	private MDLabel selectedLabel;
	
	public ActionScreenRent(Player player, ActionStateRent state)
	{
		super();
		this.player = player;
		this.rent = state.getRent();
		this.state = state;
		cardSelect = new CardSelectUI();
		//cardSelect.setPreferredSize(new Dimension(1350, 2000));
		scrollPane = new JScrollPane(cardSelect);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		add(scrollPane);
		scrollPane.setSize(1400, 700);
		scrollPane.setLocation(100, 100);
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
		
		MDLabel rentLabel = new MDLabel("Rent: " + rent + "M");
		rentLabel.setLocation(300, 30);
		rentLabel.setSize(160, 40);
		add(rentLabel);
		
		selectedLabel = new MDLabel("");
		selectedLabel.setLocation(1100 - 50, 30);
		selectedLabel.setSize(260, 40);
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
		
		//jsn = new MDButton("Just Say No!");
		//jsn.setLocation(820, 825);
		//jsn.setSize(180, 50);
		//jsn.setFontSize(24);
		//add(jsn);
		
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
		int selectedValue = 0;
		for (MDCardSelection select : cardSelect.getSelects())
		{
			if (select.isSelected())
			{
				selectedValue += select.getCard().getValue();
			}
		}
		return selectedValue >= rent || player.getTotalMonetaryAssets() <= rent;
	}
	
	public class CardSelectUI extends JComponent
	{
		private List<MDCardSelection> selects = new ArrayList<MDCardSelection>();
		
		private List<MDCardSelection> bankSelects = new ArrayList<MDCardSelection>();
		private List<MDCardSelection> propSelects = new ArrayList<MDCardSelection>();
		
		public CardSelectUI()
		{
			super();
			positionCards();
		}
		
		public void constructComponents()
		{
			boolean mustPayEverything = player.getTotalMonetaryAssets() <= rent;
			
			Bank bank = player.getBank();
			List<Card> cards = bank.getCards();
			for (int i = 0 ; i < cards.size() ; i++)
			{
				int value = cards.get(i).getValue();
				
				MDCardSelection cs = new MDCardSelection(cards.get(i), mustPayEverything && value > 0);
				cs.setListener(new CardSelectListener()
				{
					@Override
					public boolean preSelect()
					{
						return !isRequiredAmountSelected();
					}
					
					@Override
					public void postSelectToggle()
					{
						checkSelected();
					}
				});
				if (mustPayEverything || value == 0)
				{
					cs.setDisabled(true);
				}
				bankSelects.add(cs);
			}
			
			// Properties
			List<PropertySet> propSets = player.getPropertySets();
			for (int i = 0 ; i < propSets.size() ; i++)
			{
				PropertySet set = propSets.get(i);
				for (int e = 0 ; e < set.getCardCount() ; e++)
				{
					int value = set.getCardAt(e).getValue();
					MDCardSelection cs = new MDCardSelection(set.getCardAt(e), mustPayEverything && value > 0);
					cs.setListener(new CardSelectListener()
					{
						@Override
						public boolean preSelect()
						{
							return !isRequiredAmountSelected();
						}
						
						@Override
						public void postSelectToggle()
						{
							checkSelected();
						}
					});
					if (mustPayEverything || value == 0)
					{
						cs.setDisabled(true);
					}
					propSelects.add(cs);
				}
			}
		}
		
		public void positionCards()
		{
			CardSelectListener listener = new CardSelectListener()
			{
				@Override
				public boolean preSelect()
				{
					return !isRequiredAmountSelected();
				}
				
				@Override
				public void postSelectToggle()
				{
					checkSelected();
				}
			};
			
			MDLabel bankLabel = new MDLabel("Bank");
			bankLabel.setLocation(10, 10);
			bankLabel.setSize(80, 40);
			add(bankLabel);
			
			int yPos = 60;
			int xPos = 5;
			int width = 1350;
			
			boolean mustPayEverything = player.getTotalMonetaryAssets() <= rent;
			Bank bank = player.getBank();
			List<Card> cards = bank.getCards();
			for (int i = 0 ; i < cards.size() ; i++)
			{
				int value = cards.get(i).getValue();
				if (xPos + 130 > width - 5)
				{
					xPos = 5;
					yPos += 190;
				}
				MDCardSelection cs = new MDCardSelection(cards.get(i), mustPayEverything && value > 0);
				if (mustPayEverything || value == 0)
				{
					cs.setDisabled(true);
				}
				if (!mustPayEverything && value > 0)
				{
					cs.setListener(listener);
				}
				cs.setLocation(xPos, yPos);
				add(cs);
				selects.add(cs);
				xPos += 130;
			}
			xPos = 5;
			yPos += 200;
			MDLabel propLabel = new MDLabel("Properties");
			propLabel.setLocation(10, yPos);
			propLabel.setSize(160, 40);
			add(propLabel);
			yPos += 50;
			List<PropertySet> propSets = player.getPropertySets();
			for (int i = 0 ; i < propSets.size() ; i++)
			{
				PropertySet set = propSets.get(i);
				for (int e = 0 ; e < set.getCardCount() ; e++)
				{
					int value = set.getCardAt(e).getValue();
					if (xPos + 130 > width - 5)
					{
						xPos = 5;
						yPos += 190;
					}
					MDCardSelection cs = new MDCardSelection(set.getCardAt(e), mustPayEverything && value > 0);
					if (mustPayEverything || value == 0)
					{
						cs.setDisabled(true);
					}
					if (!mustPayEverything && value > 0)
					{
						cs.setListener(listener);
					}
					cs.setLocation(xPos, yPos);
					add(cs);
					selects.add(cs);
					xPos += 130;
				}
			}
			setPreferredSize(new Dimension(1350, yPos + 190));
		}
		
		public List<MDCardSelection> getSelects()
		{
			return selects;
		}
		
		@Override
		public void paintComponent(Graphics gr)
		{
			super.paintComponent(gr);
			/*
			Graphics2D g = (Graphics2D) gr;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawRect(0, 0, getWidth() - 5, getHeight() - 5);
			g.setFont(GraphicsUtils.getBoldMDFont(30));
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(10, 10, 75, 35);
			g.setColor(Color.DARK_GRAY);
			TextPainter tp = new TextPainter("Bank", g.getFont(), new Rectangle(15, 10, 80, 40));
			tp.setVerticalAlignment(Alignment.CENTER);
			tp.paint(g);
			*/
			/*
			Graphics2D gBank = (Graphics2D) g.create();
			gBank.translate(5, 50);
			Bank bank = player.getBank();
			if (bank != null)
			{
				for (Card card : bank.getCards())
				{
					gBank.drawImage(card.getGraphics(4), 0, 0, MDCard.CARD_SIZE.width * 2, MDCard.CARD_SIZE.height * 2, null);
					gBank.translate(130, 0);
				}
			}
			Graphics2D gPropSets = (Graphics2D) g.create();
			gPropSets.translate(5, 50 + 180 + 50);
			List<PropertySet> propSets = player.getPropertySets();
			for (int i = 0 ; i < propSets.size() ; i++)
			{
				Graphics2D gProps = (Graphics2D) gPropSets.create();
				for (Card card : propSets.get(i).getCards())
				{
					gProps.drawImage(card.getGraphics(4), 0, 0, MDCard.CARD_SIZE.width * 2, MDCard.CARD_SIZE.height * 2, null);
					gProps.translate(130, 0);
				}
				gPropSets.translate(0, 185);
			}
			*/
		}
		
		public class CardSelectLayout implements LayoutManager2
		{

			@Override
			public void addLayoutComponent(String name, Component comp)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void layoutContainer(Container parent)
			{
				
			}

			@Override
			public Dimension minimumLayoutSize(Container parent)
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Dimension preferredLayoutSize(Container parent)
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void removeLayoutComponent(Component comp)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void addLayoutComponent(Component comp, Object constraints)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public float getLayoutAlignmentX(Container target)
			{
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public float getLayoutAlignmentY(Container target)
			{
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void invalidateLayout(Container target)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public Dimension maximumLayoutSize(Container target)
			{
				// TODO Auto-generated method stub
				return null;
			}
			
		}
	}
	
	public class RentLayout implements LayoutManager2
	{

		@Override
		public void addLayoutComponent(String arg0, Component arg1)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void layoutContainer(Container arg0)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public Dimension minimumLayoutSize(Container arg0)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Dimension preferredLayoutSize(Container arg0)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void removeLayoutComponent(Component arg0)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addLayoutComponent(Component comp, Object constraints)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public float getLayoutAlignmentX(Container target)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getLayoutAlignmentY(Container target)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void invalidateLayout(Container target)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public Dimension maximumLayoutSize(Container target)
		{
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
