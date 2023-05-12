package oldmana.md.client.gui.action;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardBuilding;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.CardProperty.PropertyColor;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.LayoutAdapter;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDCardOption;
import oldmana.md.client.gui.component.MDColorSelection;
import oldmana.md.client.gui.component.MDLabel;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.state.client.ActionStateClientModifyPropertySet;

public class ActionScreenModifyPropertySet extends ActionScreen
{
	private ActionStateClientModifyPropertySet state;
	private PropertySet set;
	
	private MDLabel propLabel;
	private MDLabel colorLabel;
	
	private List<MDCardOption> cards = new ArrayList<MDCardOption>();
	private List<MDColorSelection> colors = new ArrayList<MDColorSelection>();
	
	private MDButton cancel;
	
	public ActionScreenModifyPropertySet(ActionStateClientModifyPropertySet state, PropertySet set)
	{
		super();
		this.state = state;
		this.set = set;
		propLabel = new MDLabel("Property Set Options");
		add(propLabel);
		colorLabel = new MDLabel("Choose Property Set Color");
		add(colorLabel);
		setup();
		setLayout(new ModifyPropertySetLayout());
	}
	
	public void setup()
	{
		List<Card> cards = set.getCards();
		for (Card card : cards)
		{
			MDCardOption ui = new MDCardOption(card);
			add(ui);
			this.cards.add(ui);
			if (card instanceof CardProperty)
			{
				CardProperty prop = (CardProperty) card;
				if (set.hasBuildings())
				{
					ui.getCardUI().setBanner("Locked");
				}
				else if (prop.isSingleColor())
				{
					ui.getCardUI().setBanner("Unmovable");
				}
				else
				{
					Runnable listener = () -> state.moveProperty((CardProperty) card);
					ui.getCardUI().addClickListener(listener);
					MDButton moveButton = new MDButton("Move Property");
					moveButton.setListener(listener);
					ui.addButton(moveButton);
				}
			}
			else if (card instanceof CardBuilding)
			{
				if (((CardBuilding) card).getTier() == set.getHighestBuildingTier())
				{
					MDButton bankButton = new MDButton("Move to Bank");
					bankButton.setListener(() -> state.moveBuilding((CardBuilding) card));
					ui.addButton(bankButton);
				}
			}
		}
		List<PropertyColor> colors = set.getPossibleBaseColors();
		for (PropertyColor color : colors)
		{
			boolean selected = set.getEffectiveColor() == color;
			MDColorSelection ui = new MDColorSelection(color, selected);
			add(ui);
			this.colors.add(ui);
			if (!selected)
			{
				ui.addClickListener(() -> state.colorSelected(ui.getColor()));
			}
		}
		cancel = new MDButton("Cancel");
		cancel.setFontSize(24);
		cancel.addClickListener(() -> state.cancel());
		add(cancel);
	}
	
	public void cleanup()
	{
	
	}
	
	public class ModifyPropertySetLayout extends LayoutAdapter
	{
		@Override
		public void layoutContainer(Container arg0)
		{
			propLabel.setSize(scale(40));
			propLabel.setLocationCentered(getWidth() * 0.5, getHeight() * 0.1);
			
			int offset = ((cards.size() - 1) * (GraphicsUtils.getCardWidth() + scale(25))) + GraphicsUtils.getCardWidth();
			for (int i = 0 ; i < cards.size() ; i++)
			{
				MDCardOption ui = cards.get(i);
				ui.setLocation((int) (getWidth() * 0.5) - offset + (i * (GraphicsUtils.getCardWidth() * 2 + 50)), propLabel.getMaxY() + scale(20));
			}
			
			int maxCardY = propLabel.getMaxY() + MDCardOption.getComponentHeight();
			
			colorLabel.setSize(scale(40));
			colorLabel.setLocationCentered(getWidth() * 0.5, maxCardY + scale(70));
			
			offset = ((colors.size() - 1) * scale(40 + 15)) + scale(40);
			for (int i = 0 ; i < colors.size() ; i++)
			{
				MDColorSelection ui = colors.get(i);
				ui.setLocation((int) (getWidth() * 0.5) - offset + (i * scale(80 + 30)), colorLabel.getMaxY() + scale(20));
			}
			
			cancel.setSize(scale(180), scale(50));
			cancel.setLocationCentered(getWidth() / 2, getHeight() - scale(40));
		}

		@Override
		public void invalidateLayout(Container target)
		{
			layoutContainer(target);
		}
	}
}
