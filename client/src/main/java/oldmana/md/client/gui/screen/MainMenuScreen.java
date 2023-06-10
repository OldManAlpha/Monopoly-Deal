package oldmana.md.client.gui.screen;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import oldmana.md.client.MDClient;
import oldmana.md.client.Settings;
import oldmana.md.client.gui.LayoutAdapter;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDComponent;
import oldmana.md.client.gui.component.MDText;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter.Alignment;
import oldmana.md.client.net.ServerConnection;
import oldmana.md.net.NetHandler;
import oldmana.md.net.packet.client.PacketInitiateLogin;
import oldmana.md.server.MDServer;
import oldmana.md.server.net.DirectClient;

public class MainMenuScreen extends MDComponent
{
	private MDText spText;
	private MDText botCountText;
	private JComboBox<Integer> botCountSelect;
	private MDButton play;
	
	private MDText mpText;
	private MDText ipText;
	private JTextField ipField;
	
	
	private MDText nameText;
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
			saltText = new MDText("Salt");
			saltText.setFontSize(30);
			saltText.setHorizontalAlignment(Alignment.CENTER);
			salt = new JTextField(settings.has("lastSalt") ? settings.getString("lastSalt") : "")
			{
				@Override
				public void revalidate() {}
			};
			
			add(saltText);
			add(salt);
		}
		
		spText = new MDText("Singleplayer");
		spText.setFontSize(32);
		spText.setBold(true);
		spText.setHorizontalAlignment(Alignment.CENTER);
		
		botCountText = new MDText("Number of Bots");
		botCountText.setFontSize(30);
		botCountSelect = new JComboBox<Integer>(new Integer[] {1, 2, 3, 4, 5})
		{
			@Override
			public void revalidate() {}
		};
		botCountSelect.setSelectedItem(2);
		
		play = new MDButton("Play");
		play.setFontSize(30);
		play.setListener(() ->
		{
			status.setText("Starting internal server...");
			status.paintImmediately(status.getVisibleRect());
			
			File folder = new File(client.getDataFolder(), "clientserver");
			if (!folder.exists())
			{
				folder.mkdirs();
			}
			
			MDServer server = new MDServer(folder);
			server.startServer();
			getClient().setIntegratedServer(server);
			
			while (!server.isRunning())
			{
				try
				{
					Thread.sleep(1);
				}
				catch (InterruptedException e) {}
			}
			
			String playerName = nameField.getText();
			
			settings.put("lastName", playerName);
			settings.saveSettings();
			
			int botCount = (int) botCountSelect.getSelectedItem();
			List<String> names = Arrays.asList("Jimmy", "Chuck", "Howard", "Kim", "Mike", "Gus", "Marie", "Clifford",
					"Huell", "Hector", "Walter", "Skyler", "Jesse", "Bill", "Hank");
			Collections.shuffle(names);
			
			status.setText("");
			try
			{
				DirectClient direct = new DirectClient();
				client.setServerConnection(new ServerConnection(direct));
				CompletableFuture.runAsync(() -> server.addClient(direct), server.getSyncExecutor()).get();
				server.getScheduler().scheduleTask(10, () ->
				{
					server.getPlayerByName(playerName).setOp(true);
					names.stream().filter(name -> !name.equalsIgnoreCase(playerName)).limit(botCount).forEach(name ->
					{
						server.getCommandHandler().executeCommand(server.getConsoleSender(), "addbot " + name);
					});
					server.getCommandHandler().executeCommand(server.getConsoleSender(), "start");
				});
				client.sendPacket(new PacketInitiateLogin(NetHandler.PROTOCOL_VERSION));
				client.getWindow().displayTable();
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		});
		
		mpText = new MDText("Multiplayer");
		mpText.setFontSize(32);
		mpText.setBold(true);
		mpText.setHorizontalAlignment(Alignment.CENTER);
		
		ipText = new MDText("IP Address");
		ipText.setFontSize(30);
		ipField = new JTextField(settings.getString("lastIP"))
		{
			@Override
			public void revalidate() {}
		};
		ipField.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, 30));
		
		status = new MDText("");
		status.setBold(true);
		status.setHorizontalAlignment(Alignment.CENTER);
		status.setFontSize(30);
		
		login = new MDButton("Connect");
		login.setFontSize(30);
		login.addClickListener(() ->
		{
			if (nameField.getText().isEmpty())
			{
				status.setText("Name required");
				status.repaint();
				return;
			}
			
			if (ipField.getText().isEmpty())
			{
				return;
			}
			
			String[] ipPort = ipField.getText().split(":");
			status.setText("Connecting...");
			status.paintImmediately(status.getVisibleRect());
			try
			{
				client.connectToServer(ipPort[0], ipPort.length > 1 ? Integer.parseInt(ipPort[1]) : 27599);
			}
			catch (Exception e)
			{
				status.setText("Connection failed");
				status.repaint();
				e.printStackTrace();
				return;
			}
			status.setText("");
			settings.put("lastIP", ipField.getText());
			String name = nameField.getText();
			settings.put("lastName", name);
			if (salt != null)
			{
				settings.put("lastSalt", salt.getText());
			}
			settings.saveSettings();
			client.sendPacket(new PacketInitiateLogin(NetHandler.PROTOCOL_VERSION));
			client.getWindow().displayTable();
		});
		
		nameText = new MDText("Your Name");
		nameText.setFontSize(30);
		nameText.setHorizontalAlignment(Alignment.CENTER);
		nameField = new JTextField(settings.getString("lastName"))
		{
			@Override
			public void revalidate() {}
		};
		nameField.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, 30));
		nameField.setHorizontalAlignment(JTextField.CENTER);
		
		add(spText);
		add(botCountText);
		add(botCountSelect);
		add(play);
		add(mpText);
		add(ipText);
		add(ipField);
		add(nameText);
		add(nameField);
		add(login);
		
		add(status);
		
		setLayout(new MainMenuLayout());
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setColor(Color.GRAY);
		int y = getHeight() - scale(180);
		g.drawLine(getWidth() / 2, 0, getWidth() / 2, y);
		g.drawLine(0, y, getWidth(), y);
	}
	
	public class MainMenuLayout extends LayoutAdapter
	{
		@Override
		public void layoutContainer(Container container)
		{
			spText.setSize(scale(300), scale(32));
			spText.setLocationCentered(getWidth() / 4, scale(30));
			
			botCountText.setSize((getWidth() / 2) - scale(40), scale(30));
			botCountText.setLocation(scale(20), spText.getY() + scale(40));
			
			botCountSelect.setSize((getWidth() / 2) - scale(40), scale(40));
			botCountSelect.setLocation(botCountText.getX(), botCountText.getMaxY() + scale(5));
			botCountSelect.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, scale(30)));
			
			play.setSize(getWidth() / 4, scale(40));
			play.setLocationCenterX(botCountSelect.getX() + (botCountSelect.getWidth() / 2), botCountSelect.getY() + botCountSelect.getHeight() + scale(20));
			
			mpText.setSize(scale(300), scale(32));
			mpText.setLocationCentered(getWidth() * 0.75, scale(30));
			
			ipText.setSize((getWidth() / 2) - scale(40), scale(30));
			ipText.setLocation((getWidth() / 2) + scale(20), mpText.getY() + scale(40));
			
			ipField.setSize((getWidth() / 2) - scale(40), scale(40));
			ipField.setLocation(ipText.getX(), ipText.getMaxY() + scale(5));
			ipField.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, scale(30)));
			
			login.setSize(getWidth() / 4, scale(40));
			login.setLocationCenterX(ipField.getX() + (ipField.getWidth() / 2), ipField.getY() + ipField.getHeight() + scale(20));
			
			nameField.setSize(getWidth() / 3, scale(40));
			nameField.setLocation((getWidth() / 2) - (nameField.getWidth() / 2), getHeight() - scale(65));
			nameField.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, scale(30)));
			
			nameText.setSize(nameField.getWidth(), scale(30));
			nameText.setLocation(nameField.getX(), nameField.getY() - scale(35));
			
			status.setSize(getWidth(), scale(30));
			status.setLocationCentered(nameText.getCenterX(), nameText.getY() - scale(40));
			
			if (saltText != null)
			{
				int startX = nameText.getMaxX() + scale(10);
				
				saltText.setSize(getWidth() - startX - scale(40), scale(30));
				saltText.setLocation(startX, nameText.getY());
				salt.setLocation(saltText.getX(), saltText.getY() + scale(35));
				salt.setSize(saltText.getWidth(), scale(40));
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
