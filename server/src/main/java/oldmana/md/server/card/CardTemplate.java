package oldmana.md.server.card;

import oldmana.md.server.card.type.CardType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CardTemplate
{
	private static CardTemplate DEFAULT_TEMPLATE = new CardTemplate(true);
	static
	{
		DEFAULT_TEMPLATE.put("value", 1);
		DEFAULT_TEMPLATE.put("name", "Unknown Card");
		DEFAULT_TEMPLATE.putStrings("displayName", "NO DISPLAY", "NAME");
		DEFAULT_TEMPLATE.put("fontSize", 7);
		DEFAULT_TEMPLATE.put("displayOffsetY", 1);
		DEFAULT_TEMPLATE.putStrings("description", "Missing Description");
		DEFAULT_TEMPLATE.put("revocable", true);
		DEFAULT_TEMPLATE.put("clearRevocableCards", false);
	}
	
	private JSONObject json;
	private CardType<?> associatedType;
	
	/**
	 * Create a CardTemplate with default values. This template does not have a CardType.
	 */
	public CardTemplate()
	{
		this(DEFAULT_TEMPLATE);
	}
	
	private CardTemplate(boolean clean)
	{
		json = new JSONObject();
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
	
	/**
	 * Create a CardTemplate, providing a base, then applying a difference
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
	
	public void put(String key, Object obj)
	{
		json.put(key, obj);
	}
	
	/**
	 * Get value of key of type. Limited to default JSON types, plus String arrays, PropertyColors, and PropertyColor arrays.
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
	
	public void putStrings(String key, String... strs)
	{
		json.put(key, Arrays.asList(strs));
	}
	
	public PropertyColor getColor(String key, PropertyColor color)
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
	
	public void putColor(String key, PropertyColor color)
	{
		json.put(key, color.getName());
	}
	
	public void putColors(String key, PropertyColor... colors)
	{
		json.put(key, Arrays.stream(colors).map(color -> color.getName()).collect(Collectors.toList()));
	}
	
	public void putColors(String key, List<PropertyColor> colors)
	{
		json.put(key, colors.stream().map(color -> color.getName()).collect(Collectors.toList()));
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
	
	public <T extends Card> T createCard()
	{
		return (T) associatedType.createCard(this);
	}
	
	public JSONObject getJson()
	{
		return json;
	}
	
	/**
	 * Create a JSONObject where information that can be deduced by the associated type's default template is removed
	 * @throws NullPointerException If there's no associated type
	 */
	public JSONObject getReducedJson()
	{
		JSONObject reduced = new JSONObject();
		for (String key : json.keySet())
		{
			JSONObject ref = getAssociatedType().getDefaultTemplate().getJson();
			if (key.equals("type") ||
					getAssociatedType().isExemptReduction(key) ||
					(!ref.get(key).equals(json.get(key)) && !(ref.get(key) instanceof JSONArray &&
					ref.getJSONArray(key).similar(json.get(key)))))
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
		return json.similar(((CardTemplate) other).getJson());
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(json.toMap().hashCode());
	}
}
