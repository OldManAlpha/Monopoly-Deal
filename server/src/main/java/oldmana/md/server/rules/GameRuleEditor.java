package oldmana.md.server.rules;

import oldmana.md.common.playerui.ChatAlignment;
import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.MDServer;
import oldmana.md.server.MessageBuilder;
import oldmana.md.server.Player;
import oldmana.md.server.rules.struct.RuleStruct;
import oldmana.md.server.rules.struct.RuleStructKey;
import oldmana.md.server.rules.struct.RuleStructObject;
import oldmana.md.server.rules.struct.RuleStructOption;
import oldmana.md.server.rules.struct.RuleStructValue;

import java.util.List;

public class GameRuleEditor
{
	public static final String CATEGORY_USAGE = "ruleTypeUsage";
	
	public static class GameRuleEditorParams
	{
		public CommandSender sender;
		public GameRule root;
		public String command;
		public String[] args;
		public Runnable applySender;
		public String category;
		
		public GameRuleEditorParams() {}
		
		public GameRuleEditorParams sender(CommandSender sender)
		{
			this.sender = sender;
			return this;
		}
		
		public GameRuleEditorParams root(GameRule root)
		{
			this.root = root;
			return this;
		}
		
		public GameRuleEditorParams command(String command)
		{
			this.command = command;
			return this;
		}
		
		public GameRuleEditorParams args(String[] args)
		{
			this.args = args;
			return this;
		}
		
		public GameRuleEditorParams applySender(Runnable applySender)
		{
			this.applySender = applySender;
			return this;
		}
		
		public GameRuleEditorParams category(String category)
		{
			this.category = category;
			return this;
		}
	}
	
	public static void handleCommand(GameRuleEditorParams params)
	{
		String[] args = params.args;
		if (args.length == 0 || args[0].equalsIgnoreCase("list"))
		{
			list(params);
		}
		else if (args[0].equalsIgnoreCase("set"))
		{
			set(params);
		}
		else if (args[0].equalsIgnoreCase("type"))
		{
			type(params);
		}
	}
	
	public static void list(GameRuleEditorParams params)
	{
		GameRule root = params.root;
		CommandSender sender = params.sender;
		String command = params.command;
		String[] args = params.args;
		String path = args.length >= 2 ? args[1] : null;
		String category = params.category;
		sender.clearMessages(category);
		GameRule listedRule = path != null ? root.traverse(path) : root;
		
		if (listedRule.getRuleStruct() instanceof RuleStructOption)
		{
			List<String> displayPathList = listedRule.getRuleStruct().getDisplayPathList();
			String displayPath = "";
			for (String element : displayPathList)
			{
				displayPath += ChatColor.YELLOW + element + ChatColor.FAINTLY_GRAY + ">";
			}
			displayPath = displayPath.substring(0, displayPath.length() - 1);
			sender.sendMessage(ChatColor.LIGHT_RED + "Options for " + displayPath, ChatAlignment.CENTER, category);
			RuleStructOption rs = (RuleStructOption) listedRule.getRuleStruct();
			rs.getChoices().forEach((key, choice) ->
			{
				MessageBuilder mb = new MessageBuilder(ChatAlignment.CENTER).setCategory(category);
				mb.startHoverText(choice.getDescription());
				mb.addCommand(ChatColor.WHITE + "> " + ChatColor.LIGHT_GREEN + choice.getName(),
						command + " set " + rs.getPath() + " " + key);
				sender.sendMessage(mb.build());
			});
			sendBackButton(sender, listedRule.getRuleStruct().getObjectParent(), command, category);
			return;
		}
		
		sender.sendMessage("", category);
		if (listedRule == root)
		{
			sender.sendMessage(new MessageBuilder(ChatAlignment.CENTER).setCategory(category).startUnderline()
					.add(ChatColor.LIGHT_RED + "Game Rules").build());
		}
		else
		{
			sender.sendMessage(ChatColor.LIGHT_RED + "Game Rules for " + ChatColor.YELLOW +
					listedRule.getRuleStruct().getName(), ChatAlignment.CENTER, category);
		}
		listedRule.getView().forEach((key, rule) ->
		{
			RuleStruct rs = rule.getRuleStruct();
			MessageBuilder mb = new MessageBuilder(ChatAlignment.CENTER).setCategory(category);
			mb.addHover(ChatColor.LIGHT_YELLOW + rs.getName(), rs.getDescription());
			mb.add(ChatColor.WHITE + ": " + ChatColor.LIGHT_ORANGE);
			if (rs instanceof RuleStructKey)
			{
				RuleStructValue<?> child = ((RuleStructKey) rs).getChild();
				mb.add(child.getDisplayValue(rule.getValueAsRule()));
				mb.add(" ");
				addEditButton(sender, mb, child, command);
			}
			else if (rs instanceof RuleStructObject)
			{
				mb.add(ChatColor.ORANGE + rs.getDisplayValue(rule));
				mb.add(" ");
				addListButton(mb, rs, command);
			}
			else if (rs instanceof RuleStructOption)
			{
				GameRule choice = rule.getChoice();
				RuleStruct choiceStruct = choice.getRuleStruct();
				mb.addHover(rs.getDisplayValue(rule), choiceStruct.getDescription());
				if (choiceStruct instanceof RuleStructKey)
				{
					mb.add(" (" + choice.getDeepValue() + ") ");
					addEditButton(sender, mb, choiceStruct, command);
				}
				else if (choiceStruct instanceof RuleStructObject)
				{
					mb.add(" " + ChatColor.ORANGE + "(" + choice.getDisplayValue() + ") ");
					addListButton(mb, choiceStruct, command);
				}
				if (sender.isOp())
				{
					mb.add(" ");
					mb.startHoverText("Change currently selected option");
					mb.addCommand(ChatColor.UTILITY + "[Choose Option]", command + " list " + rs.getPath());
					mb.endHoverText();
				}
			}
			sender.sendMessage(mb.build());
		});
		
		if (listedRule.getRuleStruct().hasParent())
		{
			sendBackButton(sender, listedRule.getRuleStruct().getObjectParent(), command, category);
		}
		else
		{
			params.applySender.run();
		}
		
		if (sender instanceof Player)
		{
			((Player) sender).setChatOpen(true);
		}
	}
	
	public static void set(GameRuleEditorParams params)
	{
		CommandSender sender = params.sender;
		GameRule root = params.root;
		String command = params.command;
		String[] args = params.args;
		if (args.length < 3)
		{
			sender.sendMessage(ChatColor.LIGHT_RED + "Insufficient arguments");
			return;
		}
		String path = args[1];
		String rawValue = args[2];
		if (!sender.isOp())
		{
			return;
		}
		sender.clearMessages(CATEGORY_USAGE);
		GameRule rule = root.traverse(path);
		String prevValue = rule.getRuleStruct() instanceof RuleStructKey ? rule.getValueAsRule().getDisplayValue() : rule.getDisplayValue();
		rule.setValue(rawValue);
		String value = rule.getRuleStruct() instanceof RuleStructKey ? rule.getValueAsRule().getDisplayValue() : rule.getDisplayValue();
		sender.sendMessage(ChatColor.PREFIX_ALERT + ChatColor.WHITE + "Changed " + ChatColor.LIGHT_YELLOW +
				rule.getRuleStruct().getName() + ChatColor.WHITE + " from " + ChatColor.LIGHT_ORANGE + prevValue + ChatColor.WHITE +
				" to " + ChatColor.LIGHT_ORANGE + value);
		args[args.length - 2] = rule.getRuleStruct().getObjectParent().getPath();
		list(params);
		//getServer().getCommandHandler().executeCommand(sender, command + " list " + rule.getRuleStruct().getObjectParent().getPath());
		//executeCommand(sender, new String[] {"list", rule.getRuleStruct().getObjectParent().getPath()});
	}
	
	public static void type(GameRuleEditorParams params)
	{
		CommandSender sender = params.sender;
		GameRule root = params.root;
		if (params.args.length < 2)
		{
			sender.sendMessage(ChatColor.LIGHT_RED + "Insufficient arguments");
			return;
		}
		String path = params.args[1];
		if (!sender.isOp())
		{
			return;
		}
		GameRule rule = root.traverse(path);
		if (rule.getRuleStruct() instanceof RuleStructKey)
		{
			sender.clearMessages(CATEGORY_USAGE);
			List<String> usage = ((RuleStructValue<?>) rule.getValueAsRule().getRuleStruct()).getValueType().getUsage();
			for (String line : usage)
			{
				sender.sendMessage(line, CATEGORY_USAGE);
			}
		}
	}
	
	private static void sendBackButton(CommandSender sender, RuleStruct to, String command, String category)
	{
		MessageBuilder mb = new MessageBuilder(ChatAlignment.CENTER).setCategory(category);
		mb.startHoverText("Go back to the previous page");
		mb.addCommand(ChatColor.LINK + "[Go Back]", command + " list " + to.getPath());
		sender.sendMessage(mb.build());
	}
	
	private static void addEditButton(CommandSender sender, MessageBuilder mb, RuleStruct rs, String command)
	{
		if (!sender.isOp())
		{
			return;
		}
		mb.startHoverText("Fill in command to edit value");
		mb.startCommand(command + " type " + rs.getPath());
		mb.startFillCommand(command + " set " + rs.getPath() + " ");
		mb.add(ChatColor.LINK + "[Edit]" + ChatColor.WHITE);
		mb.endSpecial();
	}
	
	private static void addListButton(MessageBuilder mb, RuleStruct rs, String command)
	{
		mb.startHoverText("View subrules");
		mb.startCommand(command + " list " + rs.getPath());
		mb.add(ChatColor.LIGHT_GREEN + "[List]" + ChatColor.WHITE);
		mb.endSpecial();
	}
	
	private static MDServer getServer()
	{
		return MDServer.getInstance();
	}
}
