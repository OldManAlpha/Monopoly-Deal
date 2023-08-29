package oldmana.md.server.command;

import oldmana.md.common.playerui.ChatAlignment;
import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.MessageBuilder;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.PropertyColor;
import oldmana.md.server.rules.ValueType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import static oldmana.md.server.card.CardAttributes.*;

/**
 * A temporary solution to building properties while a better method is developed.
 */
public class CommandBuildProperty extends Command
{
	public static final String CATEGORY = "propbuilder";
	
	private Map<Player, CardTemplate> builds = new WeakHashMap<Player, CardTemplate>();
	
	public CommandBuildProperty()
	{
		super("buildproperty", true);
		setAliases("buildprop", "propbuilder", "propertybuilder");
		setDescription("A tool that allows you to build a custom property card.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage("Only players can execute this command.");
			return;
		}
		Player player = (Player) sender;
		
		if (!builds.containsKey(player))
		{
			builds.put(player, CardProperty.createTemplate(1, "Custom Property", PropertyColor.BROWN));
		}
		CardTemplate template = builds.get(player);
		
		player.clearMessages(CATEGORY);
		
		player.sendMessage("", CATEGORY);
		if (args.length == 0)
		{
			sender.sendMessage(new MessageBuilder(ChatAlignment.CENTER).setCategory(CATEGORY).startUnderline()
					.add(ChatColor.LIGHT_RED + "Property Builder").build());
			
			// Name
			MessageBuilder mb = new MessageBuilder(ChatAlignment.CENTER).setCategory(CATEGORY);
			mb.add(formatEntry("Name") + template.getString(NAME) + " ");
			mb.addFillCommand(ChatColor.LINK + "[Edit]", "buildproperty name ");
			player.sendMessage(mb.build());
			
			// Value
			mb = new MessageBuilder(ChatAlignment.CENTER).setCategory(CATEGORY);
			mb.add(formatEntry("Value") + template.getInt(VALUE) + " ");
			mb.addFillCommand(ChatColor.LINK + "[Edit]", "buildproperty value ");
			player.sendMessage(mb.build());
			
			// Colors
			mb = new MessageBuilder(ChatAlignment.CENTER).setCategory(CATEGORY);
			mb.add(formatEntry("Colors") + Arrays.stream(template.getColorArray(CardProperty.COLORS))
					.map(c -> ChatColor.toChatColor(c.getColor()) + c.getName())
					.collect(Collectors.joining(ChatColor.FAINTLY_GRAY + "  |  ")) + " ");
			mb.addCommand(ChatColor.LINK + "[Edit]", "buildproperty colors");
			player.sendMessage(mb.build());
			
			// Description
			mb = new MessageBuilder(ChatAlignment.CENTER).setCategory(CATEGORY);
			mb.add(formatEntry("Description") + String.join(" | ", template.getStringArray(DESCRIPTION)) + " ");
			mb.addFillCommand(ChatColor.LINK + "[Edit]", "buildproperty description ");
			player.sendMessage(mb.build());
			
			// Base
			boolean base = template.getBoolean(CardProperty.BASE);
			mb = new MessageBuilder(ChatAlignment.CENTER).setCategory(CATEGORY);
			mb.startHoverText("If a property is a base, it can be used as a foundation of a color. The only card in " +
					"vanilla Monopoly Deal that is not a base is the 10-Color Property Wild Card.");
			mb.add(formatEntry("Base") + ValueType.BOOLEAN.toDisplay(base) + " ");
			mb.endHoverText();
			mb.addCommand(ChatColor.LINK + "[Toggle]", "buildproperty base " + !base);
			player.sendMessage(mb.build());
			
			// Stealable
			boolean stealable = template.getBoolean(CardProperty.STEALABLE);
			mb = new MessageBuilder(ChatAlignment.CENTER).setCategory(CATEGORY);
			mb.startHoverText("If a property is stealable, it can be targeted by Sly Deals and Forced Deals.");
			mb.add(formatEntry("Stealable") + ValueType.BOOLEAN.toDisplay(stealable) + " ");
			mb.endHoverText();
			mb.addCommand(ChatColor.LINK + "[Toggle]", "buildproperty stealable " + !stealable);
			player.sendMessage(mb.build());
			
			// Options
			mb = new MessageBuilder(ChatAlignment.CENTER).setCategory(CATEGORY);
			mb.addCommand(ChatColor.LINK + "[Create]", "buildproperty create");
			mb.add("         ");
			mb.addCommand(ChatColor.LIGHT_GREEN + "[Add To Deck]", "buildproperty createToDeck");
			mb.add("         ");
			mb.addCommand(ChatColor.LINK + "[Go to Deck Editor]", "editdeck");
			player.sendMessage(mb.build());
			
			player.setChatOpen(true);
			return;
		}
		
		if (args[0].equalsIgnoreCase("name"))
		{
			if (args.length == 1)
			{
				player.sendMessage(ChatColor.LIGHT_RED + "You must provide a name!");
				return;
			}
			String name = getFullStringArgument(args, 1);
			template.put(NAME, name);
			executeCommand(player, new String[0]);
		}
		else if (args[0].equalsIgnoreCase("value"))
		{
			if (args.length == 1)
			{
				player.sendMessage(ChatColor.LIGHT_RED + "You must provide a value!");
				return;
			}
			if (!verifyInt(args[1]))
			{
				player.sendMessage(ChatColor.LIGHT_RED + "The value must be an integer!");
				return;
			}
			template.put(VALUE, parseInt(args[1]));
			executeCommand(player, new String[0]);
		}
		else if (args[0].equalsIgnoreCase("colors"))
		{
			player.sendMessage("", CATEGORY);
			List<PropertyColor> colors = template.getColorList(CardProperty.COLORS);
			player.sendMessage(ChatColor.LIGHT_RED + "Choose Property Colors (" + colors.size() + " selected)",
					ChatAlignment.CENTER, CATEGORY);
			MessageBuilder mb = new MessageBuilder(ChatAlignment.CENTER).setCategory(CATEGORY);
			for (PropertyColor color : PropertyColor.getAllColors())
			{
				boolean hasColor = colors.contains(color);
				ChatColor bracketColor = hasColor ? ChatColor.LIGHT_GREEN : ChatColor.LIGHT_RED;
				String cmd = "buildproperty " + (hasColor ? "removecolor" : "addcolor") + " " + color.getName();
				mb.startHoverText("Click to " + (hasColor ? "remove" : "add"));
				mb.addCommand(bracketColor + "[" + ChatColor.toChatColor(color.getColor()) + color.getName() + bracketColor + "]" + " ", cmd);
				mb.endHoverText();
				mb.add(" ");
			}
			player.sendMessage(mb.build());
			player.sendMessage(new MessageBuilder(ChatAlignment.CENTER).setCategory(CATEGORY)
					.addCommand(ChatColor.LINK + "[Back]", "buildproperty").build());
		}
		else if (args[0].equalsIgnoreCase("addcolor"))
		{
			List<PropertyColor> colors = template.getColorList(CardProperty.COLORS);
			PropertyColor add = PropertyColor.fromName(getFullStringArgument(args, 1));
			colors.add(add);
			colors.sort(PropertyColor.ID_SORTER);
			template.putColors(CardProperty.COLORS, colors);
			executeCommand(player, new String[] {"colors"});
		}
		else if (args[0].equalsIgnoreCase("removecolor"))
		{
			List<PropertyColor> colors = template.getColorList(CardProperty.COLORS);
			PropertyColor remove = PropertyColor.fromName(getFullStringArgument(args, 1));
			colors.remove(remove);
			template.putColors(CardProperty.COLORS, colors);
			executeCommand(player, new String[] {"colors"});
		}
		else if (args[0].equalsIgnoreCase("description"))
		{
			String desc = getFullStringArgument(args, 1);
			template.putStrings(DESCRIPTION, desc);
			executeCommand(player, new String[0]);
		}
		else if (args[0].equalsIgnoreCase("base"))
		{
			template.put(CardProperty.BASE, parseBoolean(args[1]));
			executeCommand(player, new String[0]);
		}
		else if (args[0].equalsIgnoreCase("stealable"))
		{
			template.put(CardProperty.STEALABLE, parseBoolean(args[1]));
			executeCommand(player, new String[0]);
		}
		else if (args[0].equalsIgnoreCase("import"))
		{
			builds.put(player, Card.getCard(parseInt(args[1])).getTemplate().clone());
			player.clearMessages(CommandEditDeck.CATEGORY);
			executeCommand(player, new String[0]);
		}
		else if (args[0].equalsIgnoreCase("create"))
		{
			Card card = template.createCard();
			builds.remove(player);
			
			sender.sendMessage("Created property " + ChatColor.UTILITY + card.getName() + ChatColor.WHITE +
					" with ID "+ card.getID(), ChatAlignment.CENTER, true);
			CommandCreateCard.sendTransferLinks(player, card);
		}
		else if (args[0].equalsIgnoreCase("createToDeck"))
		{
			Card card = template.createCard();
			card.transfer(getServer().getDeck(), -1, 0.4);
			builds.remove(player);
			
			sender.sendMessage("Created property " + ChatColor.UTILITY + card.getName() + ChatColor.WHITE +
					" with ID "+ card.getID(), ChatAlignment.CENTER, true);
			player.executeCommand("editdeck");
		}
	}
	
	private String formatEntry(String key)
	{
		return ChatColor.LIGHT_YELLOW + key + ChatColor.WHITE + ": " + ChatColor.LIGHT_ORANGE;
	}
}
