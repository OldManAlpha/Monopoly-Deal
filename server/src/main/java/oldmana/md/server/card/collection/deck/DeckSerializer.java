package oldmana.md.server.card.collection.deck;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardMoney;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.action.CardActionRent;

public class DeckSerializer
{
	private Map<Class<? extends Card>, CardSerializer> serializers = new HashMap<Class<? extends Card>, CardSerializer>();
	
	@SuppressWarnings("unchecked")
	public void serialize(DeckStack deck)
	{
		JSONArray arr = new JSONArray();
		for (Card card : deck.getCards())
		{
			Class<? extends Card> clazz = card.getClass();
			while (!serializers.containsKey(clazz))
			{
				clazz = (Class<? extends Card>) clazz.getSuperclass();
			}
			arr.put(serializers.get(clazz).toJSON(card));
		}
	}
	
	public void registerDefaultSerializers()
	{
		registerCardSerializer(CardMoney.class, new CardSerializer("Money")
		{
			@Override
			public JSONObject toJSON(Card card)
			{
				JSONObject obj = new JSONObject();
				obj.put("Type", "Money");
				if (card.getClass() != CardMoney.class)
				{
					obj.put("Class", getClass().getName());
				}
				obj.put("Value", card.getValue());
				if (!card.getName().equals(card.getValue() + "M"))
				{
					obj.put("Name", card.getName());
				}
				return obj;
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public CardMoney fromJSON(JSONObject obj)
			{
				try
				{
					Class<? extends Card> clazz = CardMoney.class;
					if (obj.has("Class"))
					{
						clazz = (Class<? extends Card>) Class.forName(obj.getString("Class"));
					}
					CardMoney card = (CardMoney) clazz.getDeclaredConstructor(int.class).newInstance(obj.getInt("Value"));
					if (obj.has("Name"))
					{
						card.setName(obj.getString("Name"));
					}
					return card;
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				return null;
			}
		});
		
		registerCardSerializer(CardActionRent.class, new CardSerializer("Rent")
		{
			@Override
			public JSONObject toJSON(Card card)
			{
				JSONObject obj = new JSONObject();
				obj.put("Type", "Rent");
				if (card.getClass() != CardProperty.class)
				{
					obj.put("Class", getClass().getName());
				}
				obj.put("Value", card.getValue());
				obj.put("Name", card.getName());
				/*
				String[] types = new String[card.colors.length];
				for (int i = 0 ; i < types.length ; i++)
				{
					types[i] = colors[i].getName();
				}
				*/
				//JSONArray colors = new JSONArray(types);
				//obj.put("Colors", colors);
				return obj;
			}
			
			@Override
			public Card fromJSON(JSONObject obj)
			{
				return null;
			}
		});
		
		registerCardSerializer(CardProperty.class, new CardSerializer("Property")
		{
			@Override
			public JSONObject toJSON(Card card)
			{
				JSONObject obj = new JSONObject();
				/*
				obj.put("Type", "Property");
				if (getClass() != CardProperty.class)
				{
					obj.put("Class", getClass().getName());
				}
				obj.put("Value", getValue());
				obj.put("Name", getName());
				String[] types = new String[colors.size()];
				for (int i = 0 ; i < types.length ; i++)
				{
					types[i] = colors.get(i).getName();
				}
				JSONArray colors = new JSONArray(types);
				obj.put("Colors", colors);
				obj.put("Base", isBase());
				*/
				return obj;
			}
			
			@Override
			public Card fromJSON(JSONObject obj)
			{
				return null;
			}
		});
	}
	
	public void registerCardSerializer(Class<? extends Card> clazz, CardSerializer serializer)
	{
		serializers.put(clazz, serializer);
	}
	
	public static abstract class CardSerializer
	{
		private String type;
		
		public CardSerializer(String type)
		{
			this.type = type;
		}
		
		public abstract JSONObject toJSON(Card card);
		
		public abstract Card fromJSON(JSONObject obj);
	}
}
