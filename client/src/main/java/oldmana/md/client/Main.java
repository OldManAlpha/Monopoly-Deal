package oldmana.md.client;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main
{
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(() ->
		{
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch (Exception e) {}
			try
			{
				new MDClient().startClient();
			}
			catch (Exception | Error e)
			{
				System.err.println("Failed to start client!");
				e.printStackTrace();
				System.exit(1);
			}
		});
	}
}
