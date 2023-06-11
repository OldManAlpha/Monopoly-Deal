package oldmana.md.server.card.collection.deck;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oldmana.md.server.mod.ModNotFoundException;
import oldmana.md.server.mod.ServerMod;
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
		this(name, f.toPath());
	}
	
	public CustomDeck(String name, Path path)
	{
		this.name = name;
		try (InputStream is = Files.newInputStream(path))
		{
			readDeck(is);
		}
		catch (ModNotFoundException | DeckLoadFailureException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DeckLoadFailureException("Failed to load deck", e);
		}
	}
	
	public CustomDeck(String name, InputStream is)
	{
		this.name = name;
		readDeck(is);
	}
	
	public void readDeck(InputStream is)
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
		
		if (object.has("requiredMods"))
		{
			List<String> requiredMods = object.getJSONArray("requiredMods").toStringList();
			for (String mod : requiredMods)
			{
				if (!getServer().isModLoaded(mod))
				{
					throw new ModNotFoundException(mod);
				}
			}
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
			System.out.println("Warning: Deck " + name + " has no rules");
			return;
		}
		JSONObject rules = object.getJSONObject("rules");
		setDeckRules(new GameRule(getServer().getGameRules().getRootRuleStruct(), rules));
	}
	
	public void writeDeck() throws IOException
	{
		writeDeck(null);
	}
	
	public void writeDeck(File f) throws IOException
	{
		Set<String> requiredMods = new HashSet<String>();
		for (Card card : getCards())
		{
			ServerMod mod = card.getType().getAssociatedMod();
			if (mod != null)
			{
				requiredMods.add(mod.getName());
			}
		}
		JSONArray array = DeckSerializer.serialize(this);
		JSONObject obj = new JSONObject();
		obj.put("version", VERSION);
		if (!requiredMods.isEmpty())
		{
			obj.put("requiredMods", requiredMods);
		}
		obj.put("rules", getDeckRules().toJSON());
		obj.put("cards", array);
		FileWriter w = new FileWriter(f == null ? new File("decks" + File.separator + name + ".json") : f);
		obj.write(w, 2, 0);
		w.close();
	}
	
	@Override
	public void createDeck()
	{
	
	}
	
	public static class DeckLoadFailureException extends RuntimeException
	{
		public DeckLoadFailureException(String msg, Throwable cause)
		{
			super(msg, cause);
		}
	}
}
