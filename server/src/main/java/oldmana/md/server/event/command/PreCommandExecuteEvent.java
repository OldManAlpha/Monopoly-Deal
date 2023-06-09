package oldmana.md.server.event.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.command.Command;
import oldmana.md.server.event.CancellableEvent;

public class PreCommandExecuteEvent extends CancellableEvent
{
	private Command command;
	private CommandSender sender;
	
	private String full;
	private String[] args;
	
	public PreCommandExecuteEvent(Command command, CommandSender sender, String full, String[] args)
	{
		this.command = command;
		this.sender = sender;
		
		this.full = full;
		this.args = args;
	}
	
	public Command getCommand()
	{
		return command;
	}
	
	public CommandSender getSender()
	{
		return sender;
	}
	
	public String getFullCommand()
	{
		return full;
	}
	
	public String[] getArguments()
	{
		return args;
	}
}
