package oldmana.md.client.state;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oldmana.md.client.Player;
import oldmana.md.client.ThePlayer;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.action.ActionScreenSelectProperty;
import oldmana.md.client.gui.action.ActionScreenSelectProperty.PropertySelectListener;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDCard;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.client.gui.component.collection.MDPropertySet;
import oldmana.md.common.net.packet.client.action.PacketActionSelectProperties;

public class ActionStateTargetProperties extends ActionState
{
	private TargetMode mode;
	private boolean canTargetSelfMonopoly;
	private boolean canTargetOtherMonopoly;
	private boolean canTargetUnstealable;
	
	private Map<Target, TargetedCard> targetMap = new HashMap<Target, TargetedCard>();
	
	public ActionStateTargetProperties(Player player, TargetMode mode, boolean canTargetSelfMonopoly, boolean canTargetOtherMonopoly, boolean canTargetUnstealable)
	{
		super(player);
		this.mode = mode;
		this.canTargetSelfMonopoly = canTargetSelfMonopoly;
		this.canTargetOtherMonopoly = canTargetOtherMonopoly;
		this.canTargetUnstealable = canTargetUnstealable;
	}
	
	@Override
	public void setup()
	{
		Player actionOwner = getActionOwner();
		if (actionOwner == getClient().getThePlayer())
		{
			List<Player> targets = new ArrayList<Player>();
			if (mode.canTarget(Target.OTHER))
			{
				targets.addAll(getClient().getOtherPlayers());
			}
			if (mode.canTarget(Target.SELF))
			{
				targets.add(actionOwner);
			}
			
			for (Player target : targets)
			{
				for (PropertySet set : target.getPropertySets())
				{
					if (canTarget(target, set))
					{
						MDPropertySet setUI = (MDPropertySet) set.getUI();
						setUI.enableSelection(() ->
						{
							if (set.getCardCount() == 1)
							{
								ActionStateTargetProperties.this.propertySelected(target, setUI, set.getPropertyCardAt(0));
								return;
							}
							ActionScreenSelectProperty screen = new ActionScreenSelectProperty(set, canTargetUnstealable);
							screen.setListener(new PropertySelectListener()
							{
								@Override
								public void propertySelected(CardProperty prop)
								{
									ActionStateTargetProperties.this.propertySelected(target, setUI, prop);
									getClient().getTableScreen().removeActionScreen();
								}
								
								@Override
								public void cancel()
								{
									getClient().getTableScreen().removeActionScreen();
								}
							});
							getClient().getTableScreen().setActionScreen(screen);
						});
					}
				}
			}
		}
	}
	
	private boolean canTarget(Player player, PropertySet set)
	{
		if (!set.hasStealable() && !canTargetUnstealable)
		{
			return false;
		}
		if (!set.isMonopoly())
		{
			return true;
		}
		return player instanceof ThePlayer ? canTargetSelfMonopoly : canTargetOtherMonopoly;
	}
	
	public void propertySelected(Player owner, MDPropertySet ui, CardProperty prop)
	{
		TargetedCard targetedCard = new TargetedCard(ui, prop);
		Target target = mode.getTargetType(owner);
		TargetedCard prev = targetMap.get(target);
		if (prev != null)
		{
			prev.destroyView();
		}
		targetedCard.applyView();
		targetMap.put(target, targetedCard);
		updateButton();
	}
	
	public void updateButton()
	{
		if (getActionOwner() == getClient().getThePlayer())
		{
			MDButton button = getClient().getTableScreen().getMultiButton();
			button.setText("Confirm");
			if (targetMap.keySet().containsAll(mode.getRequiredTargets()))
			{
				button.setEnabled(true);
				button.setListener(new MouseAdapter()
				{
					@Override
					public void mouseReleased(MouseEvent event)
					{
						getClient().sendPacket(mode.getPacket(targetMap));
						cleanup();
						getClient().setAwaitingResponse(true);
						button.setEnabled(false);
						button.removeListener();
					}
				});
			}
			else
			{
				button.setEnabled(false);
				button.removeListener();
			}
		}
	}
	
	@Override
	public void cleanup()
	{
		for (Player player : getClient().getAllPlayers())
		{
			for (PropertySet set : player.getPropertySets())
			{
				((MDPropertySet) set.getUI()).disableSelection();
			}
		}
		
		for (TargetedCard target : targetMap.values())
		{
			target.destroyView();
		}
	}
	
	@Override
	public void updateUI()
	{
		targetMap.values().forEach(target -> target.updateView());
	}
	
	public static class TargetedCard
	{
		private MDPropertySet ui;
		private Card card;
		
		private MDCard view;
		private MDSelection selection;
		
		public TargetedCard(MDPropertySet ui, Card card)
		{
			this.ui = ui;
			this.card = card;
		}
		
		public void applyView()
		{
			destroyView();
			
			view = new MDCard(card);
			view.setLocation(ui.getLocationOf(card));
			ui.add(view, 0);
			selection = new MDSelection(Color.BLUE);
			selection.setLocation(view.getLocation());
			selection.setSize(view.getSize());
			ui.add(selection, 0);
		}
		
		public void destroyView()
		{
			if (view != null)
			{
				view.getParent().remove(view);
				selection.getParent().remove(selection);
				view = null;
				selection = null;
			}
		}
		
		public void updateView()
		{
			destroyView();
			applyView();
		}
		
		public Card getCard()
		{
			return view.getCard();
		}
	}
	
	public enum Target
	{
		SELF, OTHER, ANY
	}
	
	public enum TargetMode
	{
		/** Target any property on the board **/
		ANY(Target.ANY)
		{
			@Override
			public boolean canTarget(Target target)
			{
				return true;
			}
			
			@Override
			public Target getTargetType(Player player)
			{
				return Target.ANY;
			}
			
			@Override
			public PacketActionSelectProperties getPacket(Map<Target, TargetedCard> targets)
			{
				return new PacketActionSelectProperties(targets.get(Target.ANY).getCard().getID());
			}
		},
		/** Target any property on your table **/
		SELF(Target.SELF)
		{
			@Override
			public boolean canTarget(Target target)
			{
				return target == Target.SELF;
			}
			
			@Override
			public Target getTargetType(Player player)
			{
				return Target.SELF;
			}
			
			@Override
			public PacketActionSelectProperties getPacket(Map<Target, TargetedCard> targets)
			{
				return new PacketActionSelectProperties(targets.get(Target.SELF).getCard().getID());
			}
		},
		/** Target any property of your opponents **/
		OTHER(Target.OTHER)
		{
			@Override
			public boolean canTarget(Target target)
			{
				return target == Target.OTHER;
			}
			
			@Override
			public Target getTargetType(Player player)
			{
				return Target.OTHER;
			}
			
			@Override
			public PacketActionSelectProperties getPacket(Map<Target, TargetedCard> targets)
			{
				return new PacketActionSelectProperties(targets.get(Target.OTHER).getCard().getID());
			}
		},
		/** Target any of your property and any of an opponent's property **/
		SELF_OTHER(Target.SELF, Target.OTHER)
		{
			@Override
			public boolean canTarget(Target target)
			{
				return true;
			}
			
			@Override
			public Target getTargetType(Player player)
			{
				return player instanceof ThePlayer ? Target.SELF : Target.OTHER;
			}
			
			@Override
			public PacketActionSelectProperties getPacket(Map<Target, TargetedCard> targets)
			{
				return new PacketActionSelectProperties(new int[] {targets.get(Target.SELF).getCard().getID(),
						targets.get(Target.OTHER).getCard().getID()});
			}
		};
		
		private final List<Target> requiredTargets;
		
		TargetMode(Target... requiredTargets)
		{
			this.requiredTargets = Arrays.asList(requiredTargets);
		}
		
		public List<Target> getRequiredTargets()
		{
			return requiredTargets;
		}
		
		public abstract boolean canTarget(Target target);
		
		public abstract Target getTargetType(Player player);
		
		public abstract PacketActionSelectProperties getPacket(Map<Target, TargetedCard> targets);
	}
}
