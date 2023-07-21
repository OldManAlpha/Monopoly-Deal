package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandPlaySound extends Command
{
	private Set<String> defaultSoundNames = Stream.of("CardMove", "CardFlip", "ImportantCardMove", "CardPlace",
			"DeckShuffle", "Alert", "DrawAlert").map(String::toLowerCase).collect(Collectors.toSet());
	
	public CommandPlaySound()
	{
		super("playsound", true);
		setUsage("/playsound [Sound Name]",
				"Sound Name: The name of the sound to play");
		setDescription("Plays a sound to all players.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendMessage("Sound name required.");
			return;
		}
		
		String sound = args[0].toLowerCase();
		if (!getServer().doesSoundExist(sound) && !defaultSoundNames.contains(sound))
		{
			sender.sendMessage("Sound does not exist.");
			return;
		}
		
		getServer().playSound(sound);
		sender.sendMessage("Playing sound: " + (getServer().getSound(sound) != null ?
				getServer().getSound(sound).getName() : args[0]), true);
	}
}
