package oldmana.md.server.command;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.MDServer;
import oldmana.md.server.MessageBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Command
{
	private String name;
	private List<String> aliases = Collections.emptyList();
	private List<String> usage;
	private String description = "Missing description.";
	
	private boolean requiresOp;
	
	public Command(String name, boolean requiresOp)
	{
		this.name = name;
		usage = Collections.singletonList("/" + name);
		this.requiresOp = requiresOp;
	}
	
	public String getName()
	{
		return name;
	}
	
	public List<String> getAliases()
	{
		return aliases;
	}
	
	protected void setAliases(String... aliases)
	{
		this.aliases = Arrays.asList(aliases);
	}
	
	protected void setUsage(String... usage)
	{
		this.usage = Arrays.asList(usage);
	}
	
	public String getDescription()
	{
		return description;
	}
	
	protected void setDescription(String description)
	{
		this.description = description;
	}
	
	public boolean isAlias(String name)
	{
		for (String alias : aliases)
		{
			if (alias.equalsIgnoreCase(name))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isThisCommand(String name)
	{
		return getName().equalsIgnoreCase(name) || isAlias(name);
	}
	
	public boolean requiresOp()
	{
		return requiresOp;
	}
	
	/**
	 * Check whether the entity has permission to execute this command.
	 * @param sender The CommandSender to check permission on
	 * @return True if the sender has permission to run this command
	 */
	public boolean checkPermission(CommandSender sender)
	{
		return !requiresOp() || sender.isOp();
	}
	
	public void sendUsage(CommandSender sender)
	{
		sender.sendMessage(ChatColor.LIGHT_GREEN + "---- Usage of " + ChatColor.LIGHT_YELLOW + getName() + ChatColor.LIGHT_GREEN + " ----");
		for (String str : usage)
		{
			sender.sendMessage(new MessageBuilder()
					.startHoverText("Click to fill command")
					.addFillCommand(ChatColor.LIGHT_YELLOW + str, getName()).build());
		}
		sender.sendMessage(ChatColor.UTILITY + "Description: " + getDescription());
		if (!aliases.isEmpty())
		{
			sender.sendMessage(ChatColor.LIGHT_BLUE + "Aliases: " + aliases.stream()
					.reduce("", (s1, s2) -> s1 + (!s1.equals("") ? " | " : "") + s2, (s1, s2) -> s1 + " | " + s2));
		}
	}
	
	public void sendInfo(CommandSender sender)
	{
		sender.sendMessage(new MessageBuilder().setCategory("help")
				.startHoverText("Click for more info")
				.addCommand(ChatColor.LIGHT_YELLOW + "/" + getName() + ChatColor.WHITE + ": " +
						ChatColor.UTILITY + getDescription(), "help " + getName()).build());
	}
	
	protected boolean verifyInt(String str)
	{
		try
		{
			Integer.parseInt(str);
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
	
	protected int parseInt(String str)
	{
		return Integer.parseInt(str);
	}
	
	protected boolean verifyDouble(String str)
	{
		try
		{
			Double.parseDouble(str);
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
	
	protected double parseDouble(String str)
	{
		return Double.parseDouble(str);
	}
	
	protected boolean verifyBoolean(String str)
	{
		return str.equalsIgnoreCase("true") || str.equalsIgnoreCase("false");
	}
	
	protected boolean parseBoolean(String str)
	{
		return Boolean.parseBoolean(str);
	}
	
	protected String getFullStringArgument(String[] args, int start)
	{
		String str = "";
		if (args.length > start)
		{
			str = args[start];
			for (int i = start + 1 ; i < args.length ; i++)
			{
				str += " " + args[i];
			}
		}
		return str;
	}
	
	protected String[] getQuotedArguments(String[] args)
	{
		
		return null;
	}
	
	protected MDServer getServer()
	{
		return MDServer.getInstance();
	}
	
	public abstract void executeCommand(CommandSender sender, String[] args);
}
