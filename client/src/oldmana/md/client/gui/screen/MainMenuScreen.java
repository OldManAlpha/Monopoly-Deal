package oldmana.md.client.gui.screen;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;

import oldmana.md.client.MDClient;
import oldmana.md.client.Settings;
import oldmana.md.client.gui.LayoutAdapter;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDComponent;
import oldmana.md.client.gui.component.MDText;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter.Alignment;
import oldmana.md.client.net.NetClientHandler;
import oldmana.md.net.packet.client.PacketLogin;

public class MainMenuScreen extends MDComponent
{
	private MDText ipText;
	private MDText idText;
	private JTextField ip;
	private JTextField id;
	
	private MDText status;
	
	private MDButton login;
	
	public MainMenuScreen()
	{
		MDClient client = MDClient.getInstance();
		Settings settings = client.getSettings();
		ipText = new MDText("IP");
		ipText.setFontSize(30);
		ip = new JTextField(settings.getSetting("Last-IP"))
		{
			@Override
			public void revalidate() {} // This is why I gotta make my own components and/or API..
		};
		ip.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, 30));
		idText = new MDText("User ID");
		idText.setFontSize(30);
		id = new JTextField(settings.getSetting("Last-ID"))
		{
			@Override
			public void revalidate() {} // This is why I gotta make my own components and/or API..
		};
		id.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, 30));
		
		status = new MDText("");
		status.setHorizontalAlignment(Alignment.CENTER);
		status.setFontSize(30);
		
		login = new MDButton("Login");
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
					status.setText("Connection failed");
					status.repaint();
					e.printStackTrace();
					return;
				}
				status.setText("");
				settings.setSetting("Last-IP", ip.getText());
				settings.setSetting("Last-ID", id.getText());
				settings.saveSettings();
				client.sendPacket(new PacketLogin(NetClientHandler.PROTOCOL_VERSION, Integer.parseInt(id.getText())));
				client.getWindow().displayTable();
			}
		});
		
		
		add(ipText);
		add(ip);
		add(idText);
		add(id);
		add(login);
		
		add(status);
		
		setLayout(new MainMenuLayout());
	}
	
	public class MainMenuLayout extends LayoutAdapter
	{
		@Override
		public void layoutContainer(Container container)
		{
			ipText.setSize(scale(400), scale(30));
			ipText.setLocation((getWidth() / 2) - scale(200), scale(15));
			ip.setSize(scale(400), scale(40));
			ip.setLocation(ipText.getX(), ipText.getMaxY() + scale(5));
			ip.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, scale(30)));
			
			idText.setSize(ipText.getSize());
			idText.setLocation(ipText.getX(), ip.getY() + ip.getHeight() + scale(25));
			id.setSize(ip.getSize());
			id.setLocation(idText.getX(), idText.getMaxY() + scale(5));
			id.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, scale(30)));
			
			status.setSize(ipText.getSize());
			status.setLocation(ipText.getX(), id.getY() + id.getHeight() + scale(40));
			
			
			login.setSize(scale(200), scale(40));
			login.setLocation((getWidth() / 2) - scale(100), status.getMaxY() + scale(20));
		}
		
		@Override
		public void invalidateLayout(Container container)
		{
			layoutContainer(container);
		}
	}
}
