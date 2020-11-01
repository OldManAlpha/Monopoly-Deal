package oldmana.md.client.gui.component.overlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import oldmana.md.client.Player;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class MDPlayerAcceptOverlay extends JComponent
{
	public MDPlayerAcceptOverlay(Player player)
	{
		super();
		setSize(player.getUI().getSize());
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		g.setColor(new Color(0, 0, 0, 50));
		g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
		g.setColor(Color.RED);
		Font f = GraphicsUtils.getBoldMDFont(40);
		TextPainter tp = new TextPainter("Click To Accept Just Say No", f, getVisibleRect());
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
	}
}
