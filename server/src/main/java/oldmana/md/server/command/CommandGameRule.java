package oldmana.md.server.command;

import oldmana.md.common.net.packet.server.PacketSetChatOpen;
import oldmana.md.common.net.packet.server.PacketRemoveMessageCategory;
import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.MessageBuilder;
import oldmana.md.server.Player;
import oldmana.md.server.rules.GameRule;
import oldmana.md.server.rules.GameRules;
import oldmana.md.server.rules.struct.RuleStruct;
import oldmana.md.server.rules.struct.RuleStructKey;
import oldmana.md.server.rules.struct.RuleStructObject;
import oldmana.md.server.rules.struct.RuleStructOption;
import oldmana.md.server.rules.struct.RuleStructValue;

import java.util.List;

public class CommandGameRule extends Command
{
	public CommandGameRule()
	{
		super("rules", new String[] {"gamerule", "gamerules", "rule"}, new String[] {"/rules", "/rules list",
				"/rules set"}, false);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		GameRules grs = getServer().getGameRules();
		
		GameRule root = grs.getRootRule();
		
		if (args.length == 0 || args[0].equalsIgnoreCase("list"))
		{
			clearMessages(sender, "gamerule");
			GameRule listedRule = args.length >= 2 ? root.traverse(args[1]) : root;
			
			if (listedRule.getRuleStruct() instanceof RuleStructOption)
			{
				List<String> displayPathList = listedRule.getRuleStruct().getDisplayPathList();
				String displayPath = "";
				for (String element : displayPathList)
				{
					displayPath += ChatColor.YELLOW + element + ChatColor.FAINTLY_GRAY + ">";
				}
				displayPath = displayPath.substring(0, displayPath.length() - 1);
				sender.sendMessage(ChatColor.LIGHT_RED + "Options for " + displayPath, "gamerule");
				RuleStructOption rs = (RuleStructOption) listedRule.getRuleStruct();
				rs.getChoices().forEach((key, choice) ->
				{
					MessageBuilder mb = new MessageBuilder();
					mb.setCategory("gamerule");
					mb.startHoverText(choice.getDescription());
					mb.addCommandString(ChatColor.WHITE + "> " + ChatColor.LIGHT_GREEN + choice.getName(),
							"gamerule set " + rs.getPath() + " " + key);
					sender.sendMessage(mb.getMessage());
				});
				sendBackButton(sender, listedRule.getRuleStruct().getObjectParent());
				return;
			}
			
			sender.sendMessage(" ", "gamerule");
			sender.sendMessage(ChatColor.LIGHT_RED + "List of rules for " + ChatColor.YELLOW + listedRule.getRuleStruct().getName(), "gamerule");
			listedRule.getView().forEach((key, rule) ->
			{
				RuleStruct rs = rule.getRuleStruct();
				MessageBuilder mb = new MessageBuilder();
				mb.setCategory("gamerule");
				mb.addHoverString(ChatColor.LIGHT_YELLOW + rs.getName(), rs.getDescription());
				mb.addString(ChatColor.WHITE + ": " + ChatColor.LIGHT_ORANGE);
				if (rs instanceof RuleStructKey)
				{
					RuleStructValue<?> child = ((RuleStructKey) rs).getChild();
					mb.addString(child.getDisplayValue(rule.getValueAsRule()));
					mb.addString(" ");
					addEditButton(sender, mb, child);
				}
				else if (rs instanceof RuleStructObject)
				{
					mb.addString(ChatColor.ORANGE + rs.getDisplayValue(rule));
					mb.addString(" ");
					addListButton(mb, rs);
				}
				else if (rs instanceof RuleStructOption)
				{
					GameRule choice = rule.getChoice();
					RuleStruct choiceStruct = choice.getRuleStruct();
					mb.addHoverString(rs.getDisplayValue(rule), choiceStruct.getDescription());
					if (choiceStruct instanceof RuleStructKey)
					{
						mb.addString(" (" + choice.getDeepValue() + ") ");
						addEditButton(sender, mb, choiceStruct);
					}
					else if (choiceStruct instanceof RuleStructObject)
					{
						mb.addString(" " + ChatColor.ORANGE + "(" + choice.getDisplayValue() + ") ");
						addListButton(mb, choiceStruct);
					}
					if (sender.isOp())
					{
						mb.addString(" ");
						mb.startHoverText("Change currently selected option");
						mb.addCommandString(ChatColor.UTILITY + "[Choose Option]", "gamerule list " + rs.getPath());
						mb.endHoverText();
					}
				}
				sender.sendMessage(mb.getMessage());
			});
			
			if (listedRule.getRuleStruct().hasParent())
			{
				sendBackButton(sender, listedRule.getRuleStruct().getObjectParent());
			}
			else
			{
				sendApplyRulesButton(sender);
			}
			
			if (sender instanceof Player)
			{
				((Player) sender).sendPacket(new PacketSetChatOpen(true));
			}
		}
		else if (args[0].equalsIgnoreCase("set"))
		{
			if (!sender.isOp())
			{
				return;
			}
			clearMessages(sender, "ruleTypeUsage");
			GameRule rule = root.traverse(args[1]);
			String prevValue = rule.getRuleStruct() instanceof RuleStructKey ? rule.getValueAsRule().getDisplayValue() : rule.getDisplayValue();
			rule.setValue(args[2]);
			String value = rule.getRuleStruct() instanceof RuleStructKey ? rule.getValueAsRule().getDisplayValue() : rule.getDisplayValue();
			sender.sendMessage(ChatColor.PREFIX_ALERT + ChatColor.WHITE + "Changed " + ChatColor.LIGHT_YELLOW +
					rule.getRuleStruct().getName() + ChatColor.WHITE + " from " + ChatColor.LIGHT_ORANGE + prevValue + ChatColor.WHITE +
					" to " + ChatColor.LIGHT_ORANGE + value);
			executeCommand(sender, new String[] {"list", rule.getRuleStruct().getObjectParent().getPath()});
		}
		else if (args[0].equalsIgnoreCase("type"))
		{
			if (!sender.isOp())
			{
				return;
			}
			GameRule rule = root.traverse(args[1]);
			if (rule.getRuleStruct() instanceof RuleStructKey)
			{
				clearMessages(sender, "ruleTypeUsage");
				List<String> usage = ((RuleStructValue<?>) rule.getValueAsRule().getRuleStruct()).getValueType().getUsage();
				for (String line : usage)
				{
					sender.sendMessage(line, "ruleTypeUsage");
				}
			}
		}
		else if (args[0].equalsIgnoreCase("reload"))
		{
			if (!sender.isOp())
			{
				return;
			}
			grs.reloadRules();
			clearMessages(sender, "gamerule");
			clearMessages(sender, "ruleTypeUsage");
			if (sender instanceof Player)
			{
				((Player) sender).sendPacket(new PacketSetChatOpen(false));
			}
			sender.sendMessage(ChatColor.LIGHT_GREEN + "Reloaded the game rules.");
		}
	}
	
	private void sendApplyRulesButton(CommandSender sender)
	{
		if (!sender.isOp())
		{
			return;
		}
		MessageBuilder mb = new MessageBuilder();
		mb.setCategory("gamerule");
		mb.addString("                                  ");
		mb.startHoverText("Apply the changed rules");
		mb.addCommandString(ChatColor.LINK + "[Apply Rules]", "gamerule reload");
		sender.sendMessage(mb.getMessage());
	}
	
	private void sendBackButton(CommandSender sender, RuleStruct to)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.setCategory("gamerule");
		mb.addString("                                  ");
		mb.startHoverText("Go back to the previous page");
		mb.addCommandString(ChatColor.LINK + "[Go Back]", "gamerule list " + to.getPath());
		sender.sendMessage(mb.getMessage());
	}
	
	private void addEditButton(CommandSender sender, MessageBuilder mb, RuleStruct rs)
	{
		if (!sender.isOp())
		{
			return;
		}
		mb.startHoverText("Fill in command to edit value");
		mb.startCommand("gamerule type " + rs.getPath());
		mb.startFillCommand("gamerule set " + rs.getPath() + " ");
		mb.addString(ChatColor.LINK + "[Edit]" + ChatColor.WHITE);
		mb.endSpecial();
	}
	
	private void addListButton(MessageBuilder mb, RuleStruct rs)
	{
		mb.startHoverText("View subrules");
		mb.startCommand("gamerule list " + rs.getPath());
		mb.addString(ChatColor.LIGHT_GREEN + "[List]" + ChatColor.WHITE);
		mb.endSpecial();
	}
	
	private void clearMessages(CommandSender sender, String category)
	{
		if (sender instanceof Player)
		{
			((Player) sender).sendPacket(new PacketRemoveMessageCategory(category));
		}
	}
}
