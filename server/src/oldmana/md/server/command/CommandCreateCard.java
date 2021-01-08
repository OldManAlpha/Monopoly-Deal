package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardMoney;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.CardProperty.PropertyColor;
import oldmana.md.server.card.action.CardActionRent;

public class CommandCreateCard extends Command
{
	public CommandCreateCard()
	{
		super("createcard", null, new String[] {"/createcard money [value]", "/createcard action [short class name]", 
				"/createcard property [value] [base] [property_name] [colors...]"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		Card card = null;
		if (args[0].equalsIgnoreCase("money"))
		{
			card = new CardMoney(Integer.parseInt(args[1]));
			sender.sendMessage("Created money card with value " + card.getValue() + " with ID " + card.getID());
		}
		else if (args[0].equalsIgnoreCase("action"))
		{
			try
			{
				card = getServer().getActionCardClass(args[1]).newInstance();
				sender.sendMessage("Created action card " + card.getName() + " with ID " + card.getID());
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
		}
		else if (args[0].equalsIgnoreCase("rent"))
		{
			//                        Value
			//                         \/
			// Example: createcard rent 4 0 1
			//                             /\
			//                          Color IDs
			int value = Integer.parseInt(args[1]);
			byte[] colors = new byte[args.length - 2];
			for (int i = 2 ; i < args.length ; i++)
			{
				colors[i - 2] = Byte.parseByte(args[i]);
			}
			card = new CardActionRent(value, PropertyColor.fromIDs(colors).toArray(new PropertyColor[colors.length]));
			sender.sendMessage("Created rent card with ID " + card.getID());
		}
		else if (args[0].equalsIgnoreCase("property"))
		{
			//                            Value               Color IDs
			//                             \/                    \/
			// Example: createcard property 4 true Property_Name 0 1
			//                                 /\
			//                                Base
			int value = Integer.parseInt(args[1]);
			boolean base = Boolean.parseBoolean(args[2]);
			String name = args[3].replace('_', ' ');
			byte[] colors = new byte[args.length - 4];
			for (int i = 4 ; i < args.length ; i++)
			{
				colors[i - 4] = Byte.parseByte(args[i]);
			}
			card = new CardProperty(PropertyColor.fromIDs(colors), value, name, base);
			sender.sendMessage("Created property card with ID " + card.getID());
		}
		if (card != null)
		{
			getServer().broadcastPacket(card.getCardDataPacket());
			getServer().getVoidCollection().addCard(card);
		}
	}
}
