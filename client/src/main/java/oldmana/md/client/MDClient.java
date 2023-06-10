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
import oldmana.md.common.net.api.MJConnection;
import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.client.EventQueue.CardMove;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardProperty.PropertyColor;
import oldmana.md.client.card.collection.CardCollection;
import oldmana.md.client.card.collection.Deck;
import oldmana.md.client.card.collection.DiscardPile;
import oldmana.md.client.card.collection.VoidCollection;
import oldmana.md.client.gui.MDFrame;
import oldmana.md.client.gui.component.collection.MDHand;
import oldmana.md.client.gui.screen.TableScreen;
import oldmana.md.client.net.ServerConnection;
import oldmana.md.client.net.NetClientHandler;
import oldmana.md.client.rules.GameRules;
import oldmana.md.client.state.ActionState;
import oldmana.md.client.state.ActionStateDiscard;
import oldmana.md.client.state.ActionStateDraw;
import oldmana.md.client.state.ActionStateFinishTurn;
import oldmana.md.client.state.ActionStatePlay;
import oldmana.md.client.state.GameState;
import oldmana.md.common.net.packet.client.PacketQuit;
import oldmana.md.common.net.packet.client.action.PacketActionDraw;
import oldmana.md.server.MDServer;

public class MDClient
{
	private static MDClient instance;
	
	public static final String VERSION = "0.7 Dev";
	
	private MDFrame window;
	
	private File dataFolder;
	
	private Settings settings;
	
	private Scheduler scheduler;
	
	private ThePlayer thePlayer;
	private List<Player> otherPlayers = new ArrayList<Player>();
	private List<Player> turnOrder = new ArrayList<Player>();
	private List<Player> othersOrdered = new ArrayList<Player>();
	
	private GameState gameState;
	
	private GameRules rules;
	
	private boolean awaitingResponse;
	
	private Deck deck;
	private DiscardPile discard;
	private VoidCollection voidCollection;
	
	private NetClientHandler netHandler;
	
	private ServerConnection connection;
	
	public EventQueue eventQueue;
	
	public int timeSincePing;
	
	
	private MDServer integratedServer;
	
	
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
		
		scheduler = new Scheduler();
		scheduler.scheduleFrameboundTask(task -> eventQueue.tick());
		scheduler.scheduleTask(task ->
		{
			if (connection != null && connection.isAlive())
			{
				if (++timeSincePing > 50)
				{
					getTableScreen().getTopbar().setText("Disconnected: Timed out");
					getTableScreen().getTopbar().repaint();
					connection.closeGracefully();
				}
			}
		}, 1000, true);
		
		eventQueue = new EventQueue();
		
		netHandler = new NetClientHandler(this);
		
		gameState = new GameState();
		
		rules = new GameRules();
		
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
			SoundSystem.loadCache();
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
	
	public Scheduler getScheduler()
	{
		return scheduler;
	}
	
	public EventQueue getEventQueue()
	{
		return eventQueue;
	}
	
	public ServerConnection getServerConnection()
	{
		return connection;
	}
	
	public void setServerConnection(ServerConnection connection)
	{
		this.connection = connection;
	}
	
	public void connectToServer(String ip, int port) throws Exception
	{
		MJConnection connect = new MJConnection();
		connect.connect(ip, port, 5000);
		connection = new ServerConnection(connect);
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
	
	public List<Player> getTurnOrder()
	{
		return turnOrder;
	}
	
	public void setTurnOrder(List<Player> order)
	{
		turnOrder = order;
		othersOrdered.clear();
		int selfIndex = turnOrder.indexOf(getThePlayer());
		for (int i = 1 ; i < turnOrder.size() ; i++)
		{
			othersOrdered.add(turnOrder.get((selfIndex + i) % turnOrder.size()));
		}
		getTableScreen().getOpponents().invalidate();
		getTableScreen().getOpponents().repaint();
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
		List<Player> players = new ArrayList<Player>(ids.length);
		for (int id : ids)
		{
			players.add(getPlayerByID(id));
		}
		return players;
	}
	
	public void addPlayer(Player player)
	{
		otherPlayers.add(player);
		turnOrder.add(player);
		othersOrdered.add(player);
		getTableScreen().addPlayer(player);
	}
	
	public void destroyPlayer(Player player)
	{
		otherPlayers.remove(player);
		turnOrder.remove(player);
		getTableScreen().removePlayer(player);
		getTableScreen().repaint();
	}
	
	public List<Player> getOtherPlayersOrdered()
	{
		return othersOrdered;
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
		turnOrder.add(thePlayer);
		getTableScreen().addPlayer(thePlayer);
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
	
	public GameRules getRules()
	{
		return rules;
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
		if (hasIntegratedServer())
		{
			getIntegratedServer().shutdown();
			getTableScreen().getTopbar().setText("Shutting down internal server..");
			getTableScreen().paintImmediately(getTableScreen().getVisibleRect());
			getIntegratedServer().waitForShutdown();
			setIntegratedServer(null);
		}
		if (!closing)
		{
			resetGame();
			
			timeSincePing = 0;
			
			getWindow().displayMenu();
		}
	}
	
	public void resetGame()
	{
		getGameState().cleanup();
		
		System.out.println(getGameState().getActionState());
		System.out.println(getGameState().getPlayerTurn());
		
		if (eventQueue.getCurrentTask() instanceof CardMove)
		{
			removeTableComponent(((CardMove) eventQueue.getCurrentTask()).getComponent());
		}
		eventQueue.clearTasks();
		
		awaitingResponse = false;
		getTableScreen().getDeck().reset();
		getTableScreen().getDiscardPile().reset();
		getTableScreen().getVoidCollection().reset();
		getTableScreen().getHand().reset();
		deck = null;
		discard = null;
		voidCollection = null;
		
		for (Player p : new ArrayList<Player>(getOtherPlayers()))
		{
			destroyPlayer(p);
		}
		if (thePlayer != null)
		{
			getTableScreen().removePlayer(thePlayer);
		}
		thePlayer = null;
		otherPlayers.clear();
		turnOrder.clear();
		othersOrdered.clear();
		
		Card.getRegisteredCards().clear();
		CardCollection.getRegisteredCardCollections().clear();
		PropertyColor.clearColors();
	}
	
	public boolean hasIntegratedServer()
	{
		return integratedServer != null;
	}
	
	public MDServer getIntegratedServer()
	{
		return integratedServer;
	}
	
	public void setIntegratedServer(MDServer server)
	{
		integratedServer = server;
	}
	
	public static MDClient getInstance()
	{
		return instance;
	}
}
