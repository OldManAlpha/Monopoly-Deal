package oldmana.md.server.card;

import java.util.LinkedList;
import java.util.List;

public class CardRegistry
{
	private List<RegisteredCard> actionCards = new LinkedList<RegisteredCard>();
	private List<RegisteredCard> specialCards = new LinkedList<RegisteredCard>();
	
	public void registerActionCard(Class<? extends Card> clazz, String name, String... aliases)
	{
		actionCards.add(new RegisteredCard(clazz, name, aliases));
	}
	
	public void registerActionCard(CardConstructor<?> constructor, String name, String... aliases)
	{
		actionCards.add(new RegisteredCard(constructor, name, aliases));
	}
	
	public void registerSpecialCard(Class<? extends Card> clazz, String name, String... aliases)
	{
		specialCards.add(new RegisteredCard(clazz, name, aliases));
	}
	
	public void registerSpecialCard(CardConstructor<?> constructor, String name, String... aliases)
	{
		specialCards.add(new RegisteredCard(constructor, name, aliases));
	}
	
	public RegisteredCard getRegisteredActionCardByName(String name)
	{
		for (RegisteredCard card : actionCards)
		{
			if (card.isReferringToThis(name))
			{
				return card;
			}
		}
		return null;
	}
	
	public RegisteredCard getRegisteredSpecialCardByName(String name)
	{
		for (RegisteredCard card : specialCards)
		{
			if (card.isReferringToThis(name))
			{
				return card;
			}
		}
		return null;
	}
	
	public List<RegisteredCard> getRegisteredActionCards()
	{
		return actionCards;
	}
	
	public List<RegisteredCard> getRegisteredSpecialCards()
	{
		return specialCards;
	}
	
	public interface CardConstructor<T extends Card>
	{
		public T createCard();
	}
	
	public class RegisteredCard
	{
		private CardConstructor<?> constructor;
		
		private String name;
		private String[] aliases;
		
		public RegisteredCard(Class<? extends Card> clazz, String name, String[] aliases)
		{
			this(() ->
			{
				try
				{
					return clazz.newInstance();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				return null;
			}, name, aliases);
		}
		
		public RegisteredCard(CardConstructor<?> constructor, String name, String[] aliases)
		{
			this.constructor = constructor;
			
			this.name = name;
			this.aliases = aliases;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String[] getAliases()
		{
			return aliases;
		}
		
		public boolean isReferringToThis(String str)
		{
			str = str.toLowerCase().replace(" ", "").replace("!", "").replace("'", "").replace(".", "");
			if (name.toLowerCase().replace(" ", "").replace("!", "").replace("'", "").replace(".", "").equals(str))
			{
				return true;
			}
			
			for (String alias : aliases)
			{
				if (alias.toLowerCase().replace(" ", "").replace("!", "").replace("'", "").replace(".", "").equals(str))
				{
					return true;
				}
			}
			
			return false;
		}
		
		public Card createCard()
		{
			return constructor.createCard();
		}
	}
}
