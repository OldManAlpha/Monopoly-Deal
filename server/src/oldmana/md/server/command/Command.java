package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

public abstract class Command
{
	private String name;
	private String[] aliases;
	private String[] usage;
	
	private boolean requiresOp;
	
	public Command(String name, String[] aliases, String[] usage, boolean requiresOp)
	{
		this.name = name;
		this.aliases = aliases != null ? aliases : new String[] {};
		this.usage = usage != null ? usage : new String[] {"No usage info."};
		
		this.requiresOp = requiresOp;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String[] getAliases()
	{
		return aliases;
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
	
	public void sendUsage(CommandSender sender)
	{
		for (String str : usage)
		{
			sender.sendMessage(str);
		}
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
	
	protected boolean verifyBoolean(String str)
	{
		try
		{
			Boolean.parseBoolean(str);
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
	
	public abstract void executeCommand(CommandSender sender, String[] args);
}
