package oldmana.md.client;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.Arrays;
import java.util.List;

public class Main
{
	public static void main(String[] args)
	{
		List<String> argsList = Arrays.asList(args);
		if (!argsList.contains("noj2dprops"))
		{
			System.setProperty("sun.java2d.transaccel", "true");
			System.setProperty("sun.java2d.d3d", "false");
			System.setProperty("sun.java2d.ddforcevram", "true");
			System.setProperty("sun.java2d.opengl", "true");
			//System.setProperty("sun.java2d.accthreshold", "0");
		}
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
