package oldmana.md.server.card;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardBuildingData;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.card.control.CardButton;
import oldmana.md.server.card.control.CardButton.CardButtonType;
import oldmana.md.server.card.control.CardControls;

import java.util.List;

public class CardBuilding extends Card
{
	private int tier;
	private int rentAddition;
	
	@Override
	public void applyTemplate(CardTemplate template)
	{
		super.applyTemplate(template);
		tier = template.getInt("tier");
		rentAddition = template.getInt("rentAddition");
	}
	
	@Override
	public CardControls createControls()
	{
		CardControls actions = super.createControls();
		
		CardButton play = new CardButton("Play", CardButton.TOP, CardButtonType.BUILDING);
		play.setCondition((player, card) ->
		{
			if (player.canPlayCards())
			{
				List<PropertySet> sets = player.getPropertySets();
				for (PropertySet set : sets)
				{
					if (set.canBuild() && set.getHighestBuildingTier() == tier - 1)
					{
						return true;
					}
				}
			}
			return false;
		});
		play.setListener((player, card, data) -> player.playCardAction((CardAction) card));
		actions.addButton(play);
		
		CardButton bank = new CardButton("Bank", CardButton.BOTTOM);
		bank.setCondition((player, card) -> getServer().getGameRules().canBankActionCards() && player.canPlayCards());
		bank.setListener((player, card, data) -> player.playCardBank(card));
		actions.addButton(bank);
		
		return actions;
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
		return new PacketCardBuildingData(getID(), getName(), getValue(), tier, rentAddition, isRevocable(), clearsRevocableCards(), getDisplayName(), 
				(byte) getFontSize(), (byte) getDisplayOffsetY(), getDescription().getID());
	}
	
	private static CardType<CardBuilding> createType()
	{
		CardType<CardBuilding> type = new CardType<CardBuilding>(CardBuilding.class,
				CardBuilding::new, "Generic Building");
		type.addExemptReduction("tier", false);
		type.addExemptReduction("rentAddition", false);
		CardTemplate template = type.getDefaultTemplate();
		template.put("value", 1);
		template.put("name", "Generic Building");
		template.putStrings("displayName", "GENERIC", "BUILDING");
		template.put("fontSize", 8);
		template.put("displayOffsetY", 2);
		template.put("revocable", true);
		template.put("clearsRevocableCards", false);
		template.put("tier", 1);
		template.put("rentAddition", 1);
		type.setDefaultTemplate(template);
		return type;
	}
}
