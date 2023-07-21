package oldmana.md.server.command;

import oldmana.md.common.Message;
import oldmana.md.common.playerui.ChatAlignment;
import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.MDServer;
import oldmana.md.server.MessageBuilder;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.CardRegistry;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType.RegisteredCardTemplate;
import oldmana.md.server.card.PropertyColor;
import oldmana.md.server.card.action.CardActionRent;
import oldmana.md.server.card.CardType;

import java.util.List;

public class CommandCreateCard extends Command
{
	public static final String CATEGORY_LIST = "cardlist";
	
	public CommandCreateCard()
	{
		super("createcard", true);
		setUsage("/createcard",
				"/createcard action [Name]",
				"/createcard rent [Value] [Colors...]",
				"/createcard property [Value] [Base?] [Stealable?] [Property_Name] [Colors...]",
				"/createcard type [Internal Name] <Template Name>");
		setDescription("A tool to create new cards.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length == 0)
		{
			sender.clearMessages(CATEGORY_LIST);
			sender.sendMessage(ChatColor.LIGHT_RED + "Registered Card List", ChatAlignment.CENTER, CATEGORY_LIST);
			List<CardType<?>> cards = CardRegistry.getRegisteredCards();
			for (CardType<?> type : cards)
			{
				if (type.isInstantiable() && (type.isVisible() || !type.getTemplates().isEmpty()))
				{
					sender.sendMessage(getMessage(type));
					for (RegisteredCardTemplate registeredTemplate : type.getTemplates())
					{
						sender.sendMessage(getMessage(registeredTemplate));
					}
				}
			}
			sender.sendMessage(new MessageBuilder(ChatAlignment.CENTER).setCategory(CATEGORY_LIST)
					.addCommand(ChatColor.LINK + "[Go To Deck Editor]", "editdeck").build());
			if (sender instanceof Player)
			{
				((Player) sender).setChatOpen(true);
			}
			return;
		}
		
		Card card = null;
		if (args[0].equalsIgnoreCase("type"))
		{
			card = createFromType(sender, args);
			sender.sendMessage("Created card " + ChatColor.UTILITY + card.getName() + ChatColor.WHITE +
					" with ID "+ card.getID(), ChatAlignment.CENTER, true);
		}
		else if (args[0].equalsIgnoreCase("typeToDeck")) // Special subcommand used for when clicking from the catalogue
		{
			card = createFromType(sender, args);
			System.out.println("Created card " + card.getName() + " with ID " + card.getID());
			card.transfer(getServer().getDeck(), -1, 0.4);
			return;
		}
		if (args[0].equalsIgnoreCase("money"))
		{
			if (verifyInt(args[1]))
			{
				int value = Integer.parseInt(args[1]);
				CardTemplate template = CardType.MONEY.getDefaultTemplate();
				template.put("value", value);
				card = CardType.MONEY.createCard(template);
				sender.sendMessage("Created money card with value " + card.getValue() + "M with ID " + card.getID(),
						ChatAlignment.CENTER, true);
			}
		}
		else if (args[0].equalsIgnoreCase("action"))
		{
			try
			{
				List<CardType<?>> types = CardRegistry.getRegisteredCards();
				for (CardType<?> type : types)
				{
					if (type.getPrimitive() != null)
					{
						Class<?> primitive = type.getPrimitive().getCardClass();
						if ((primitive == CardAction.class) && type.isReferringToThis(args[1]))
						{
							card = type.createCard();
						}
					}
				}
				if (card == null)
				{
					sender.sendMessage("Could not find card '" + args[1] + "'");
					return;
				}
				sender.sendMessage("Created action card " + ChatColor.UTILITY + card.getName() + ChatColor.WHITE +
						" with ID "+ card.getID(), ChatAlignment.CENTER, true);
			}
			catch (Exception e)
			{
				sender.sendMessage(ChatColor.PREFIX_ALERT + "Error creating card: " + e.getMessage());
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
			card = CardActionRent.create(value, PropertyColor.fromIDs(colors).toArray(new PropertyColor[0]));
			sender.sendMessage("Created rent card with ID " + card.getID(), ChatAlignment.CENTER, true);
		}
		else if (args[0].equalsIgnoreCase("property"))
		{
			//                            Value  Stealable         Color IDs
			//                             \/       \/                \/
			// Example: createcard property 4 true true Property_Name 0 1
			//                                 /\
			//                                Base
			int value = parseInt(args[1]);
			boolean base = parseBoolean(args[2]);
			boolean stealable = parseBoolean(args[3]);
			String name = args[3].replace('_', ' ');
			byte[] colors = new byte[args.length - 5];
			for (int i = 5 ; i < args.length ; i++)
			{
				colors[i - 5] = Byte.parseByte(args[i]);
			}
			card = CardProperty.create(value, name, base, stealable, PropertyColor.fromIDs(colors).toArray(new PropertyColor[0]));
			sender.sendMessage("Created property card with ID " + card.getID(), ChatAlignment.CENTER, true);
		}
		if (card != null && sender instanceof Player)
		{
			Player player = (Player) sender;
			sendTransferLinks(player, card);
			player.setChatOpen(true);
		}
	}
	
	private Card createFromType(CommandSender sender, String[] args)
	{
		CardType<?> type = CardType.getByName(args[1]);
		CardTemplate template = args.length > 2 ? type.getTemplateNoCopy(getFullStringArgument(args, 2)) :
				type.getDefaultTemplateNoCopy();
		return template.createCard();
	}
	
	public Message getMessage(CardType<?> type)
	{
		MessageBuilder mb = new MessageBuilder().setCategory(CATEGORY_LIST);
		mb.add("- " + ChatColor.LIGHT_YELLOW + type.getFriendlyName());
		List<String> aliases = type.getAliases();
		if (aliases.size() > 0)
		{
			mb.add(ChatColor.FAINTLY_GRAY + " (" + aliases.get(0));
			if (aliases.size() > 1)
			{
				for (int i = 1 ; i < aliases.size() ; i++)
				{
					mb.add(", " + aliases.get(i));
				}
			}
			mb.add(")");
		}
		if (type.isVisible())
		{
			mb.add(" ");
			mb.addCommand(ChatColor.LINK + "[Create]", "createcard type " + type.getInternalName());
			mb.add(" ");
			mb.addCommand(ChatColor.LIGHT_GREEN + "[Add To Deck]", "createcard typeToDeck " + type.getInternalName());
		}
		return mb.build();
	}
	
	public Message getMessage(RegisteredCardTemplate rct)
	{
		CardTemplate template = rct.getTemplate();
		MessageBuilder mb = new MessageBuilder().setCategory(CATEGORY_LIST);
		mb.add("  - " + ChatColor.LIGHT_ORANGE + rct.getName());
		List<String> aliases = rct.getAliases();
		if (aliases.size() > 0)
		{
			mb.add(ChatColor.FAINTLY_GRAY + " (" + aliases.get(0));
			if (aliases.size() > 1)
			{
				for (int i = 1 ; i < aliases.size() ; i++)
				{
					mb.add(", " + aliases.get(i));
				}
			}
			mb.add(")");
		}
		mb.add(" ");
		mb.addCommand(ChatColor.LINK + "[Create]", "createcard type " +
				template.getAssociatedType().getInternalName() + " " + rct.getName());
		mb.add(" ");
		mb.addCommand(ChatColor.LIGHT_GREEN + "[Add To Deck]", "createcard typeToDeck " +
				template.getAssociatedType().getInternalName() + " " + rct.getName());
		return mb.build();
	}
	
	public static void sendTransferLinks(Player player, Card card)
	{
		MessageBuilder mb = new MessageBuilder(ChatAlignment.CENTER);
		mb.addCommand(ChatColor.LINK + "[Transfer To Hand]",
				"transfercard " + card.getID() + " " + player.getHand().getID());
		mb.add("        ");
		mb.addCommand("[Transfer To Deck]",
				"transfercard " + card.getID() + " " + MDServer.getInstance().getDeck().getID());
		player.sendMessage(mb.build());
	}
}
