package oldmana.md.server.card.type;

import oldmana.md.server.MDServer;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.Card.CardDescription;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardBuilding;
import oldmana.md.server.card.CardMoney;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.CardRegistry;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.action.CardActionDealBreaker;
import oldmana.md.server.card.action.CardActionDebtCollector;
import oldmana.md.server.card.action.CardActionDoubleTheRent;
import oldmana.md.server.card.action.CardActionForcedDeal;
import oldmana.md.server.card.action.CardActionItsMyBirthday;
import oldmana.md.server.card.action.CardActionJustSayNo;
import oldmana.md.server.card.action.CardActionPassGo;
import oldmana.md.server.card.action.CardActionRent;
import oldmana.md.server.card.action.CardActionSlyDeal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A CardType associates general information and utilities with every card. Every class that extends Card somewhere down
 * the line should register a CardType associated with it. The friendly name, internal name, and aliases should all be
 * unique. If there are different common variations of the same card class, templates can be added to distinguish them.
 * @param <T> The Card type
 */
public class CardType<T extends Card>
{
	// Base Type
	public static CardType<Card> CARD;
	// Primitive Card Types
	public static CardType<CardMoney> MONEY;
	public static CardType<CardAction> ACTION;
	public static CardType<CardProperty> PROPERTY;
	public static CardType<CardBuilding> BUILDING;
	
	public static CardType<CardActionDealBreaker> DEAL_BREAKER;
	public static CardType<CardActionDebtCollector> DEBT_COLLECTOR;
	public static CardType<CardActionDoubleTheRent> DOUBLE_THE_RENT;
	public static CardType<CardActionForcedDeal> FORCED_DEAL;
	public static CardType<CardActionItsMyBirthday> ITS_MY_BIRTHDAY;
	public static CardType<CardActionJustSayNo> JUST_SAY_NO;
	public static CardType<CardActionPassGo> PASS_GO;
	public static CardType<CardActionRent> RENT;
	public static CardType<CardActionSlyDeal> SLY_DEAL;
	
	
	private Class<T> cardClass;
	
	private String internalName;
	private String friendlyName;
	private List<String> aliases;
	
	private CardType<? super T> parent;
	private CardType<? super T> primitive;
	
	private boolean instantiable;
	
	private CardTemplate defaultTemplate;
	
	private Map<CardTemplateInfo, CardTemplate> templates = new LinkedHashMap<CardTemplateInfo, CardTemplate>();
	private Map<String, CardTemplate> nameTemplateMap = new HashMap<String, CardTemplate>(); // For faster lookups
	
	private List<String> exemptReductions = new ArrayList<String>();
	
	public CardType(Class<T> cardClass, String friendlyName, String... aliases)
	{
		this(cardClass, toInternalName(friendlyName), friendlyName, true, aliases);
	}
	
	public CardType(Class<T> cardClass, String friendlyName, boolean instantiable, String... aliases)
	{
		this(cardClass, toInternalName(friendlyName), friendlyName, instantiable, aliases);
	}
	
	// Order is like this because of dumb varargs ambiguity
	public CardType(Class<T> cardClass, String internalName, String friendlyName, boolean instantiable, String... aliases)
	{
		this.cardClass = cardClass;
		this.internalName = internalName;
		this.friendlyName = friendlyName;
		this.aliases = new ArrayList<String>(Arrays.asList(aliases));
		this.instantiable = instantiable;
		if (Card.class.isAssignableFrom(cardClass.getSuperclass()))
		{
			parent = (CardType<? super T>) CardRegistry.getTypeByClass((Class<? extends Card>) cardClass.getSuperclass());
			if (parent == null)
			{
				throw new IllegalStateException("Could not create " + cardClass.getName() + " because " +
						cardClass.getSuperclass().getName() + " is not registered!");
			}
			defaultTemplate = parent.getDefaultTemplate().clone();
			defaultTemplate.setAssociatedType(this);
			exemptReductions = new ArrayList<String>(parent.getExemptReductions());
			
			if (parent.getCardClass() == Card.class)
			{
				primitive = this;
			}
			else
			{
				CardType<? super T> parentType = parent;
				while (parentType.getParent().getCardClass() != Card.class)
				{
					parentType = parentType.getParent();
				}
				primitive = parentType;
			}
		}
	}
	
	public Class<T> getCardClass()
	{
		return cardClass;
	}
	
	public String getInternalName()
	{
		return internalName;
	}
	
	public String getFriendlyName()
	{
		return friendlyName;
	}
	
	public List<String> getAliases()
	{
		return aliases;
	}
	
	public int getValue()
	{
		return defaultTemplate.getInt("value");
	}
	
	public String[] getDisplayName()
	{
		return defaultTemplate.getStringArray("displayName");
	}
	
	public CardDescription getDescription()
	{
		return CardDescription.getDescription(defaultTemplate.getStringArray("description"));
	}
	
	public int getFontSize()
	{
		return defaultTemplate.getInt("fontSize");
	}
	
	public int getDisplayOffsetY()
	{
		return defaultTemplate.getInt("displayOffsetY");
	}
	
	public boolean isRevocable()
	{
		return defaultTemplate.getBoolean("revocable");
	}
	
	public boolean clearsRevocableCards()
	{
		return defaultTemplate.getBoolean("clearsRevocableCards");
	}
	
	public CardType<? super T> getParent()
	{
		return parent;
	}
	
	/**
	 * Checks if the type is this type or a parent of this type.
	 */
	public boolean isRelated(CardType<?> type)
	{
		if (type == this)
		{
			return true;
		}
		if (parent == null)
		{
			return false;
		}
		CardType<? super T> parentType = parent;
		do
		{
			if (parentType == type)
			{
				return true;
			}
		}
		while ((parentType = parentType.getParent()) != null);
		return false;
	}
	
	public CardType<? super T> getPrimitive()
	{
		return primitive;
	}
	
	public boolean isPrimitive()
	{
		return this == primitive;
	}
	
	public boolean isInstantiable()
	{
		return instantiable;
	}
	
	public void setDefaultTemplate(CardTemplate template)
	{
		defaultTemplate = template;
		//addTemplate(template, friendlyName, aliases.toArray(new String[0]));
	}
	
	public CardTemplate getDefaultTemplate()
	{
		return defaultTemplate;
	}
	
	public void addTemplate(CardTemplate template, String name, String... aliases)
	{
		template.setAssociatedType(this);
		CardTemplateInfo info = new CardTemplateInfo(name, aliases);
		templates.put(info, template);
		nameTemplateMap.put(name, template);
		for (String alias : aliases)
		{
			nameTemplateMap.put(alias, template);
		}
	}
	
	public CardTemplate getTemplate(String name)
	{
		return nameTemplateMap.get(standardize(name));
	}
	
	public Map<CardTemplateInfo, CardTemplate> getTemplates()
	{
		return templates;
	}
	
	public List<CardTemplate> getTemplateList()
	{
		return new ArrayList<CardTemplate>(templates.values());
	}
	
	public void addExemptReduction(String exempt)
	{
		exemptReductions.add(exempt);
	}
	
	public void removeExemptReduction(String exempt)
	{
		exemptReductions.remove(exempt);
	}
	
	public List<String> getExemptReductions()
	{
		return exemptReductions;
	}
	
	public boolean isExemptReduction(String key)
	{
		return exemptReductions.contains(key);
	}
	
	/**
	 * Create a card of this type with the default template.
	 * @return A newly created Card
	 */
	public T createCard()
	{
		return createCard(defaultTemplate);
	}
	
	/**
	 * Create a card of this type with the provided template.
	 * @param template The template to base the card off of
	 * @return A newly created Card
	 */
	public T createCard(CardTemplate template)
	{
		T card = createCardRaw(template);
		MDServer.getInstance().getVoidCollection().addCard(card);
		MDServer.getInstance().broadcastPacket(card.getCardDataPacket());
		return card;
	}
	
	/**
	 * The Consumer is called after the default template is applied and before the Card is added to the void and is sent to clients.
	 * @param constructor The function to call
	 * @return A newly created Card
	 */
	public T createCard(Consumer<T> constructor)
	{
		return createCard(defaultTemplate, constructor);
	}
	
	public T createCard(CardTemplate template, Consumer<T> constructor)
	{
		T card = createCardRaw(template);
		constructor.accept(card);
		MDServer.getInstance().getVoidCollection().addCard(card);
		MDServer.getInstance().broadcastPacket(card.getCardDataPacket());
		return card;
	}
	
	/**
	 * Create a card of this type with the provided template. The returned Card will not yet have been added to a
	 * collection or sent to clients.
	 * @param template The template to base the card off of
	 * @return A newly created Card
	 */
	public T createCardRaw(CardTemplate template)
	{
		T card = createCardRaw();
		card.setType(this);
		card.applyTemplate(template);
		return card;
	}
	
	/**
	 * Create a card of this type with the provided template. The returned Card will not yet have been added to a
	 * collection, not been sent to clients, and have no type and template. Should only be used if you know what
	 * you're doing.
	 * @return A newly created Card
	 */
	public T createCardRaw()
	{
		try
		{
			return cardClass.getConstructor().newInstance();
		}
		catch (ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Checks if the String is referring to the internal name or any aliases.
	 */
	public boolean isReferringToThis(String str)
	{
		str = standardize(str);
		if (standardize(internalName).equals(str))
		{
			return true;
		}
		
		for (String alias : aliases)
		{
			if (standardize(alias).equals(str))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private String standardize(String str)
	{
		return str.toLowerCase().replace(" ", "").replace("!", "").replace("'", "").replace(".", "");
	}
	
	private static String toInternalName(String friendlyName)
	{
		friendlyName = friendlyName.replace("!", "").replace("'", "").replace(".", "");
		String internalName = "";
		String[] split = friendlyName.split(" ");
		if (split.length > 0)
		{
			internalName += split[0].toLowerCase();
			for (int i = 1 ; i < split.length ; i++)
			{
				internalName += Character.toUpperCase(split[i].charAt(0));
				internalName += split[i].substring(1);
			}
		}
		return internalName;
	}
	
	public static class CardTemplateInfo
	{
		private String name;
		private List<String> aliases;
		
		public CardTemplateInfo(String name, String... aliases)
		{
			this.name = name;
			this.aliases = new ArrayList<String>(Arrays.asList(aliases));
		}
		
		public String getName()
		{
			return name;
		}
		
		public List<String> getAliases()
		{
			return aliases;
		}
	}
}
