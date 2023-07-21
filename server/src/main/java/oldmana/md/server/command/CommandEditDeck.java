package oldmana.md.server.command;

import oldmana.md.common.playerui.ChatAlignment;
import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.MessageBuilder;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardProperty;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;

public class CommandEditDeck extends Command
{
	public static final String CATEGORY = "editdeck";
	
	private static final int PAGE_LIMIT = 16;
	private static final String altColor = ChatColor.toChatColor(new Color(230, 230, 255));
	
	public CommandEditDeck()
	{
		super("editdeck", true);
		setAliases("deckedit", "deckeditor");
		setDescription("A tool to edit the deck and access other tools relating to deck editing.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (sender instanceof Player)
		{
			sender.clearMessages(CATEGORY);
			((Player) sender).setChatOpen(true);
		}
		
		int page = 0;
		
		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("page"))
			{
				page = parseInt(args[1]);
			}
			else if (args[0].equalsIgnoreCase("remove"))
			{
				int id = parseInt(args[1]);
				page = parseInt(args[2]);
				Card.getCard(id).transfer(getServer().getVoidCollection(), -1, 0.4);
			}
			else if (args[0].equalsIgnoreCase("copy"))
			{
				int id = parseInt(args[1]);
				page = parseInt(args[2]);
				
				Card.getCard(id).getTemplate().createCard().transfer(getServer().getDeck(), -1, 0.4);
			}
		}
		
		sender.sendMessage("", CATEGORY);
		sender.sendMessage(new MessageBuilder(ChatAlignment.CENTER).setCategory(CATEGORY).startUnderline()
				.add(ChatColor.LIGHT_RED + "Deck Editor").build());
		
		List<Card> cards = getServer().getDeck().getCards(true);
		cards.sort(Comparator.comparing(Card::getName));
		
		int maxPage = (int) Math.max(Math.ceil(cards.size() / (double) PAGE_LIMIT) - 1, 0);
		page = Math.max(page, 0);
		page = Math.min(page, maxPage);
		
		int bound = Math.min((page + 1) * PAGE_LIMIT, cards.size());
		for (int i = page * PAGE_LIMIT ; i < bound ; i++)
		{
			Card card = cards.get(i);
			MessageBuilder mb = new MessageBuilder().setCategory(CATEGORY);
			mb.startHoverText("Remove card from deck");
			mb.addCommand(ChatColor.LIGHT_RED + "[-]", "editdeck remove " + cards.get(i).getID() + " " + page);
			mb.endHoverText();
			mb.add(" ");
			mb.startHoverText("Duplicate card");
			mb.addCommand(ChatColor.LIGHT_GREEN + "[+]", "editdeck copy " + cards.get(i).getID() + " " + page);
			mb.endHoverText();
			if (card instanceof CardProperty)
			{
				mb.add(" ");
				mb.startHoverText("Import to property editor");
				mb.addCommand(ChatColor.LIGHT_BLUE + "[*]", "buildproperty import " + cards.get(i).getID());
				mb.endHoverText();
			}
			mb.add("  ");
			mb.startHoverText(card.toString(), "ID: " + card.getID());
			mb.add((i % 2 == 0 ? altColor : ChatColor.WHITE) + card.getName());
			mb.endHoverText();
			sender.sendMessage(mb.build());
			
		}
		MessageBuilder mb = new MessageBuilder(ChatAlignment.CENTER).setCategory(CATEGORY);
		mb.addCommand(ChatColor.LINK + "[Prev]", "editdeck page " + (page - 1));
		mb.add(ChatColor.LIGHT_ORANGE + "    Page " + (page + 1) + " of " + (maxPage + 1) + "    ");
		mb.addCommand(ChatColor.LINK + "[Next]", "editdeck page " + (page + 1));
		sender.sendMessage(mb.build());
		
		mb = new MessageBuilder(ChatAlignment.CENTER).setCategory(CATEGORY);
		mb.startHoverText("Create new cards");
		mb.addCommand(ChatColor.LINK + "[Create Card]", "createcard");
		mb.endHoverText();
		mb.add("  ");
		mb.startHoverText("Build a custom property");
		mb.addCommand(ChatColor.LINK + "[Property Builder]", "buildproperty");
		mb.endHoverText();
		mb.add("  ");
		mb.startHoverText("Edit the game rules");
		mb.addCommand("[Edit Rules]", "rules");
		mb.endHoverText();
		mb.add("  ");
		mb.startHoverText("Fill command, then enter name you'd like to save deck as");
		mb.addFillCommand("[Save Deck]", "savedeck ");
		sender.sendMessage(mb.build());
	}
}
