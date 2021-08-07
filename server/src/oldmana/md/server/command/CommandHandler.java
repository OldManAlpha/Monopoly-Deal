package oldmana.md.server.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oldmana.md.server.CommandSender;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.event.CommandExecutedEvent;
import oldmana.md.server.event.PreCommandExecuteEvent;

public class CommandHandler
{
	private List<Command> commands = new ArrayList<Command>();
	
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
		registerCommand(new CommandRegisterPlayer());
		registerCommand(new CommandUnregisterPlayer());
		registerCommand(new CommandListRegisteredPlayers());
		registerCommand(new CommandSetTurn());
		registerCommand(new CommandSetTurns());
		registerCommand(new CommandNextTurn());
		registerCommand(new CommandGameRule());
		registerCommand(new CommandKickPlayer());
		registerCommand(new CommandPlaySound());
		registerCommand(new CommandLoadSound());
		registerCommand(new CommandListEffects());
		registerCommand(new CommandRemoveEffect());
		registerCommand(new CommandOp());
		registerCommand(new CommandDeop());
		registerCommand(new CommandStop());
		registerCommand(new CommandListRegisteredCards());
		registerCommand(new CommandCreateDeck());
		
		
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
			sender.sendMessage("Command not found.");
			return;
		}
		if (cmd.requiresOp() && !sender.isOp())
		{
			sender.sendMessage("Insufficient permissions.");
			return;
		}
		String[] args = Arrays.copyOfRange(split, 1, split.length);
		try
		{
			PreCommandExecuteEvent event = new PreCommandExecuteEvent(cmd, sender, fullCmd, args);
			MDServer.getInstance().getEventManager().callEvent(event);
			if (!event.isCanceled())
			{
				cmd.executeCommand(sender, args);
				MDServer.getInstance().getEventManager().callEvent(new CommandExecutedEvent(cmd, sender, fullCmd, args));
			}
		}
		catch (Exception e)
		{
			System.out.println("Error while executing command: " + fullCmd);
			if (sender instanceof Player)
			{
				sender.sendMessage("Error: " + e.getMessage());
			}
			e.printStackTrace();
		}
	}
	
	public Command findCommand(String name)
	{
		for (Command cmd : commands)
		{
			if (cmd.isThisCommand(name))
			{
				return cmd;
			}
		}
		return null;
	}
	
	public List<Command> getCommands()
	{
		return commands;
	}
}
