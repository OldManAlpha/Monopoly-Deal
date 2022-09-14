package oldmana.md.client;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.Timer;

import javafx.embed.swing.JFXPanel;
import oldmana.general.mjnetworkingapi.MJConnection;
import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.client.MDEventQueue.CardMove;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardProperty.PropertyColor;
import oldmana.md.client.card.collection.CardCollection;
import oldmana.md.client.card.collection.Deck;
import oldmana.md.client.card.collection.DiscardPile;
import oldmana.md.client.card.collection.VoidCollection;
import oldmana.md.client.gui.MDFrame;
import oldmana.md.client.gui.component.collection.MDHand;
import oldmana.md.client.gui.screen.TableScreen;
import oldmana.md.client.net.ConnectionThread;
import oldmana.md.client.net.NetClientHandler;
import oldmana.md.client.state.ActionState;
import oldmana.md.client.state.ActionStateDiscard;
import oldmana.md.client.state.ActionStateDraw;
import oldmana.md.client.state.ActionStateFinishTurn;
import oldmana.md.client.state.ActionStatePlay;
import oldmana.md.client.state.GameState;
import oldmana.md.net.packet.client.PacketQuit;
import oldmana.md.net.packet.client.action.PacketActionDraw;

public class MDClient
{
	private static MDClient instance;
	
	public static final String VERSION = "0.6.5";
	
	private MDFrame window;
	
	private File dataFolder;
	
	private Settings settings;
	
	private MDScheduler scheduler;
	
	private ThePlayer thePlayer;
	private List<Player> otherPlayers = new ArrayList<Player>(3);
	
	private GameState gameState;
	
	private boolean awaitingResponse;
	
	private Deck deck;
	private DiscardPile discard;
	private VoidCollection voidCollection;
	
	private NetClientHandler netHandler;
	
	private ConnectionThread connection;
	
	public MDEventQueue eventQueue;
	
	public int timeSincePing;
	
	private boolean debug = false;
	private boolean developerMode = false;
	
	public MDClient()
	{
		instance = this;
	}
	
	public void startClient()
	{
		new JFXPanel(); // I guess we'll do this for certain reasons
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try
		{
			Font f = Font.createFont(Font.TRUETYPE_FONT, MDClient.class.getResourceAsStream("/oldmana/md/client/gui/font/ITCKabelStd-Bold.otf"));
			ge.registerFont(f);
			Font f2 = Font.createFont(Font.TRUETYPE_FONT, MDClient.class.getResourceAsStream("/oldmana/md/client/gui/font/ITCKabelStd-Book.otf"));
			ge.registerFont(f2);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Could not load fonts");
		}
		
		scheduler = new MDScheduler();
		scheduler.scheduleFrameboundTask(task -> eventQueue.tick());
		scheduler.scheduleTask(task ->
		{
			if (connection != null && connection.isAlive())
			{
				if (++timeSincePing > 50)
				{
					getTableScreen().getTopbar().setText("Disconnected: Timed out");
					getTableScreen().getTopbar().repaint();
					connection.close();
				}
			}
		}, 1000, true);
		
		eventQueue = new MDEventQueue();
		
		netHandler = new NetClientHandler(this);
		
		gameState = new GameState();
		
		settings = new Settings();
		
		File folder = findUserData();
		
		if (folder == null)
		{
			// If there's a local runtime folder, then this must be installed
			if (new File("runtime").exists())
			{
				folder = getLocalFolder();
				folder.mkdirs();
				setDataFolder(folder);
				getSettings().setLocation(folder);
			}
		}
		
		if (folder != null)
		{
			dataFolder = folder;
			settings.loadSettings(folder);
			MDSoundSystem.loadCache();
		}
		scheduler.setFPS(settings.getInt("framerate"));
		
		developerMode = settings.getBoolean("developerMode");
		
		window = new MDFrame(folder == null);
		
		
		Timer timer = new Timer(50, event -> tickClient());
		timer.setRepeats(true);
		timer.start();
	}
	
	// RUNS ON SWING THREAD
	public void tickClient()
	{
		if (connection != null)
		{
			netHandler.processPackets(connection);
		}
	}
	
	public File getDataFolder()
	{
		return dataFolder;
	}
	
	public void setDataFolder(File folder)
	{
		dataFolder = folder;
	}
	
	public File findUserData()
	{
		File portable = getJarFolder();
		for (File f : portable.listFiles())
		{
			if (f.getName().equals("settings.dat"))
			{
				return portable;
			}
		}
		File local = getLocalFolder();
		if (local.exists())
		{
			return local;
		}
		return null;
	}
	
	public File getJarFolder()
	{
		try
		{
			return new File(MDClient.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public File getLocalFolder()
	{
		String dirPath;
		
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win"))
		{
			dirPath = System.getenv("AppData");
		}
		else
		{
			// lol who knows if this actually will work properly on linux/mac
			dirPath = System.getProperty("user.home");
			if (os.contains("mac"))
			{
				dirPath += "/Library/Application Support";
			}
		}
		return new File(dirPath, "Monopoly Deal");
	}
	
	public void loadSettings(File folder)
	{
		settings.loadSettings(new File(folder, "settings.dat"));
	}
	
	public Settings getSettings()
	{
		return settings;
	}
	
	public MDScheduler getScheduler()
	{
		return scheduler;
	}
	
	public MDEventQueue getEventQueue()
	{
		return eventQueue;
	}
	
	public void connectToServer(String ip, int port) throws Exception
	{
		MJConnection connect = new MJConnection();
		connect.connect(ip, port, 5000);
		connection = new ConnectionThread(connect);
		getTableScreen().getTopbar().setText("Logging in..");
	}
	
	public boolean isConnected()
	{
		return connection != null;
	}
	
	public void setDeck(Deck deck)
	{
		this.deck = deck;
		getTableScreen().setDeck(deck);
	}
	
	public Deck getDeck()
	{
		return deck;
	}
	
	public void setDiscardPile(DiscardPile discard)
	{
		this.discard = discard;
		getTableScreen().setDiscardPile(discard);
	}
	
	public DiscardPile getDiscardPile()
	{
		return discard;
	}
	
	public void setVoidCollection(VoidCollection voidCollection)
	{
		this.voidCollection = voidCollection;
		getTableScreen().setVoidCollection(voidCollection);
	}
	
	public VoidCollection getVoidCollection()
	{
		return voidCollection;
	}
	
	public List<Player> getAllPlayers()
	{
		List<Player> players = new ArrayList<Player>(otherPlayers);
		if (thePlayer != null)
		{
			players.add(thePlayer);
		}
		return players;
	}
	
	public List<Player> getOtherPlayers()
	{
		return otherPlayers;
	}
	
	public Player getPlayerByID(int id)
	{
		if (thePlayer.getID() == id)
		{
			return thePlayer;
		}
		for (Player player : otherPlayers)
		{
			if (player.getID() == id)
			{
				return player;
			}
		}
		return null;
	}
	
	public List<Player> getPlayersByIDs(int[] ids)
	{
		List<Player> players = new ArrayList<Player>();
		for (int id : ids)
		{
			players.add(getPlayerByID(id));
		}
		return players;
	}
	
	public void addPlayer(Player player)
	{
		otherPlayers.add(player);
		getTableScreen().add(player.getUI());
		getTableScreen().positionPlayers();
	}
	
	public void destroyPlayer(Player player)
	{
		otherPlayers.remove(player);
		getTableScreen().remove(player.getUI());
		getTableScreen().positionPlayers();
		getTableScreen().repaint();
	}
	
	public Player getThePlayer()
	{
		return thePlayer;
	}
	
	public boolean isThePlayersTurn()
	{
		return getGameState().getWhoseTurn() == thePlayer;
	}
	
	public boolean canPlayCard()
	{
		return isThePlayersTurn() && getGameState().getActionState() instanceof ActionStatePlay;
	}
	
	public boolean canActFreely()
	{
		ActionState state = getGameState().getActionState();
		return isThePlayersTurn() && (state instanceof ActionStatePlay || state instanceof ActionStateFinishTurn);
	}
	
	public boolean canModifySets()
	{
		ActionState state = getGameState().getActionState();
		return isThePlayersTurn() && (state instanceof ActionStatePlay || state instanceof ActionStateFinishTurn || state instanceof ActionStateDiscard);
	}
	
	public void createThePlayer(int id, String name)
	{
		thePlayer = new ThePlayer(this, id, name);
		window.getTableScreen().add(thePlayer.getUI());
		getTableScreen().positionPlayers();
	}
	
	public boolean canDraw()
	{
		ActionState state = getGameState().getActionState();
		return state instanceof ActionStateDraw && state.getActionOwner() == getThePlayer();
	}
	
	public void draw()
	{
		sendPacket(new PacketActionDraw());
		setAwaitingResponse(true);
	}
	
	public void sendPacket(Packet packet)
	{
		connection.addOutPacket(packet);
	}
	
	public GameState getGameState()
	{
		return gameState;
	}
	
	public void setAwaitingResponse(boolean awaitingResponse)
	{
		if (this.awaitingResponse != awaitingResponse)
		{
			this.awaitingResponse = awaitingResponse;
			((MDHand) getThePlayer().getHand().getUI()).removeOverlay();
		}
	}
	
	public boolean isAwaitingResponse()
	{
		return awaitingResponse;
	}
	
	public boolean isInputBlocked()
	{
		return eventQueue.hasTasks() || awaitingResponse || getGameState().getClientActionState() != null;
	}
	
	public boolean isDebugEnabled()
	{
		return debug;
	}
	
	public void setDebugEnabled(boolean debug)
	{
		this.debug = debug;
	}
	
	public boolean isDevMode()
	{
		return developerMode;
	}
	
	public MDFrame getWindow()
	{
		return window;
	}
	
	public TableScreen getTableScreen()
	{
		return window.getTableScreen();
	}
	
	public void addTableComponent(JComponent component, int layer)
	{
		getTableScreen().add(component, new Integer(layer));
	}
	
	public void removeTableComponent(JComponent component)
	{
		if (component != null)
		{
			getTableScreen().remove(component);
		}
	}
	
	public void removeTableComponents(List<? extends JComponent> components)
	{
		if (components != null)
		{
			for (JComponent component : components)
			{
				removeTableComponent(component);
			}
		}
	}
	
	public void removeTableComponents(JComponent... components)
	{
		for (JComponent component : components)
		{
			removeTableComponent(component);
		}
	}
	
	public void disconnect()
	{
		disconnect(null);
	}
	
	public void disconnect(String reason)
	{
		disconnect(reason, false);
	}
	
	public void disconnect(String reason, boolean closing)
	{
		if (connection != null)
		{
			sendPacket(new PacketQuit(reason));
			while ((connection.hasOutPackets() || connection.isSendingPackets()) && connection.isAlive())
			{
				try
				{
					Thread.sleep(1);
				}
				catch (InterruptedException e) {}
			}
			connection.close();
		}
		if (!closing)
		{
			getGameState().setActionState(null);
			
			if (eventQueue.getCurrentTask() instanceof CardMove)
			{
				removeTableComponent(((CardMove) eventQueue.getCurrentTask()).getComponent());
			}
			eventQueue.clearTasks();
			
			awaitingResponse = false;
			getTableScreen().getDeck().setCollection(null);
			getTableScreen().getDiscardPile().setCollection(null);
			getTableScreen().getVoidCollection().setCollection(null);
			getTableScreen().getHand().setCollection(null);
			deck = null;
			discard = null;
			voidCollection = null;
			
			for (Player p : getAllPlayers())
			{
				removeTableComponent(p.getUI());
			}
			thePlayer = null;
			otherPlayers.clear();
			
			timeSincePing = 0;
			
			Card.getRegisteredCards().clear();
			CardCollection.getRegisteredCardCollections().clear();
			PropertyColor.clearColors();
			
			getWindow().displayMenu();
		}
	}
	
	public static MDClient getInstance()
	{
		return instance;
	}
}
