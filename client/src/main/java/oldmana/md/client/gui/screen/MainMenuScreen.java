package oldmana.md.client.gui.screen;

import java.awt.Container;
import java.awt.Font;

import javax.swing.JTextField;

import oldmana.md.client.MDClient;
import oldmana.md.client.Settings;
import oldmana.md.client.gui.LayoutAdapter;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDComponent;
import oldmana.md.client.gui.component.MDText;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter.Alignment;
import oldmana.md.net.NetHandler;
import oldmana.md.net.packet.client.PacketInitiateLogin;

public class MainMenuScreen extends MDComponent
{
	private MDText ipText;
	private MDText idText;
	private JTextField ip;
	private JTextField nameField;
	
	private MDText status;
	
	private MDButton login;
	
	// Dev mode components
	private MDText saltText;
	private JTextField salt;
	
	public MainMenuScreen()
	{
		MDClient client = MDClient.getInstance();
		Settings settings = client.getSettings();
		
		if (client.isDevMode())
		{
			saltText = new MDText("Salt:");
			saltText.setFontSize(30);
			salt = new JTextField(settings.has("lastSalt") ? settings.getString("lastSalt") : "")
			{
				@Override
				public void revalidate() {}
			};
			
			add(saltText);
			add(salt);
		}
		
		ipText = new MDText("IP");
		ipText.setFontSize(30);
		ip = new JTextField(settings.getString("lastIP"))
		{
			@Override
			public void revalidate() {} // This is why I gotta make my own components and/or API..
		};
		ip.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, 30));
		idText = new MDText("Name");
		idText.setFontSize(30);
		nameField = new JTextField(settings.getString("lastName"))
		{
			@Override
			public void revalidate() {} // This is why I gotta make my own components and/or API..
		};
		nameField.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, 30));
		
		status = new MDText("");
		status.setHorizontalAlignment(Alignment.CENTER);
		status.setFontSize(30);
		
		login = new MDButton("Login");
		login.setFontSize(30);
		login.addClickListener(() ->
		{
			if (nameField.getText().isEmpty())
			{
				status.setText("Name required");
				status.repaint();
				return;
			}
			
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
			settings.put("lastIP", ip.getText());
			try
			{
				String name = nameField.getText();
				settings.put("lastName", name);
				if (salt != null)
				{
					settings.put("lastSalt", salt.getText());
				}
				settings.saveSettings();
				client.sendPacket(new PacketInitiateLogin(NetHandler.PROTOCOL_VERSION));
				client.getWindow().displayTable();
			}
			catch (NumberFormatException e)
			{
				status.setText("Invalid ID");
				status.repaint();
			}
		});
		
		
		add(ipText);
		add(ip);
		add(idText);
		add(nameField);
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
			nameField.setSize(ip.getSize());
			nameField.setLocation(idText.getX(), idText.getMaxY() + scale(5));
			nameField.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, scale(30)));
			
			status.setSize(ipText.getSize());
			status.setLocation(ipText.getX(), nameField.getY() + nameField.getHeight() + scale(40));
			
			
			login.setSize(scale(200), scale(40));
			login.setLocation((getWidth() / 2) - scale(100), status.getMaxY() + scale(20));
			
			if (saltText != null)
			{
				saltText.setSize(ipText.getSize());
				saltText.setLocation(ipText.getX(), login.getY() + login.getHeight() + scale(35));
				salt.setLocation(saltText.getX() + scale(55), saltText.getY() - scale(5));
				salt.setSize(scale(200), scale(40));
				salt.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, scale(30)));
			}
		}
		
		@Override
		public void invalidateLayout(Container container)
		{
			layoutContainer(container);
		}
	}
}
