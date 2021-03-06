package oldmana.md.server.card.collection.deck;

import oldmana.md.server.card.CardMoney;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.CardProperty.PropertyColor;
import oldmana.md.server.card.action.*;
import oldmana.md.server.card.property.CardPropertyRainbowWildCard;

public class VanillaDeck extends DeckStack
{
	@Override
	public void createDeck()
	{
		addCard(new CardProperty(PropertyColor.BROWN, 1, "Baltic Avenue"));
		addCard(new CardProperty(PropertyColor.BROWN, 1, "Mediterranean Avenue"));
		addCard(new CardProperty(PropertyColor.LIGHT_BLUE, 1, "Connecticut Avenue"));
		addCard(new CardProperty(PropertyColor.LIGHT_BLUE, 1, "Oriental Avenue"));
		addCard(new CardProperty(PropertyColor.LIGHT_BLUE, 1, "Vermont Avenue"));
		addCard(new CardProperty(PropertyColor.MAGENTA, 2, "States Avenue"));
		addCard(new CardProperty(PropertyColor.MAGENTA, 2, "St. Charles Place"));
		addCard(new CardProperty(PropertyColor.MAGENTA, 2, "Virginia Avenue"));
		addCard(new CardProperty(PropertyColor.ORANGE, 2, "New York Avenue"));
		addCard(new CardProperty(PropertyColor.ORANGE, 2, "St. James Place"));
		addCard(new CardProperty(PropertyColor.ORANGE, 2, "Tennessee Avenue"));
		addCard(new CardProperty(PropertyColor.RED, 3, "Illinois Avenue"));
		addCard(new CardProperty(PropertyColor.RED, 3, "Indiana Avenue"));
		addCard(new CardProperty(PropertyColor.RED, 3, "Kentucky Avenue"));
		addCard(new CardProperty(PropertyColor.YELLOW, 3, "Marvin Gardens"));
		addCard(new CardProperty(PropertyColor.YELLOW, 3, "Ventnor Avenue"));
		addCard(new CardProperty(PropertyColor.YELLOW, 3, "Atlantic Avenue"));
		addCard(new CardProperty(PropertyColor.GREEN, 4, "Pacific Avenue"));
		addCard(new CardProperty(PropertyColor.GREEN, 4, "North Carolina Avenue"));
		addCard(new CardProperty(PropertyColor.GREEN, 4, "Pennsylyvania Avenue"));
		addCard(new CardProperty(PropertyColor.DARK_BLUE, 4, "Park Place"));
		addCard(new CardProperty(PropertyColor.DARK_BLUE, 4, "Boardwalk"));
		addCard(new CardProperty(PropertyColor.RAILROAD, 2, "Short Line"));
		addCard(new CardProperty(PropertyColor.RAILROAD, 2, "Reading Railroad"));
		addCard(new CardProperty(PropertyColor.RAILROAD, 2, "B. & O. Railroad"));
		addCard(new CardProperty(PropertyColor.RAILROAD, 2, "Pennsylvania Railroad"));
		addCard(new CardProperty(PropertyColor.UTILITY, 2, "Electric Company"));
		addCard(new CardProperty(PropertyColor.UTILITY, 2, "Water Works"));
		
		addCard(new CardProperty(new PropertyColor[] {PropertyColor.BROWN, PropertyColor.LIGHT_BLUE}, 1, "Property Wild Card", true));
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(new CardProperty(new PropertyColor[] {PropertyColor.MAGENTA, PropertyColor.ORANGE}, 2, "Property Wild Card", true));
			addCard(new CardProperty(new PropertyColor[] {PropertyColor.RED, PropertyColor.YELLOW}, 3, "Property Wild Card", true));
		}
		addCard(new CardProperty(new PropertyColor[] {PropertyColor.GREEN, PropertyColor.DARK_BLUE}, 4, "Property Wild Card", true));
		addCard(new CardProperty(new PropertyColor[] {PropertyColor.LIGHT_BLUE, PropertyColor.RAILROAD}, 4, "Property Wild Card", true));
		addCard(new CardProperty(new PropertyColor[] {PropertyColor.GREEN, PropertyColor.RAILROAD}, 4, "Property Wild Card", true));
		addCard(new CardProperty(new PropertyColor[] {PropertyColor.RAILROAD, PropertyColor.UTILITY}, 2, "Property Wild Card", true));
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(new CardPropertyRainbowWildCard());
		}
		
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(new CardActionDealBreaker());
		}
		for (int i = 0 ; i < 3 ; i++)
		{
			addCard(new CardActionForcedDeal());
		}
		for (int i = 0 ; i < 3 ; i++)
		{
			addCard(new CardActionSlyDeal());
		}
		for (int i = 0 ; i < 3 ; i++)
		{
			addCard(new CardActionJustSayNo());
		}
		for (int i = 0 ; i < 3 ; i++)
		{
			addCard(new CardActionDebtCollector());
		}
		for (int i = 0 ; i < 3 ; i++)
		{
			addCard(new CardActionItsMyBirthday());
		}
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(new CardActionDoubleTheRent());
		}
		for (int i = 0 ; i < 10 ; i++)
		{
			addCard(new CardActionPassGo());
		}
		
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(new CardActionRent(1, PropertyColor.TIER_1));
		}
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(new CardActionRent(1, PropertyColor.TIER_2));
		}
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(new CardActionRent(1, PropertyColor.TIER_3));
		}
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(new CardActionRent(1, PropertyColor.TIER_4));
		}
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(new CardActionRent(1, PropertyColor.TIER_OTHER));
		}
		for (int i = 0 ; i < 3 ; i++)
		{
			addCard(new CardActionRent(3, PropertyColor.getAllColors()));
		}
		
		for (int i = 0 ; i < 6 ; i++)
		{
			addCard(new CardMoney(1));
		}
		for (int i = 0 ; i < 5 ; i++)
		{
			addCard(new CardMoney(2));
		}
		for (int i = 0 ; i < 3 + 3 ; i++) // +3 to compensate for houses
		{
			addCard(new CardMoney(3));
		}
		for (int i = 0 ; i < 3 + 2 ; i++) // +2 to compensate for hotels
		{
			addCard(new CardMoney(4));
		}
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(new CardMoney(5));
		}
		addCard(new CardMoney(10));
	}
}
