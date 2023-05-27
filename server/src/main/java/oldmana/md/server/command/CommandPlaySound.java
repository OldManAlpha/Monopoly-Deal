package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandPlaySound extends Command
{
	private Set<String> defaultSoundNames = new HashSet<String>(Arrays.asList("CardMove", "CardFlip",
			"ImportantCardMove", "CardPlace", "DeckShuffle", "Alert", "DrawAlert"));
	
	public CommandPlaySound()
	{
		super("playsound", null, new String[] {"/playsound [Sound Name]"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			if (getServer().doesSoundExist(args[0]) || defaultSoundNames.contains(args[0]))
			{
				getServer().playSound(args[0]);
				sender.sendMessage("Playing sound: " + args[0], true);
			}
			else
			{
				sender.sendMessage("Sound does not exist.");
			}
		}
		else
		{
			sender.sendMessage("Sound name required.");
		}
	}
}
