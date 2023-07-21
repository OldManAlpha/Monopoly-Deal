package oldmana.md.server.command;

import java.io.File;

import oldmana.md.server.CommandSender;

public class CommandLoadSound extends Command
{
	public CommandLoadSound()
	{
		super("loadsound", true);
		setUsage("/loadsound [File Name]",
				"File Name: The name of the file in the sounds folder, minus .wav");
		setDescription("Loads a sound for use by the server.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			File f = new File("sounds" + File.separator + getFullStringArgument(args, 0) + ".wav");
			getServer().loadSound(f, true);
		}
		else
		{
			sender.sendMessage("File name required.");
		}
	}
}
