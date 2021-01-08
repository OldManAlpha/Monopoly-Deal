package oldmana.md.server;

public interface CommandSender
{
	public void sendMessage(String message);
	
	public boolean isOp();
}
