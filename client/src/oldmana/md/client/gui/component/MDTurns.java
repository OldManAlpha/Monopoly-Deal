package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;
import oldmana.md.client.state.ActionStateDraw;
import oldmana.md.client.state.GameState;

public class MDTurns extends MDComponent
{
	public MDTurns()
	{
		super();
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.GRAY);
		Polygon t1 = new Polygon();
		t1.addPoint(scale(10), 0);
		t1.addPoint(scale(65), 0);
		t1.addPoint(scale(55), scale(30));
		t1.addPoint(0, scale(30));
		Polygon t2 = new Polygon();
		t2.addPoint(scale(65), 0);
		t2.addPoint(scale(125), 0);
		t2.addPoint(scale(115), scale(30));
		t2.addPoint(scale(55), scale(30));
		Polygon t3 = new Polygon();
		t3.addPoint(scale(125), 0);
		t3.addPoint(scale(180), 0);
		t3.addPoint(scale(170), scale(30));
		t3.addPoint(scale(115), scale(30));
		
		GameState gs = getClient().getGameState();
		int turns = gs.getTurns();
		boolean drawing = gs.getActionState() instanceof ActionStateDraw;
		
		g.setColor(turns >= 3 || drawing ? Color.GRAY : GraphicsUtils.GREEN);
		g.fillPolygon(t1);
		g.setColor(turns >= 2 || drawing ? Color.GRAY : GraphicsUtils.GREEN);
		g.fillPolygon(t2);
		g.setColor(turns >= 1 || drawing ? Color.GRAY : GraphicsUtils.GREEN);
		g.fillPolygon(t3);
		g.setColor(Color.DARK_GRAY);
		g.drawLine(scale(65), 0, scale(55), scale(30));
		g.drawLine(scale(125), 0, scale(115), scale(30));
		Polygon whole = new Polygon();
		whole.addPoint(scale(10), 0);
		whole.addPoint(scale(180), 0);
		whole.addPoint(scale(170), scale(30) - 1);
		whole.addPoint(0, scale(30) - 1);
		g.drawPolygon(whole);
		if (!drawing)
		{
			g.setColor(Color.BLACK);
			TextPainter tp = new TextPainter("Turns: " + turns, GraphicsUtils.getBoldMDFont(Font.PLAIN, scale(24)), getVisibleRect());
			tp.setVerticalAlignment(Alignment.CENTER);
			tp.setHorizontalAlignment(Alignment.CENTER);
			tp.paint(g);
		}
	}
}
