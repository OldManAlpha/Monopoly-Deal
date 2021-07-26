package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.MDScheduler.MDTask;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;
import oldmana.md.client.state.ActionStateDraw;

public class MDTurns extends MDComponent
{
	private int maxTurns = 3;
	private int visibleMaxTurns = maxTurns;
	
	private double visibleTurns;
	
	private boolean rush;
	
	public MDTurns()
	{
		super();
		
		getClient().getScheduler().scheduleTask(new MDTask(1, true)
		{
			@Override
			public void run()
			{
				int turns = getTurns();
				
				if (visibleTurns > turns + 1 || visibleTurns < turns - 1)
				{
					rush = true;
				}
				
				if (visibleTurns > turns)
				{
					visibleTurns -= rush ? 0.1 : 0.05;
					if (visibleTurns < turns)
					{
						visibleTurns = turns;
					}
					repaint();
				}
				else if (visibleTurns < turns)
				{
					visibleTurns += rush ? 0.1 : 0.05;
					if (visibleTurns > turns)
					{
						visibleTurns = turns;
					}
					repaint();
				}
				else
				{
					rush = false;
				}
				if (visibleTurns > visibleMaxTurns)
				{
					visibleMaxTurns++;
					repaint();
				}
				if (visibleMaxTurns > maxTurns && visibleTurns <= maxTurns)
				{
					visibleMaxTurns--;
					repaint();
				}
			}
		});
	}
	
	public void setMaxTurns(int maxTurns)
	{
		this.maxTurns = maxTurns;
	}
	
	public int getMaxTurns()
	{
		return maxTurns;
	}
	
	private int getTurns()
	{
		return getClient().getGameState().getTurns();
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		int turns = getTurns();
		boolean drawing = getClient().getGameState().getActionState() instanceof ActionStateDraw;
		
		int width = 170;
		double turnWidth = (double) width / visibleMaxTurns;
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.GRAY);
		List<Polygon> turnPolygons = new ArrayList<Polygon>();
		List<Polygon> activePolygons = new ArrayList<Polygon>();
		for (int i = 0 ; i < visibleMaxTurns ; i++)
		{
			Polygon p = new Polygon();
			double startX = turnWidth * i;
			double endX = turnWidth * (i + 1);
			p.addPoint(scale(10 + startX), 0);
			p.addPoint(scale(10 + endX), 0);
			p.addPoint(scale(endX), scale(30));
			p.addPoint(scale(startX), scale(30));
			turnPolygons.add(p);
			if (visibleTurns >= i + 1)
			{
				activePolygons.add(p);
			}
			else if ((int) visibleTurns == i)
			{
				Polygon ap = new Polygon();
				double visibleEndX = startX + ((endX - startX) * (visibleTurns - (int) visibleTurns));
				ap.addPoint(scale(10 + startX), 0);
				ap.addPoint(scale(10 + visibleEndX), 0);
				ap.addPoint(scale(visibleEndX), scale(30));
				ap.addPoint(scale(startX), scale(30));
				activePolygons.add(ap);
			}
		}
		
		g.setColor(Color.GRAY);
		for (int i = 0 ; i < visibleMaxTurns ; i++)
		{
			g.fillPolygon(turnPolygons.get(i));
		}
		g.setColor(GraphicsUtils.GREEN);
		for (int i = 0 ; i < activePolygons.size() ; i++)
		{
			Polygon p = activePolygons.get(i);
			g.fillPolygon(p);
			if (i == activePolygons.size() - 1 && (visibleTurns != turns && visibleTurns < visibleMaxTurns))
			{
				LinearGradientPaint paint = new LinearGradientPaint(p.xpoints[2] - scale(4), 0, p.xpoints[2] + scale(10), scale(3), 
						new float[] {0F, 1F}, new Color[] {new Color(255, 255, 255, 0), new Color(255, 255, 255, 200)});
	    		g.setPaint(paint);
	    		g.fillPolygon(p);
			}
		}
		g.setColor(Color.DARK_GRAY);
		for (int i = 0 ; i < visibleMaxTurns ; i++)
		{
			Polygon p = turnPolygons.get(i);
			g.drawLine(p.xpoints[0], p.ypoints[0], p.xpoints[3], p.ypoints[3]);
		}
		
		g.setColor(Color.DARK_GRAY);
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
