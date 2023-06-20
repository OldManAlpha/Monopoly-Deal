package oldmana.md.server.card;

import oldmana.md.server.MDServer;
import oldmana.md.server.card.action.CardActionCharge;
import oldmana.md.server.card.action.CardActionDealBreaker;
import oldmana.md.server.card.action.CardActionDebtCollector;
import oldmana.md.server.card.action.CardActionDoubleTheRent;
import oldmana.md.server.card.action.CardActionForcedDeal;
import oldmana.md.server.card.action.CardActionHotel;
import oldmana.md.server.card.action.CardActionHouse;
import oldmana.md.server.card.action.CardActionItsMyBirthday;
import oldmana.md.server.card.action.CardActionJustSayNo;
import oldmana.md.server.card.action.CardActionPassGo;
import oldmana.md.server.card.action.CardActionRent;
import oldmana.md.server.card.action.CardActionSlyDeal;
import oldmana.md.server.mod.ServerMod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
	// Intermediate Action Cards
	public static CardType<CardActionCharge> CHARGE;
	public static CardType<CardActionBuilding> BUILDING;
	// Charge Cards
	public static CardType<CardActionDebtCollector> DEBT_COLLECTOR;
	public static CardType<CardActionItsMyBirthday> ITS_MY_BIRTHDAY;
	// Building Cards
	public static CardType<CardActionHouse> HOUSE;
	public static CardType<CardActionHotel> HOTEL;
	// Action Cards
	public static CardType<CardActionDealBreaker> DEAL_BREAKER;
	public static CardType<CardActionDoubleTheRent> DOUBLE_THE_RENT;
	public static CardType<CardActionForcedDeal> FORCED_DEAL;
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
	
	private Supplier<T> factory;
	private boolean visible;
	
	private CardTemplate defaultTemplate;
	
	private List<RegisteredCardTemplate> templates = new ArrayList<RegisteredCardTemplate>();
	private Map<String, CardTemplate> nameTemplateMap = new HashMap<String, CardTemplate>(); // For faster lookups
	
	private Map<String, Boolean> exemptReductions = new HashMap<String, Boolean>(); // Key: Json Name / Value: Carry to children?
	
	private ServerMod associatedMod;
	
	/**
	 * Constructor for card types that cannot be instantiated.
	 */
	public CardType(Class<T> cardClass, String friendlyName, String... aliases)
	{
		this(cardClass, toInternalName(friendlyName), friendlyName, null, false, aliases);
	}
	
	public CardType(Class<T> cardClass, Supplier<T> factory, String friendlyName, String... aliases)
	{
		this(cardClass, toInternalName(friendlyName), friendlyName, factory, true, aliases);
	}
	
	public CardType(Class<T> cardClass, Supplier<T> factory, boolean visible, String friendlyName, String... aliases)
	{
		this(cardClass, toInternalName(friendlyName), friendlyName, factory, visible, aliases);
	}
	
	// Order is like this because of dumb varargs ambiguity
	public CardType(Class<T> cardClass, String internalName, String friendlyName, Supplier<T> factory, boolean visible, String... aliases)
	{
		this.cardClass = cardClass;
		this.internalName = internalName;
		this.friendlyName = friendlyName;
		this.aliases = new ArrayList<String>(Arrays.asList(aliases));
		this.factory = factory;
		this.visible = visible;
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
			parent.getExemptReductionsMap().forEach((exempt, carries) ->
			{
				if (carries)
				{
					exemptReductions.put(exempt, true);
				}
			});
			
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
		Object desc = defaultTemplate.getObject("description");
		return desc instanceof String ? CardDescription.getDescription((String) desc) :
				CardDescription.getDescription(defaultTemplate.getStringArray("description"));
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
	
	/**
	 * Gets this type's primitive ancestor.
	 * @return The primitive ancestor
	 */
	public CardType<? super T> getPrimitive()
	{
		return primitive;
	}
	
	/**
	 * Primitive types are those that directly extend the Card class, such as CardAction, CardMoney, and CardProperty.
	 * @return Whether the type is primitive
	 */
	public boolean isPrimitive()
	{
		return this == primitive;
	}
	
	/**
	 * Check if this type refers to the root Card class.
	 * @return True if this is the root card type
	 */
	public boolean isRoot()
	{
		return cardClass == Card.class;
	}
	
	public boolean isInstantiable()
	{
		return factory != null;
	}
	
	public Supplier<T> getFactory()
	{
		return factory;
	}
	
	/**
	 * Set the factory that constructs cards of this type. Try to avoid using this if there's anything else you can do.
	 * @param factory A factory that supplies this type of card
	 */
	public void setFactory(Supplier<T> factory)
	{
		this.factory = factory;
	}
	
	public boolean isVisible()
	{
		return visible;
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
		template.put("template", name);
		RegisteredCardTemplate registeredTemplate = new RegisteredCardTemplate(template, name, aliases);
		templates.add(registeredTemplate);
		nameTemplateMap.put(name, template);
		for (String alias : aliases)
		{
			nameTemplateMap.put(alias, template);
		}
	}
	
	public CardTemplate getTemplate(String name)
	{
		return nameTemplateMap.get(name);
	}
	
	public List<RegisteredCardTemplate> getTemplates()
	{
		return templates;
	}
	
	public List<CardTemplate> getTemplateList()
	{
		return templates.stream().map(RegisteredCardTemplate::getTemplate)
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
	public void addExemptReduction(String exempt)
	{
		addExemptReduction(exempt, true);
	}
	
	public void addExemptReduction(String exempt, boolean carryToChildren)
	{
		exemptReductions.put(exempt, carryToChildren);
	}
	
	public void removeExemptReduction(String exempt)
	{
		exemptReductions.remove(exempt);
	}
	
	public List<String> getExemptReductions()
	{
		return new ArrayList<String>(exemptReductions.keySet());
	}
	
	public Map<String, Boolean> getExemptReductionsMap()
	{
		return exemptReductions;
	}
	
	public boolean isExemptReduction(String key)
	{
		return exemptReductions.containsKey(key);
	}
	
	public ServerMod getAssociatedMod()
	{
		return associatedMod;
	}
	
	protected void setAssociatedMod(ServerMod mod)
	{
		this.associatedMod = mod;
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
		getServer().getVoidCollection().addCard(card);
		getServer().broadcastPacket(card.getCardDataPacket());
		return card;
	}
	
	/**
	 * The Consumer is called after the default template is applied and before the Card is added to the void and is sent to clients.
	 * @param constructor The function to call before adding the card to the void and sending to clients
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
		getServer().getVoidCollection().addCard(card);
		getServer().broadcastPacket(card.getCardDataPacket());
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
	 * Create a card of this type. The returned Card will not yet have been added to a collection, not been
	 * sent to clients, and have no type and template. Should only be used if you know what you're doing.
	 * @return A newly created Card
	 */
	public T createCardRaw()
	{
		return factory.get();
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
	
	private MDServer getServer()
	{
		return MDServer.getInstance();
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
	
	public static class RegisteredCardTemplate
	{
		private CardTemplate template;
		private String name;
		private List<String> aliases;
		
		public RegisteredCardTemplate(CardTemplate template, String name, String... aliases)
		{
			this.template = template;
			this.name = name;
			this.aliases = new ArrayList<String>(Arrays.asList(aliases));
		}
		
		public CardTemplate getTemplate()
		{
			return template;
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
	
	public static <T extends Card> CardType<T> getByClass(Class<T> clazz)
	{
		return CardRegistry.getTypeByClass(clazz);
	}
	
	public static CardType<?> getByName(String name)
	{
		return CardRegistry.getTypeByName(name);
	}
	
	public static void register(ServerMod mod, CardType<?> type)
	{
		CardRegistry.registerCardType(mod, type);
	}
	
	/**
	 * Registers a CardType based on the class. The class MUST contain a static method named "createType" that returns
	 * a CardType. This method can have any access modifier.
	 * @param mod The mod that is registering this card type
	 * @param cardClass The class to register
	 * @return The recently registered type
	 */
	public static <T extends Card> CardType<T> register(ServerMod mod, Class<T> cardClass)
	{
		return CardRegistry.registerCardType(mod, cardClass);
	}
	
	public static <T extends Card> T createCard(Class<T> cardClass)
	{
		return getByClass(cardClass).createCard();
	}
	
	public static <T extends Card> T createCard(Class<T> cardClass, CardTemplate template)
	{
		return getByClass(cardClass).createCard(template);
	}
}
