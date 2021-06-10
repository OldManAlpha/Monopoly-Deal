package oldmana.md.server.command;

import oldmana.md.server.ChatColor;
import oldmana.md.server.ChatLinkHandler.ChatLink;
import oldmana.md.server.CommandSender;
import oldmana.md.server.MDScheduler.MDTask;
import oldmana.md.server.MessageBuilder;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardMoney;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.CardProperty.PropertyColor;
import oldmana.md.server.card.CardRegistry.RegisteredCard;
import oldmana.md.server.card.action.CardActionRent;

public class CommandCreateCard extends Command
{
	public CommandCreateCard()
	{
		super("createcard", null, new String[] {"/createcard money [value]", "/createcard action [name]", "/createcard rent [value] [colors...]", 
				"/createcard property [value] [base] [property_name] [colors...]"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		Card card = null;
		if (args[0].equalsIgnoreCase("money"))
		{
			card = new CardMoney(Integer.parseInt(args[1]));
			sender.sendMessage("Created money card with value " + card.getValue() + "M with ID " + card.getID(), true);
		}
		else if (args[0].equalsIgnoreCase("action"))
		{
			try
			{
				RegisteredCard rc = getServer().getCardRegistry().getRegisteredActionCardByName(args[1]);
				if (rc == null)
				{
					sender.sendMessage("Could not find card '" + args[1] + "'");
					return;
				}
				card = rc.createCard();
				sender.sendMessage("Created action card " + card.getName() + " with ID " + card.getID(), true);
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
			sender.sendMessage("Created rent card with ID " + card.getID(), true);
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
			sender.sendMessage("Created property card with ID " + card.getID(), true);
		}
		if (card != null)
		{
			getServer().broadcastPacket(card.getCardDataPacket());
			getServer().getVoidCollection().addCard(card);
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				MessageBuilder mb = new MessageBuilder(ChatColor.LINK.toString());
				ChatLink link = mb.addLinkedString("[Transfer To Hand]");
				mb.addString(ChatColor.WHITE + " (Link expires in 1 minute)");
				Card c = card;
				link.setListener(() -> 
				{
					if (c.getOwningCollection() != player.getHand())
					{
						c.transfer(player.getHand());
					}
				});
				player.sendMessage(mb.getMessage());
				getServer().getScheduler().scheduleTask(new MDTask(60 * 20, false)
				{
					@Override
					public void run()
					{
						getServer().getChatLinkHandler().deleteChatLink(link);
					}
				});
			}
		}
	}
}
