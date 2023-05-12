package oldmana.md.server.card.collection.deck;

import oldmana.md.server.card.CardMoney;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.PropertyColor;
import oldmana.md.server.card.action.*;
import oldmana.md.server.card.CardType;

public class VanillaDeck extends DeckStack
{
	@Override
	public void createDeck()
	{
		addCard(CardProperty.create(1, "Baltic Avenue", PropertyColor.BROWN));
		addCard(CardProperty.create(1, "Mediterranean Avenue", PropertyColor.BROWN));
		addCard(CardProperty.create(1, "Connecticut Avenue", PropertyColor.LIGHT_BLUE));
		addCard(CardProperty.create(1, "Oriental Avenue", PropertyColor.LIGHT_BLUE));
		addCard(CardProperty.create(1, "Vermont Avenue", PropertyColor.LIGHT_BLUE));
		addCard(CardProperty.create(2, "States Avenue", PropertyColor.MAGENTA));
		addCard(CardProperty.create(2, "St. Charles Place", PropertyColor.MAGENTA));
		addCard(CardProperty.create(2, "Virginia Avenue", PropertyColor.MAGENTA));
		addCard(CardProperty.create(2, "New York Avenue", PropertyColor.ORANGE));
		addCard(CardProperty.create(2, "St. James Place", PropertyColor.ORANGE));
		addCard(CardProperty.create(2, "Tennessee Avenue", PropertyColor.ORANGE));
		addCard(CardProperty.create(3, "Illinois Avenue", PropertyColor.RED));
		addCard(CardProperty.create(3, "Indiana Avenue", PropertyColor.RED));
		addCard(CardProperty.create(3, "Kentucky Avenue", PropertyColor.RED));
		addCard(CardProperty.create(3, "Marvin Gardens", PropertyColor.YELLOW));
		addCard(CardProperty.create(3, "Ventnor Avenue", PropertyColor.YELLOW));
		addCard(CardProperty.create(3, "Atlantic Avenue", PropertyColor.YELLOW));
		addCard(CardProperty.create(4, "Pacific Avenue", PropertyColor.GREEN));
		addCard(CardProperty.create(4, "North Carolina Avenue", PropertyColor.GREEN));
		addCard(CardProperty.create(4, "Pennsylvania Avenue", PropertyColor.GREEN));
		addCard(CardProperty.create(4, "Park Place", PropertyColor.DARK_BLUE));
		addCard(CardProperty.create(4, "Boardwalk", PropertyColor.DARK_BLUE));
		addCard(CardProperty.create(2, "Short Line", PropertyColor.RAILROAD));
		addCard(CardProperty.create(2, "Reading Railroad", PropertyColor.RAILROAD));
		addCard(CardProperty.create(2, "B. & O. Railroad", PropertyColor.RAILROAD));
		addCard(CardProperty.create(2, "Pennsylvania Railroad", PropertyColor.RAILROAD));
		addCard(CardProperty.create(2, "Electric Company", PropertyColor.UTILITY));
		addCard(CardProperty.create(2, "Water Works", PropertyColor.UTILITY));
		
		addCard(CardProperty.create(1, "Property Wild Card", PropertyColor.BROWN, PropertyColor.LIGHT_BLUE));
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(CardProperty.create(2, "Property Wild Card", PropertyColor.MAGENTA, PropertyColor.ORANGE));
			addCard(CardProperty.create(3, "Property Wild Card", PropertyColor.RED, PropertyColor.YELLOW));
		}
		addCard(CardProperty.create(4, "Property Wild Card", PropertyColor.GREEN, PropertyColor.DARK_BLUE));
		addCard(CardProperty.create(4, "Property Wild Card", PropertyColor.LIGHT_BLUE, PropertyColor.RAILROAD));
		addCard(CardProperty.create(4, "Property Wild Card", PropertyColor.GREEN, PropertyColor.RAILROAD));
		addCard(CardProperty.create(2, "Property Wild Card", PropertyColor.RAILROAD, PropertyColor.UTILITY));
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(CardProperty.RAINBOW_WILD.createCard());
		}
		
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(CardType.DEAL_BREAKER.createCard());
		}
		for (int i = 0 ; i < 3 ; i++)
		{
			addCard(CardType.FORCED_DEAL.createCard());
		}
		for (int i = 0 ; i < 3 ; i++)
		{
			addCard(CardType.SLY_DEAL.createCard());
		}
		for (int i = 0 ; i < 3 ; i++)
		{
			addCard(CardType.JUST_SAY_NO.createCard());
		}
		for (int i = 0 ; i < 3 ; i++)
		{
			addCard(CardType.DEBT_COLLECTOR.createCard());
		}
		for (int i = 0 ; i < 3 ; i++)
		{
			addCard(CardType.ITS_MY_BIRTHDAY.createCard());
		}
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(CardType.DOUBLE_THE_RENT.createCard());
		}
		for (int i = 0 ; i < 10 ; i++)
		{
			addCard(CardType.PASS_GO.createCard());
		}
		for (int i = 0 ; i < 3 ; i++)
		{
			addCard(CardType.HOUSE.createCard());
		}
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(CardType.HOTEL.createCard());
		}
		
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(CardActionRent.BROWN_LIGHT_BLUE.createCard());
		}
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(CardActionRent.MAGENTA_ORANGE.createCard());
		}
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(CardActionRent.RED_YELLOW.createCard());
		}
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(CardActionRent.GREEN_DARK_BLUE.createCard());
		}
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(CardActionRent.RAILROAD_UTILITY.createCard());
		}
		for (int i = 0 ; i < 3 ; i++)
		{
			addCard(CardActionRent.RAINBOW.createCard());
		}
		
		for (int i = 0 ; i < 6 ; i++)
		{
			addCard(CardMoney.ONE_MIL.createCard());
		}
		for (int i = 0 ; i < 5 ; i++)
		{
			addCard(CardMoney.TWO_MIL.createCard());
		}
		for (int i = 0 ; i < 3 ; i++)
		{
			addCard(CardMoney.THREE_MIL.createCard());
		}
		for (int i = 0 ; i < 3 ; i++)
		{
			addCard(CardMoney.FOUR_MIL.createCard());
		}
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(CardMoney.FIVE_MIL.createCard());
		}
		addCard(CardMoney.TEN_MIL.createCard());
		
		setDeckRules(getServer().getGameRules().getRootRuleStruct().generateDefaults());
	}
}
