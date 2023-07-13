package oldmana.md.server.command;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.MessageBuilder;
import oldmana.md.server.Player;
import oldmana.md.server.rules.GameRule;
import oldmana.md.server.rules.GameRuleEditor;
import oldmana.md.server.rules.GameRuleEditor.GameRuleEditorParams;
import oldmana.md.server.rules.GameRules;

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
		GameRules gameRules = getServer().getGameRules();
		
		GameRule root = gameRules.getRootRule();
		
		if (args.length > 0 && args[0].equalsIgnoreCase("reload"))
		{
			if (!sender.isOp())
			{
				return;
			}
			gameRules.reloadRules();
			clearMessages(sender, "gamerule");
			clearMessages(sender, "ruleTypeUsage");
			sender.sendMessage(ChatColor.LIGHT_GREEN + "Reloaded the game rules.");
			if (sender instanceof Player)
			{
				if (getServer().getGameState().isGameRunning())
				{
					((Player) sender).setChatOpen(false);
				}
				else
				{
					((Player) sender).executeCommand("editdeck");
				}
			}
			return;
		}
		GameRuleEditor.handleCommand(new GameRuleEditorParams()
				.sender(sender)
				.root(root)
				.command(getName())
				.args(args)
				.applySender(() -> sendApplyRulesButton(sender)));
	}
	
	private void sendApplyRulesButton(CommandSender sender)
	{
		if (!sender.isOp())
		{
			return;
		}
		MessageBuilder mb = new MessageBuilder();
		mb.setCategory("gamerule");
		mb.add("                                  ");
		mb.startHoverText("Apply the changed rules");
		mb.addCommand(ChatColor.LINK + "[Apply Rules]", "rules reload");
		sender.sendMessage(mb.build());
	}
	
	private void clearMessages(CommandSender sender, String category)
	{
		if (sender instanceof Player)
		{
			((Player) sender).clearMessages(category);
		}
	}
}
