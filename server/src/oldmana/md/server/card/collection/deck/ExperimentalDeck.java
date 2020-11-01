package oldmana.md.server.card.collection.deck;

import oldmana.md.server.card.action.CardActionCorruptedGame;
import oldmana.md.server.card.action.CardActionExecutiveDecision;
import oldmana.md.server.card.action.CardActionFilibuster;
import oldmana.md.server.card.action.CardActionMutualAgreement;
import oldmana.md.server.card.action.CardActionRedistributeWealth;
import oldmana.md.server.card.action.CardActionSecondTerm;
import oldmana.md.server.card.action.CardActionThisHandIsShit;

public class ExperimentalDeck extends BigGovDeck
{
	@Override
	public void createDeck()
	{
		super.createDeck();
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(new CardActionExecutiveDecision());
		}
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(new CardActionSecondTerm());
		}
		addCard(new CardActionCorruptedGame());
		for (int i = 0 ; i < 4 ; i++)
		{
			addCard(new CardActionMutualAgreement());
		}
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(new CardActionRedistributeWealth());
		}
		for (int i = 0 ; i < 2 ; i++)
		{
			addCard(new CardActionThisHandIsShit());
		}
		addCard(new CardActionFilibuster());
	}
}
