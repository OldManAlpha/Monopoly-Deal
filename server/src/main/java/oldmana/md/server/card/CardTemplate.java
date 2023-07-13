package oldmana.md.server.card;

import oldmana.md.common.card.CardAnimationType;
import oldmana.md.common.util.ColorUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static oldmana.md.server.card.CardAttributes.*;

/**
 * Card templates store the information about a card, allowing for retrieval of information without a Card instance and
 * permitting easy instantiation of cards. There is a {@link CardType} associated with every CardTemplate, and CardTypes
 * contain at least one CardTemplate(the default template). Attributes can be modified to create cards that have
 * different from normal attributes, while retaining the base mechanics of the card.
 */
public class CardTemplate implements Cloneable
{
	private static final CardTemplate DEFAULT_TEMPLATE = new CardTemplate(true);
	static
	{
		DEFAULT_TEMPLATE.put(VALUE, 1);
		DEFAULT_TEMPLATE.put(NAME, "Unknown Card");
		DEFAULT_TEMPLATE.putStrings(DISPLAY_NAME, "NO DISPLAY", "NAME");
		DEFAULT_TEMPLATE.put(FONT_SIZE, 7);
		DEFAULT_TEMPLATE.put(DISPLAY_OFFSET_Y, 1);
		DEFAULT_TEMPLATE.putStrings(DESCRIPTION, "Missing Description");
		DEFAULT_TEMPLATE.put(OUTER_COLOR, Color.WHITE);
		DEFAULT_TEMPLATE.put(UNDOABLE, true);
		DEFAULT_TEMPLATE.put(CLEARS_UNDOABLE_ACTIONS, false);
		DEFAULT_TEMPLATE.put(PLAY_ANIMATION, CardAnimationType.NORMAL);
		DEFAULT_TEMPLATE.put(MOVE_COST, 1);
		DEFAULT_TEMPLATE.put(CONSUME_MOVES_STAGE, CardPlayStage.RIGHT_BEFORE_PLAY);
		DEFAULT_TEMPLATE.put(MOVE_STAGE, CardPlayStage.BEFORE_PLAY);
	}
	
	private static final Map<String, Integer> sortOrder = new HashMap<String, Integer>();
	static
	{
		sortOrder.put("type", -10);
		sortOrder.put("template", -9);
		sortOrder.put(VALUE, 0);
		sortOrder.put(NAME, 1);
		sortOrder.put(DISPLAY_NAME, 2);
		sortOrder.put(DESCRIPTION, 3);
		sortOrder.put(PLAY_ANIMATION, 4);
		sortOrder.put("amount", 100);
	}
	
	private CardType<?> associatedType;
	private JSONObject json;
	
	/**
	 * Create a CardTemplate with default values. This template does not have a CardType.
	 */
	public CardTemplate()
	{
		this(DEFAULT_TEMPLATE);
	}
	
	/**
	 * Create a CardTemplate that is empty if the parameter is true, otherwise has default values.
	 * @param clean Whether the CardTemplate should be empty to start
	 */
	private CardTemplate(boolean clean)
	{
		this(clean ? new JSONObject() : DEFAULT_TEMPLATE.getJson());
	}
	
	/**
	 * Create a complete duplicate of the given CardTemplate
	 * @param template The CardTemplate to duplicate
	 */
	public CardTemplate(CardTemplate template)
	{
		this(template.json);
		if (template.getAssociatedType() != null)
		{
			this.associatedType = template.getAssociatedType();
		}
	}
	
	public CardTemplate(JSONObject json)
	{
		this.json = new JSONObject(json.toMap());
		if (has("type"))
		{
			associatedType = CardRegistry.getTypeByName(json.getString("type"));
		}
	}
	
	/**
	 * Create a CardTemplate, providing a base, then applying a difference.
	 * @param template The base template
	 * @param diff The difference
	 */
	public CardTemplate(CardTemplate template, JSONObject diff)
	{
		this(template);
		for (String key : diff.keySet())
		{
			json.put(key, diff.get(key));
		}
	}
	
	public boolean has(String key)
	{
		return json.has(key);
	}
	
	public CardTemplate put(String key, Object obj)
	{
		json.put(key, obj);
		return this;
	}
	
	public CardTemplate put(String key, Enum<?> e)
	{
		json.put(key, e.toString());
		return this;
	}
	
	public CardTemplate put(String key, Color color)
	{
		json.put(key, "#" + ColorUtil.toRGBHex(color));
		return this;
	}
	
	/**
	 * Get value of key of type. Limited to default JSON types, plus String arrays, PropertyColors,
	 * PropertyColor arrays, and awt Colors.
	 */
	public <T> T get(String key, Class<T> type)
	{
		if (type == String[].class)
		{
			return (T) getStringArray(key);
		}
		else if (type == PropertyColor[].class)
		{
			return (T) getColorArray(key);
		}
		else if (type == PropertyColor.class)
		{
			return (T) getPropertyColor(key);
		}
		else if (type == Color.class)
		{
			return (T) getColor(key);
		}
		return (T) json.get(key);
	}
	
	public boolean getBoolean(String key)
	{
		return json.getBoolean(key);
	}
	
	public int getInt(String key)
	{
		return json.getInt(key);
	}
	
	public String getString(String key)
	{
		return json.getString(key);
	}
	
	public String[] getStringArray(String key)
	{
		List<String> list = getList(key);
		return list.toArray(new String[0]);
	}
	
	/**
	 * Types limited to default JSON types.
	 */
	public <T> List<T> getList(String key)
	{
		return (List<T>) json.getJSONArray(key).toList();
	}
	
	public CardTemplate putStrings(String key, String... strs)
	{
		json.put(key, Arrays.asList(strs));
		return this;
	}
	
	public Color getColor(String key)
	{
		String value = json.getString(key);
		try
		{
			char prefix = value.charAt(0);
			if (prefix == '#') // # will parse RGB hex
			{
				return ColorUtil.fromRGBHex(value.substring(1));
			}
			else if (prefix == '$') // $ will use internal value color
			{
				int v = Integer.parseInt(value.substring(1));
				return CardValueColor.getByValue(v).getColor();
			}
			else if (prefix == '~') // ~ will use property color from provided label
			{
				return PropertyColor.fromLabel(value.substring(1)).getColor();
			}
			return ColorUtil.fromRGBHex(value); // Defaults to hex parsing if there's no prefix
		}
		catch (Exception e)
		{
			System.err.println("Failed to parse color: " + value);
			e.printStackTrace();
		}
		return Color.LIGHT_GRAY;
	}
	
	public PropertyColor getPropertyColor(String key)
	{
		return PropertyColor.fromName(json.getString(key));
	}
	
	public PropertyColor[] getColorArray(String key)
	{
		List<String> list = getList(key);
		return list.stream().map(name -> PropertyColor.fromName(name)).toArray(PropertyColor[]::new);
	}
	
	public List<PropertyColor> getColorList(String key)
	{
		List<String> list = getList(key);
		return list.stream().map(name -> PropertyColor.fromName(name)).collect(Collectors.toCollection(ArrayList::new));
	}
	
	public CardTemplate putColor(String key, PropertyColor color)
	{
		json.put(key, color.getName());
		return this;
	}
	
	public CardTemplate putColors(String key, PropertyColor... colors)
	{
		json.put(key, Arrays.stream(colors).map(color -> color.getName()).collect(Collectors.toList()));
		return this;
	}
	
	public CardTemplate putColors(String key, List<PropertyColor> colors)
	{
		json.put(key, colors.stream().map(color -> color.getName()).collect(Collectors.toList()));
		return this;
	}
	
	public Object getObject(String key)
	{
		return json.get(key);
	}
	
	public CardType<?> getAssociatedType()
	{
		return associatedType;
	}
	
	public void setAssociatedType(CardType<?> associatedType)
	{
		this.associatedType = associatedType;
		json.put("type", associatedType.getInternalName());
	}
	
	/**
	 * Creates a Card from the associated type using this template.
	 * @return A newly created Card
	 */
	public <T extends Card> T createCard()
	{
		return (T) associatedType.createCard(this);
	}
	
	public JSONObject getJson()
	{
		return json;
	}
	
	/**
	 * Create a JSONObject where information that can be deduced by the associated type's default template is removed.
	 * @throws NullPointerException If there's no associated type
	 */
	public JSONObject getReducedJson()
	{
		JSONObject reduced = new JSONObject();
		boolean usingTemplate = has("template");
		JSONObject ref = usingTemplate ? getAssociatedType().getTemplateNoCopy(getString("template")).getJson() :
				getAssociatedType().getDefaultTemplateNoCopy().getJson();
		List<String> keys = new ArrayList<String>(json.keySet());
		keys.sort((s1, s2) ->
		{
			int v1 = sortOrder.getOrDefault(s1, Character.getNumericValue(s1.charAt(0)));
			int v2 = sortOrder.getOrDefault(s2, Character.getNumericValue(s2.charAt(0)));
			return Integer.compare(v1, v2);
		});
		for (String key : keys)
		{
			if (key.equals("type") ||
					key.equals("template") || // Type and template are always included
					(!usingTemplate && getAssociatedType().isExemptReduction(key)) || // Include exempt reductions, unless using a template
					!ref.has(key) || // Include if default template doesn't have the key
					(!ref.get(key).equals(json.get(key)) && !(ref.get(key) instanceof JSONArray &&
					ref.getJSONArray(key).similar(json.get(key))))) // Finally, include if values from templates don't match
			{
				reduced.put(key, json.get(key));
			}
		}
		return reduced;
	}
	
	@Override
	public CardTemplate clone()
	{
		return new CardTemplate(this);
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof CardTemplate))
		{
			return false;
		}
		if (other == this)
		{
			return true;
		}
		return associatedType == ((CardTemplate) other).getAssociatedType() && json.similar(((CardTemplate) other).getJson());
	}
	
	@Override
	public int hashCode()
	{
		return json.hashCode();
	}
}
