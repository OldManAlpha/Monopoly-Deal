package oldmana.md.server.card.collection.deck;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import oldmana.md.server.rules.GameRule;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import oldmana.md.server.card.Card;

public class CustomDeck extends DeckStack
{
	private static int MIN_VERSION = 1;
	private static int VERSION = 2;
	
	private String name;
	private File file;
	
	public CustomDeck(String name, DeckStack stack)
	{
		this(name, stack.getCards());
	}
	
	public CustomDeck(String name, List<Card> cards)
	{
		this.name = name;
		for (Card card : cards)
		{
			addCard(card.getTemplate().createCard());
		}
	}
	
	public CustomDeck(String name, List<Card> cards, GameRule rules)
	{
		this(name, cards);
		setDeckRules(rules);
	}
	
	public CustomDeck(String name)
	{
		this.name = name;
	}
	
	public CustomDeck(String name, File f)
	{
		this.name = name;
		file = f;
		createDeck();
	}
	
	public void readDeck(File f)
	{
		try (FileInputStream is = new FileInputStream(f))
		{
			JSONObject object = new JSONObject(new JSONTokener(is));
			int version = object.getInt("version");
			if (version < MIN_VERSION)
			{
				throw new DeckLoadFailureException("Deck format version(" + version + ") is not supported. Minimum required is " +
						MIN_VERSION, null);
			}
			if (version > VERSION)
			{
				System.out.println("Warning: Loading deck from a newer version of Monopoly Deal.");
			}
			
			JSONArray array = object.getJSONArray("cards");
			DeckSerializer.deserialize(array).forEach((template, amount) ->
			{
				for (int i = 0 ; i < amount ; i++)
				{
					addCard(template.createCard());
				}
			});
			
			if (!object.has("rules"))
			{
				System.out.println("Warning: Deck has no rules");
				return;
			}
			JSONObject rules = object.getJSONObject("rules");
			setDeckRules(new GameRule(getServer().getGameRules().getRootRuleStruct(), rules));
		}
		catch (Exception e)
		{
			throw new DeckLoadFailureException("Failed to load deck", e);
		}
	}
	
	public void writeDeck() throws IOException
	{
		writeDeck(null);
	}
	
	public void writeDeck(File f) throws IOException
	{
		JSONArray array = DeckSerializer.serialize(this);
		JSONObject obj = new JSONObject();
		obj.put("version", VERSION);
		obj.put("rules", getDeckRules().toJSON());
		obj.put("cards", array);
		FileWriter w = new FileWriter(f == null ? new File("decks" + File.separator + name + ".json") : f);
		obj.write(w, 2, 0);
		w.close();
	}
	
	@Override
	public void createDeck()
	{
		if (file != null)
		{
			readDeck(file);
		}
	}
	
	public static class DeckLoadFailureException extends RuntimeException
	{
		public DeckLoadFailureException(String msg, Throwable cause)
		{
			super(msg, cause);
		}
	}
}
