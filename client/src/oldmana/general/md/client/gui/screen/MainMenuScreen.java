package oldmana.general.md.client.gui.screen;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import oldmana.general.md.client.MDClient;
import oldmana.general.md.client.Settings;
import oldmana.general.md.client.gui.MDFrame;
import oldmana.general.md.client.gui.component.MDButton;
import oldmana.general.md.client.gui.util.GraphicsUtils;
import oldmana.general.md.client.net.NetClientHandler;
import oldmana.general.md.net.packet.client.PacketLogin;

public class MainMenuScreen extends JLayeredPane
{
	public MainMenuScreen()
	{
		MDClient client = MDClient.getInstance();
		Settings settings = client.getSettings();
		setSize(new Dimension(600, 400));
		JLabel ipLabel = new JLabel("IP");
		ipLabel.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, 30));
		ipLabel.setBounds(100, 10, 400, 40);
		JTextField ip = new JTextField(settings.getSetting("Last-IP"));
		ip.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, 30));
		ip.setBounds(100, 50, 400, 40);
		JLabel idLabel = new JLabel("User ID");
		idLabel.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, 30));
		idLabel.setBounds(100, 110, 400, 40);
		JTextField id = new JTextField(settings.getSetting("Last-ID"));
		id.setBounds(100, 150, 400, 40);
		id.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, 30));
		
		JLabel status = new JLabel();
		status.setHorizontalAlignment(SwingConstants.CENTER);
		status.setBounds(0, 240, getWidth(), 40);
		status.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, 30));
		
		MDButton login = new MDButton("Login");
		login.setBounds(200, 300, 200, 40);
		login.setFontSize(30);
		login.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent event)
			{
				MDClient client = MDClient.getInstance();
				String[] ipPort = ip.getText().split(":");
				status.setText("Connecting...");
				status.paintImmediately(status.getVisibleRect());
				try
				{
					client.connectToServer(ipPort[0], Integer.parseInt(ipPort[1]));
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					status.setText("Connection failed");
					e.printStackTrace();
					return;
				}
				settings.setSetting("Last-IP", ip.getText());
				settings.setSetting("Last-ID", id.getText());
				settings.saveSettings();
				client.sendPacket(new PacketLogin(NetClientHandler.PROTOCOL_VERSION, Integer.parseInt(id.getText())));
				MDFrame frame = client.getWindow();
				frame.getContentPane().setPreferredSize(new Dimension(1600, 900));
				frame.pack();
				frame.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - frame.getWidth()) / 2, 
						(int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - frame.getHeight()) / 2);
				frame.remove(MainMenuScreen.this);
				frame.add(client.getTableScreen());
			}
		});
		
		
		add(ipLabel);
		add(ip);
		add(idLabel);
		add(id);
		add(login);
		
		add(status);
	}
}
