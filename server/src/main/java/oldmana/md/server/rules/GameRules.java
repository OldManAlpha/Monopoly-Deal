package oldmana.md.server.rules;

import oldmana.md.common.net.packet.server.PacketGameRules;
import oldmana.md.server.ChatColor;
import oldmana.md.server.MDServer;
import oldmana.md.server.event.rule.GameRulesApplyEvent;
import oldmana.md.server.event.rule.GameRulesReloadedEvent;
import oldmana.md.server.rules.struct.*;
import oldmana.md.server.rules.struct.RuleStructKey.RuleKeyBuilder;
import oldmana.md.server.rules.struct.RuleStructObject.RuleObjectBuilder;
import oldmana.md.server.rules.struct.RuleStructOption.RuleOptionBuilder;
import oldmana.md.server.rules.win.WinCondition;
import oldmana.md.server.rules.win.WinConditionType;
import org.json.JSONObject;

public class GameRules
{
	private RuleStructObject rootRuleStruct;
	
	private GameRule rootRule;
	
	private WinCondition winCondition;
	private int movesPerTurn = 3;
	private boolean twoColorRentChargesAll = true;
	private boolean multiColorRentChargesAll = true;
	private boolean allowMultipleRentModifiers = false;
	private boolean dealBreakersDiscardSets = false;
	private DoubleRentPolicy doubleRentPolicy = DoubleRentPolicy.MULTIPLY;
	private boolean allowUndo = true;
	private int maxCardsInHand = 7;
	private int cardsDealt = 5;
	private int cardsDrawnPerTurn = 2;
	private DrawExtraCardsPolicy drawExtraCardsPolicy = DrawExtraCardsPolicy.IMMEDIATELY_AFTER_ACTION;
	private int extraCardsDrawn = 5;
	private boolean canDiscardEarly = false;
	private DiscardOrderPolicy discardOrderPolicy = DiscardOrderPolicy.MONEY_ACTION_FIRST;
	private DiscardLocationPolicy discardLocationPolicy = DiscardLocationPolicy.TOP_OF_DISCARD;
	private boolean canBankActionCards = true;
	private boolean canBankPropertyCards = false;
	private boolean isBankValueVisible = false;
	
	public GameRules()
	{
		rootRuleStruct = RuleObjectBuilder.from(null)
			.jsonName("root")
			.name("Root Rule")
			.description("The root of the rules.")
			.register();
		
		/*
		RuleStructArray testArray = RuleArrayBuilder.from(rootRuleStruct)
				.jsonName("testArray")
				.name("Test Array")
				.description("A test array")
				.register();
		
		{
			RuleStructValue<Integer> testValue = RuleStructValue.of(testArray, 1);
		}
		 */
		
		
		
		RuleStruct cardRules = RuleObjectBuilder.from(rootRuleStruct)
				.jsonName("cardRules")
				.name("Card-Specific Rules")
				.description("Rules that affect specific cards.")
				.register();
		
		{
			RuleStruct dealBreaker = RuleObjectBuilder.from(cardRules)
					.jsonName("dealBreaker")
					.name("Deal Breaker")
					.description("Deal Breaker Rules")
					.register();
			{
				RuleKeyBuilder.from(dealBreaker)
						.jsonName("dealBreakersDiscardSets")
						.name("Deal Breakers Discard Monopolies")
						.description("If enabled, Deal Breakers will discard monopolies instead of stealing them.")
						.defaultValue(false)
						.register();
			}
			
			RuleStruct rent = RuleObjectBuilder.from(cardRules)
					.jsonName("rent")
					.name("Rent")
					.description("Rent Rules")
					.register();
			{
				RuleKeyBuilder.from(rent)
						.jsonName("twoColorRentChargesAll")
						.name("2-Color Rent Charges All Players")
						.description("If enabled, 2-color rent cards will charge all other players instead of just one.")
						.defaultValue(true)
						.register();
				
				RuleKeyBuilder.from(rent)
						.jsonName("multiColorRentChargesAll")
						.name("Multi-Color Rent Charges All Players")
						.description("If enabled, multi-color rent cards will charge all other players instead of just one.")
						.defaultValue(true)
						.register();
				
				RuleKeyBuilder.from(rent)
						.jsonName("allowMultipleModifiers")
						.name("Allow Multiple Rent Modifiers")
						.description("If enabled, players can play more than just one rent modifier, " +
								"such as Double The Rent, on a single Rent card.")
						.defaultValue(false)
						.register();
			}
			
			RuleStruct doubleRent = RuleObjectBuilder.from(cardRules)
					.jsonName("doubleTheRent")
					.name("Double The Rent!")
					.description("Double The Rent Rules")
					.register();
			{
				RuleOptionBuilder.from(doubleRent)
						.jsonName("doubleRule")
						.name("Rent Doubling Rule")
						.description("In what way to increase rent. This rule has no effect when multiple aren't allowed.")
						.addChoice("Add", "Add", "Rent will be added by the base charge for each Double The Rent card played.", " ",
								"Example: 8M charge with 2 Double Rent = 8+8+8 = 24M")
						.addChoice("Multiply", "Multiply", "Rent will be multiplied by 2x repeatedly for the number of " +
										"Double The Rent cards played.", " ",
								"Example: 8M charge with 2 Double Rent = 8x2x2 = 32M")
						.defaultChoice("Multiply")
						.register();
			}
		}
		
		RuleStruct winCondition = RuleOptionBuilder.from(rootRuleStruct)
				.jsonName("winCondition")
				.name("Win Condition")
				.description("The condition to win the game.")
				.defaultChoice("propertySets")
				.register();
		{
			RuleStruct propertySetCondition = RuleObjectBuilder.from(winCondition)
					.jsonName("propertySets")
					.name("Full Property Sets")
					.description("Win by number of full property sets.")
					.register();
			{
				RuleKeyBuilder.from(propertySetCondition)
						.jsonName("setsRequired")
						.name("Sets Required")
						.description("The amount of property sets required to win")
						.defaultValue(3)
						.register();
				
				RuleKeyBuilder.from(propertySetCondition)
						.jsonName("unique")
						.name("Unique Colors")
						.description("If enabled, each property set needs to be a unique color.")
						.defaultValue(true)
						.register();
			}
			RuleKeyBuilder.from(winCondition)
					.jsonName("money")
					.name("Money")
					.description("Win by the total value of cards in the bank.")
					.defaultValue(40)
					.register();
		}
		
		RuleKeyBuilder.from(rootRuleStruct)
				.jsonName("movesPerTurn")
				.name("Moves Per Turn")
				.description("The number of moves players have on their turn.")
				.defaultValue(3)
				.register();
		
		RuleKeyBuilder.from(rootRuleStruct)
				.jsonName("allowUndo")
				.name("Allow Undo")
				.description("If enabled, money and properties can be undone until an offensive action card is played.")
				.defaultValue(true)
				.register();
		
		RuleKeyBuilder.from(rootRuleStruct)
				.jsonName("maxCardsInHand")
				.name("Maximum Cards In Hand")
				.description("The maximum amount of cards a player can have in their hand before ending their turn.")
				.defaultValue(7)
				.register();
		
		RuleKeyBuilder.from(rootRuleStruct)
				.jsonName("cardsDealt")
				.name("Cards Dealt")
				.description("The number of cards each player is dealt when the game starts.")
				.defaultValue(5)
				.register();
		
		RuleKeyBuilder.from(rootRuleStruct)
				.jsonName("cardsDrawnPerTurn")
				.name("Cards Drawn Each Turn")
				.description("The number of cards a player draws when their turn starts.")
				.defaultValue(2)
				.register();
		
		RuleStruct emptyHandRules = RuleObjectBuilder.from(rootRuleStruct)
				.jsonName("emptyHandRules")
				.name("Empty Hand Rules")
				.description("What happens when a player runs out of cards.")
				.register();
		
		{
			RuleOptionBuilder.from(emptyHandRules)
					.jsonName("drawExtraCards")
					.name("Draw Extra Cards After")
					.description("When to draw extra cards.")
					.addChoice("Immediately", "Immediately", "Immediately draw cards the moment a player runs out of cards.")
					.addChoice("ImmediatelyAfterAction", "Immediately After Action",
							"Cards are drawn after the current action is finished. For example, the player's last card " +
							"is a Sly Deal and they play it. The player will not draw cards until after they steal a card.")
					.addChoice("NextDraw", "Next Draw", "Draw these cards the next time a player draws, instead of what " +
							"they would normally draw.")
					.addChoice("Never", "Never", "Never draw extra cards.")
					.defaultChoice("ImmediatelyAfterAction")
					.register();
			
			RuleKeyBuilder.from(emptyHandRules)
					.jsonName("extraCardsDrawn")
					.name("Extra Cards Drawn")
					.description("The number of cards to draw.")
					.defaultValue(5)
					.register();
		}
		
		RuleStruct discardRules = RuleObjectBuilder.from(rootRuleStruct)
				.jsonName("discardRules")
				.name("Discard Rules")
				.description("How discarding works.")
				.register();
		
		{
			RuleKeyBuilder.from(discardRules)
					.jsonName("canDiscardEarly")
					.name("Can Discard Early")
					.description("If enabled, players can end their turn and start discarding even if they have moves left.")
					.defaultValue(false)
					.register();
			
			RuleOptionBuilder.from(discardRules)
					.jsonName("discardOrder")
					.name("Discard Order")
					.description("What types of cards should be discarded before others.")
					.addChoice("MoneyActionFirst", "Discard Money/Action Cards First", "Discard Money/Action Cards before Property Cards.")
					.addChoice("PropertyFirst", "Discard Property Cards First", "Discard Property Cards before Money/Action Cards.")
					.addChoice("Any", "Discard Anything", "Discard any card of choice.")
					.defaultChoice("MoneyActionFirst")
					.register();
			
			RuleOptionBuilder.from(discardRules)
					.jsonName("discardLocation")
					.name("Discard Location")
					.description("Where cards go when discarded.")
					.addChoice("TopOfDiscard", "Top Of Discard Pile", "Cards are discarded to the top of the discard pile.")
					.addChoice("BottomOfDeck", "Bottom Of Deck", "Cards are discarded to the bottom of the deck.")
					.addChoice("RandomlyInDeck", "Randomly In Deck", "Cards are discarded to a random location in the deck.")
					.defaultChoice("TopOfDiscard")
					.register();
		}
		
		RuleStruct bankRules = RuleObjectBuilder.from(rootRuleStruct)
				.jsonName("bankRules")
				.name("Bank Rules")
				.description("How the player's bank work.")
				.register();
		{
			RuleKeyBuilder.from(bankRules)
					.jsonName("canBankActionCards")
					.name("Allow Banking Action Cards")
					.description("If enabled, players can play action cards from their hand into their bank.")
					.defaultValue(true)
					.register();
			
			RuleKeyBuilder.from(bankRules)
					.jsonName("canBankPropertyCards")
					.name("Allow Banking Property Cards")
					.description("If enabled, players can play property cards from their hand into their bank.")
					.defaultValue(false)
					.register();
			
			RuleKeyBuilder.from(bankRules)
					.jsonName("canSeeValue")
					.name("Visible Value")
					.description("If enabled, the total value of all players' banks will be visible.")
					.defaultValue(false)
					.register();
		}
		
		RuleStruct modRules = RuleObjectBuilder.from(rootRuleStruct)
				.jsonName("modRules")
				.name("Mod Rules")
				.description("Rules that mods have added.")
				.reducible(true)
				.register();
		
		setRules(rootRuleStruct.generateDefaults());
	}
	
	public RuleStructObject getRootRuleStruct()
	{
		return rootRuleStruct;
	}
	
	public WinCondition getWinCondition()
	{
		return winCondition;
	}
	
	public int getMovesPerTurn()
	{
		return movesPerTurn;
	}
	
	public boolean doesTwoColorRentChargeAll()
	{
		return twoColorRentChargesAll;
	}
	
	public boolean doesMultiColorRentChargeAll()
	{
		return multiColorRentChargesAll;
	}
	
	public boolean isMultipleRentModifiersAllowed()
	{
		return allowMultipleRentModifiers;
	}
	
	public boolean doDealBreakersDiscardSets()
	{
		return dealBreakersDiscardSets;
	}
	
	public DoubleRentPolicy getDoubleRentPolicy()
	{
		return doubleRentPolicy;
	}
	
	public boolean isUndoAllowed()
	{
		return allowUndo;
	}
	
	public int getMaxCardsInHand()
	{
		return maxCardsInHand;
	}
	
	public int getCardsDealt()
	{
		return cardsDealt;
	}
	
	public int getCardsDrawnPerTurn()
	{
		return cardsDrawnPerTurn;
	}
	
	public DrawExtraCardsPolicy getDrawExtraCardsPolicy()
	{
		return drawExtraCardsPolicy;
	}
	
	public int getExtraCardsDrawn()
	{
		return extraCardsDrawn;
	}
	
	public boolean canDiscardEarly()
	{
		return canDiscardEarly;
	}
	
	public DiscardOrderPolicy getDiscardOrderPolicy()
	{
		return discardOrderPolicy;
	}
	
	public DiscardLocationPolicy getDiscardLocationPolicy()
	{
		return discardLocationPolicy;
	}
	
	public boolean canBankActionCards()
	{
		return canBankActionCards;
	}
	
	public boolean canBankPropertyCards()
	{
		return canBankPropertyCards;
	}
	
	public boolean isBankValueVisible()
	{
		return isBankValueVisible;
	}
	
	public GameRule getRootRule()
	{
		return rootRule;
	}
	
	public GameRule getRule(String jsonName)
	{
		return rootRule.getSubrule(jsonName);
	}
	
	public GameRule getModRule(String modName)
	{
		return getRule("modRules").getSubrule(modName);
	}
	
	public boolean setRules(GameRule rootRule)
	{
		this.rootRule = rootRule;
		return reloadRules();
	}
	
	/**
	 * Reloads the rules from the currently set root rule.
	 * @return True if the application was successful
	 */
	public boolean reloadRules()
	{
		try
		{
			applyRules();
		}
		catch (Exception e)
		{
			// Apply default rules if application fails
			System.out.println("Rule application failed, falling back to default rules.");
			e.printStackTrace();
			rootRule = rootRuleStruct.generateDefaults();
			applyRules();
			return false;
		}
		finally
		{
			if (getServer() != null)
			{
				getServer().broadcastPacket(constructPacket());
				getServer().getEventManager().callEvent(new GameRulesReloadedEvent());
				getServer().broadcastMessage(ChatColor.PREFIX_ALERT + ChatColor.LIGHT_YELLOW +
						"The game rules have been reloaded! Use " + ChatColor.LIGHT_BLUE +
						"/rules" + ChatColor.LIGHT_YELLOW + " to review potential rule changes.");
			}
		}
		return true;
	}
	
	private void applyRules()
	{
		GameRule cardRules = getRule("cardRules");
		GameRule winConditionRule = getRule("winCondition").getChoice();
		winCondition = WinConditionType.fromRule(winConditionRule).create(winConditionRule);
		movesPerTurn = getRule("movesPerTurn").getInteger();
		twoColorRentChargesAll = cardRules.getSubrule("rent").getSubrule("twoColorRentChargesAll").getBoolean();
		multiColorRentChargesAll = cardRules.getSubrule("rent").getSubrule("multiColorRentChargesAll").getBoolean();
		allowMultipleRentModifiers = cardRules.getSubrule("rent").getSubrule("allowMultipleModifiers").getBoolean();
		dealBreakersDiscardSets = cardRules.getSubrule("dealBreaker").getSubrule("dealBreakersDiscardSets").getBoolean();
		doubleRentPolicy = DoubleRentPolicy.fromJson(cardRules.getSubrule("doubleTheRent").getSubrule("doubleRule").getString());
		allowUndo = getRule("allowUndo").getBoolean();
		maxCardsInHand = getRule("maxCardsInHand").getInteger();
		cardsDealt = getRule("cardsDealt").getInteger();
		cardsDrawnPerTurn = getRule("cardsDrawnPerTurn").getInteger();
		GameRule emptyHandRules = getRule("emptyHandRules");
		drawExtraCardsPolicy = DrawExtraCardsPolicy.fromJson(emptyHandRules.getSubrule("drawExtraCards").getString());
		extraCardsDrawn = emptyHandRules.getSubrule("extraCardsDrawn").getInteger();
		GameRule discardRules = getRule("discardRules");
		canDiscardEarly = discardRules.getSubrule("canDiscardEarly").getBoolean();
		discardOrderPolicy = DiscardOrderPolicy.fromJson(discardRules.getSubrule("discardOrder").getString());
		discardLocationPolicy = DiscardLocationPolicy.fromJson(discardRules.getSubrule("discardLocation").getString());
		GameRule bankRules = getRule("bankRules");
		canBankActionCards = bankRules.getSubrule("canBankActionCards").getBoolean();
		canBankPropertyCards = bankRules.getSubrule("canBankPropertyCards").getBoolean();
		isBankValueVisible = bankRules.getSubrule("canSeeValue").getBoolean();
		if (getServer() != null)
		{
			getServer().getEventManager().callEvent(new GameRulesApplyEvent(rootRule));
		}
	}
	
	public PacketGameRules constructPacket()
	{
		JSONObject rules = new JSONObject();
		rules.put("maxCardsInHand", maxCardsInHand);
		rules.put("maxMoves", movesPerTurn);
		rules.put("canDiscardEarly", canDiscardEarly);
		rules.put("bankValueVisible", isBankValueVisible);
		return new PacketGameRules(rules);
	}
	
	private MDServer getServer()
	{
		return MDServer.getInstance();
	}
}
