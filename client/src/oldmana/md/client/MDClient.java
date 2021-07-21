package oldmana.md.client;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.Timer;

import javafx.embed.swing.JFXPanel;
import oldmana.general.mjnetworkingapi.MJConnection;
import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.client.MDScheduler.MDTask;
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
import oldmana.md.net.packet.client.action.PacketActionDraw;

public class MDClient
{
	private static MDClient instance;
	
	public static final String VERSION = "0.6.2 Dev";
	
	private MDFrame window;
	
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
	
	public boolean debugEnabled = false;
	
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
		}
		
		settings = new Settings();
		settings.loadSettings();
		
		MDSoundSystem.loadCache();
		
		scheduler = new MDScheduler();
		scheduler.scheduleTask(new MDTask(1, true)
		{
			@Override
			public void run()
			{
				eventQueue.tick();
			}
		});
		scheduler.scheduleTask(new MDTask(60, true)
		{
			@Override
			public void run()
			{
				if (++timeSincePing > 50)
				{
					getTableScreen().getTopbar().setText("Disconnected: Timed out");
					getTableScreen().getTopbar().repaint();
					connection.close();
				}
			}
		});
		/*
		scheduler.scheduleTask(new MDTask(60, true)
		{
			{
				GraphicsUtils.SCALE = 0.75;
			}
			boolean growing = true;
			@Override
			public void run()
			{
				if (growing)
				{
					GraphicsUtils.SCALE += 0.01;
					if (GraphicsUtils.SCALE >= 1.6)
					{
						growing = false;
					}
				}
				else
				{
					GraphicsUtils.SCALE -= 0.01;
					if (GraphicsUtils.SCALE <= 0.5)
					{
						growing = true;
					}
				}
				getTableScreen().invalidate();
				getTableScreen().repaint();
			}
		});
		*/
		
		eventQueue = new MDEventQueue();
		
		netHandler = new NetClientHandler(this);
		netHandler.registerPackets();
		
		gameState = new GameState();
		
		window = new MDFrame();
		//window.setVisible(true);
		
		
		Timer timer = new Timer(50, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				tickClient();
			}
		});
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
		for (Player player : getAllPlayers())
		{
			for (int id : ids)
			{
				if (player.getID() == id)
				{
					players.add(player);
					break;
				}
			}
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
		if (state instanceof ActionStateDraw && state.getActionOwner() == getThePlayer())
		{
			return true;
		}
		return false;
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
		this.awaitingResponse = awaitingResponse;
		if (awaitingResponse)
		{
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
		return debugEnabled;
	}
	
	public void setDebugEnabled(boolean debug)
	{
		debugEnabled = debug;
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
		getTableScreen().remove(component);
	}
	
	public static MDClient getInstance()
	{
		return instance;
	}
}
