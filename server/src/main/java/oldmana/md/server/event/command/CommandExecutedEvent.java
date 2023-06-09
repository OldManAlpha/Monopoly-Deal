package oldmana.md.server.event.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.command.Command;
import oldmana.md.server.event.Event;

public class CommandExecutedEvent extends Event
{
	private Command command;
	private CommandSender sender;
	
	private String full;
	private String[] args;
	
	public CommandExecutedEvent(Command command, CommandSender sender, String full, String[] args)
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
