package oldmana.md.server.command;

import java.io.File;

import oldmana.md.server.CommandSender;

public class CommandLoadSound extends Command
{
	public CommandLoadSound()
	{
		super("loadsound", null, new String[] {"/loadsound [File Name]"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			File f = new File("sounds" + File.separator + args[0] + ".wav");
			getServer().loadSound(f);
		}
		else
		{
			sender.sendMessage("File name required.");
		}
	}
}
