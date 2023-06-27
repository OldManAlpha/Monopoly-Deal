package oldmana.md.server.card.collection.deck;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import oldmana.md.server.card.CardRegistry;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;
import org.json.JSONArray;
import org.json.JSONObject;

import oldmana.md.server.card.Card;

public class DeckSerializer
{
	private static final Map<String, Integer> cardSortOrder = new HashMap<String, Integer>();
	static
	{
		cardSortOrder.put(CardType.MONEY.getInternalName(), -1);
		cardSortOrder.put(CardType.PROPERTY.getInternalName(), 100);
	}
	
	public static JSONArray serialize(DeckStack deck)
	{
		Map<CardTemplate, Integer> templates = toTemplateMap(deck.getCards());
		JSONArray arr = new JSONArray();
		for (Entry<CardTemplate, Integer> entry : templates.entrySet())
		{
			JSONObject obj = entry.getKey().getReducedJson();
			if (entry.getValue() > 1)
			{
				obj.put("amount", entry.getValue());
			}
			arr.put(obj);
		}
		arr.sort((o1, o2) ->
		{
			String type1 = ((JSONObject) o1).getString("type");
			String type2 = ((JSONObject) o2).getString("type");
			int v1 = cardSortOrder.getOrDefault(type1, Character.getNumericValue(type1.charAt(0)));
			int v2 = cardSortOrder.getOrDefault(type2, Character.getNumericValue(type2.charAt(0)));
			return Integer.compare(v1, v2);
		});
		return arr;
	}
	
	public static Map<CardTemplate, Integer> deserialize(JSONArray array)
	{
		Map<CardTemplate, Integer> templates = new HashMap<CardTemplate, Integer>();
		for (Object o : array)
		{
			JSONObject obj = (JSONObject) o;
			int amount = obj.has("amount") ? obj.getInt("amount") : 1;
			obj.remove("amount");
			CardType<?> type = CardRegistry.getTypeByName(obj.getString("type"));
			CardTemplate baseTemplate = type.getDefaultTemplate();
			if (obj.has("template"))
			{
				baseTemplate = type.getTemplate(obj.getString("template"));
			}
			CardTemplate template = new CardTemplate(baseTemplate, obj);
			templates.merge(template, amount, Integer::sum);
		}
		return templates;
	}
	
	public static Map<CardTemplate, Integer> toTemplateMap(Collection<Card> cards)
	{
		Map<CardTemplate, Integer> templates = new HashMap<CardTemplate, Integer>();
		for (Card card : cards)
		{
			templates.merge(card.getTemplate(), 1, Integer::sum);
		}
		return templates;
	}
}
