package oldmana.md.server;

public class Main
{
	public static void main(String[] args) throws InterruptedException
	{
		new Thread(() -> new MDServer().startServer(), "Server Thread").start();
	}
}
