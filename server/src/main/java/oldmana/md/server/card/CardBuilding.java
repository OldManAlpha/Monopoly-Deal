package oldmana.md.server.card;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.common.playerui.CardButtonType;
import oldmana.md.net.packet.server.PacketCardBuildingData;
import oldmana.md.server.Player;
import oldmana.md.server.card.play.argument.PropertySetArgument;
import oldmana.md.server.card.play.PlayArguments;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.card.control.CardButton;
import oldmana.md.server.card.control.CardControls;

import java.util.List;

import static oldmana.md.server.card.CardAttributes.*;

public class CardBuilding extends Card
{
	public static final String TIER = "tier";
	public static final String RENT_ADDITION = "rentAddition";
	
	private int tier;
	private int rentAddition;
	
	@Override
	public void applyTemplate(CardTemplate template)
	{
		super.applyTemplate(template);
		tier = template.getInt(TIER);
		rentAddition = template.getInt(RENT_ADDITION);
	}
	
	@Override
	public void doPlay(Player player, PlayArguments args)
	{
		PropertySet set = args.getArgument(PropertySetArgument.class).getTargetSet();
		transfer(set, getPlayAnimation());
	}
	
	@Override
	protected void playStageMoveCard(Player player, PlayArguments args) {}
	
	@Override
	public CardControls createControls()
	{
		CardControls controls = super.createControls();
		
		CardButton play = controls.getButtonByText("Play");
		play.setType(CardButtonType.BUILDING);
		
		return controls;
	}
	
	@Override
	public boolean canPlay(Player player)
	{
		List<PropertySet> sets = player.getPropertySets();
		for (PropertySet set : sets)
		{
			if (set.canBuild() && set.getHighestBuildingTier() == tier - 1)
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean canBank(Player player)
	{
		return getServer().getGameRules().canBankActionCards();
	}
	
	public int getTier()
	{
		return tier;
	}
	
	public int getRentAddition()
	{
		return rentAddition;
	}
	
	@Override
	public Packet getCardDataPacket()
	{
		return new PacketCardBuildingData(getID(), getName(), getValue(), tier, rentAddition, isUndoable(),
				shouldClearUndoableCards(), getDisplayName(), (byte) getFontSize(), (byte) getDisplayOffsetY(),
				getDescription().getID());
	}
	
	private static CardType<CardBuilding> createType()
	{
		CardType<CardBuilding> type = new CardType<CardBuilding>(CardBuilding.class,
				CardBuilding::new, "Generic Building");
		type.addExemptReduction(TIER, false);
		type.addExemptReduction(RENT_ADDITION, false);
		CardTemplate template = type.getDefaultTemplate();
		template.put(VALUE, 1);
		template.put(NAME, "Generic Building");
		template.putStrings(DISPLAY_NAME, "GENERIC", "BUILDING");
		template.put(FONT_SIZE, 8);
		template.put(DISPLAY_OFFSET_Y, 2);
		template.put(UNDOABLE, true);
		template.put(CLEARS_UNDOABLE_ACTIONS, false);
		template.put(CONSUME_MOVES_STAGE, CardPlayStage.AFTER_PLAY);
		template.put(TIER, 1);
		template.put(RENT_ADDITION, 1);
		type.setDefaultTemplate(template);
		return type;
	}
}
