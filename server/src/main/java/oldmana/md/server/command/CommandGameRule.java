package oldmana.md.server.command;

import oldmana.md.common.playerui.ChatAlignment;
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
	public static final String CATEGORY = "gamerule";
	
	public CommandGameRule()
	{
		super("rules", false);
		setAliases("gamerule", "gamerules", "rule");
		setDescription("A tool that allows you to view and edit game rules.");
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
			sender.clearMessages(CATEGORY);
			sender.clearMessages(GameRuleEditor.CATEGORY_USAGE);
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
				.applySender(() -> sendApplyRulesButton(sender))
				.category(CATEGORY));
	}
	
	private void sendApplyRulesButton(CommandSender sender)
	{
		if (!sender.isOp())
		{
			return;
		}
		MessageBuilder mb = new MessageBuilder(ChatAlignment.CENTER).setCategory(CATEGORY);
		mb.startHoverText("Apply the changed rules");
		mb.addCommand(ChatColor.LINK + "[Apply Rules]", "rules reload");
		sender.sendMessage(mb.build());
	}
}
