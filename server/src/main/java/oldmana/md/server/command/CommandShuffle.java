package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

public class CommandShuffle extends Command
{
	public CommandShuffle()
	{
		super("shuffle", true);
		setDescription("Shuffles the deck.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		getServer().getDeck().shuffle();
		sender.sendMessage("Shuffled deck");
	}
}
