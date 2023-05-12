package oldmana.md.server.card;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardRegistry
{
	private static List<CardType<?>> registeredCards = new ArrayList<CardType<?>>();
	private static Map<String, CardType<?>> nameCardTypeMap = new HashMap<String, CardType<?>>();
	private static Map<Class<? extends Card>, CardType<?>> classCardTypeMap = new HashMap<Class<? extends Card>, CardType<?>>();
	
	public static <T extends Card> CardType<T> getTypeByClass(Class<T> cardClass)
	{
		return (CardType<T>) classCardTypeMap.get(cardClass);
	}
	
	/**
	 * Get a CardType by its unique internal name.
	 * @param name The name of the CardType
	 * @return The CardType of the given name
	 */
	public static CardType<?> getTypeByName(String name)
	{
		return nameCardTypeMap.get(name);
	}
	
	public static List<CardType<?>> getRegisteredCards()
	{
		return registeredCards;
	}
	
	public static <T extends Card> T createCard(Class<T> cardClass)
	{
		return getTypeByClass(cardClass).createCard();
	}
	
	public static <T extends Card> T createCard(Class<T> cardClass, CardTemplate template)
	{
		return getTypeByClass(cardClass).createCard(template);
	}
	
	public static void registerCardType(CardType<?> type)
	{
		registeredCards.add(type);
		classCardTypeMap.put(type.getCardClass(), type);
		nameCardTypeMap.put(type.getInternalName(), type);
	}
	
	/**
	 * Registers a CardType based on the class. The class MUST contain a static method named "createType" that returns
	 * a CardType. This method can have any access modifier.
	 * @param cardClass The class to register
	 * @return The recently registered type
	 */
	public static <T extends Card> CardType<T> registerCardType(Class<T> cardClass)
	{
		try
		{
			Method m = cardClass.getDeclaredMethod("createType");
			m.setAccessible(true);
			CardType<T> type = (CardType<T>) m.invoke(null);
			registerCardType(type);
			return type;
		}
		catch (NoSuchMethodException e)
		{
			throw new RuntimeException("Class " + cardClass.getName() + " does not contain static createType method", e);
		}
		catch (ReflectiveOperationException e)
		{
			throw new RuntimeException("Error while creating the card type in class " + cardClass.getName(), e);
		}
	}
}
