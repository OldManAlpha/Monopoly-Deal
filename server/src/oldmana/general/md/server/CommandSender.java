package oldmana.general.md.server;

public interface CommandSender
{
	public void sendMessage(String message);
	
	public void sendMessage(String message, boolean printConsole);
}
