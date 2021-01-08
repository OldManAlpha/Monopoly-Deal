package oldmana.md.client;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
				catch (Exception e) {}
				new MDClient().startClient();
			}
		});
	}
}
