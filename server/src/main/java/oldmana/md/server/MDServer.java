package oldmana.md.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardMoney;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.CardRegistry;
import oldmana.md.server.card.collection.deck.CustomDeck.DeckLoadFailureException;
import oldmana.md.server.card.CardType;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardDescription;
import oldmana.md.net.packet.server.PacketDestroyPlayer;
import oldmana.md.net.packet.server.PacketKick;
import oldmana.md.net.packet.server.PacketPlaySound;
import oldmana.md.net.packet.server.PacketPlayerStatus;
import oldmana.md.net.packet.server.PacketSoundData;
import oldmana.md.net.packet.server.PacketStatus;
import oldmana.md.net.packet.universal.PacketChat;
import oldmana.md.net.packet.universal.PacketKeepConnected;
import oldmana.md.server.MDScheduler.MDTask;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.Card.CardDescription;
import oldmana.md.server.card.PropertyColor;
import oldmana.md.server.card.action.*;
import oldmana.md.server.card.collection.Deck;
import oldmana.md.server.card.collection.DiscardPile;
import oldmana.md.server.card.collection.VoidCollection;
import oldmana.md.server.card.collection.deck.CustomDeck;
import oldmana.md.server.card.collection.deck.DeckStack;
import oldmana.md.server.card.collection.deck.VanillaDeck;
import oldmana.md.server.event.EventManager;
import oldmana.md.server.event.PlayerRemovedEvent;
import oldmana.md.server.net.IncomingConnectionsThread;
import oldmana.md.server.net.NetServerHandler;
import oldmana.md.server.rules.GameRules;
import oldmana.md.server.rules.win.PropertySetCondition;
import oldmana.md.server.state.ActionState;
import oldmana.md.server.state.ActionStateDoNothing;
import oldmana.md.server.state.GameState;

public class MDServer
{
	private static MDServer instance;
	
	public static final String VERSION = "0.6.5 Dev";
	
	private File dataFolder;
	
	private List<MDMod> mods = new ArrayList<MDMod>();
	
	private ServerConfig config;
	private PlayerRegistry playerRegistry;
	
	private List<Client> newClients = new ArrayList<Client>();
	
	private List<Player> players = new ArrayList<Player>();
	
	private GameState gameState;
	
	private VoidCollection voidCollection;
	private Deck deck;
	private DiscardPile discardPile;
	
	private Map<String, DeckStack> decks = new HashMap<String, DeckStack>();
	
	private NetServerHandler netHandler = new NetServerHandler(this);
	
	private EventManager eventManager = new EventManager();
	private ChatLinkHandler linkHandler = new ChatLinkHandler();
	
	private Console consoleSender = new Console();
	private CommandHandler cmdHandler = new CommandHandler();

	private MDScheduler scheduler = new MDScheduler();

	private final List<String> cmdQueue = Collections.synchronizedList(new ArrayList<String>());

	private volatile boolean shutdown = false;

	private GameRules rules = new GameRules();

	private int tickCount;
	
	private boolean verbose;
	
	private Map<String, MDSound> sounds = new HashMap<String, MDSound>();
	
	private IncomingConnectionsThread threadIncConnect;
	
	public MDServer()
	{
		this(new File(System.getProperty("user.dir")));
	}
	
	public MDServer(File dataFolder)
	{
		instance = this;
		this.dataFolder = dataFolder;
		config = new ServerConfig();
		playerRegistry = new PlayerRegistry();
	}
	
	public void startServer()
	{
		System.setOut(new MDPrintStream(System.out));
		System.setErr(new MDPrintStream(System.err));
		
		System.out.println("Starting Monopoly Deal Server Version " + VERSION);
		
		registerDefaultCards();
		
		gameState = new GameState(this);
		gameState.setActionState(new ActionStateDoNothing());
		
		voidCollection = new VoidCollection();
		decks.put("vanilla", new VanillaDeck());
		deck = new Deck(decks.get("vanilla"));
		discardPile = new DiscardPile();
		config.loadConfig();
		verbose = config.getBoolean("verbose");
		int port = config.getInt("port");
		try
		{
			threadIncConnect = new IncomingConnectionsThread(port);
		}
		catch (IOException e)
		{
			System.out.println("Failed to bind port " + port);
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Using port " + port);
		rules.setWinCondition(new PropertySetCondition(3));
		cmdHandler.registerDefaultCommands();
		playerRegistry.loadPlayers();
		
		// Keep connected checker
		scheduler.scheduleTask(20, true, task ->
		{
			for (Player player : getPlayers())
			{
				if (player.isOnline())
				{
					if (!player.hasSentPing() && player.getLastPing() + (20 * 20) <= tickCount)
					{
						player.sendPacket(new PacketKeepConnected());
						player.setSentPing(true);
					}
					else if (player.getLastPing() + (40 * 20) <= tickCount)
					{
						player.setOnline(false);
						player.setSentPing(false);
						if (player.getNet() != null)
						{
							player.getNet().close();
							player.setNet(null);
						}
						System.out.println(player.getName() + " (ID: " + player.getID() + ") timed out");
					}
				}
			}
		});
		
		loadSounds();
		
		System.out.println("Loading Mods");
		try
		{
			loadMods();
		}
		catch (Exception | Error e)
		{
			System.out.println("Failed to load mods!");
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Loading Decks");
		loadDecks();
		
		Thread consoleReader = new Thread(() ->
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while (!shutdown)
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
		}, "Console Reader Thread");
		consoleReader.setDaemon(true);
		consoleReader.start();
		
		// AI ticking task
		getScheduler().scheduleTask(new MDTask(50, true)
		{
			@Override
			public void run()
			{
				for (Player player : getPlayers())
				{
					if (player.isBot())
					{
						player.doAIAction();
					}
				}
			}
		});
		
		System.out.println("Finished initialization");
		
		while (!shutdown)
		{
			tickServer();
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		broadcastPacket(new PacketKick("Server shut down"));
		threadIncConnect.interrupt();
		tickServer();
		while (true)
		{
			boolean hasPackets = false;
			for (Player player : getPlayers())
			{
				if (player.getNet() != null && player.getNet().hasOutPackets())
				{
					hasPackets = true;
					break;
				}
			}
			if (!hasPackets)
			{
				break;
			}
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("Server stopped");
		System.exit(0);
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
					player.setOnline(false);
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
		
		scheduler.runTick();
		
		gameState.tick();
		
		tickCount++;
	}
	
	public File getDataFolder()
	{
		return dataFolder;
	}
	
	public void shutdown()
	{
		shutdown = true;
	}
	
	public boolean isShuttingDown()
	{
		return shutdown;
	}
	
	public void loadMods()
	{
		File modsFolder = new File(getDataFolder(), "mods");
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
					URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {new URL("jar:file:" + f.getPath() + "!/")},
							getClass().getClassLoader());
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
	
	public void loadDecks()
	{
		File folder = new File(getDataFolder(), "decks");
		if (!folder.exists())
		{
			folder.mkdir();
		}
		if (!folder.isDirectory())
		{
			System.out.println("Failed to load decks. Non-directory file named decks in server folder?");
			return;
		}
		for (File f : folder.listFiles())
		{
			String name = f.getName();
			if (!f.isDirectory() && name.endsWith(".json"))
			{
				String deckName = f.getName().substring(0, name.length() - 5);
				try
				{
					decks.put(deckName, new CustomDeck(deckName, f));
				}
				catch (DeckLoadFailureException e)
				{
					System.out.println("Failed to load deck file " + name);
					e.printStackTrace();
				}
			}
		}
	}
	
	private void registerDefaultCards()
	{
		CardType.CARD = CardRegistry.registerCardType(Card.class);
		CardType.ACTION = CardRegistry.registerCardType(CardAction.class);
		CardType.MONEY = CardRegistry.registerCardType(CardMoney.class);
		CardType.PROPERTY = CardRegistry.registerCardType(CardProperty.class);
		//CardRegistry.registerCardType(CardBuilding.class); Soon(TM)
		
		CardType.DEAL_BREAKER = CardRegistry.registerCardType(CardActionDealBreaker.class);
		CardType.DEBT_COLLECTOR = CardRegistry.registerCardType(CardActionDebtCollector.class);
		CardType.DOUBLE_THE_RENT = CardRegistry.registerCardType(CardActionDoubleTheRent.class);
		CardType.FORCED_DEAL = CardRegistry.registerCardType(CardActionForcedDeal.class);
		CardType.ITS_MY_BIRTHDAY = CardRegistry.registerCardType(CardActionItsMyBirthday.class);
		CardType.JUST_SAY_NO = CardRegistry.registerCardType(CardActionJustSayNo.class);
		CardType.PASS_GO = CardRegistry.registerCardType(CardActionPassGo.class);
		CardType.RENT = CardRegistry.registerCardType(CardActionRent.class);
		CardType.SLY_DEAL = CardRegistry.registerCardType(CardActionSlyDeal.class);
	}
	
	public CommandHandler getCommandHandler()
	{
		return cmdHandler;
	}
	
	public EventManager getEventManager()
	{
		return eventManager;
	}
	
	public ChatLinkHandler getChatLinkHandler()
	{
		return linkHandler;
	}
	
	public MDScheduler getScheduler()
	{
		return scheduler;
	}
	
	public GameState getGameState()
	{
		return gameState;
	}
	
	
	public void setActionState(ActionState state)
	{
		gameState.setActionState(state);
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
		kickPlayer(player, "You've been kicked");
	}
	
	public void kickPlayer(Player player, String reason)
	{
		player.sendPacket(new PacketKick(reason));
		if (player.getNet() != null)
		{
			player.getNet().close();
			player.setNet(null);
		}
		players.remove(player);
		for (Player p : getPlayers())
		{
			p.removeButtons(player);
		}
		eventManager.callEvent(new PlayerRemovedEvent(player));
		broadcastPacket(new PacketDestroyPlayer(player.getID()));
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
	
	public Player getPlayerByName(String name)
	{
		name = name.toLowerCase();
		for (Player player : players)
		{
			if (player.getName().toLowerCase().equals(name))
			{
				return player;
			}
		}
		return null;
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
		File soundsFolder = new File(getDataFolder(), "sounds");
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
		loadSound(file, file.getName().substring(0, file.getName().length() - 4));
	}
	
	public void loadSound(File file, String name)
	{
		loadSound(file, name, false);
	}
	
	public void loadSound(File file, boolean sendPackets)
	{
		loadSound(file, file.getName().substring(0, file.getName().length() - 4), sendPackets);
	}
	
	public void loadSound(File file, String name, boolean sendPackets)
	{
		try
		{
			DigestInputStream is = new DigestInputStream(new FileInputStream(file), MessageDigest.getInstance("MD5"));
			byte[] data = new byte[is.available()];
			is.read(data);
			int hash = Arrays.hashCode(is.getMessageDigest().digest());
			MDSound sound = new MDSound(name, data, hash);
			sounds.put(name, sound);
			is.close();
			System.out.println("Loaded sound file: " + file.getName());
			if (sendPackets)
			{
				broadcastPacket(new PacketSoundData(sound.getName(), sound.getData(), sound.getHash()));
			}
		}
		catch (Exception e)
		{
			System.out.println("Error loading sound file: " + file.getName());
			e.printStackTrace();
		}
	}
	
	public boolean doesSoundExist(String name)
	{
		return sounds.containsKey(name);
	}
	
	public void playSound(String name)
	{
		broadcastPacket(new PacketPlaySound(name));
	}
	
	public void verifySounds(Player player, Map<String, Integer> cachedSounds)
	{
		Set<Entry<String, MDSound>> sounds = this.sounds.entrySet();
		int i = 1;
		for (Entry<String, MDSound> entry : sounds)
		{
			MDSound sound = entry.getValue();
			if (!cachedSounds.containsKey(sound.getName()) || sound.getHash() != cachedSounds.get(sound.getName()))
			{
				player.sendPacket(new PacketStatus("Downloading sound " + i + "/" + sounds.size() + ".."));
				player.sendPacket(new PacketSoundData(sound.getName(), sound.getData(), sound.getHash()));
			}
			i++;
		}
		getGameState().sendStatus(player);
	}
	
	public void refreshPlayer(Player player)
	{
		//player.sendPacket(new PacketRefresh());
		
		for (Player other : getPlayersExcluding(player))
		{
			player.sendPacket(other.getInfoPacket());
		}
		
		// Send card descriptions
		for (CardDescription desc : CardDescription.getAllDescriptions())
		{
			player.sendPacket(new PacketCardDescription(desc.getID(), desc.getText()));
		}
		
		// Send card colors
		player.sendPacket(PropertyColor.getColorsPacket());
		
		// Send all card data
		for (Card card : Card.getRegisteredCards().values())
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
		player.resendActionState();
		player.resendCardButtons();
		player.sendButtonPackets();
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
		broadcastMessage(message, false);
	}
	
	public void broadcastMessage(String message, Player exception)
	{
		broadcastMessage(message, exception, false);
	}
	
	public void broadcastMessage(String message, boolean printConsole)
	{
		broadcastPacket(new PacketChat(message));
		if (printConsole)
		{
			System.out.println(message);
		}
	}
	
	public void broadcastMessage(String message, Player exception, boolean printConsole)
	{
		broadcastPacket(new PacketChat(message), exception);
		if (printConsole)
		{
			System.out.println(message);
		}
	}
	
	public boolean isVerbose()
	{
		return verbose;
	}
	
	public static MDServer getInstance()
	{
		return instance;
	}
}
