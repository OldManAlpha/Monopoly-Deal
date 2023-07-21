package oldmana.md.server.command;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.MessageBuilder;

public class CommandEditTools extends Command
{
	public CommandEditTools()
	{
		super("edittools", true);
		setDescription("Shows a list of useful tools related to deck editing.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		sender.sendMessage(ChatColor.LIGHT_RED + "   > Deck Editing Tools <");
		
		sender.sendMessage(new MessageBuilder().addFillCommand(ChatColor.LIGHT_ORANGE + "/rules", "rules").build());
		sender.sendMessage(ChatColor.FAINTLY_GRAY + "* Allows you to customize the game rules for the deck");
		
		sender.sendMessage(new MessageBuilder().addFillCommand(ChatColor.LIGHT_ORANGE + "/editdeck", "editdeck").build());
		sender.sendMessage(ChatColor.FAINTLY_GRAY + "* Allows you to remove and duplicate cards that are in the deck");
		
		sender.sendMessage(new MessageBuilder().addFillCommand(ChatColor.LIGHT_ORANGE + "/createcard", "createcard").build());
		sender.sendMessage(ChatColor.FAINTLY_GRAY + "* Allows you to create new cards");
		
		sender.sendMessage(new MessageBuilder().addFillCommand(ChatColor.LIGHT_ORANGE + "/buildproperty", "buildproperty").build());
		sender.sendMessage(ChatColor.FAINTLY_GRAY + "* Allows you to create a custom property card");
		
		sender.sendMessage(new MessageBuilder().addFillCommand(ChatColor.LIGHT_ORANGE + "/listdecks", "listdecks").build());
		sender.sendMessage(ChatColor.FAINTLY_GRAY + "* List all decks");
		
		sender.sendMessage(new MessageBuilder().addFillCommand(ChatColor.LIGHT_ORANGE + "/setdeck [Name]", "setdeck ").build());
		sender.sendMessage(ChatColor.FAINTLY_GRAY + "* Swap to another deck");
		
		sender.sendMessage(new MessageBuilder().addFillCommand(ChatColor.LIGHT_ORANGE + "/savedeck [Name]", "savedeck ").build());
		sender.sendMessage(ChatColor.FAINTLY_GRAY + "* Saves the deck and game rules with the provided name");
	}
}
