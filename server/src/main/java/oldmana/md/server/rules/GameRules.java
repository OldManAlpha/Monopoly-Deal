package oldmana.md.server.rules;

import oldmana.md.net.packet.server.PacketGameRules;
import oldmana.md.server.MDServer;
import oldmana.md.server.event.GameRulesReloadedEvent;
import oldmana.md.server.rules.RuleStructKey.RuleKeyBuilder;
import oldmana.md.server.rules.RuleStructObject.RuleObjectBuilder;
import oldmana.md.server.rules.RuleStructOption.RuleOptionBuilder;
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
	private boolean dealBreakersDiscardSets = false;
	private boolean allowUndo = true;
	private int maxCardsInHand = 7;
	private int cardsDealt = 5;
	private int cardsDrawnPerTurn = 2;
	private DrawExtraCardsPolicy drawExtraCardsPolicy = DrawExtraCardsPolicy.IMMEDIATELY_AFTER_ACTION;
	private int extraCardsDrawn = 5;
	
	public GameRules()
	{
		rootRuleStruct = RuleObjectBuilder.from(null)
			.jsonName("root")
			.name("Root Rule")
			.description("The root of the rules.")
			.register();
		
		RuleStruct winCondition = RuleOptionBuilder.from(rootRuleStruct)
				.jsonName("winCondition")
				.name("Win Condition")
				.description("The condition to win the game.")
				.defaultChoice("propertySet")
				.register();
		{
			RuleStruct propertySetCondition = RuleObjectBuilder.from(winCondition)
					.jsonName("propertySet")
					.name("Property Set")
					.description("Win by number of full property sets")
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
					.description("Win by amount of money in bank")
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
				.jsonName("twoColorRentChargesAll")
				.name("2-Color Rent Charges All Players")
				.description("If enabled, 2-color rent cards will charge all other players instead of just one.")
				.defaultValue(true)
				.register();
		
		RuleKeyBuilder.from(rootRuleStruct)
				.jsonName("multiColorRentChargesAll")
				.name("Multi-Color Rent Charges All Players")
				.description("If enabled, multi-color rent cards will charge all other players instead of just one.")
				.defaultValue(true)
				.register();
		
		RuleKeyBuilder.from(rootRuleStruct)
				.jsonName("dealBreakersDiscardSets")
				.name("Deal Breakers Discard Monopolies")
				.description("If enabled, Deal Breakers will discard monopolies instead of stealing them.")
				.defaultValue(false)
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
		
		RuleStruct emptyHandMechanics = RuleObjectBuilder.from(rootRuleStruct)
				.jsonName("emptyHandMechanics")
				.name("Empty Hand Mechanics")
				.description("What happens when a player runs out of cards.")
				.register();
		
		{
			RuleOptionBuilder.from(emptyHandMechanics)
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
			
			RuleKeyBuilder.from(emptyHandMechanics)
					.jsonName("extraCardsDrawn")
					.name("Extra Cards Drawn")
					.description("The number of cards to draw.")
					.defaultValue(5)
					.register();
		}
		
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
	
	public boolean doDealBreakersDiscardSets()
	{
		return dealBreakersDiscardSets;
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
	
	public GameRule getRootRule()
	{
		return rootRule;
	}
	
	public GameRule getRule(String jsonName)
	{
		return rootRule.getSubrule(jsonName);
	}
	
	public void setRules(GameRule rootRule)
	{
		// Apply the default rules, in case the new rules being applied aren't complete or has errors
		this.rootRule = rootRuleStruct.generateDefaults();
		reloadRules();
		// Apply the actual rules
		this.rootRule = rootRule;
		reloadRules();
		if (MDServer.getInstance() != null)
		{
			MDServer.getInstance().broadcastPacket(constructPacket());
			MDServer.getInstance().getEventManager().callEvent(new GameRulesReloadedEvent());
		}
	}
	
	public void reloadRules()
	{
		GameRule winConditionRule = getRule("winCondition").getChoice();
		applyRule(() -> winCondition = WinConditionType.fromRule(winConditionRule).create(winConditionRule));
		applyRule(() -> movesPerTurn = getRule("movesPerTurn").getInteger());
		applyRule(() -> twoColorRentChargesAll = getRule("twoColorRentChargesAll").getBoolean());
		applyRule(() -> multiColorRentChargesAll = getRule("multiColorRentChargesAll").getBoolean());
		applyRule(() -> dealBreakersDiscardSets = getRule("dealBreakersDiscardSets").getBoolean());
		applyRule(() -> allowUndo = getRule("allowUndo").getBoolean());
		applyRule(() -> maxCardsInHand = getRule("maxCardsInHand").getInteger());
		applyRule(() -> cardsDealt = getRule("cardsDealt").getInteger());
		applyRule(() -> cardsDrawnPerTurn = getRule("cardsDrawnPerTurn").getInteger());
		GameRule emptyHandMechanics = getRule("emptyHandMechanics");
		applyRule(() -> drawExtraCardsPolicy = DrawExtraCardsPolicy.fromJson(emptyHandMechanics.getSubrule("drawExtraCards").getString()));
		applyRule(() -> extraCardsDrawn = emptyHandMechanics.getSubrule("extraCardsDrawn").getInteger());
	}
	
	/**
	 * Compactly encapsulates a single application so one rule application error doesn't halt the entire process.
	 * @param application The application to run
	 */
	private void applyRule(Runnable application)
	{
		try
		{
			application.run();
		}
		catch (Exception e)
		{
			System.err.println("Error applying a game rule:");
			e.printStackTrace();
		}
	}
	
	public PacketGameRules constructPacket()
	{
		JSONObject rules = new JSONObject();
		rules.put("maxCardsInHand", maxCardsInHand);
		rules.put("maxMoves", movesPerTurn);
		return new PacketGameRules(rules);
	}
}
