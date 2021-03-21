package oldmana.md.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardDescription;
import oldmana.md.net.packet.server.PacketPlaySound;
import oldmana.md.net.packet.server.PacketPlayerStatus;
import oldmana.md.net.packet.server.PacketSoundData;
import oldmana.md.net.packet.server.actionstate.PacketUpdateActionStateAccepted;
import oldmana.md.net.packet.server.actionstate.PacketUpdateActionStateRefusal;
import oldmana.md.net.packet.universal.PacketChat;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.Card.CardDescription;
import oldmana.md.server.card.action.CardActionItsMyBirthday;
import oldmana.md.server.card.action.CardActionDealBreaker;
import oldmana.md.server.card.action.CardActionDebtCollector;
import oldmana.md.server.card.action.CardActionDoubleTheRent;
import oldmana.md.server.card.action.CardActionForcedDeal;
import oldmana.md.server.card.action.CardActionPassGo;
import oldmana.md.server.card.action.CardActionJustSayNo;
import oldmana.md.server.card.action.CardActionSlyDeal;
import oldmana.md.server.card.collection.Deck;
import oldmana.md.server.card.collection.DiscardPile;
import oldmana.md.server.card.collection.VoidCollection;
import oldmana.md.server.card.collection.deck.DeckStack;
import oldmana.md.server.card.collection.deck.VanillaDeck;
import oldmana.md.server.command.CommandHandler;
import oldmana.md.server.event.EventManager;
import oldmana.md.server.net.IncomingConnectionsThread;
import oldmana.md.server.net.NetServerHandler;
import oldmana.md.server.state.ActionState;
import oldmana.md.server.state.ActionStateDoNothing;
import oldmana.md.server.state.GameState;

public class MDServer
{
	private static MDServer instance;
	
	public static final String VERSION = "0.6 PR4";
	
	private List<MDMod> mods = new ArrayList<MDMod>();
	
	private Map<String, Class<? extends Card>> actionCards = new HashMap<String, Class<? extends Card>>();
	
	private ServerConfig config = new ServerConfig();
	private PlayerRegistry playerRegistry = new PlayerRegistry();
	
	private List<Client> newClients = new ArrayList<Client>();
	
	private List<Player> players = new ArrayList<Player>();
	
	private GameState gameState;
	
	private VoidCollection voidCollection = new VoidCollection();
	private Deck deck;
	private DiscardPile discardPile;
	
	private Map<String, DeckStack> decks = new HashMap<String, DeckStack>();
	
	private NetServerHandler netHandler = new NetServerHandler(this);
	
	private EventManager eventManager = new EventManager();
	
	private Console consoleSender = new Console();
	private CommandHandler cmdHandler = new CommandHandler();
	
	private List<String> cmdQueue = Collections.synchronizedList(new ArrayList<String>());
	
	private boolean shutdown = false;
	
	private GameRules rules;
	
	private int tickCount;
	
	private Map<String, byte[]> soundMap = new HashMap<String, byte[]>();
	
	public MDServer()
	{
		instance = this;
	}
	
	public void startServer() throws Exception
	{
		System.out.println("Starting Monopoly Deal Server Version " + VERSION);
		rules = new GameRules();
		decks.put("vanilla", new VanillaDeck());
		deck = new Deck(decks.get("vanilla"));
		discardPile = new DiscardPile();
		netHandler.registerPackets();
		config.loadConfig();
		int port = Integer.parseInt(config.getSetting("Server-Port"));
		new IncomingConnectionsThread(port);
		System.out.println("Using port " + port);
		gameState = new GameState(this);
		gameState.setCurrentActionState(new ActionStateDoNothing());
		cmdHandler.registerDefaultCommands();
		playerRegistry.loadPlayers();
		registerDefaultActionCards();
		
		loadSounds();
		
		System.out.println("Loading Mods");
		loadMods();
		
		new Thread()
		{
			@Override
			public void run()
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				while (true)
				{
					try
					{
						String line = reader.readLine();
						synchronized (cmdQueue)
						{
							cmdQueue.add(line);
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}.start();
		System.out.println("Finished initialization");
		while (!shutdown)
		{
			tickServer();
			Thread.sleep(50);
		}
		System.out.println("Server has shut down");
	}
	
	public void tickServer()
	{
		List<Client> clients = new ArrayList<Client>(newClients);
		for (Client client : clients)
		{
			if (!client.isConnected())
			{
				newClients.remove(client);
				continue;
			}
			netHandler.processPackets(client);
		}
		
		// Process packets
		for (Player player : getPlayers())
		{
			if (player.isConnected())
			{
				if (player.getNet().isClosed())
				{
					player.setNet(null);
					player.setLoggedIn(false);
					broadcastPacket(new PacketPlayerStatus(player.getID(), false));
					continue;
				}
				netHandler.processPackets(player);
			}
		}
		
		// Process commands
		synchronized (cmdQueue)
		{
			for (String line : cmdQueue)
			{
				getCommandHandler().executeCommand(consoleSender, line);
			}
			cmdQueue.clear();
		}
		
		tickCount++;
	}
	
	public void loadMods()
	{
		File modsFolder = new File("mods");
		if (!modsFolder.exists())
		{
			modsFolder.mkdir();
		}
		for (File f : modsFolder.listFiles())
		{
			try
			{
				if (f.isFile() && f.getName().endsWith(".jar"))
				{
					List<Class<?>> classes = new ArrayList<Class<?>>();
					URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {new URL("jar:file:" + f.getPath() + "!/")});
					JarFile jar = new JarFile(f);
					Enumeration<JarEntry> entries = jar.entries();
					while (entries.hasMoreElements())
					{
						JarEntry e = entries.nextElement();
						if (e.isDirectory() || !e.getName().endsWith(".class"))
						{
							continue;
						}
						String className = e.getName().substring(0, e.getName().length() - 6).replace('/', '.');
						Class<?> clazz = classLoader.loadClass(className);
						classes.add(clazz);
					}
					jar.close();
					boolean hasModClass = false;
					for (Class<?> clazz : classes)
					{
						if (MDMod.class.isAssignableFrom(clazz))
						{
							MDMod mod = (MDMod) clazz.newInstance();
							System.out.println("Loading Mod: " + mod.getName());
							mod.onLoad();
							mods.add(mod);
							hasModClass = true;
							break;
						}
					}
					if (!hasModClass)
					{
						System.out.println(f.getName() + " is missing a mod class!");
					}
				}
			}
			catch (Exception e)
			{
				System.out.println("Error loading: " + f.getName());
				e.printStackTrace();
			}
		}
	}
	
	public List<MDMod> getMods()
	{
		return mods;
	}
	
	private void registerDefaultActionCards()
	{
		registerActionCard("DealBreaker", CardActionDealBreaker.class);
		registerActionCard("Go", CardActionPassGo.class);
		registerActionCard("ForcedDeal", CardActionForcedDeal.class);
		registerActionCard("SlyDeal", CardActionSlyDeal.class);
		registerActionCard("JustSayNo", CardActionJustSayNo.class);
		registerActionCard("DoubleTheRent", CardActionDoubleTheRent.class);
		registerActionCard("ItsMyBirthday", CardActionItsMyBirthday.class);
		registerActionCard("DebtCollector", CardActionDebtCollector.class);
	}
	
	public void registerActionCard(String name, Class<? extends Card> clazz)
	{
		actionCards.put(name.toLowerCase(), clazz);
	}
	
	public Class<? extends Card> getActionCardClass(String name)
	{
		return actionCards.get(name.toLowerCase());
	}
	
	public CommandHandler getCommandHandler()
	{
		return cmdHandler;
	}
	
	public EventManager getEventManager()
	{
		return eventManager;
	}
	
	public GameState getGameState()
	{
		return gameState;
	}
	
	public void setActionState(ActionState state)
	{
		gameState.setCurrentActionState(state);
	}
	
	public PlayerRegistry getPlayerRegistry()
	{
		return playerRegistry;
	}
	
	public Player getPlayerByUID(int uid)
	{
		for (Player player : getPlayers())
		{
			if (player.getUID() == uid)
			{
				return player;
			}
		}
		return null;
	}
	
	public boolean isPlayerWithUIDLoggedIn(int uid)
	{
		return getPlayerByUID(uid) != null;
	}
	
	public void addClient(Client client)
	{
		newClients.add(client);
	}
	
	public void removeClient(Client client)
	{
		newClients.remove(client);
	}
	
	public void disconnectClient(Client client)
	{
		client.getNet().close();
		removeClient(client);
	}
	
	public void addPlayer(Player player)
	{
		players.add(player);
	}
	
	public void kickPlayer(Player player)
	{
		if (player.getNet() != null)
		{
			player.getNet().close();
			player.setNet(null);
		}
		players.remove(player);
	}
	
	public List<Player> getPlayers()
	{
		return new ArrayList<Player>(players);
	}
	
	public List<Player> getPlayersExcluding(Player excluded)
	{
		List<Player> players = getPlayers();
		players.remove(excluded);
		return players;
	}
	
	public List<Player> getPlayersExcluding(List<Player> excluded)
	{
		List<Player> players = getPlayers();
		players.removeAll(excluded);
		return players;
	}
	
	public Player getPlayerByID(int id)
	{
		for (Player player : players)
		{
			if (player.getID() == id)
			{
				return player;
			}
		}
		return null;
	}
	
	public List<Player> findWinners()
	{
		List<Player> winners = new ArrayList<Player>();
		for (Player player : getPlayers())
		{
			if (player.getMonopolyCount() >= getGameRules().getMonopoliesRequiredToWin())
			{
				winners.add(player);
			}
		}
		return winners;
	}
	
	public GameRules getGameRules()
	{
		return rules;
	}
	
	public VoidCollection getVoidCollection()
	{
		return voidCollection;
	}
	
	public Deck getDeck()
	{
		return deck;
	}
	
	public DiscardPile getDiscardPile()
	{
		return discardPile;
	}
	
	public Map<String, DeckStack> getDeckStacks()
	{
		return decks;
	}
	
	public int getTickCount()
	{
		return tickCount;
	}
	
	public void loadSounds()
	{
		File soundsFolder = new File("sounds");
		if (!soundsFolder.exists())
		{
			soundsFolder.mkdir();
		}
		for (File f : soundsFolder.listFiles())
		{
			if (!f.isDirectory() && f.getName().endsWith(".wav"))
			{
				loadSound(f);
			}
		}
	}
	
	public void loadSound(File file)
	{
		try
		{
			BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
			byte[] data = new byte[is.available()];
			is.read(data);
			soundMap.put(file.getName().substring(0, file.getName().length() - 4), data);
			is.close();
			System.out.println("Loaded sound file: " + file.getName());
		}
		catch (Exception e)
		{
			System.out.println("Error loading sound file: " + file.getName());
			e.printStackTrace();
		}
	}
	
	public void playSound(String name)
	{
		broadcastPacket(new PacketPlaySound(name));
	}
	
	public void refreshPlayer(Player player)
	{
		//player.sendPacket(new PacketRefresh());
		
		for (Entry<String, byte[]> sound : soundMap.entrySet())
		{
			player.sendPacket(new PacketSoundData(sound.getKey(), sound.getValue()));
		}
		
		for (Player other : getPlayersExcluding(player))
		{
			player.sendPacket(other.getInfoPacket());
		}
		
		// Send card descriptions
		for (CardDescription desc : CardDescription.getAllDescriptions())
		{
			player.sendPacket(new PacketCardDescription(desc.getID(), desc.getText()));
		}
		
		// Send all card data
		for (Card card : Card.getRegisteredCards())
		{
			player.sendPacket(card.getCardDataPacket());
		}
		// Send void
		player.sendPacket(voidCollection.getCollectionDataPacket());
		// Send deck
		player.sendPacket(deck.getCollectionDataPacket());
		// Send discard pile
		player.sendPacket(discardPile.getCollectionDataPacket());
		// Send player data (including own)
		for (Player other : getPlayers())
		{
			for (Packet packet : other.getPropertySetPackets())
			{
				player.sendPacket(packet);
			}
			player.sendPacket(other.getBank().getCollectionDataPacket());
			player.sendPacket(player == other ? other.getHand().getOwnerHandDataPacket() : other.getHand().getCollectionDataPacket());
		}
		// Send action state
		ActionState state = getGameState().getCurrentActionState();
		if (state != null)
		{
			player.sendPacket(state.constructPacket());
			for (Player accepted : state.getAccepted())
			{
				player.sendPacket(new PacketUpdateActionStateAccepted(accepted.getID(), true));
			}
			for (Player refused : state.getRefused())
			{
				player.sendPacket(new PacketUpdateActionStateRefusal(refused.getID(), true));
			}
		}
		player.sendUndoStatus();
		getGameState().sendStatus(player);
	}
	
	public void broadcastPacket(Packet packet)
	{
		for (Player player : players)
		{
			player.sendPacket(packet);
		}
	}
	
	public void broadcastPacket(Packet packet, Player exception)
	{
		for (Player player : players)
		{
			if (player != exception)
			{
				player.sendPacket(packet);
			}
		}
	}
	
	public void broadcastMessage(String message)
	{
		broadcastPacket(new PacketChat(message));
	}
	
	public static MDServer getInstance()
	{
		return instance;
	}
}
