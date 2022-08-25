package oldmana.md.server.card.collection.deck;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import oldmana.md.server.card.CardRegistry;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.type.CardType;
import org.json.JSONArray;
import org.json.JSONObject;

import oldmana.md.server.card.Card;

public class DeckSerializer
{
	public static JSONArray serialize(DeckStack deck)
	{
		Map<CardTemplate, Integer> cardAmounts = new HashMap<CardTemplate, Integer>();
		for (Card card : deck.getCards())
		{
			CardTemplate template = card.getTemplate();
			if (cardAmounts.putIfAbsent(template, 1) != null)
			{
				cardAmounts.compute(template, (k, v) -> v + 1);
			}
		}
		JSONArray arr = new JSONArray();
		for (Entry<CardTemplate, Integer> entry : cardAmounts.entrySet())
		{
			JSONObject obj = entry.getKey().getReducedJson();
			obj.put("amount", entry.getValue());
			arr.put(obj);
		}
		return arr;
	}
	
	public static Map<CardTemplate, Integer> deserialize(JSONArray array)
	{
		Map<CardTemplate, Integer> templates = new HashMap<CardTemplate, Integer>();
		for (Object o : array)
		{
			JSONObject obj = (JSONObject) o;
			int amount = obj.getInt("amount");
			obj.remove("amount");
			CardType<?> type = CardRegistry.getTypeByName(obj.getString("type"));
			CardTemplate template = new CardTemplate(type.getDefaultTemplate(), obj);
			if (templates.putIfAbsent(template, amount) != null)
			{
				templates.compute(template, (k, v) -> v + 1);
			}
		}
		return templates;
	}
}
