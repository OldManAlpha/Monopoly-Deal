package oldmana.general.md.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import oldmana.general.md.net.packet.server.PacketDestroyPlayer;
import oldmana.general.md.net.packet.server.PacketPlayerStatus;
import oldmana.general.md.net.packet.server.PacketRefresh;
import oldmana.general.md.net.packet.server.PacketStatus;
import oldmana.general.md.net.packet.server.actionstate.PacketUpdateActionStateAccepted;
import oldmana.general.md.net.packet.server.actionstate.PacketUpdateActionStateRefusal;
import oldmana.general.md.server.card.Card;
import oldmana.general.md.server.card.CardMoney;
import oldmana.general.md.server.card.CardProperty;
import oldmana.general.md.server.card.CardRegistry;
import oldmana.general.md.server.card.CardProperty.PropertyColor;
import oldmana.general.md.server.card.action.CardActionRent;
import oldmana.general.md.server.card.collection.CardCollection;
import oldmana.general.md.server.card.collection.CardCollectionRegistry;
import oldmana.general.md.server.card.collection.Deck;
import oldmana.general.md.server.card.collection.DiscardPile;
import oldmana.general.md.server.card.collection.PropertySet;
import oldmana.general.md.server.card.collection.VoidCollection;
import oldmana.general.md.server.card.collection.deck.BigGovDeck;
import oldmana.general.md.server.card.collection.deck.DeckStack;
import oldmana.general.md.server.card.collection.deck.ExperimentalDeck;
import oldmana.general.md.server.card.collection.deck.RandomDeck;
import oldmana.general.md.server.card.collection.deck.VanillaDeck;
import oldmana.general.md.server.net.IncomingConnectionsThread;
import oldmana.general.md.server.net.NetServerHandler;
import oldmana.general.md.server.state.ActionState;
import oldmana.general.md.server.state.ActionStateDoNothing;
import oldmana.general.md.server.state.GameState;
import oldmana.general.mjnetworkingapi.packet.Packet;

public class MDServer
{
	private static MDServer instance;
	
	private PlayerRegistry playerRegistry = new PlayerRegistry(); // TODO: Read from disk
	
	private List<Client> newClients = new ArrayList<Client>();
	
	private List<Player> players = new ArrayList<Player>();
	
	private GameState gameState;
	
	private VoidCollection voidCollection = new VoidCollection();
	private Deck deck;
	private DiscardPile discardPile;
	
	private Map<String, DeckStack> decks = new HashMap<String, DeckStack>();
	
	private NetServerHandler netHandler = new NetServerHandler(this);
	
	private boolean shutdown = false;
	private Scanner console = new Scanner(System.in);
	
	private GameRules rules;
	
	private int tickCount;
	
	private Map<String, Object> reflectMap = new HashMap<String, Object>();
	
	public MDServer()
	{
		instance = this;
		rules = new GameRules();
		decks.put("vanilla", new VanillaDeck());
		decks.put("biggov", new BigGovDeck());
		decks.put("experimental", new ExperimentalDeck());
		deck = new Deck(decks.get("biggov"));
		discardPile = new DiscardPile();
	}
	
	public void startServer() throws Exception
	{
		System.out.println("Starting Monopoly Deal Server");
		netHandler.registerPackets();
		new IncomingConnectionsThread();
		gameState = new GameState(this);
		gameState.setCurrentActionState(new ActionStateDoNothing());
		System.out.println("Finished initialization");
		while (!shutdown)
		{
			tickServer();
			Thread.sleep(50);
		}
		System.out.println("Server has shut down");
		console.close();
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
					broadcastPacket(new PacketPlayerStatus(player.getID(), false));
					continue;
				}
				netHandler.processPackets(player);
			}
		}
		
		// Process console commands
		try
		{
			if (System.in.available() > 0)
			{
				String line = console.nextLine();
				String[] full = line.split(" ");
				String cmd = full[0];
				String[] args = Arrays.copyOfRange(full, 1, full.length);
				System.out.println("CMD: " + cmd);
				if (cmd.equalsIgnoreCase("start"))
				{
					System.out.println("Starting game");
					for (int i = 0 ; i < 5 ; i++)
					{
						for (Player player : getPlayers())
						{
							getDeck().drawCard(player, 1.8);
						}
					}
					getGameState().nextTurn();
					getGameState().nextNaturalActionState();
				}
				else if (cmd.equalsIgnoreCase("listplayers"))
				{
					System.out.println("List of players:");
					System.out.println();
					for (Player player : getPlayers())
					{
						System.out.println("- " + player.getName() + "(ID: " + player.getID() + ")");
						System.out.println("Hand ID: " + player.getHand().getID());
						System.out.println("Bank ID: " + player.getBank().getID());
					}
				}
				else if (cmd.equalsIgnoreCase("listcards"))
				{
					if (args.length >= 1)
					{
						CardCollection collection = CardCollectionRegistry.getCardCollection(Integer.parseInt(args[0]));
						System.out.println("List of cards in collection ID " + collection.getID() + "(Count: " + collection.getCardCount() + ")");
						for (Card card : collection.getCards())
						{
							System.out.println("- " + card.getID() + ": " + card.toString());
						}
					}
				}
				else if (cmd.equalsIgnoreCase("listids"))
				{
					if (args.length >= 1)
					{
						CardCollection collection = CardCollectionRegistry.getCardCollection(Integer.parseInt(args[0]));
						System.out.println("List of card IDs in collection ID " + collection.getID() + "(Count: " + collection.getCardCount() + ")");
						for (Card card : collection.getCards())
						{
							System.out.println("- " + card.getID());
						}
					}
				}
				else if (cmd.equalsIgnoreCase("collectioninfo"))
				{
					if (args.length >= 1)
					{
						CardCollection collection = CardCollectionRegistry.getCardCollection(Integer.parseInt(args[0]));
						System.out.println("Collection ID " + collection.getID() + " is a " + collection.getClass().getSimpleName() + " with a card count of " + 
						collection.getCardCount());
					}
				}
				else if (cmd.equalsIgnoreCase("transfercard"))
				{
					//                    Card ID  Index
					//                       \/     \/
					// Example: transfercard 107 2 [0]
					//                          /\
					//                        Set ID
					
					// Creating new property sets:
					//                    Card ID Player ID
					//                       \/     \/
					// Example: transfercard 107 -1 2
					//                           /\
					//                   -1 indicates new set
					if (args.length >= 2)
					{
						Card card = CardRegistry.getCard(Integer.parseInt(args[0]));
						int setId = Integer.parseInt(args[1]);
						if (setId > -1)
						{
							CardCollection collection = CardCollectionRegistry.getCardCollection(setId);
							if (!(collection instanceof PropertySet) || card instanceof CardProperty)
							{
								int index = -1;
								if (args.length >= 3)
								{
									index = Integer.parseInt(args[2]);
								}
								card.transfer(collection, index);
								System.out.println("Transferred card");
							}
							else
							{
								System.out.println("Card of type " + card.getClass().getSimpleName() + " cannot be inserted into a PropertySet");
							}
						}
						else
						{
							if (card instanceof CardProperty)
							{
								if (args.length >= 3)
								{
									Player owner = getPlayerByID(Integer.parseInt(args[2]));
									PropertySet set = owner.createPropertySet();
									card.transfer(set);
									System.out.println("Created new set and transferred card");
								}
							}
							else
							{
								System.out.println("Card of type " + card.getClass().getSimpleName() + " cannot be inserted into a PropertySet");
							}
						}
					}
				}
				else if (cmd.equalsIgnoreCase("transferindex"))
				{
					//                   From ID To ID
					//                       \/   \/
					// Example: transferindex 1 0 2 [0] <- To Index
					//                          /\
					//                       From Index
					if (args.length >= 3)
					{
						CardCollection from = CardCollectionRegistry.getCardCollection(Integer.parseInt(args[0]));
						CardCollection to = CardCollectionRegistry.getCardCollection(Integer.parseInt(args[2]));
						int fromIndex = Integer.parseInt(args[1]);
						int toIndex = args.length >= 4 ? Integer.parseInt(args[3]) : -1;
						from.getCardAt(fromIndex).transfer(to, toIndex);
						System.out.println("Transferred card from collection " + from.getID() + " at index " + fromIndex + " to collection " + to.getID() + 
								" at index " + toIndex);
					}
				}
				else if (cmd.equalsIgnoreCase("transferall"))
				{
					CardCollection from = CardCollectionRegistry.getCardCollection(Integer.parseInt(args[0]));
					CardCollection to = CardCollectionRegistry.getCardCollection(Integer.parseInt(args[1]));
					double speed = 1;
					if (args.length >= 3)
					{
						speed = Double.parseDouble(args[2]);
					}
					for (Card card : new ArrayList<Card>(from.getCards()))
					{
						card.transfer(to, -1, speed);
					}
				}
				else if (cmd.equalsIgnoreCase("createcard"))
				{
					Card card = null;
					if (args[0].equalsIgnoreCase("money"))
					{
						card = new CardMoney(Integer.parseInt(args[1]));
						System.out.println("Created money card with value " + card.getValue() + " with ID " + card.getID());
					}
					else if (args[0].equalsIgnoreCase("action"))
					{
						card = (Card) Class.forName("oldmana.general.md.server.card.action.CardAction" + args[1]).newInstance();
						System.out.println("Created action card " + card.getName() + " with ID " + card.getID());
					}
					else if (args[0].equalsIgnoreCase("rent"))
					{
						//                        Value
						//                         \/
						// Example: createcard rent 4 0 1
						//                             /\
						//                          Color IDs
						int value = Integer.parseInt(args[1]);
						byte[] colors = new byte[args.length - 2];
						for (int i = 2 ; i < args.length ; i++)
						{
							colors[i - 2] = Byte.parseByte(args[i]);
						}
						card = new CardActionRent(value, PropertyColor.fromIDs(colors).toArray(new PropertyColor[colors.length]));
						System.out.println("Created rent card with ID " + card.getID());
					}
					else if (args[0].equalsIgnoreCase("property"))
					{
						//                            Value               Color IDs
						//                             \/                    \/
						// Example: createcard property 4 true Property_Name 0 1
						//                                 /\
						//                                Base
						int value = Integer.parseInt(args[1]);
						boolean base = Boolean.parseBoolean(args[2]);
						String name = args[3].replace('_', ' ');
						byte[] colors = new byte[args.length - 4];
						for (int i = 4 ; i < args.length ; i++)
						{
							colors[i - 4] = Byte.parseByte(args[i]);
						}
						card = new CardProperty(PropertyColor.fromIDs(colors), value, name, base);
						System.out.println("Created property card with ID " + card.getID());
					}
					if (card != null)
					{
						broadcastPacket(card.getCardDataPacket());
						voidCollection.addCard(card);
					}
				}
				else if (cmd.equalsIgnoreCase("nextturn"))
				{
					getGameState().nextTurn();
					getGameState().nextNaturalActionState();
				}
				else if (cmd.equalsIgnoreCase("setturns"))
				{
					getGameState().setTurns(Integer.parseInt(args[0]));
					getGameState().nextNaturalActionState();
				}
				else if (cmd.equalsIgnoreCase("setturn"))
				{
					if (args.length >= 1)
					{
						boolean draw = true;
						if (args.length >= 2)
						{
							draw = Boolean.parseBoolean(args[1]);
						}
						getGameState().setTurn(getPlayerByID(Integer.parseInt(args[0])), draw);
						getGameState().nextNaturalActionState();
					}
				}
				else if (cmd.equalsIgnoreCase("kickplayer"))
				{
					Player player = getPlayerByID(Integer.parseInt(args[0]));
					for (Card card : player.getAllCards())
					{
						card.transfer(deck, -1, 6);
					}
					deck.shuffle();
					if (getGameState().getActivePlayer() == player)
					{
						getGameState().nextTurn();
						getGameState().nextNaturalActionState();
					}
					disconnectPlayer(player);
					players.remove(player);
					broadcastPacket(new PacketDestroyPlayer(player.getID()));
				}
				else if (cmd.equalsIgnoreCase("reset"))
				{
					broadcastPacket(new PacketStatus(""));
					getGameState().endGame();
					for (Player player : getPlayers())
					{
						for (Card card : player.getBank().getCardsInReverse())
						{
							card.transfer(deck, -1, 4);
						}
						List<PropertySet> sets = new ArrayList<PropertySet>(player.getPropertySets());
						Collections.reverse(sets);
						for (PropertySet set : sets)
						{
							for (Card card : set.getCardsInReverse())
							{
								card.transfer(deck, -1, 4);
							}
						}
						for (Card card : player.getHand().getCardsInReverse())
						{
							card.transfer(deck, -1, 4);
						}
					}
					for (Card card : getDiscardPile().getCardsInReverse())
					{
						card.transfer(deck, -1, 4);
					}
					deck.shuffle();
				}
				else if (cmd.equalsIgnoreCase("shuffle"))
				{
					deck.shuffle();
					System.out.println("Shuffled deck");
				}
				else if (cmd.equalsIgnoreCase("setdeck"))
				{
					deck.setDeckStack(decks.get(args[0]));
					System.out.println("Set deck stack to " + args[0]);
				}
				else if (cmd.equalsIgnoreCase("listdecks"))
				{
					System.out.println("Available decks(" + decks.size() + "):");
					for (Entry<String, DeckStack> entry : decks.entrySet())
					{
						System.out.println(entry.getKey() + ": " + entry.getValue().getClass().getSimpleName());
					}
				}
				else if (cmd.equalsIgnoreCase("createdeck"))
				{
					RandomDeck rd = new RandomDeck();
					rd.broadcastCardPackets();
					decks.put(args[0], rd);
				}
				else if (cmd.equalsIgnoreCase("setwin"))
				{
					getGameRules().setMonopoliesRequiredToWin(Integer.parseInt(args[0]));
					System.out.println("Set required monopolies to win to " + getGameRules().getMonopoliesRequiredToWin());
				}
				else if (cmd.equalsIgnoreCase("gamerule"))
				{
					if (args[0].equalsIgnoreCase("rentall"))
					{
						boolean rentAll = Boolean.parseBoolean(args[1]);
						rules.setDoesRentChargeAll(rentAll);
						System.out.println("Rent charges all: " + rentAll);
					}
					else if (args[0].equalsIgnoreCase("dealbreakerdiscard"))
					{
						boolean dealBreakerDiscard = Boolean.parseBoolean(args[1]);;
						rules.setDoDealBreakersDiscardSets(dealBreakerDiscard);
						System.out.println("Deal breakers discard: " + dealBreakerDiscard);
					}
				}
				else if (cmd.equalsIgnoreCase("reflect"))
				{
					if (args[0].equalsIgnoreCase("call"))
					{
						// Example:
						//
						//
						// reflect call this nameToStore methodName arg1 arg2
						//
						//
						Object obj = null;
						if (args[1].equalsIgnoreCase("this"))
						{
							obj = this;
						}
						else
						{
							obj = reflectMap.get(args[1]);
						}
						Class<?>[] methodArgsTypes = new Class<?>[args.length - 4];
						Object[] methodArgs = new Object[args.length - 4];
						if (args.length > 4)
						{
							for (int i = 4 ; i < args.length ; i++)
							{
								String[] arg = args[i].split("=");
								if (arg.length == 1)
								{
									Object param = reflectMap.get(arg[0]);
									methodArgsTypes[i - 4] = param.getClass();
									methodArgs[i - 4] = param;
								}
								else
								{
									Class<?> paramType = null;
									if (arg[1].equalsIgnoreCase("int"))
									{
										paramType = int.class;
									}
									else if (arg[1].equalsIgnoreCase("double"))
									{
										paramType = double.class;
									}
									else
									{
										//if (arg[1].startsWith("*"))
										paramType = Class.forName(/*"oldmana.general.md.server." + */arg[1]);
									}
									Object param = reflectMap.get(arg[0]);
									methodArgsTypes[i - 4] = paramType;
									methodArgs[i - 4] = param;
								}
							}
						}
						
						Object val = obj.getClass().getMethod(args[3], methodArgsTypes).invoke(obj, methodArgs);
						//if (args.length > 3)
						{
							reflectMap.put(args[2], val);
						}
						System.out.println("Called method " + args[3] + " in class " + obj.getClass().getSimpleName());
					}
					else if (args[0].equalsIgnoreCase("checkvalue"))
					{
						Object val = reflectMap.get(args[1]);
						if (val instanceof Integer)
						{
							System.out.println(args[1] + " is an int with the value of " + ((Integer) val).intValue());
						}
						else
						{
							System.out.println(val.toString());
						}
					}
					else if (args[0].equalsIgnoreCase("setvalue"))
					{
						if (args[2].equalsIgnoreCase("int"))
						{
							reflectMap.put(args[1], Integer.parseInt(args[3]));
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// For some reason, the server doesn't automatically garbage collect when it sits idle for long periods of time
		if (tickCount % 10000 == 0)
		{
			System.gc();
		}
		tickCount++;
	}
	
	public GameState getGameState()
	{
		return gameState;
	}
	
	public void setActionState(ActionState state)
	{
		gameState.setCurrentActionState(state);
	}
	
	public int getMonopoliesRequiredToWin()
	{
		return getGameRules().getMonopoliesRequiredToWin();
	}
	
	public boolean doesRentChargeAll()
	{
		return getGameRules().doesRentChargeAll();
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
		if (players.size() > 4)
		{
			//players.remove(0);
		}
	}
	
	public void disconnectPlayer(Player player)
	{
		if (player.getNet() != null)
		{
			player.getNet().close();
			player.setNet(null);
		}
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
			if (player.getMonopolyCount() >= getMonopoliesRequiredToWin())
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
	
	public void refreshPlayer(Player player)
	{
		//player.sendPacket(new PacketRefresh());
		
		for (Player other : getPlayersExcluding(player))
		{
			player.sendPacket(other.getInfoPacket());
		}
		// Send all card data
		for (Card card : CardRegistry.getRegisteredCards())
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
	
	public void sendPacket(Packet packet, Player player)
	{
		player.sendPacket(packet);
	}
	
	public static MDServer getInstance()
	{
		return instance;
	}
}
