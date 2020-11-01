package oldmana.general.md.client.gui.action;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import oldmana.general.md.client.card.Card;
import oldmana.general.md.client.card.CardProperty;
import oldmana.general.md.client.card.CardProperty.PropertyColor;
import oldmana.general.md.client.card.collection.PropertySet;
import oldmana.general.md.client.gui.component.MDButton;
import oldmana.general.md.client.gui.component.MDCard;
import oldmana.general.md.client.gui.component.MDColorSelection;
import oldmana.general.md.client.gui.component.MDLabel;
import oldmana.general.md.client.state.client.ActionStateClientModifyPropertySet;

public class ActionScreenModifyPropertySet extends ActionScreen
{
	private ActionStateClientModifyPropertySet state;
	private PropertySet set;
	
	private List<MDCard> cards = new ArrayList<MDCard>();
	private List<MDColorSelection> colors = new ArrayList<MDColorSelection>();
	
	public ActionScreenModifyPropertySet(ActionStateClientModifyPropertySet state, PropertySet set)
	{
		super();
		this.state = state;
		this.set = set;
		MDLabel prop = new MDLabel("Select Property To Move");
		prop.setSize(400, 40);
		prop.setLocation(600, 50);
		add(prop);
		MDLabel color = new MDLabel("Select Property Set Color");
		color.setSize(400, 40);
		color.setLocation(600, 400);
		add(color);
		setup();
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
			if (!prop.isSingleColor())
			{
				ui.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mouseReleased(MouseEvent event)
					{
						state.propertySelected(ui.getCard());
					}
				});
			}
		}
		List<PropertyColor> colors = set.getPossibleBaseColors();
		offset = ((colors.size() - 1) * (40 + 15)) + 40;
		for (int i = 0 ; i < colors.size() ; i++)
		{
			boolean selected = set.getEffectiveColor() == colors.get(i);
			MDColorSelection ui = new MDColorSelection(colors.get(i), selected);
			ui.setLocation(800 - offset + (i * (80 + 30)), 500);
			add(ui);
			if (!selected)
			{
				ui.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mouseReleased(MouseEvent event)
					{
						state.colorSelected(ui.getColor());
					}
				});
			}
		}
		MDButton cancel = new MDButton("Cancel");
		cancel.setSize(180, 50);
		cancel.setLocation(800 - 90, 900 - 50 - 10);
		cancel.setFontSize(24);
		cancel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				state.cancel();
			}
		});
		add(cancel);
	}
	
	public void cleanup()
	{
		
	}
}
