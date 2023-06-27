package oldmana.md.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oldmana.md.server.command.*;
import oldmana.md.server.event.command.CommandExecutedEvent;
import oldmana.md.server.event.command.PreCommandExecuteEvent;

public class CommandHandler
{
	private List<Command> commands = new ArrayList<Command>();
	private Map<String, Command> commandMap = new HashMap<String, Command>();
	
	public void registerDefaultCommands()
	{
		registerCommand(new CommandStart());
		registerCommand(new CommandReset());
		registerCommand(new CommandShuffle());
		registerCommand(new CommandSetDeck());
		registerCommand(new CommandListCards());
		registerCommand(new CommandListIDs());
		registerCommand(new CommandCollectionInfo());
		registerCommand(new CommandCreateCard());
		registerCommand(new CommandTransferCard());
		registerCommand(new CommandTransferAll());
		registerCommand(new CommandTransferIndex());
		registerCommand(new CommandListPlayers());
		registerCommand(new CommandListDecks());
		registerCommand(new CommandHelp());
		registerCommand(new CommandListRegisteredPlayers());
		registerCommand(new CommandSetTurn());
		registerCommand(new CommandSetMoves());
		registerCommand(new CommandNextTurn());
		registerCommand(new CommandGameRule());
		registerCommand(new CommandKickPlayer());
		registerCommand(new CommandKickPlayerID());
		registerCommand(new CommandPlaySound());
		registerCommand(new CommandLoadSound());
		registerCommand(new CommandListEffects());
		registerCommand(new CommandRemoveEffect());
		registerCommand(new CommandOp());
		registerCommand(new CommandDeop());
		registerCommand(new CommandStop());
		registerCommand(new CommandListRegisteredCards());
		registerCommand(new CommandCreateDeck());
		registerCommand(new CommandBroadcast());
		registerCommand(new CommandAddBot());
		registerCommand(new CommandToggleBot());
		registerCommand(new CommandShufflePlayers());
		registerCommand(new CommandDeckBuilder());
		
		
		// Registering test/debug commands through reflection, as they're only used in a development environment.
		try
		{
			Class<?> clazz = Class.forName("oldmana.md.server.command.CommandTest");
			registerCommand((Command) clazz.newInstance());
		}
		catch (Exception e) {}
		
		try
		{
			Class<?> clazz = Class.forName("oldmana.md.server.command.CommandDebug");
			registerCommand((Command) clazz.newInstance());
		}
		catch (Exception e) {}
	}
	
	public void registerCommand(Command cmd)
	{
		commands.add(cmd);
		commandMap.put(cmd.getName().toLowerCase(), cmd);
		for (String alias : cmd.getAliases())
		{
			commandMap.put(alias.toLowerCase(), cmd);
		}
	}
	
	public void executeCommand(CommandSender sender, String fullCmd)
	{
		String[] split = fullCmd.split(" ");
		String name = split[0];
		Command cmd = findCommand(name);
		if (sender instanceof Player)
		{
			System.out.println(((Player) sender).getName() + " issued " + (cmd == null ? "unknown " : "") + "command: " + fullCmd);
		}
		if (cmd == null)
		{
			sender.sendMessage(ChatColor.PREFIX_ALERT + "Command not found.");
			return;
		}
		if (!cmd.checkPermission(sender))
		{
			sender.sendMessage(ChatColor.PREFIX_ALERT + "Insufficient permissions.");
			return;
		}
		String[] args = Arrays.copyOfRange(split, 1, split.length);
		PreCommandExecuteEvent event = new PreCommandExecuteEvent(cmd, sender, fullCmd, args);
		MDServer.getInstance().getEventManager().callEvent(event);
		if (!event.isCancelled())
		{
			try
			{
				cmd.executeCommand(sender, args);
			}
			catch (Exception | Error e)
			{
				System.out.println("Error while executing command: " + fullCmd);
				e.printStackTrace();
				if (sender instanceof Player)
				{
					sender.sendMessage(ChatColor.PREFIX_ALERT + "Error: " + e.getMessage());
				}
			}
			MDServer.getInstance().getEventManager().callEvent(new CommandExecutedEvent(cmd, sender, fullCmd, args));
		}
	}
	
	public Command findCommand(String name)
	{
		return commandMap.get(name.toLowerCase());
	}
	
	public List<Command> getCommands()
	{
		return commands;
	}
}
