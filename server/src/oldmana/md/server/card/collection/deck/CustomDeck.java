package oldmana.md.server.card.collection.deck;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import oldmana.md.server.card.Card;

public class CustomDeck extends DeckStack
{
	private String name;
	private File file;
	
	public CustomDeck(String name)
	{
		this.name = name;
	}
	
	public CustomDeck(File f)
	{
		file = f;
	}
	
	public void readDeck(File f)
	{
		try
		{
			JSONArray arr = new JSONArray(new JSONTokener(new FileInputStream(f)));
			for (Object o1 : arr.toList())
			{
				if (o1 instanceof JSONObject)
				{
					JSONObject obj = (JSONObject) o1;
					String className = obj.getString("Class");
					addCard((Card) Class.forName(className).newInstance());
					
					/*
					for (Object o2 : ((JSONArray) o1).toList())
					{
						if (o2 instanceof JSONObject)
						{
							JSONObject obj = (JSONObject) o2;
							if (obj.g)
						}
					}*/
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeDeck(File f)
	{
		JSONArray array = new JSONArray();
		for (Card card : getCards())
		{
			//JSONObject obj = new JSONObject(card.toJSONString());
			//array.put(obj);
		}
		try
		{
			System.out.println(array.toString());
			FileWriter w = new FileWriter(file == null ? new File("decks" + File.separator + name + ".json") : file);
			array.write(w, 2, 0);
			w.close();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void createDeck()
	{
		if (file != null)
		{
			readDeck(file);
		}
	}
}
