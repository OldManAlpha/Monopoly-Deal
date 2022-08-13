package oldmana.md.server.card;

import java.util.LinkedList;
import java.util.List;

public class CardRegistry
{
	private List<RegisteredCard> actionCards = new LinkedList<RegisteredCard>();
	private List<RegisteredCard> specialCards = new LinkedList<RegisteredCard>();
	
	public void registerActionCard(Class<? extends Card> type, String name, String... aliases)
	{
		actionCards.add(new RegisteredCard(type, name, aliases));
	}
	
	public void registerActionCard(Class<? extends Card> type, CardConstructor<? extends Card> constructor, String name, String... aliases)
	{
		actionCards.add(new RegisteredCard(type, constructor, name, aliases));
	}
	
	public void registerSpecialCard(Class<? extends Card> type, String name, String... aliases)
	{
		specialCards.add(new RegisteredCard(type, name, aliases));
	}
	
	public void registerSpecialCard(Class<? extends Card> type, CardConstructor<? extends Card> constructor, String name, String... aliases)
	{
		specialCards.add(new RegisteredCard(type, constructor, name, aliases));
	}
	
	/**
	 * Multiple cards may be registered under the same class, so this method may return an ambiguous card.
	 * @param clazz - The class type of the card
	 * @return A RegisteredCard with the given type
	 */
	public RegisteredCard getRegisteredActionCardByClass(Class<? extends Card> clazz)
	{
		for (RegisteredCard card : actionCards)
		{
			if (card.getType() == clazz)
			{
				return card;
			}
		}
		return null;
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
	
	/**
	 * Multiple cards may be registered under the same class, so this method may return an ambiguous card.
	 * @param clazz - The class type of the card
	 * @return A RegisteredCard with the given type
	 */
	public RegisteredCard getRegisteredSpecialCardByClass(Class<? extends Card> clazz)
	{
		for (RegisteredCard card : specialCards)
		{
			if (card.getType() == clazz)
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
	
	/**
	 * Multiple cards may be registered under the same class, so this method may return an ambiguous card. This method searches both action cards and special cards.
	 * @param clazz - The class type of the card
	 * @return A RegisteredCard with the given type
	 */
	public RegisteredCard getRegisteredCardByClass(Class<? extends Card> clazz)
	{
		for (RegisteredCard card : actionCards)
		{
			if (card.getType() == clazz)
			{
				return card;
			}
		}
		for (RegisteredCard card : specialCards)
		{
			if (card.getType() == clazz)
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
		private Class<? extends Card> type;
		private CardConstructor<?> constructor;
		
		private String name;
		private String[] aliases;
		
		public RegisteredCard(Class<? extends Card> type, String name, String[] aliases)
		{
			this(type, () ->
			{
				try
				{
					return type.newInstance();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				return null;
			}, name, aliases);
		}
		
		public RegisteredCard(Class<? extends Card> type, CardConstructor<? extends Card> constructor, String name, String[] aliases)
		{
			this.type = type;
			this.constructor = constructor;
			
			this.name = name;
			this.aliases = aliases;
		}
		
		public Class<? extends Card> getType()
		{
			return type;
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
