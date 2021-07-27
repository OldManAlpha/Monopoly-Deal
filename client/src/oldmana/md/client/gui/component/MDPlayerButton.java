package oldmana.md.client.gui.component;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import oldmana.md.client.Player;
import oldmana.md.client.state.ActionState;
import oldmana.md.client.state.ActionStateRent;
import oldmana.md.net.packet.client.action.PacketActionAccept;
import oldmana.md.net.packet.client.action.PacketActionButtonClick;
import oldmana.md.net.packet.client.action.PacketActionPlayCardSpecial;

public class MDPlayerButton extends MDButton
{
	private Player player;
	private int id;
	private ButtonType type = ButtonType.NORMAL;
	
	private String acceptText;
	private String refuseText;
	
	public MDPlayerButton(Player player, int id)
	{
		super("");
		this.player = player;
		this.id = id;
		setListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				ActionState state = getClient().getGameState().getActionState();
				if (isEnabled() && !getClient().isInputBlocked())
				{
					switch (type)
					{
						case NORMAL:
						{
							getClient().sendPacket(new PacketActionButtonClick(id, player.getID()));
							setEnabled(false);
							break;
						}
						case RENT:
						{
							if (state instanceof ActionStateRent)
							{
								((ActionStateRent) state).getRentScreen().setVisible(true);
							}
							break;
						}
						case REFUSABLE:
						{
							if (state.isUsingActionCounter())
							{
								getClient().sendPacket(new PacketActionPlayCardSpecial(state.getActionCounterCard().getID(), player.getID()));
								state.removeActionCounter();
							}
							else
							{
								getClient().sendPacket(new PacketActionAccept(player.getID()));
							}
							setEnabled(false);
							break;
						}
					}
				}
			}
		});
	}
	
	@Override
	public void setText(String text)
	{
		if (type == ButtonType.REFUSABLE)
		{
			String[] split = text.split("`");
			if (split.length > 1)
			{
				acceptText = split[0];
				refuseText = split[1];
				
			}
			else
			{
				acceptText = text;
				refuseText = text;
			}
			updateRefusableText();
			return;
		}
		super.setText(text);
	}
	
	public void updateRefusableText()
	{
		super.setText(getClient().getGameState().getActionState().isUsingActionCounter() ? refuseText : acceptText);
		repaint();
	}

	public void setType(ButtonType type)
	{
		this.type = type;
	}
	
	public ButtonType getType()
	{
		return type;
	}
	
	public static enum ButtonType
	{
		NORMAL, RENT, REFUSABLE;
	}
}
