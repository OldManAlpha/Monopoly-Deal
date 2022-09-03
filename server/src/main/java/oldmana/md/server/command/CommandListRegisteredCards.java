package oldmana.md.server.command;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.MessageBuilder;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.CardRegistry;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.collection.Hand;
import oldmana.md.server.card.CardType;
import oldmana.md.server.card.CardType.CardTemplateInfo;

import java.util.List;
import java.util.Map.Entry;

public class CommandListRegisteredCards extends Command
{
	public CommandListRegisteredCards()
	{
		super("listregisteredcards", null, new String[] {"/listregisteredcards"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		sender.sendMessage(ChatColor.LIGHT_BLUE + "List of registered cards:");
		List<CardType<?>> cards = CardRegistry.getRegisteredCards();
		for (CardType<?> type : cards)
		{
			if (type.getCardClass() != CardProperty.class && type.isInstantiable())
			{
				sender.sendMessage(getMessage(type, sender));
				for (Entry<CardTemplateInfo, CardTemplate> entry : type.getTemplates().entrySet())
				{
					sender.sendMessage(getMessage(entry, sender));
				}
			}
		}
	}
	
	public String getMessage(CardType<?> type, CommandSender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addString("- " + type.getFriendlyName());
		List<String> aliases = type.getAliases();
		if (aliases.size() > 0)
		{
			mb.addString(" (" + aliases.get(0));
			if (aliases.size() > 1)
			{
				for (int i = 1 ; i < aliases.size() ; i++)
				{
					mb.addString(", " + aliases.get(i));
				}
			}
			mb.addString(")");
		}
		addCreateCardLink(mb, type.getDefaultTemplate(), sender);
		return mb.getMessage();
	}
	
	public String getMessage(Entry<CardTemplateInfo, CardTemplate> entry, CommandSender sender)
	{
		CardTemplateInfo info = entry.getKey();
		CardTemplate template = entry.getValue();
		MessageBuilder mb = new MessageBuilder();
		mb.addString(ChatColor.of(230, 230, 230) + "  - " + info.getName());
		List<String> aliases = info.getAliases();
		if (aliases.size() > 0)
		{
			mb.addString(" (" + aliases.get(0));
			if (aliases.size() > 1)
			{
				for (int i = 1 ; i < aliases.size() ; i++)
				{
					mb.addString(", " + aliases.get(i));
				}
			}
			mb.addString(")");
		}
		addCreateCardLink(mb, template, sender);
		return mb.getMessage();
	}
	
	private void addCreateCardLink(MessageBuilder mb, CardTemplate template, CommandSender sender)
	{
		mb.addLinkedString(ChatColor.LINK + " [Create]").setListener(() ->
		{
			Card card = template.createCard();
			sender.sendMessage("Created card " + card.getName() + " with ID " + card.getID());
			MessageBuilder link = new MessageBuilder();
			link.addLinkedString(ChatColor.LINK + "[Transfer To Hand]").setListener(() ->
			{
				Hand hand = ((Player) sender).getHand();
				if (card.getOwningCollection() != hand)
				{
					card.transfer(hand);
				}
			});
			sender.sendMessage(link.getMessage());
		});
	}
}
