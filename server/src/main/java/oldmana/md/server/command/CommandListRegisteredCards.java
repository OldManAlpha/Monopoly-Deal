package oldmana.md.server.command;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.MessageBuilder;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardRegistry;
import oldmana.md.server.card.CardRegistry.RegisteredCard;

public class CommandListRegisteredCards extends Command
{
	public CommandListRegisteredCards()
	{
		super("listregisteredcards", null, new String[] {"/listregisteredcards"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		CardRegistry registry = getServer().getCardRegistry();
		sender.sendMessage(ChatColor.LIGHT_BLUE + "List of registered action cards:");
		for (RegisteredCard rc : registry.getRegisteredActionCards())
		{
			sender.sendMessage(getMessage(rc));
		}
		if (registry.getRegisteredSpecialCards().size() > 0)
		{
			sender.sendMessage(ChatColor.LIGHT_BLUE + "List of registered special cards:");
			for (RegisteredCard rc : registry.getRegisteredSpecialCards())
			{
				sender.sendMessage(getMessage(rc));
			}
		}
	}
	
	public String getMessage(RegisteredCard rc)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addString("- " + rc.getName());
		String[] aliases = rc.getAliases();
		if (aliases.length > 0)
		{
			mb.addString(" (" + aliases[0]);
			if (aliases.length > 1)
			{
				for (int i = 1 ; i < aliases.length ; i++)
				{
					mb.addString(", " + aliases[i]);
				}
			}
			mb.addString(")");
		}
		/*
		mb.addLinkedString(ChatColor.LINK + " [Create]").setListener(() ->
		{
			Card card = rc.createCard();
			sender.sendMessage("Created action card " + card.getName() + " with ID " + card.getID(), true);
		});
		*/
		return mb.getMessage();
	}
}
