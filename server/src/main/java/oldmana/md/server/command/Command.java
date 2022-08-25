package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.MDServer;

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
